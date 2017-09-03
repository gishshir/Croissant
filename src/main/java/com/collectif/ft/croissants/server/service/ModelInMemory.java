package com.collectif.ft.croissants.server.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.collectif.ft.croissants.server.business.Absence;
import com.collectif.ft.croissants.server.business.Alert;
import com.collectif.ft.croissants.server.business.Alert.AlertType;
import com.collectif.ft.croissants.server.business.History;
import com.collectif.ft.croissants.server.business.Task;
import com.collectif.ft.croissants.server.business.User;
import com.collectif.ft.croissants.server.dao.DaoModel;
import com.collectif.ft.croissants.server.util.DateUtils;
import com.collectif.ft.croissants.shared.model.bean.AbsenceBean;
import com.collectif.ft.croissants.shared.model.bean.AlertBean;
import com.collectif.ft.croissants.shared.model.bean.TaskBean;
import com.collectif.ft.croissants.shared.model.bean.UserBean;

public class ModelInMemory {
	
	private static final Log log = LogFactory.getLog(ModelInMemory.class);
	
	public enum Order {
		croissant, decroissant, NA
	}
	
	private static final ModelInMemory instance = new ModelInMemory();
	public static final ModelInMemory getInstance() {
		return instance;
	}
	private ModelInMemory() {}
	
		
	private DaoModel _daoModel = new DaoModel();
	
	public DaoModel getDaoModel() {
		return this._daoModel;
	}
	void setDaoModel (DaoModel daoModel) {
		this._daoModel = daoModel;
	}

	//------------------------------------------------------- pulic methods
	public void addHistory(History history) {
		this._daoModel.getHistoryList().add(history);
	}
	public int getListHistorySize ()  {
		final List<History> listHistory = this._daoModel.getHistoryList();
		return (listHistory == null)?0:listHistory.size();
	}
	/**
	 * Retourne la date de la première tache historisée
	 * @return null si pas d'historique
	 */
	public Date getDateFirstHistory() {
		
		final List<History> listHistory = this.getListHistory(Order.croissant);
		return (listHistory.isEmpty())?null:listHistory.get(0).getDate();
	}
	/**
	 * Retourne la liste par ordre de date croissante ou decroissante
	 * @return
	 */
	public List<History> getListHistory(Order order) {
		final List<History> listHistory = this._daoModel.getHistoryList();
		if (order == Order.NA) {
			return listHistory;
		}
		
		// tri par ordre croissant de la plus ancienne à la plus récente
		Collections.sort(listHistory);
		
		if (order == Order.decroissant) {
		  Collections.reverse(listHistory);
		}
		return listHistory;
	}
	public User createUser (String login) {
		User user =  new User(login);
		// la date d'enregistrement correspond par défaut à la date de creation
		// elle peut être modifiée par l'utilisateur
		user.setRegistration(DateUtils.getNewUTCDate());
		this._daoModel.getUserList().add(user);
		return  user;
	}
	public Absence createAbsence( AbsenceBean absenceBean , User user) {
		final Absence absence = new Absence(absenceBean, user);
		this._daoModel.getAbsenceList().add(absence);
		return absence;
	}
	/**
	 * Remove the absence
	 * @param absence
	 * @return
	 */
	public boolean deleteAbsence (Absence absence) {

        boolean result =  this._daoModel.getAbsenceList().remove(absence);
        return result;
	}

	public Alert createAlert(AlertBean alertBean, User user) {
		Alert alert = new Alert(alertBean, user);
		this._daoModel.getAlertList().add(alert);
		return alert;
	}
	public void updateAlert(AlertBean alertBean) {
		
		final List<Alert> listAlerts = this.getListAlertForUser(alertBean.getUserId());
		
		boolean found = false;
		Alert localAlert = null;
		AlertType alertType = Enum.valueOf(AlertType.class, alertBean.getType());
		for (Alert alert : listAlerts) {
			if (alert.getAlerteType() == alertType) {
				found = true;
				localAlert = alert;
				break;
			}
		}
		
		if (found) {
			localAlert.updateFromBean(alertBean);
		}		
		
	}
	public List<Alert> getListAlertForUser(int userId) {
		final List<Alert> listAlerts = new ArrayList<Alert>(3);
		
		for (Alert alert : this._daoModel.getAlertList()) {
			if (alert.getUser().getId() != userId) continue;
			
			listAlerts.add(alert);
		}
		
		return listAlerts;
	}
	
	public List<Absence> getListAbsenceForUser(int userId) {
		final List<Absence> listAbsences = new ArrayList<Absence>();
		
		for (Absence absence : this._daoModel.getAbsenceList()) {
			if (absence.getUser().getId() != userId) {
				continue;
			}
			listAbsences.add(absence);
		}
		return listAbsences;
	}
	
	public boolean hasAbsence (int userId) {
		 List<Absence> listAbsence = this.getListAbsenceForUser(userId);
		 return listAbsence != null && !listAbsence.isEmpty();
	}
	
	public boolean deleteAlert(Alert alert) {
		return this._daoModel.getAlertList().remove(alert);
	}
	
	public void deleteEmptyHistories() {
		
		Iterator<History> iter =this._daoModel.getHistoryList().iterator();
		while (iter.hasNext()) {
			History history = (History) iter.next();
			if (history.isEmpty()) {
				iter.remove();
			}
		}
		

	}

	public TaskBean createTask (Date date) {
		
		Task task =  new Task(date);
		this._daoModel.getTaskList().add(task);
		return task.asBean();
	}
	
	public User updateUser (UserBean userBean) {

		log.debug("addOrUpdateUser() " + userBean.getId());
		final User localUser = this.findLocalUserById(userBean.getId());
		localUser.updateFromBean(userBean);
		
		return localUser;
	}
	public Task updateTaskBean (TaskBean bean) {

		log.debug("updateTaskBean() " + bean.getId());
		final Task localTask = this.findLocalTaskById(bean.getId());
	
		if (localTask != null) {
			localTask.update(bean);
		}
		
		return localTask;
	}
	
	public User getUserById(int userId) {
		return this.findLocalUserById(userId);
	}
	
	public boolean hasUsers() {
		return !this._daoModel.getUserList().isEmpty();
	}
	public int getCountUsers () {
		 final List<User> listUser = this._daoModel.getUserList();
		 return (listUser == null)?0:listUser.size();
	}
	public List<Integer> getListUserIds() {

	   final List<User> listUser = this._daoModel.getUserList();
       final List<Integer> list = new ArrayList<Integer>( listUser == null?0:listUser.size());
  
       if (listUser != null) {
         for (User user :listUser) {
		    list.add(user.getId());
	     }
       }
       return list;
	}
	public List<Integer> getListTaskIds() {

	       final List<Integer> list = new ArrayList<Integer>(this._daoModel.getTaskList().size());
	       for (Task task : this._daoModel.getTaskList()) {
			    list.add(task.getId());
		    }
	       return list;
		}
	
	public List<User> getAllUsers() {
		return this._daoModel.getUserList();
	}
	public List<User> getFreeUsers() {

          if (!this.hasUsers()) {
        	 return null; 
          }
          
          final List<User> listFreeUserBeans = new ArrayList<User>();
          for (User user : _daoModel.getUserList()) {
			if (user.isFree()) {
				listFreeUserBeans.add(user);
			}
		  }
          
          return listFreeUserBeans;
	}
	
	public List<Task> getListTask() {
		return this._daoModel.getTaskList();
	}
	
	public List<Task> getListTaskAfterDate(Date date) {
		
		if (date == null) {
			return null;
		}
		List<Task> allTasks = this._daoModel.getTaskList();
		List<Task> filteredTasks = new ArrayList<Task>();
		
		if (allTasks != null) {
			for (Task task : allTasks) {
				if (DateUtils.isDate1AfterDate2(task.getDate(), date))	 {
					filteredTasks.add(task);
				}
			}				
		}
		
		return filteredTasks;
	}
	
   public List<Task> getListTaskBeforeDate(Date date) {
		
		if (date == null) {
			return null;
		}
		List<Task> allTasks = this._daoModel.getTaskList();
		List<Task> filteredTasks = new ArrayList<Task>();
		
		if (allTasks != null) {
			for (Task task : allTasks) {
				if (DateUtils.isDate1AfterDate2(date, task.getDate()))	 {
					filteredTasks.add(task);
				}
			}				
		}
		
		return filteredTasks;
	}
	
	public void deleteUser (int userId) {
		
		final User localUser = this.findLocalUserById(userId);
		if (localUser != null) {
			this._daoModel.getUserList().remove(localUser);
		}
	}
	
	public void deleteTask(Task task){
		
		this._daoModel.getTaskList().remove(task);
	}
	
	public List<History> findLocalHistoriesByDate(Date date) {
		
		final List<History> listHistories = this._daoModel.getHistoryList();
		if (listHistories == null || listHistories.isEmpty()) {
			return null;
		}
		List<History> listForDate = new ArrayList<History>();
		for (History history : listHistories) {
			if (history.getDate().equals(date)){
				listForDate.add(history);
			}
		}
		return listForDate;
	}
	
	public User findLocalUserById(int userId) {
		log.trace("findLocalUserById() - userId: " + userId);
		for (User user : _daoModel.getUserList()) {
			if (user.getId() == userId) {
				return user;
			}
		}
		log.debug("findLocalUserById() - result >> null");
		return null;
	}
	public Task findLocalTaskById(int taskId) {
		for (Task task :_daoModel.getTaskList()) {
			if (task.getId() == taskId) {
				return task;
			}
		}
		return null;
	}
	
}
