package com.collectif.ft.croissants.server.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.collectif.ft.croissants.server.business.Absence;
import com.collectif.ft.croissants.server.business.Alert;
import com.collectif.ft.croissants.server.business.Alert.AlertType;
import com.collectif.ft.croissants.server.business.History;
import com.collectif.ft.croissants.server.business.History.UserFulfillment;
import com.collectif.ft.croissants.server.business.Task;
import com.collectif.ft.croissants.server.business.User;
import com.collectif.ft.croissants.server.dao.DaoModel;
import com.collectif.ft.croissants.server.dao.DaoService;
import com.collectif.ft.croissants.server.service.ModelInMemory.Order;
import com.collectif.ft.croissants.server.service.alert.AlertManager;
import com.collectif.ft.croissants.server.service.alert.IAlertEventListener;
import com.collectif.ft.croissants.server.util.DateUtils;
import com.collectif.ft.croissants.shared.model.bean.AlertBean;
import com.collectif.ft.croissants.shared.model.bean.TaskBean;
import com.collectif.ft.croissants.shared.model.bean.UserBean;
import com.collectif.ft.croissants.shared.model.dto.TaskAndUserDto;
import com.collectif.ft.croissants.shared.model.dto.UserAndAlertDto;
import com.collectif.ft.croissants.shared.model.dto.UserScoreDto;
import com.collectif.ft.croissants.shared.model.dto.UserScoreInternal;

/**
 * Méthodes de service mise à la disposition de toutes les classes du serveur
 * @author sylvie
 *
 */
public class CroissantManager {

	
	private static final CroissantManager instance = new CroissantManager();
	public static final CroissantManager getInstance() {
		return instance;
	}
	
	private static final Log log = LogFactory.getLog(CroissantManager.class);
	private final ModelInMemory modelInMemory = ModelInMemory.getInstance();
	
	private static final ResourceBundle resoureBundle  = ResourceBundle.getBundle("messages", Locale.getDefault());
	private static final String USER_SCORE_COMMENT = "user.score.comment";
	private static final String USER_SCORE_NO_PARTICIPATION = "user.score.noParticipation";
	
	private static final long DAY_IN_MS = 24 * 60 * 60 * 1000;
	
	private IAlertEventListener _alertEventListener;
	
	// intervalle entre deux taches
	private int _intervallBetweenTask;
	
	// timer pour réaliser des taches journalières de maintenance
	private Timer _timerEachDay;
	private boolean _initDone = false;
	
    //------------------------------------------ private constructor
	private CroissantManager() {}
	
	
	//----------------------------------------- public methods
	void init(int intervallBetweenTask, IAlertEventListener alertEventListener) {
		
		if (!this._initDone) {
		  this._intervallBetweenTask = intervallBetweenTask;
		  this._alertEventListener = alertEventListener;
		  this.loadTimer();
		  this._initDone = true;
		}
	}
	void restoreAndControlDaoModel(String contextRootPathname) {
		DaoModel daoModel = DaoService.getInstance().restore(contextRootPathname);
		if (daoModel != null) {
		  ModelInMemory.getInstance().setDaoModel(daoModel);
		  CroissantManager.getInstance().controlCoherenceModel(daoModel);
		}	
	}
	
	
	/**
	 * Completer eventuellement les informations manquantes
	 * au fur et à mesure de l'evolution du model
	 * @param daoModel
	 */
	void controlCoherenceModel(DaoModel daoModel) {
		
		log.info("controlCoherenceModel()");
		if (daoModel == null) {
			return;
		}
		// AJouter taskIncomplete si absente
		List<User> listUser =  daoModel.getUserList();
		if (listUser != null) {
			
			for (User user : listUser) {
				this.controleAlerts(user);
			}
		}
		
		
	}
	
	void createAlertsForNewUser(UserAndAlertDto userAndAlertDto, User user) {
		
		// par defaut creer une alerte de chaque type
		for (AlertType alertType : AlertType.values()) {
			this.createAlert(userAndAlertDto, user, alertType);		
		}	
	}
	
		
	
	/**
	 * retourne les scores des utilisateurs passés en argument par ordre croissant
	 * Règle de classement du score:
	 * <ul>
	 *   <li>score positif croissant
	 *   <li>score negatif decroissant
	 *   <li>dernière realisation de tache effectuée
	 * <ul>
	 * 
	 * Traitement particulier des utilisateurs n'ayant jamais participé.
	 * Leur position relative dépendra de leur date d'arrivée, et du nombre
	 * de fois qu'ils ont bénéficié des croissants.
	 * Lors de leur arrivée ils sont en fin de liste, puis rétrograde de 2 places à chaque
	 * tache consommée.
	 * @param userIds : utilisateurs pour lesquels on souhaite le score
	 * @return
	 */
	public List<UserScoreDto> getUserScoreForListUser(List<Integer> userIds)  {
		
		// On établit la liste des scores/user puis on les classe par ordre croissant
		// (tient compte du score et de la dernière tache realisée)
		List<UserScoreDto> listUserScoreDto = this.calculateAllScoreAndParticipation(userIds);
		Collections.sort(listUserScoreDto);
		
		if (listUserScoreDto != null) {
			
			for (UserScoreDto userScoreDto : listUserScoreDto) {
				log.debug(userScoreDto.toString());
			}
		}
		
		return listUserScoreDto;
	}
	
	
	
	
	UserScoreDto loadUserScore(int userId)  {
		
		if (this.modelInMemory.getUserById(userId) == null) {
			 return null;
		}
						
		final UserScoreDto userScoreDto = new UserScoreDto(userId);
		List<History> listHistory = modelInMemory.getListHistory(Order.NA);
		if (listHistory == null || listHistory.isEmpty()) {
			return userScoreDto;
		}
		// for each History
		Date lastRealizedTask = null;
		for (History history : listHistory) {
			if (history.contains(userId)) {
				UserFulfillment fulfillment = history.getUserFulfillment(userId);
				if (fulfillment.isFulfillment()) {
					if (DateUtils.isDate1AfterDate2(history.getDate(), lastRealizedTask)) {
						lastRealizedTask = history.getDate();
					}
				}
				userScoreDto.addScore(fulfillment.isFulfillment());
			}
		}
		userScoreDto.setLastRealisedTask(lastRealizedTask);
		

		return userScoreDto;
	}


	boolean removeUserFromTask(User user, Task task) {
		
		if (user == null || task == null) {
			return false;
		}
		return task.removeUserFromTask(user);
	}

	void deleteTask(Task localTask)  {

        boolean taskInThePast = false;
        
        // if past task then add to history
        if (DateUtils.isDateTimeInPast(localTask.getDate()) ) {
        	final History history = new History(localTask);
        	this.modelInMemory.addHistory(history);
        	taskInThePast = true;
        }
        
        // remove the users from task
        if (!localTask.isEmpty()) {
        	
        	List<User> listUsers = localTask.getListUsers();
        	if (listUsers != null) {
        		
        		List<User> tempoList = new ArrayList<User>(listUsers);
        		
        		for (User user : tempoList) {
        			if (user == null) continue;
        			// ne pas envoyer d'alerte si la tache est passée.
        			if (!taskInThePast) {
        			    this._alertEventListener.onTaskDateChanged(user.getId(), localTask.getDate(), null);
        			}
        			this.removeUserFromTask(user, localTask);
				}
        	}
        }
        
        // delete the task
		this.modelInMemory.deleteTask(localTask);
	}
	boolean cancelTimer() {
		if (this._timerEachDay != null) {
			log.info("cancelTimer()");
			this._timerEachDay.cancel();
			this._timerEachDay = null;
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Propose/valide la distribution d'une liste d'utilisateur (free) dans les taches disponibles.<br/>
	 * Auparavant il a fallu récuperer
	 * - la liste des utilisateur libre classes par ordre croissant de score
	 * - la liste des taches incomplete dans un interval de date
     * 
	 * @param listUserId : liste d'utilisateur à distribuer déjà classés pas score croissant
	 * @param taskIds : liste des taches à attribuer, classées par ordre croissant de date
	 * @param simulation : true simulation, false validation
	 * 
	 * @return list de proposition. Il faudra la valider ultérieurement!!
	 * non null uniquement lors de la simulation
	 */
	List<TaskAndUserDto> dispatchFreeUserInIncompleteTask (List<Integer> userIds, List<Integer> taskIds, boolean simulation)  throws Exception {
		
		// on récupère la liste des utilisateurs, on s'assure que les utilisateurs selectionnés sont libres
		
		ListUserToDistribute listFreeUsersToDistribute = new ListUserToDistribute();
		for (Integer userId : userIds) {
		    User user = this.modelInMemory.getUserById(userId);
		    List<Absence> listAbsence = this.modelInMemory.getListAbsenceForUser(userId);
			if (user.isFree()) {
				listFreeUsersToDistribute.add(new UserToDistribute(user, listAbsence));
			}
		}
		if (listFreeUsersToDistribute.isEmpty()) {
			throw new Exception("The users to distribute are no free!!");
		}
		
		// On récupère la liste des task, on s'assure qu'elle sont toujours incompletes
		List<Task> listTasks = this.modelInMemory.getListTask();
		
		List<Task> listTaskToAttribute = new ArrayList<Task>();
		for (Task task : listTasks) {
			
			if (taskIds.contains(task.getId())) {
				if (task.isSpaceForAnotherUser()) {
					listTaskToAttribute.add(task);
				}
			}
			
     	}
		
		// Si aucune tache avec disponibilité >> erreur
		if (listTaskToAttribute.isEmpty()) {
			throw new Exception("There is no empty task in this interval of dates !!");
		}

		
        // On cree la liste des taches avec les users associés pour validation
		// Rien n'est changé dans la base de données !!!
		List<TaskAndUserDto> listTaskAndUserDto = (simulation)?new ArrayList<TaskAndUserDto>():null;
		
		final int maxCountUser = listFreeUsersToDistribute.size();
		int indexUser = 0;
		
		// Pour chaque tache disponible on associe un ou plusieur utilisateur libre dans l'ordre de priorite
		for (Task task : listTaskToAttribute) {
			
			if (indexUser >= maxCountUser) {
				// il n'y a plus d'utilisateur à distribuer!!
				break;
			}
			
			TaskBean taskBean = task.asBean();
			int freeSpace = taskBean.getFreeSpace();
			if (freeSpace == 0) {
				continue;
			}
			// pour chaque emplacement libre on y met un nouvel utilisateur
			for (int i = 0; i < freeSpace; i++) {
				
				User userToDistribute = listFreeUsersToDistribute.getNextUserToDistribute(task);
				
				if (userToDistribute != null) {
				  if (simulation) {
				    taskBean.addUserId(userToDistribute.getId());
				  }
				  else {
					this.moveUserToTask(userToDistribute.getId(), taskBean.getId());
				  }
				}
				indexUser++;
				
				if (indexUser >= maxCountUser) {
					// il n'y a plus d'utilisateur à distribuer!!
					break;
				}
			}
			
			if (simulation) {
			  // nouvelle liste d'utilisateur pour la tache
			  List<UserBean> listUserBean = new ArrayList<UserBean>();
			  for (Integer userId : taskBean.getListUserIds()) {
				User user = this.modelInMemory.getUserById(userId);
				if (user != null) {
					listUserBean.add(user.asBean());
				}
			  }
						
			listTaskAndUserDto.add(new TaskAndUserDto(taskBean, listUserBean));
			}
		}
			
		return (simulation)?listTaskAndUserDto:null;		
		
	}
	
	void moveUserToTask(int userId, int taskId)
			throws Exception {

      final User localUser = this.modelInMemory.findLocalUserById(userId);
      final Task nextTask = this.modelInMemory.findLocalTaskById(taskId);
      if (localUser == null) {
    	  throw new Exception("this user doesnt exist!");
      }
      if (nextTask == null) {
    	  throw new Exception("this task doesnt exist!");
      }
      
      Task previousTask = this.findTaskOfUser(localUser);
     this.removeUserFromTask(localUser, previousTask);

      if (nextTask.isSpaceForAnotherUser()) {
    	  nextTask.setUserBeanInAnyFreePlace(localUser);
    	  this._alertEventListener.onTaskDateChanged(userId, 
    			  (previousTask == null)?null:previousTask.getDate(),
    					  nextTask.getDate());
      }	
	}
	

	/**
	 * On parcours les task. Dès qu'on trouve le User c'est bon on peut arreter l'iteration
	 * En effet un User ne peut être affect� � deux taches simultan�es.
	 * @param userBean
	 * @return la task où était l'utilisateur
	 */
	Task findTaskOfUser (User user) {
		for (Task task : this.modelInMemory.getListTask()) {
			
			if (task.containsUser(user)) {
				return task;
			}
		}
		return null;
	}
	
	//---------------------------------------------- private methods
    private void loadTimer() {
		
		log.info("loadTimer()");
		this.cancelTimer();
	    this._timerEachDay = new Timer();
		
		TimerTask timerTask = new TimerTask() {
			
			@Override
			public void run() {
				manageOldTaskToHistory();
			}
		};
		this._timerEachDay.scheduleAtFixedRate(timerTask, 500,  DAY_IN_MS);
	}


    /**
     * Historise automatiquement les taches réalisées depuis plus de 'intervallBetweenTask' jours.
     */
    private void manageOldTaskToHistory() {
    	
    	log.info("manageOldTaskToHistory()");
    	// List des taches avant (now - _intervallBetweenTask)
    	Date dateSeuil = DateUtils.addXDayToDate(DateUtils.getNewUTCDate(), -1 * this._intervallBetweenTask);
    	
    	List<Task> taskList = this.modelInMemory.getListTaskBeforeDate(dateSeuil);
    	if (taskList != null) {

    		for (Task task : taskList) {
        		// forcer l'historisation de la tache
        		log.info(">> historiser automatiquement la tache: " + task.getDate());
				this.deleteTask(task);
			}
    	}
    }


    private List<UserScoreDto> calculateAllScoreAndParticipation(List<Integer> userIds) {
	
	    // liste des taches historisées
	   List<History> listHistory = new ArrayList<History>(
			   this.modelInMemory.getListHistory(ModelInMemory.Order.croissant));
	   
	   // on ne retient que les taches qui ont été réalisée au moins par un participant
	   Iterator<History> iterHistory = listHistory.iterator();
	   while (iterHistory.hasNext()) {
		   
		    History history = (History) iterHistory.next();
         	if (!history.hasBeenFulfilled()) {
         	 iterHistory.remove();
         	}
	   }
	   int countTotRealizableTasks = listHistory.size();
		
		// Map [idUser / UserScoreDto]
		final Map<Integer, UserScoreInternal> mapUserScoreByUserId = new HashMap<Integer, UserScoreInternal>();
		
		//-------------------------------------------------------
		// calcul du nombre de taches realisables par participant
		//-------------------------------------------------------
		
		// Pour chaque participant de la liste
		// on calcule le nombre de task realisables en fonction de ses absences
		// c'est à dire dans l'ensemble des taches historisées on enlève celle qui se sont déroulées
		// pendant son absence ou avant son inscription
		for (Integer userId : userIds) {
			
			User user = modelInMemory.getUserById(userId);
			UserScoreInternal userScoreInternal = new UserScoreInternal(userId, user.getLogin());
			mapUserScoreByUserId.put(userId, userScoreInternal);
			
			Date dateRegistration = user.getRegistration();
			Date dateFirstHistory = this.modelInMemory.getDateFirstHistory();
			// si la registration a eu lieu après le démarrage de l'historique, alors il faudra en tenir compte dans
			// le calcul de la participation
			boolean isRegistrationAfterFirtsHistory = (dateFirstHistory == null)?false:
					DateUtils.isDate1AfterDate2(dateRegistration, dateFirstHistory);
			
			// aucune absence et registration avant la première date de l'historique,
			// le nombre de taches réalisables correspond au nombre de taches historisées.
			if (!isRegistrationAfterFirtsHistory && !this.modelInMemory.hasAbsence(userId)) {
				userScoreInternal.setCountRealizableTask(countTotRealizableTasks);
				continue; // next user
			}

			// avec au moins une periode d'absence ou une registration après le démarrage de l'historique
			log.info("au moins une absence or registration after history beginning - user " + userId);
			for (History history : listHistory) {

	        	if (this.isTaskWhileUserPresence(userId, history.getDate(), dateRegistration)) {
        			userScoreInternal.incrementsRealizableTask();
        		}
			}
			
		}
		
		//-----------------------------------------------------
		// calcul du nombre de taches realisées par participant
		//-----------------------------------------------------
		
		// on parcours la liste de l'historique	realizable	
        for (History history : listHistory) {

        	List<UserFulfillment> listUserFulfillment = history.getListUserFulfillment();
        	for (UserFulfillment userFulfillment : listUserFulfillment) {
        		
        		int userId = userFulfillment.getUser().getId();			 
        		UserScoreInternal userScoreInternal = mapUserScoreByUserId.get(userId);
				 if (userScoreInternal == null) {
					 //participant ignored
					continue;
				 }
				  
        		if (userFulfillment.isFulfillment()) {
				  userScoreInternal.setLastRealisedTask(history.getDate());
				  userScoreInternal.incrementsRealizedTask();
        		}
			    userScoreInternal.addScore(userFulfillment.isFulfillment());
			}
		}
        
        //------------------------------------------
        // calcul de la participation relative
        // ajout du commentaire
        //------------------------------------------
        this.calculateRelativeParticipation(mapUserScoreByUserId);
        
       String pattern = resoureBundle.getString(USER_SCORE_COMMENT);

        // on recupère la liste finale de UserScoreDto
        final List<UserScoreDto> listUserScore = new ArrayList<UserScoreDto>();
        for (UserScoreInternal userScoreDto : mapUserScoreByUserId.values()) {
        	

			String comment =  (userScoreDto.getCountRealizedTask() == 0)?
									
			MessageFormat.format(resoureBundle.getString(USER_SCORE_NO_PARTICIPATION), Integer.toString(userScoreDto.getCountRealizableTask())):
			MessageFormat.format(pattern, userScoreDto.getUserName(),
					Integer.toString(userScoreDto.getCountRealizedTask()),
					Integer.toString(userScoreDto.getCountRealizableTask()), Integer.toString(userScoreDto.getRelativeParticipation()));
        	userScoreDto.setComment(comment);
        	
			listUserScore.add(userScoreDto);
		}
        
 
        
		
		return listUserScore; 
	}
	
	/**
	 * Calcul la participation relative sur un intervale [0,10]
	 * Le principe est de rapporter la note à la note maximale sur une échelle de 10
	 * 
	 * @param mapUserScoreByUserId
	 */
	private void calculateRelativeParticipation (Map<Integer, UserScoreInternal> mapUserScoreByUserId) {
		
		int userCount = mapUserScoreByUserId.size();
		float aRetrancherParTacheConsommee = 2 * 10 / (float)userCount;
		
		//-------------------------------
		// chercher max
		//------------------------------
        float maxNote = 0;
        	
		for (UserScoreInternal userScore : mapUserScoreByUserId.values()) {
			
			maxNote = Math.max(maxNote,userScore.getParticipation());
		}

		
		// -------------------------------------------------------------------------
		//pour chaque participant calculer la
		// participation relative sur une echelle [0, 10] où 10 est la note max de l'ensemble
		// -------------------------------------------------------------------------
		for (UserScoreInternal userScore : mapUserScoreByUserId.values()) {
			
			// participation ramenée à une note de 10
			float exactParticipation = 0;
			int relativeParticipation = 0;
			
			// cas particulier des utilisateur n'ayant jamais participé
			if (userScore.getCountRealizedTask() == 0) {
				
				// donner une note relative de 10 par defaut puis retrancher
				// une valeur de (2 * 10/(nombre total user)) à chaque tache consommée
				exactParticipation = 10 - (userScore.getCountRealizableTask() * aRetrancherParTacheConsommee);
				exactParticipation = Math.max(exactParticipation, 0f);
				
			}
			else { // cas nominal
			
			     exactParticipation = userScore.getParticipation() *10 /maxNote;
			
			}

			relativeParticipation = Math.round(exactParticipation);
			log.info("user: " + userScore.getUserId() + " - exactParticipation: " + exactParticipation + "- round: " + relativeParticipation);
			userScore.setRelativeParticipation(relativeParticipation);
			userScore.setExactParticipation(exactParticipation);
		}
	}
	/**
	 * On determine si la tache s'est déroulée pendant la période de présence de l'utilisateur
	 */
	private boolean isTaskWhileUserPresence(int userId, Date taskDate, Date userRegistration) {
		
		// si tache avant registration du participant
		if (DateUtils.isDate1AfterDate2(userRegistration, taskDate)) {
			return false;
		}
		
		List<Absence> listAbsence = this.modelInMemory.getListAbsenceForUser(userId);
		if (listAbsence == null || listAbsence.isEmpty()) {
			return true;
		}
				
		// la tache est-elle située lors d'une absence du participant ?
		for (Absence absence : listAbsence) {
			if (DateUtils.isDayDateInDayInterval(absence.getBeginDate(), absence.getEndDate(), taskDate)) {
				return false;
			}
		}		
		
		return true;
	}
	private void createAlert(UserAndAlertDto userAndAlertDto, User user, AlertType alertType) {

		switch (alertType) {
		case ChangeDate:
			this.modelInMemory.createAlert(new AlertBean(user.getId(), AlertType.ChangeDate.name(), null, userAndAlertDto.isChangeDateAlert()), user);
			break;

		case DeleteUser:
			this.modelInMemory.createAlert(new AlertBean(user.getId(), AlertType.DeleteUser.name(), null, userAndAlertDto.isDeleteUserAlert()), user);
			break;
			
		case TaskFree:
			this.modelInMemory.createAlert(new AlertBean(user.getId(), AlertType.TaskFree.name(), null, userAndAlertDto.isTaskFreeAlert()), user);
			break;
			
		case TaskToDo:
			this.modelInMemory.createAlert(new AlertBean(user.getId(), AlertType.TaskToDo.name(), Integer.toString(userAndAlertDto.getDelai()), userAndAlertDto.isTaskToDoAlert()), user);
			break;
		}
		
	}
	
	/**
	 * S'assure que toutes les alertes existent bien pour l'utilisateur
	 * @param user
	 */
	private void controleAlerts (User user) {
		
		log.debug("controleAlerts() - user: " + user.getId());
		List<Alert> listAlert = this.modelInMemory.getListAlertForUser(user.getId());
		final UserAndAlertDto userAndAlertDto = new UserAndAlertDto(user.asBean());
		
		// cas limite : pas d'alertes!
		if (listAlert == null || listAlert.isEmpty()) {
			log.warn("Aucune alerte!!! .... creation de toutes les alertes");
			CroissantManager.getInstance().createAlertsForNewUser(userAndAlertDto, user);
			return;
		}
		
		// manque au moins une alert		
		if (listAlert.size() < AlertType.values().length) {
			
			log.warn("manque au moins une alert...");
			
			// chercher celles qui manquent... et la creer
			for (AlertType alertType : AlertType.values()) {
				Alert alert = AlertManager.getInstance().getAlert(user.getId(), alertType);
				if (alert == null) {
					log.warn("creation alert de type: " + alertType);
                   this.createAlert(userAndAlertDto, user, alertType);
				}
			}			
		}
	}
	
	private static class UserToDistribute {
		
		private final User _user;
		private boolean _distibuted = false;
		private final List<Absence> _listAbsence;
		
		private UserToDistribute (User user, List<Absence> listAbsence) {
			this._user = user;
			this._listAbsence = listAbsence;
		}
		
		/**
		 * Cherche si utilisateur disponible pour la tache
		 * - non distribué
		 * - pas d'absence pendant la date prévue de la tâche
		 * @param date
		 * @return
		 */
		private boolean isFreeForTask(Date date) {
			if (this._distibuted) {
				return false;
			}
			if (this._listAbsence == null || this._listAbsence.isEmpty()){
				return true;
			}
					
			// chercher si la date est en dehors des absences futures
			for (Absence absence : this._listAbsence) {
				if (DateUtils.isDayDateInDayInterval(absence.getBeginDate(), absence.getEndDate(), date)){
					return false;
				}
			}

			return true; 
		}
	}
	
	private static class ListUserToDistribute {
		
		private final List<UserToDistribute> list = new ArrayList<CroissantManager.UserToDistribute>();
		
		/**
		 * On cherche le premier utilisateur de la liste disponible pour la distribution
		 * @param task
		 * @return
		 */
		private User getNextUserToDistribute (Task task) {

            for (UserToDistribute userToDistribute : list) {
				if (userToDistribute.isFreeForTask(task.getDate())) {
					userToDistribute._distibuted = true;
					return userToDistribute._user;
				}
			}
			return null;
		}
		private void add(UserToDistribute userToDistribute) {
			list.add(userToDistribute);
		}
		private boolean isEmpty() {
			return this.list.isEmpty();
		}
		private int size() {
			return list.size();
		}
	}
}
