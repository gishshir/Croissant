package com.collectif.ft.croissants.server.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.collectif.ft.croissants.client.service.ICroissantService;
import com.collectif.ft.croissants.server.business.Absence;
import com.collectif.ft.croissants.server.business.Alert;
import com.collectif.ft.croissants.server.business.Alert.AlertType;
import com.collectif.ft.croissants.server.business.History;
import com.collectif.ft.croissants.server.business.History.UserFulfillment;
import com.collectif.ft.croissants.server.business.Task;
import com.collectif.ft.croissants.server.business.User;
import com.collectif.ft.croissants.server.dao.DaoService;
import com.collectif.ft.croissants.server.service.ModelInMemory.Order;
import com.collectif.ft.croissants.server.service.alert.AlertManager;
import com.collectif.ft.croissants.server.service.alert.IAlertEventListener;
import com.collectif.ft.croissants.server.service.message.MessageManager;
import com.collectif.ft.croissants.server.service.message.MessageManager.SmtpParams;
import com.collectif.ft.croissants.server.util.DateUtils;
import com.collectif.ft.croissants.server.util.ModelUtils;
import com.collectif.ft.croissants.shared.model.bean.AbsenceBean;
import com.collectif.ft.croissants.shared.model.bean.AlertBean;
import com.collectif.ft.croissants.shared.model.bean.TaskBean;
import com.collectif.ft.croissants.shared.model.bean.UserBean;
import com.collectif.ft.croissants.shared.model.dto.HistoryAndUserDto;
import com.collectif.ft.croissants.shared.model.dto.TaskAndUserDto;
import com.collectif.ft.croissants.shared.model.dto.UserAndAbsencesDto;
import com.collectif.ft.croissants.shared.model.dto.UserAndAlertDto;
import com.collectif.ft.croissants.shared.model.dto.UserAndScoreDto;
import com.collectif.ft.croissants.shared.model.dto.UserHistoryDto;
import com.collectif.ft.croissants.shared.model.dto.UserScoreDto;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;



/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CroissantServiceImpl extends RemoteServiceServlet implements
		ICroissantService {
	
	private static final Log log = LogFactory.getLog(CroissantServiceImpl.class);
	
	// nombre d'éléments dans la page de l'historique
	private static final int HISTORIQUE_PAGINATION = 5;
	
	//Serveur SMTP pour l'envoi des mails d'alerte
	static final String PARAM_SMTP_SERVER = "smtpServer";
	// login du serveur SMTP
	static final String PARAM_SMTP_LOGIN = "smtpLogin";
	// Pwd du serveur SMTP
	static final String PARAM_SMTP_PWD = "smtpPwd";
	// email from
	static final String PARAM_SMTP_FROM = "smtpFrom";
	// Activation de l'envoi reel des mails
	static final String PARAM_ACTIVE_SEND_MAIL = "activeSendMail";
	// Periode du timer des alertes taskToDo et taskFree en minute
	static final String PARAM_PERIOD_TIMER_ALERT_TASKTODO = "periodTimerAlertTaskToDoMinute";
	
	// Interval par defaut entre deux task
	static final String PARAM_INTERVAL_BETWEEN_TASK = "intervallBetweenTask";
	
	private final static String GWT_MODULE_NAME = "croissant";
	
	private final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	// intervalle en jour entre deux taches
	private int _intervalBetweenTask = 1;
	
	// url de l'application construite après la première requete
	private String _applicationUrl = null;
	
	private final ModelInMemory modelInMemory = ModelInMemory.getInstance();
	private final IAlertEventListener alertEventListener = AlertManager.getInstance();
	private final CroissantManager croissantManager = CroissantManager.getInstance();

  // only for tests
	public CroissantServiceImpl() {}

	//------------------------------------------------ overriding HttpServlet
	
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
			
		boolean activeSendMail = new Boolean(config.getInitParameter(PARAM_ACTIVE_SEND_MAIL));
		SmtpParams smtpParams = new SmtpParams(config.getInitParameter(PARAM_SMTP_SERVER),
				                               config.getInitParameter(PARAM_SMTP_LOGIN),
				                               config.getInitParameter(PARAM_SMTP_PWD),
				                               config.getInitParameter(PARAM_SMTP_FROM),
				                               activeSendMail);
	
		
		MessageManager.getInstance().init(smtpParams, this.getModuleRootPath());
		
		int periodTimerAlertTaskToDoMinutes = -1;
		try {
			periodTimerAlertTaskToDoMinutes = Integer.parseInt(config.getInitParameter(PARAM_PERIOD_TIMER_ALERT_TASKTODO));
		} catch (NumberFormatException e) {
			log.warn("periodTimerAlertTaskToDoMinutes not defined!");
		}

		
		this._intervalBetweenTask = 1;
		try {
			this._intervalBetweenTask = Integer.parseInt(config.getInitParameter(PARAM_INTERVAL_BETWEEN_TASK));
		} catch (NumberFormatException e) {
			log.warn("intervalBetweenTask not defined!");
		}

		AlertManager.getInstance().init(periodTimerAlertTaskToDoMinutes,( 2 * this._intervalBetweenTask) + 1);
      
		CroissantManager.getInstance().init(this._intervalBetweenTask, alertEventListener);
	}
	
	


	@Override
	public ServletContext getServletContext() {
		return super.getServletContext();
	}
	
	
	
//    @Override
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//			throws ServletException, IOException {
//		this.buildApplicationUrl(req);
//		super.doGet(req, resp);
//	}

	private void buildApplicationUrl(HttpServletRequest req) {
		if (this._applicationUrl == null) {
			
		  String contextPath = this.getServletConfig().getServletContext().getContextPath();
			
			this._applicationUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort()  + contextPath;
		    log.info("ApplicationUrl: " + this._applicationUrl);
			MessageManager.getInstance().setApplicationUrl(this._applicationUrl);
		}
	}
	//------------------------------------------------- overriding ICroissantService
   /**
    * Retourne la liste des absences de l'utilisateur ou liste vide si aucunne
    * @param userId
    * @return
    * @throws Exception
    */
	@Override
	public UserAndAbsencesDto loadListAbsenceForUser(int userId) throws Exception {
		
		// On recupere la liste des absences de l'utilisateur
		 User user = this.modelInMemory.getUserById(userId);
	        if (user == null) {
	        	 throw new Exception("this user doesnt exists!");
	        }

	    final UserAndAbsencesDto userAndAbsencesDto = new UserAndAbsencesDto(user.asBean());
	    final List<AbsenceBean> listAbsenceBean = new ArrayList<AbsenceBean>();
	    final List<Absence> listAbsence = this.modelInMemory.getListAbsenceForUser(userId);
	    if (listAbsence == null || listAbsence.isEmpty()) {
	    	userAndAbsencesDto.setListAbsences(new ArrayList<AbsenceBean>(0));
	    	return userAndAbsencesDto;
	    }
	    
	    for (Absence absence : listAbsence) {
	    	listAbsenceBean.add(absence.asBean());
		}
	    Collections.sort(listAbsenceBean);
	    userAndAbsencesDto.setListAbsences(listAbsenceBean);
	    return userAndAbsencesDto;
	}
	
	/**
	 * Nouvelle absence pour l'utilisateur
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@Override
	public AbsenceBean loadNewAbsence(int userId, int clientOffset) throws Exception{
		
		// On recupere  l'utilisateur
		 User user = this.modelInMemory.getUserById(userId);
		 if (user == null) {
			throw new Exception("this user doesnt exists!");
		}
		
		 this.calendar.setTime(DateUtils.getNewUTCDate());
		 Date beginDate =    DateUtils.verifyTaskDate(this.calendar.getTime(), clientOffset);
		 this.calendar.add(Calendar.DAY_OF_YEAR, 7);
		 Date endDate = DateUtils.verifyTaskDate(this.calendar.getTime(), clientOffset);
		 AbsenceBean absenceBean = new AbsenceBean(user.getId(), beginDate, endDate, clientOffset);
		 absenceBean = this.modelInMemory.createAbsence(absenceBean, user).asBean();	
		 return absenceBean;
		
	}
	@Override
	public boolean deleteAbsence (AbsenceBean absenceBean) throws Exception{
		

        final Absence absenceToDelete = this.findAbsence(absenceBean);
        if (absenceToDelete == null) {
        	return false;
        }
        // delete absence
        return this.modelInMemory.deleteAbsence(absenceToDelete);
	}
	@Override
	public AbsenceBean updateAbsence (AbsenceBean absenceBean) throws Exception{
		
		// on recherche l'absence à modifier
		final Absence absenceToModify = this.findAbsence(absenceBean);
        if (absenceToModify == null) {
        	throw new Exception("this absence doesnt exists!");
        }
        absenceToModify.updateFromBean(absenceBean);
		return absenceToModify.asBean();
	}
	
	
	
	/**
	 * Retourne la liste des utilisateur free avec leur score par ordre croissant
	 * @return
	 */
	@Override
	public List<UserAndScoreDto> loadListUserAndScore ()  throws Exception {
		
		
		// on récupère la liste de tous les utilisateurs
		List<User> listUsers = this.modelInMemory.getAllUsers();
		if (listUsers == null) {
			return  new ArrayList<UserAndScoreDto>(0);
		}
		
		// list des scores par ordre croissant
		List<UserScoreDto> listUserScore =
				this.croissantManager.getUserScoreForListUser(ModelUtils.listIds(listUsers));
		
		// list des utilisateur et score
		List<UserAndScoreDto> listUserAndScore = new ArrayList<UserAndScoreDto>(listUsers.size());
		for (UserScoreDto userScoreDto : listUserScore) {
			User user  = this.modelInMemory.getUserById(userScoreDto.getUserId());
			listUserAndScore.add(new UserAndScoreDto(user.asBean(), userScoreDto,  user.isFree()));
		}
		
		return listUserAndScore;
		
	}
	
	/**
	 * Retourne la liste des taches dans un intervalle de date
	 *  qui ont au moins une place disponible. Eliminer les taches dans le passé
	 * 
	 *  @param beginDate : debut de l'intervalle de temps
	 * @param endDate : fin de l'intervalle de temps
	 * @return
	 */
	@Override
	public List<TaskAndUserDto> loadListOfIncompleteTask(Date beginDate, Date endDate, int clientOffset) throws Exception {
		
		
		// On cherche dans la liste des taches celles qui correspondent à l'intervalle de date
		// on retient celles qui ont au moins une disponibilité
		List<Task> listTasks = this.modelInMemory.getListTask();
		
		List<Task> listOfIncompleteTask = new ArrayList<Task>();
		for (Task task : listTasks) {
			if (DateUtils.isDateInInterval(beginDate, endDate, task.getDate())) {
				if (task.isSpaceForAnotherUser() && !DateUtils.isDateTimeInPast(task.getDate())) {
					listOfIncompleteTask.add(task);
				}
			}
		}
		
		// on associe task and user
		return this.loadListOfTaskAndUserDto(listOfIncompleteTask, clientOffset);
		
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
	@Override
	public List<TaskAndUserDto> dispatchFreeUserInIncompleteTask (List<Integer> userIds, List<Integer> taskIds, boolean simulation)  throws Exception {
		
		if (userIds == null || userIds.isEmpty() || taskIds == null || taskIds.isEmpty()) {
			return null;
		}
		
		return CroissantManager.getInstance().dispatchFreeUserInIncompleteTask(userIds, taskIds, simulation);
		
	}
	/**
	 * Liste des dates des taches réalisées par l'utilsateur
	 * avec indication si fulfillment true or false
	 */
	@Override
	public UserHistoryDto loadUserHistory(int userId) throws Exception {

		User user = this.modelInMemory.getUserById(userId);
		if (user  == null) {
			 throw new Exception("this user doesnt exists!");
		}
					
		final UserHistoryDto userHistoryDto = new UserHistoryDto(user.asBean());
		List<History> listHistory = modelInMemory.getListHistory(Order.croissant);
		if (listHistory == null || listHistory.isEmpty()) {
			return userHistoryDto;
		}
		
		// for each History
		for (History history : listHistory) {
		  if (history.contains(userId)) {
				
			  UserFulfillment fulfillment = history.getUserFulfillment(userId);
			  userHistoryDto.addDate(history.getDate(), fulfillment.isFulfillment());				
		  }
		}
		
		return userHistoryDto;
	}


	/**
	 * Charge le score positif/negatif d'un utilisateur
	 */
	@Override
	public UserScoreDto loadUserScore(int userId) throws Exception {
		
		if (this.modelInMemory.getUserById(userId) == null) {
			 throw new Exception("this user doesnt exists!");
		}
		return this.croissantManager.loadUserScore(userId);
	}
	/**
	 * Décale les users d'une série de tâche vers le haut ou vers le bas à partir d'une tâche 
	 * de départ.
	 * Ceci n'est possible que si au bout de la série il existe une tache vide. 
	 * @param beginTaskId première tache de la série à décaler
	 * @param up true: vers le haut, false vers le bas
	 */
	@Override
	public void shiftListUsers(int beginTaskId, boolean up) throws Exception {

       // reconstruit la liste completes des tasks
		List<Task> _listInMemory = modelInMemory.getListTask();
		if (_listInMemory == null || _listInMemory.size() < 2) {
			return;
		}
		List<Task> listTask = new ArrayList<Task>(_listInMemory);
		// tri par date croissante
		Collections.sort(listTask);
		
		// si shift up on inverse la liste
		if (up) {
		   Collections.reverse(listTask);
		}
		
		// on recherche la tache de démarrage et la première tache vide
		// pour construire la serie
		// si on ne trouve pas la tache de demarrage ou la tache vide >> exception
		List<Task> serieToShift = new ArrayList<Task>();
		boolean beginTaskFound = false;
		boolean emptyTaskFound = false;
		
		for (Task task : listTask) {
			
			//ne pas prendre en compte les taches before today
			if (DateUtils.isDateTimeInPast(task.getDate())) {
				continue; //next task
			}
			
			if (emptyTaskFound && beginTaskFound) {
				break;
			}
			
			if (beginTaskFound) {
				// on ajoute les suivantes
				serieToShift.add(task);
				if (task.isEmpty()) {
					emptyTaskFound = true;
				}
			}
			
			// on cherche la premiere tache
			if (!beginTaskFound && task.getId() == beginTaskId && !task.isEmpty()) {
				beginTaskFound = true;
				serieToShift.add(task);			
			}
		}
		if (!beginTaskFound || !emptyTaskFound) {
			throw new Exception ("the list of task must be reloaded!");
		}
		
		// on a maintenant la serie des taches pour le décalage
		// on la retourne pour commencer par la tache vide
		Collections.reverse(serieToShift);
		Task previousTask = serieToShift.get(0);
		for (int i = 1; i < serieToShift.size(); i++) {
			Task currentTask = serieToShift.get(i);
			
			// on deplace les utilisateurs de la currentTask vers previousTask
			List<User> listUser = new ArrayList<User>(currentTask.getListUsers());
			for (User user : listUser) {
				this.moveUserToTask(user.getId(), previousTask.getId());
			}
			
			// la currentTask est vide, elle devient la previous Task
			previousTask = currentTask;
			
		}
		
	}
	
	/**
	 * Retourne le nombre de page d'historique
	 * @return
	 */
	@Override
	public int getHistoryPageCount()  throws Exception {
		
		log.debug("getHistoryPageCount()");
		final int historySize = this.modelInMemory.getListHistorySize();
		if (historySize == 0) {
			return 0;
		}
		
		int mod = historySize % HISTORIQUE_PAGINATION;
		int count = historySize / HISTORIQUE_PAGINATION + ((mod > 0)?1:0);
		log.debug("... historySize: " + historySize + " - pages " + count);
		return count;
	}
	
	
	/**
	 * retourne l'historique par ordre décroissant
	 * @param page numero de page de l'historique
	 */
	@Override
	public List<HistoryAndUserDto> loadListHistoryAndUserDto(int page) throws Exception {

        log.debug("loadListHistoryAndUserDto()");
        List<HistoryAndUserDto> listHistoryAndUserDto = new ArrayList<HistoryAndUserDto>();
        
        List<History> listHistory = modelInMemory.getListHistory(Order.decroissant);
        if (!listHistory.isEmpty()) {
        	
            // bornes théoriques
            int offset = HISTORIQUE_PAGINATION * (page - 1);
            int debut = 1 + offset;
            int fin = HISTORIQUE_PAGINATION+ offset;
        	
            int total = (listHistory == null)?0:listHistory.size();
            
            // bornes en fonction de la taille réelle de l'historique
            // 1 --> n
            debut = (total >= debut)?debut:0;
            fin = (total >= fin)?fin:(total >= debut)?total:0;
           
        	for (int i = debut-1; i < fin; i++) {
				History history = listHistory.get(i);
				
        		HistoryAndUserDto historyAndUserDto = new HistoryAndUserDto(history.asBean());
        		listHistoryAndUserDto.add(historyAndUserDto);
        		
        		//l'history est-elle modifiable ?
        		if (!DateUtils.isDateOldBeforePresent(history.getDate().getTime(), this._intervalBetweenTask)) {
        			historyAndUserDto.setEditable(true);
        		}
        		
        		// liste des utilisateurs
        		List<History.UserFulfillment> userFulfillments = history.getListUserFulfillment();	
        		if (userFulfillments != null) {
        			
        			for (UserFulfillment userFulfillment : userFulfillments) {
						User user = userFulfillment.getUser();
						
						historyAndUserDto.addUserBean(user.asBean());	
					}
        		}
			}
        }
       
        
		return listHistoryAndUserDto;
	}
	
	@Override
	public List<String> getListSmileyUrls() {
		
		return SmileysManager.getInstance().getListSmileyUrls(this.getModuleRootPath());
	}
	


	@Override
	public List<UserBean> loadListOfFreeUser() throws Exception {
		
		this.buildApplicationUrl(this.getThreadLocalRequest());
			     
		final List<User> list = this.modelInMemory.getFreeUsers();
		final List<UserBean> listBeans = new ArrayList<UserBean>((list == null)?0:list.size());
		
		if (list != null) {
		  for (User user : list) {
			listBeans.add(user.asBean());
		  }
		}
		
		// ordonner par ordre alphabetique
		Collections.sort(listBeans, new Comparator<UserBean>() {

			@Override
			public int compare(UserBean user1, UserBean user2) {
				String login1 = user1.getLogin();
				String login2 = user2.getLogin();
				if (login1 == null && login2 == null) {
					return 0;
				}
				if (login1 == null) {
					return -1;
				}
				if (login2 == null) {
					return 1;
				}
				return user1.getLogin().compareTo(user2.getLogin());
			}
		});
		
		return listBeans;
	}

	@Override
	public UserBean loadNewUser() throws Exception {
		User user =this.modelInMemory.createUser(null);
		final UserAndAlertDto userAndAlertDto = new UserAndAlertDto(user.asBean());
		this.croissantManager.createAlertsForNewUser(userAndAlertDto, user);
		return user.asBean();
	}
	


	@Override
	public UserAndAlertDto loadUserAndAlert(int userId) throws Exception {

        User user = this.modelInMemory.getUserById(userId);
        if (user == null) {
        	 throw new Exception("this user doesnt exists!");
        }
			
			final UserAndAlertDto userAndAlertDto = new UserAndAlertDto(user.asBean());
			List<Alert> listAlert = this.modelInMemory.getListAlertForUser(userId);
						
			for (Alert alert : listAlert) {
				switch (alert.getAlerteType()) {
				  case DeleteUser: userAndAlertDto.setDeleteUserAlert(alert.isActive());
					break;
				  case TaskToDo: userAndAlertDto.setTaskToDoAlert(alert.isActive());
				                 userAndAlertDto.setDelai(Integer.parseInt(alert.getParam()));
					  break;
				  case ChangeDate: userAndAlertDto.setChangeDateAlert(alert.isActive());
					  break;
				  case TaskFree: userAndAlertDto.setTaskFreeAlert(alert.isActive());
				      break;
				}
			}
			return userAndAlertDto;		
        
	}

	
	@Override
	public UserAndAlertDto updateUserAndAlerts(UserAndAlertDto userAndAlertDto)
			throws Exception {
		
		 UserBean userBean = userAndAlertDto.getUserBean(); 
		 if (this.modelInMemory.getUserById(userBean.getId()) == null) {
			 throw new Exception("this user doesnt exists!");
		}
		 this.updateUser(userBean);
		 
		 // update alertes
		 int userId = userAndAlertDto.getUserBean().getId();
		 
		// si modification du delai --> en informer AlertManager
	
		Alert alertTaskToDo = AlertManager.getInstance().getAlert(userId,  AlertType.TaskToDo);
		int oldDelai = Integer.parseInt(alertTaskToDo.getParam());
		 
		 this.modelInMemory.updateAlert(new AlertBean(userId, AlertType.TaskToDo.name(), Integer.toString(userAndAlertDto.getDelai()),
				 userAndAlertDto.isTaskToDoAlert()));
		 if (oldDelai != userAndAlertDto.getDelai()) {
		    this.alertEventListener.onAlertTaskToDoChanged(userId);
		 }
		 this.modelInMemory.updateAlert(new AlertBean(userId, AlertType.TaskFree.name(),null, userAndAlertDto.isTaskFreeAlert()));
		 this.modelInMemory.updateAlert(new AlertBean(userId, AlertType.ChangeDate.name(),null, userAndAlertDto.isChangeDateAlert()));
		 this.modelInMemory.updateAlert(new AlertBean(userId, AlertType.DeleteUser.name(), null, userAndAlertDto.isDeleteUserAlert()));
		 	 
		 return this.loadUserAndAlert(userBean.getId());
	}


	@Override
	public UserBean updateUser(UserBean userBean) throws Exception {
		
		if (this.modelInMemory.getUserById(userBean.getId()) == null) {
			 throw new Exception("this user doesnt exists!");
		}
        return  this.modelInMemory.updateUser(userBean).asBean();	
	}

	@Override
	public void deleteUser(int userId) throws Exception {
		
		log.debug("deleteUser(): " + userId);
		if (this.modelInMemory.getUserById(userId) == null) {
			throw new Exception("this user doesnt exists!");
		}
		
		// Save before deleting
		this.saveDaoModel();
		
		this.alertEventListener.onUserDeleted(userId);
		
		// Enlever l'utilisateur de la tache si existe
		this.removeUserFromTask(userId);

        // supprimer les alertes de l'utilisateur
        List<Alert> listAlert = this.modelInMemory.getListAlertForUser(userId);
        for (Alert alert : listAlert) {
			this.modelInMemory.deleteAlert(alert);
		}
        
        
        // supprimer l'utilisateur de l'historique
        // et si historique est sans utilisateur alors supprimer l'historique
        List<History> listHistory = this.modelInMemory.getListHistory(Order.NA);
        for (History history : listHistory) {
    	   this.removeUserFromHistory(history, userId);
	    }
        ModelInMemory.getInstance().deleteEmptyHistories();
       

        // supprimer l'utilisateur
        this.modelInMemory.deleteUser(userId);
	}
	


	/**
	 * Retourne une liste de task and user
	 * warning : task date in client timezone
	 */
	@Override
	public List<TaskAndUserDto> loadListOfTaskAndUserDto(int clientOffset) throws Exception {
		
		//liste de toutes les taches
		final List<Task> listTask = new ArrayList<Task>( this.modelInMemory.getListTask());
		
		final List<TaskAndUserDto> listBean = this.loadListOfTaskAndUserDto(listTask, clientOffset);
		// determine les shiftvalues
		this.manageShiftValues(listBean);
		return listBean;
	}
	

	



	//Creation d'une tache avec J+intervallTask par rapport a la tache la plus loin dans le futur
	// La date crée doit être vu dans le timezone du client comme une date à 23:00
	@Override
	public TaskBean loadNewTask(int clientOffset) throws Exception {
		
	  final List<Task> listTask = ModelInMemory.getInstance().getListTask();
		
		Date date = DateUtils.getNewUTCDate();
		if (listTask != null) {
		
			// on cherche la plus loin dans le futur
		  for (Task task : listTask) {
			 
			  if (task.getDate().getTime() >= date.getTime()) {
				  date = task.getDate();
			  }
		  }

		  synchronized (calendar) {
			  // on ajoute n jour
			  calendar.setTime(date);
			  calendar.add(Calendar.DAY_OF_YEAR, this._intervalBetweenTask);
			  date = DateUtils.verifyTaskDate(calendar.getTime(), clientOffset);
		 }

		  
		}
		DateUtils.logDate("new date: ", date);
		return this.modelInMemory.createTask(date);
	}

	@Override
	public TaskBean updateTask(TaskBean taskBean) throws Exception {
		
		
		log.info("updateTask() - date: " + taskBean.getDate());
		
		Task localTask = this.modelInMemory.findLocalTaskById(taskBean.getId());
		if (localTask == null) {
			 throw new Exception("this task doesnt exists!");
		}
		
		// mettre la date à 23h00mn dans le timezone du client
		Date verifiedTaskDate = DateUtils.verifyTaskDate(taskBean.getDate(), taskBean.getOffset());
		if (verifiedTaskDate != taskBean.getDate()) {
			DateUtils.logDate("client date", taskBean.getDate());
			DateUtils.logDate("utc date",verifiedTaskDate);
			taskBean.setDate(verifiedTaskDate,  taskBean.getOffset());	
		}
		

		// control : do not set past date
		taskBean.setBeforeToday(DateUtils.isDateTimeInPast(taskBean.getDate()));
		if (taskBean.isDateBeforeToday()) {
			throw new Exception("The date cannot be set in the past!!");
		}
		
		
		Date oldDate = localTask.getDate();
		
		localTask =  this.modelInMemory.updateTaskBean(taskBean);
		Date newDate = localTask.getDate();
		
		if (!oldDate.equals(newDate)) {
			List<User> listUser = localTask.getListUsers();
			if (listUser != null) {
				for (User user : listUser) {
					this.alertEventListener.onTaskDateChanged(user.getId(), oldDate, newDate);
				}
			}
		}
		
		return localTask.asBean();
	}  

	@Override
	public void moveUserToTask(int userId, int taskId)
			throws Exception {

      CroissantManager.getInstance().moveUserToTask(userId, taskId);
	}

	/**
	 * On enlève l'utilisateur de la tache pour le mettre dans la box des users free
	 */
	@Override
	public void removeUserFromTask(int userId) throws Exception {
		 final User localUser = this.modelInMemory.findLocalUserById(userId);
		 if (localUser == null) {
			 throw new Exception("this user doesnt exists!");
		 }

		 Task previousTask = this.croissantManager.findTaskOfUser(localUser);
		 this.croissantManager.removeUserFromTask(localUser, previousTask);
	      
		 if (previousTask != null) {
			 this.alertEventListener.onTaskDateChanged(userId, previousTask.getDate(), null);
		 }

	}

	@Override
	public void deleteTask(int taskId) throws Exception {

		log.debug("deleteTask(): " + taskId);
		
        final Task localTask = this.modelInMemory.findLocalTaskById(taskId);
        if (localTask == null) {
        	throw new Exception("this task doesnt exists!");
        }
        
        this.croissantManager.deleteTask(localTask);
	}
	@Override
	public void deleteAllUsers() throws Exception {
		// Save before deleting
		this.saveDaoModel();
		
		final List<Integer> listUserId = this.modelInMemory.getListUserIds();
		if (listUserId == null || listUserId.isEmpty()) {
			return;
		}
		for (Integer userId : listUserId) {
			this.deleteUser(userId);
		}
	}

	@Override
	public void deleteAllTasks() throws Exception {
		
		// Save before deleting
		this.saveDaoModel();
		
		final List<Integer> listTaskId = this.modelInMemory.getListTaskIds();
		if (listTaskId == null || listTaskId.isEmpty()) {
			return;
		}
		for (Integer taskId : listTaskId) {
			this.deleteTask(taskId);
		}
	}
	@Override
	public void updateUserFulfillment(int userId, Date taskDate,
			boolean fulfillment) throws Exception {

		final User localUser = this.modelInMemory.findLocalUserById(userId);
	    if (localUser == null) {
	    	  throw new Exception("this user doesnt exist!");
	    }
	    final List<History> listHistoryForDate = this.modelInMemory.findLocalHistoriesByDate(taskDate);
	    if (listHistoryForDate == null) {
	    	throw new Exception("no history for this date: " + taskDate.toString());
	    }
	    
	    //on cherche la premiere history pour cet user
	    //et on met à jour le fulfillment
	    for (History history : listHistoryForDate) {
			if (history.contains(userId)) {
				history.updateUserFulfillment(userId, fulfillment);				
			}
		}
		
	}
	//------------------------------------------------------ private methods
	
	/**
	 * Retourne la liste des Task and User en fonction de la liste de task fournit en argument
	 * classe par date croissante (Dans le TZ du client)
	 * @param listTask
	 * @return
	 * @throws Exception
	 */
	private List<TaskAndUserDto> loadListOfTaskAndUserDto(List<Task> listTask, int clientOffset) throws Exception {
		// classer par date croissante
		
		final List<TaskAndUserDto> listTaskAndUserDto = new ArrayList<TaskAndUserDto>();
		for (Task task : listTask) {
			
			
			TaskBean taskBean = task.asBean();
			//taskBean.setDate(DateUtils.rebuildClientTZDate(taskBean.getDate()), clientOffset);
			final List<User> listUsers = task.getListUsers();
			List<UserBean> listUserBeans = (listUsers == null)?null:new ArrayList<UserBean>(listUsers.size());
			if (listUsers != null) {
				for (User user : listUsers) {
					if (user != null) {
					  listUserBeans.add(user.asBean());
					}
				}
			}
			
			TaskAndUserDto taskAndUserDto = new TaskAndUserDto(taskBean, listUserBeans);
			listTaskAndUserDto.add(taskAndUserDto);
		}
		Collections.sort(listTaskAndUserDto);
		return listTaskAndUserDto;
	}

	private String getModuleRootPath() {
		return this.getServletContext().getRealPath("/" + GWT_MODULE_NAME);
	}
	



	
	private void saveDaoModel() {
		if (this.getServletConfig() != null) {
			   DaoService.getInstance().save(this.getServletContext().getRealPath(""), ModelInMemory.getInstance().getDaoModel());			
			}
	}
	
	private boolean removeUserFromHistory(History history, int userId) {
		
		if (history.contains(userId)) {
			 history.removeUser(userId);
			 return true;
		}
		return false;
	}

	/**
	 * Parcourt la liste des taches et détermine les valeurs upShiftEnabled et downShiftEnabled
	 * - décalage d'une série vers le bas possible si il existe une tache vide à la fin de la série
	 * - décalage d'une série vers le haut possible si il existe une tache vide au début de la série
	 * @param listOfTaskAndUserDto
	 * @param down
	 */
	private void manageShiftValues(List<TaskAndUserDto> listOfTaskAndUserDto) {
		
		// up shift managment
		this._manageShiftValues(listOfTaskAndUserDto, true);
		
		// down shift managment : on inverse la liste
		Collections.reverse(listOfTaskAndUserDto);
		this._manageShiftValues(listOfTaskAndUserDto, false);
		// down shift managment : on retablit l'ordre initial
		Collections.reverse(listOfTaskAndUserDto);
		
	}
	private void _manageShiftValues(List<TaskAndUserDto> listOfTaskAndUserDto, boolean up) {
		
		if (listOfTaskAndUserDto == null) {
			return;
		}
		// on cherche la premiere tache vide
		boolean existTaskEmpty = false;
		
		
		//up or down shift managment
		for (int i = 0; i < listOfTaskAndUserDto.size(); i++) {
			TaskAndUserDto taskAndUserDto = listOfTaskAndUserDto.get(i);
			
			// ne pas prendre en compte les task before today
			if (taskAndUserDto.getTaskBean().isDateBeforeToday()) {
				continue; // next task
			}
			
			if (taskAndUserDto.getTaskBean().isEmpty()) {
				existTaskEmpty = true;
			} else {
				if (existTaskEmpty) {
					if (up) {
					  taskAndUserDto.setUpShiftEnabled(true);
					}
					else {
						taskAndUserDto.setDownShiftEnabled(true);
					}
				}
			}
		}
		
	}

	/**
	 * Trouver l'instance Absence en mémoire à partir d'une absenceBean
	 * @param absenceBean
	 * @return null si non trouvée
	 * @throws Exception
	 */
	private Absence findAbsence (AbsenceBean absenceBean) throws Exception{
		
		//on recupère l'utilisateur
		 User user = this.modelInMemory.getUserById(absenceBean.getUserId());
		 if (user == null) {
			throw new Exception("this user doesnt exists!");
		}
		 
		// rechercher l'absence
		final List<Absence> listAbsencesForUser = this.modelInMemory.getListAbsenceForUser(user.getId());
		if (listAbsencesForUser != null && !listAbsencesForUser.isEmpty()) {
			
			// chercher avec sur l'identifiant beginDate
			for (Absence absence : listAbsencesForUser) {
				if (absence.getBeginDate().getTime() == absenceBean.getInitialBeginDateTimeStamp()) {
					return absence;
				}
			}
		}
		return null;
	}



}
	
