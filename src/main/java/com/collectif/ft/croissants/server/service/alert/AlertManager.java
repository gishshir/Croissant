package com.collectif.ft.croissants.server.service.alert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.collectif.ft.croissants.server.business.Alert;
import com.collectif.ft.croissants.server.business.Alert.AlertState;
import com.collectif.ft.croissants.server.business.Alert.AlertType;
import com.collectif.ft.croissants.server.business.Task;
import com.collectif.ft.croissants.server.business.User;
import com.collectif.ft.croissants.server.service.CroissantManager;
import com.collectif.ft.croissants.server.service.ModelInMemory;
import com.collectif.ft.croissants.server.service.message.MessageManager;
import com.collectif.ft.croissants.server.util.DateUtils;
import com.collectif.ft.croissants.server.util.ModelUtils;
import com.collectif.ft.croissants.shared.model.dto.UserScoreDto;
import com.collectif.ft.croissants.shared.util.FieldValidator;

/**
 * Parcourt les alertes associées aux utilisateurs
 * et génère des messages stockes dans MessageBoxToFilter
 * @author sylvie
 *
 */
public class AlertManager implements IAlertEventListener {
	
	private static final AlertManager instance = new AlertManager();
	public static final AlertManager getInstance() {
		return instance;
	}
	
	private static final Log log = LogFactory.getLog(AlertManager.class);
	
	private static final long PERIOD_MANAGE_ALERTE = 1000 * 60 * 15; //15 mn
	
	private boolean _initDone = false;
	
	// initialisé à 2 * intervallBetweenTask
	private int _delaiForAlerteIncompleteTaskInDay = 2;
	// initialisé à 1 * intervallBetweenTask
	private int _delaiForRearmPeriodicallyAlertInDay = 1;
	// periode du timer pour les taskToDo
	private int _periodTimerAlertTaskToDoMinutes = 15;
	
	private Timer _timerTaskToDo;
	
    //------------------------------------------ private constructor
	private AlertManager() {}
	
	//------------------------------------------ implements IAlertEventListener

	@Override
	public void onAlertTaskToDoChanged(int userId) {

		// si existe une alerte taskTodo il faut la rearmer
		Alert alertTaskToDo = this.getAlert(userId, AlertType.TaskToDo);
		if (alertTaskToDo != null) {
			alertTaskToDo.reArms();
		}
	}

	@Override
	public void onUserDeleted(int userId) {
		this.manageAlertDeleteUser(this.getAlert(userId, AlertType.DeleteUser));
	}
	@Override
	public void onTaskDateChanged(int userId, Date oldDate, Date newDate) {
		
		// si existe une alerte taskTodo il faut la rearmer
		Alert alertTaskToDo = this.getAlert(userId, AlertType.TaskToDo);
		if (alertTaskToDo != null) {
			alertTaskToDo.reArms();
		}
		// manage alert change date
		this.manageAlertChangeDate(this.getAlert(userId, AlertType.ChangeDate), oldDate, newDate);
	}
	
	//----------------------------------------- public methods
	/**
	 * 
	 * @param periodTimerAlertTaskToDoMinutes
	 * @param delaiForAlerteTaskFreeInDay : nombre de jours dans le futur où on recherche les taches incompletes
	 */
	public void init(int periodTimerAlertTaskToDoMinutes, int delaiForAlerteIncompleTaskInDay) {
		
		if (!this._initDone) {
		  this._delaiForAlerteIncompleteTaskInDay = delaiForAlerteIncompleTaskInDay;
		  this._delaiForRearmPeriodicallyAlertInDay = delaiForAlerteIncompleTaskInDay / 2;
		  this._periodTimerAlertTaskToDoMinutes = periodTimerAlertTaskToDoMinutes;
		  this.loadTimer();
		  this._initDone = true;
		}
	}
	public boolean cancelTimer() {
		if (this._timerTaskToDo != null) {
			log.info("cancelTimer()");
			this._timerTaskToDo.cancel();
			return true;
		} else {
			return false;
		}
	}
	//------------------------------------------ private methods
	private void loadTimer() {
		
		log.info("loadTimer()");
		this.cancelTimer();
		  
		this._timerTaskToDo = new Timer();

		TimerTask timerTask = this.createTimerTask();
		this._timerTaskToDo.scheduleAtFixedRate(timerTask, 1000, 
				(this._periodTimerAlertTaskToDoMinutes>0)?this._periodTimerAlertTaskToDoMinutes * 1000 * 60:PERIOD_MANAGE_ALERTE);
	}
	
	private TimerTask createTimerTask() {
		log.info("createTimeTask()");
		return new TimerTask() {
			
			@Override
			public void run() {
				
				try {
				  manageAlertTaskToDoForAllUser();
				  manageAlertIncompleteTaskForAllUser();
				}
				catch (Exception ex) {
					log.error("Error in manageAlertTaskToDoForAllUser() .. continue timer... : " + ex.toString());
				}
			}
		};
	}

	
	/**
	 * On cherche la première tache incomplete dans l'interval 'delaiForAlertTaskFree'
	 * On établit la liste des utilisateurs à alerter de la manière suivante
	 * - tous les utilisateurs free
	 * - les utilisateurs associés aux taches suivant la tache à completer 
	 * TODO on ne prend pas en compte si user n'est pas inscrit pour cette alerte!
     *
	 * Classement des utilisateurs restants par score croissant
	 * Prendre les 10 premiers ou le 1/3 du nombre total d'utilisateur (le MIN)
	 * Leur envoyer une alerte.
	 */
	private void manageAlertIncompleteTaskForAllUser() {
		
		log.info("manageAlertIncompleteTaskForAllUser()");
		
		// On cherche la première tache incomplete dans l'interval 'delaiForAlertTaskFree'
		Task firstIncompleteTask = this.getFirsIncompleteTask();
		if (firstIncompleteTask == null) {
			return;
		}
		
		// On établit la liste des utilisateurs à alerter
		
		// 1/ utilisateur libres
		List<Integer> listFreeUserIds =  ModelUtils.listIds(ModelInMemory.getInstance().getFreeUsers());
		
		// 2/ utilisateur déjà affectés à une tache mais postérieure (proposition de décalage)
		List<Task> listTaskInFuture = ModelInMemory.getInstance().getListTaskAfterDate(firstIncompleteTask.getDate());
		List<Integer> listNoFreeUserIds = new ArrayList<Integer>();
		for (Task taskInFuture : listTaskInFuture) {
			if (!taskInFuture.isEmpty()) {
				listNoFreeUserIds.addAll(ModelUtils.listIds(taskInFuture.getListUsers()));
			}
		}
		
		// Liste potentielle de tous les utilisateurs à alerter
		List<Integer> listUserToAlertIds = (listFreeUserIds == null)?new ArrayList<Integer>(0):new ArrayList<Integer>(listFreeUserIds);
		listUserToAlertIds.addAll(listNoFreeUserIds);
		
		
		// classement par score croissant
		List<UserScoreDto> listUserScore =
				CroissantManager.getInstance().getUserScoreForListUser(listUserToAlertIds);
		
		// liste définitive des utilisateurs à alerter
		// on ne garde que les 10 premiers ayant un email valide ou le 1/3 de la liste totale (valeur min)
		int nbrUsers = ModelInMemory.getInstance().getCountUsers();
		int countMax = Math.min(10, nbrUsers / 3);
		countMax =	Math.min(countMax, listUserScore.size());
		
		// on compte les alertes valides
		int compteur = 0;
		
		for (UserScoreDto userScoreDto : listUserScore) {
					
			//Si la tache a realiser est non complètement attribuée 
			// et a moins de now + <delai> jours alors on envoie un message	
			int userId = userScoreDto.getUserId();
			boolean userfree = (listFreeUserIds == null)?false:listFreeUserIds.contains(userId);
		    boolean result = this.manageAlertIncompleteTask(firstIncompleteTask, this.getAlert(userId, AlertType.TaskFree), userfree);
		    if (result) {
		    	compteur++;
		    	if( compteur > countMax) {
		    		break;
		    	}
		    }
		}
		
	}
	
	private Task getFirsIncompleteTask() {
		
      // liste de toutes les taches
		List<Task> listTask = ModelInMemory.getInstance().getListTask();
		if (listTask == null || listTask.isEmpty()) {
			return null;
		}
		
		// chercher si il existe des taches à realiser non attribuées dans ce delai.
		// Dans ce cas on ne traite que la première
		Task taskFree = null;
		for (Task task : listTask) {
			if (task.isSpaceForAnotherUser() && this.isTimeToSendAlert(task.getDate(), this._delaiForAlerteIncompleteTaskInDay)) {
				taskFree = task;
				break;
			}
		}
		return taskFree;
	}

	private void manageAlertTaskToDoForAllUser() {
		
		log.info("manageAlertTaskToDoForAllUser()");
		List<Integer> listUserIds = ModelInMemory.getInstance().getListUserIds();
		if (listUserIds == null || listUserIds.isEmpty()) {
			return;
		}
		// for each user
		for (Integer userId : listUserIds) {
			this.manageAlertTaskToDo(this.getAlert(userId, AlertType.TaskToDo));
		}
	}
	public Alert getAlert(int userId, AlertType alertType) {
		
		List<Alert> listAlert = ModelInMemory.getInstance().getListAlertForUser(userId);
		if (listAlert != null) {
			for (Alert alert : listAlert) {
				if (alert.getAlerteType() == alertType) {
					return alert;
				}
			}
		}
		return null;
	}
	/**
	 * Si la tache a realiser est a moins de now + <delai> jours alors on envoie un message
	 * @param alert
	 */
	private void manageAlertTaskToDo(Alert alert) {
				
		if (!this.verifyPrerequis(alert)) {
			return;
		}
		log.debug("manageAlertTaskToDo() - userId: "+ alert.getUser().getId() );
		
		// si la tache a realiser est a moins de now + <delai> jours alors on envoie un message
		int delai = Integer.parseInt(alert.getParam());
		if (delai > 0) {
			// recuperer la tache a realiser
			final Task task = this.getTask(alert.getUser().getId());
			if (task == null) {
				return;
			}

            if (isTimeToSendAlert(task.getDate(), delai)) {
        	  MessageManager.getInstance().createMessageTaskToDo(alert, task.getDate(), delai);
          	  alert.setAlerteState(AlertState.done);
           }
	
		}
	}
	/**
	 * Envoyer un message d'alerte taskFree
	 * @param alert
	 * @param delai
	 * 
	 * @return true si l'alerte a été envoyée
	 */
	private boolean manageAlertIncompleteTask (Task taskFree, Alert alert, boolean userfree) {
			
		
		if (!this.verifyPrerequis(alert)) {
			return false;
		}
		log.debug("manageAlertIncompleteTask() - date: " + taskFree.getDate() + " - userId: "+ alert.getUser().getId());
		
        MessageManager.getInstance().createMessageIncompleteTask(alert, taskFree.getDate(), userfree);
        alert.setAlerteState(AlertState.done);
        return true;
	}
	

    private void manageAlertDeleteUser(Alert alert) {
    	
    	if (!this.verifyPrerequis(alert)) {
			return;
		}
    	log.debug("manageAlertDeleteUser()");
    	 MessageManager.getInstance().createMessageDeleteUser(alert);
    	 alert.setAlerteState(AlertState.done);
	}
    private void manageAlertChangeDate(Alert alert, Date oldDate, Date newDate) {
    	
    	log.debug("manageAlertChangeDate()");
    	if (!this.verifyPrerequis(alert)) {
			return;
		}
    	 MessageManager.getInstance().createMessageChangeDate(alert, oldDate, newDate);
    	 alert.setAlerteState(AlertState.done);
    	 alert.reArms();
	}
    
 
    /**
     * Prérequis : 
     * - alerte active
     * - alerte running
     * - user with validated email
     * @param alert
     * @return
     */
    private boolean verifyPrerequis(Alert alert) {
    	if (alert == null || !alert.isActive()) {
    		return false;
    	}
    	
    	// on rearme une alerte quand elle est done et de type periodique si la periode ecoulée
    	// depuis sa realisation est superieure au delai (_delaiForRearmPeriodicallyAlertInDay)
    	// rearm alert periodically ?
    	if ( alert.getAlerteType().isRearmPeriodically() && alert.getTimestampDone() > 0) {
    		if (DateUtils.isDateOldBeforePresent(alert.getTimestampDone(),
    				this._delaiForRearmPeriodicallyAlertInDay)) {
    			alert.reArms();
    		}
    	}
    	
    	if (alert.getAlerteState() != AlertState.running) {
    		return false;
    	}
    	User user = alert.getUser();
    	if (user == null || !user.hasEmail() ||
    			!FieldValidator.controlEmail(user.getEmail().getEmail())) {
    		return false;
    	}
    	return true;
    }

    // Faut-il envoyer une alerte pour la tache
	// on cherche si  l'écart de date (day date - day now) est <= au delai d'alerte
	// return false si tache est depassee ou  si on a pas encore atteind le seuil
	// et true si un message doit etre envoye
	private boolean isTimeToSendAlert(Date dateTask, int delai) {
				
	    // tache passee
		if (DateUtils.isDateTimeInPast(dateTask)) {
			log.debug("task in the past!");
			return false;
		}
				
		boolean timeToSendAlert = DateUtils.isDayDateCloseToPresentDay(dateTask, delai);
		String messageLog = "isTimeToSendAlert(): " + timeToSendAlert + "(task: " + dateTask + " - delai: " + delai + ")";
		if (timeToSendAlert) {
		   log.debug(messageLog);
		} else {
			log.trace(messageLog);
		}

		return timeToSendAlert;
		            
	}

	
	// chercher la tache que doit realiser l'utilisateur
	private Task getTask (int userId) {
		
		List<Task> listTask = ModelInMemory.getInstance().getListTask();
		if (listTask != null) {
			for (Task task : listTask) {
				List<User> listUserForTask = task.getListUsers();
				if (listUserForTask != null) {
					for (User user : listUserForTask) {
						if (user.getId() == userId) {
							return task;
						}
					}
				}
			}
		}
		return null;
	}


}
