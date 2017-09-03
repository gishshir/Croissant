package com.collectif.ft.croissants.server.dao;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.collectif.ft.croissants.server.business.Absence;
import com.collectif.ft.croissants.server.business.Alert;
import com.collectif.ft.croissants.server.business.History;
import com.collectif.ft.croissants.server.business.History.UserFulfillment;
import com.collectif.ft.croissants.server.business.Task;
import com.collectif.ft.croissants.server.business.User;
import com.collectif.ft.croissants.server.util.DateUtils;
import com.collectif.ft.croissants.shared.model.bean.AbsenceBean;
import com.collectif.ft.croissants.shared.model.bean.AlertBean;
import com.collectif.ft.croissants.shared.model.bean.HistoryBean;
import com.collectif.ft.croissants.shared.model.bean.IBean;
import com.collectif.ft.croissants.shared.model.bean.TaskBean;
import com.collectif.ft.croissants.shared.model.bean.UserBean;

public class DaoModelHelper {
	
	private static final String VERSION_1v0 = "1.0";
	
	private static final DaoModelHelper instance = new DaoModelHelper();
	public static final DaoModelHelper getInstance() {
		return instance;
	}
	
	private static final Log log = LogFactory.getLog(DaoModelHelper.class);

	private DaoModelHelper() {}
	
	
	@SuppressWarnings("unchecked")
	public JSONObject encode(DaoModel daoModel)  {
		
		// list users
		JSONArray listUser = new JSONArray();
		for (User user : daoModel.getUserList()) {
			JSONObject userJson = this.encode(user.asBean());
			listUser.add(userJson);
		}
		// list task
		JSONArray listTask = new JSONArray();
		for (Task task : daoModel.getTaskList()) {
			JSONObject taskJson = this.encode(task.asBean());
			listTask.add(taskJson);
		}
		
		//list alert
		JSONArray listAlert = new JSONArray();
		for (Alert alert : daoModel.getAlertList()) {
			JSONObject alertJson = this.encode(alert.asBean());
			listAlert.add(alertJson);
		}
		
		// list absence
		JSONArray listAbsence = new JSONArray();
		for (Absence absence: daoModel.getAbsenceList()) {
			JSONObject absenceJson = this.encode(absence.asBean());
			listAbsence.add(absenceJson);
		}
		
		// list history
		JSONArray listHistory =  new JSONArray();
		for (History history : daoModel.getHistoryList()) {
			JSONObject historyJson = DaoModelHelper.getInstance().encode(history.asBean());
			listHistory.add(historyJson);
		}
		
		//DaoModel
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("version", daoModel.getVersion());
		
		if (daoModel.getSaveDate() == null) {
			daoModel.setSaveDate(DateUtils.getNewUTCDate());
		}
		jsonObject.put("date", daoModel.getSaveDate().getTime());
		jsonObject.put("users", listUser);
		jsonObject.put("tasks", listTask);
		jsonObject.put("alerts", listAlert);
		jsonObject.put("histories", listHistory);
		jsonObject.put("absences", listAbsence);
		return jsonObject;
	}
	
	/**
	 * La version 1.0 ne connait pas la date de registration.
	 * Celle-ci par défaut doit être définie par la date de la première tache
	 * de l'historique
	 * @param daoModel
	 */
	private void transformFrom1v0Version(DaoModel daoModel) {
		
		log.debug("transformFrom1v0Version()");
		List<History> listHistory = daoModel.getHistoryList();
		
		if (listHistory != null && !listHistory.isEmpty()) {
		  // tri par ordre croissant, la date la plus ancienne en premier
		  Collections.sort(listHistory);
		  Date beginning = DateUtils.addXDayToDate(listHistory.get(0).getDate(), -1);
		  
			// on parcourt la liste des utilisateurs et on initialise la date d'inscription
			// si elle n'existe pas
			List<User> listUsers = daoModel.getUserList();
			if (listUsers != null) {
				for (User user : listUsers) {
					if (user.getRegistration() == null) {
						user.setRegistration(beginning);
					}
				}
			}
		}
		

	}
	
	public DaoModel decodeModel(String jsonModel) {
		DaoModel daoModel = new DaoModel();
		JSONParser parser = new JSONParser();

		JSONObject jsonObject;
		try {
			jsonObject =(JSONObject) parser.parse(jsonModel);
			
			// version
			String version = this.getStrValue(jsonObject, "version", null);
			if (version == null) {
				return null;
			}
			
			// date
			long datetime = this.getLongValue(jsonObject, "date", -1);
			if (datetime >= 0) {
				daoModel.setSaveDate(DateUtils.getNewUTCDate(datetime));
			}
			
			// list of users
			JSONArray userListArray = (JSONArray)jsonObject.get("users");		
			ListIterator<?> iterUser = userListArray.listIterator();
			while (iterUser.hasNext()) {
				JSONObject userJson = (JSONObject) iterUser.next();
                UserBean userBean = DaoModelHelper.getInstance().decodeUser(userJson);
                
                User user = new User(userBean.getId());
                user.updateFromBean(userBean);
                daoModel.getUserList().add(user);
			}
			
			// list of tasks
			JSONArray taskListArray = (JSONArray)jsonObject.get("tasks");		
			ListIterator<?> iterTask = taskListArray.listIterator();
			while (iterTask.hasNext()) {
				JSONObject taskJson = (JSONObject) iterTask.next();
                TaskBean taskBean = DaoModelHelper.getInstance().decodeTask(taskJson, daoModel);
                
                final Task task = new Task(taskBean.getId());
                task.updateFromBean(taskBean);
                
                // associate users
                final List<Integer> listUserIds = taskBean.getListUserIds();
                if (listUserIds != null) {

                   for (Integer userId : listUserIds) {
                	   User user = daoModel.getUser(userId);
                	   if (user != null) {
                	     task.addUser(user);
                	   }
				   }
                }

                daoModel.getTaskList().add(task);
			}
			
			// list of alerts
			JSONArray alertListArray = (JSONArray)jsonObject.get("alerts");
			if (alertListArray != null) {
				
			  ListIterator<?> iterAlert = alertListArray.listIterator();
			  while (iterAlert.hasNext()) {
				JSONObject alertJson = (JSONObject) iterAlert.next();
				AlertBean alertBean = this.decodeAlert(alertJson);
				
				User user = daoModel.getUser(alertBean.getUserId());
				if (user != null) {
				  Alert alert = new Alert(alertBean, user);
                  daoModel.getAlertList().add(alert);
				}
				
			}
			  
			}
			
			// list of absences
			JSONArray absenceListArray = (JSONArray)jsonObject.get("absences");
			if (absenceListArray != null) {
				
				 ListIterator<?> iterAbsence = absenceListArray.listIterator();
				  while (iterAbsence.hasNext()) {
					JSONObject absenceJson = (JSONObject) iterAbsence.next();
					AbsenceBean absenceBean = this.decodeAbsence(absenceJson);
					
					User user = daoModel.getUser(absenceBean.getUserId());
					if (user != null) {
					  Absence absence = new Absence(absenceBean, user);
	                  daoModel.getAbsenceList().add(absence);
					}
				  }
				
			}
			
			// list of history
			JSONArray historyListArray = (JSONArray)jsonObject.get("histories");
			if (historyListArray != null) {
				
				ListIterator<?> iterHistory = historyListArray.listIterator();
				while(iterHistory.hasNext()) {
					JSONObject historyJson = (JSONObject)iterHistory.next();
					HistoryBean historyBean = this.decodeHistory(historyJson);
					
					final Map<Integer, UserFulfillment> mapUserFulfillment = new HashMap<Integer, History.UserFulfillment>();
					
					if (historyBean.getMapUserAndFulfillment() != null) {
						
						for (Integer userId : historyBean.getMapUserAndFulfillment().keySet()) {
							User user = daoModel.getUser(userId);
							if (user != null) {
								mapUserFulfillment.put(userId, new UserFulfillment(user, historyBean.getMapUserAndFulfillment().get(userId)));
							}
						}
					}
					
					
					final History history = new History(historyBean.getDate(), mapUserFulfillment);
					daoModel.getHistoryList().add(history);
				}
				
			}
			
			if (version.equals(VERSION_1v0)) {
				this.transformFrom1v0Version(daoModel);
			}
			
			
		} catch (ParseException e) {
			log.error(e.toString());
		}
		
		return daoModel;
		
	}
	//----------------------------------------------------- private methods
	@SuppressWarnings("unchecked")
	private JSONObject encode(UserBean userBean) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", userBean.getId());
		jsonObject.put("login", userBean.getLogin());
		if (userBean.getEmail() != null) {
		  jsonObject.put("email", userBean.getEmail());
		}
		if (userBean.getLogo() != null) {
			jsonObject.put("logo", userBean.getLogo());
		}
		if (userBean.getRegistration() != null) {
			jsonObject.put("timestampRegistration", userBean.getRegistration().getTime());
		}
		
		return jsonObject;
	}
	
	private UserBean decodeUser(JSONObject userJson) {
		
		UserBean userBean = new UserBean(this.getIntValue(userJson, "id", IBean.ID_UNDEFINED));
		userBean.setLogin(this.getStrValue(userJson, "login", null));
		userBean.setEmail(this.getStrValue(userJson, "email", null));
		userBean.setLogo(this.getStrValue(userJson, "logo", null));
		long timestampRegistration = this.getLongValue(userJson, "timestampRegistration", -1);
		if (timestampRegistration != -1) {
			userBean.setRegistration(new Date(timestampRegistration));
		}
		return userBean;
	}
	@SuppressWarnings("unchecked")
	private JSONObject encode(AbsenceBean absenceBean) {
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("userId", absenceBean.getUserId());
		
		jsonObject.put("begindatetime", absenceBean.getBeginDate().getTime());
		jsonObject.put("enddatetime", absenceBean.getEndDate().getTime());
		return jsonObject;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject encode(AlertBean alertBean) {
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("userId", alertBean.getUserId());
		jsonObject.put("active", alertBean.isActive());
		jsonObject.put("type", alertBean.getType());
		jsonObject.put("state", alertBean.getState());
		if (alertBean.getParam() != null) {
			jsonObject.put("param", alertBean.getParam());
		}
		jsonObject.put("timestampDone", alertBean.getTimestampDone());
		return jsonObject;
		
	}
	
	private AlertBean decodeAlert(JSONObject alertJson) {
		
		int userId = this.getIntValue(alertJson, "userId", IBean.ID_UNDEFINED);
		String type = this.getStrValue(alertJson, "type", null);
		String state = this.getStrValue(alertJson, "state", null);
		String param = this.getStrValue(alertJson, "param", null);
		boolean active = this.getBooleanValue(alertJson, "active", true);
		long timestampDone = this.getLongValue(alertJson, "timestampDone", -1);
		
		return new AlertBean(userId, type, state, param, active, timestampDone);
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject encode(TaskBean taskBean) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", taskBean.getId());
		jsonObject.put("datetime", taskBean.getDate().getTime());
		
		final List<Integer> listUserIds = taskBean.getListUserIds();
		if (listUserIds != null) {
			
			JSONArray listIdJson = new JSONArray();
			for (int i = 0; i < listUserIds.size(); i++) {
				int userId = listUserIds.get(i);
				if (userId != IBean.ID_UNDEFINED) {
				  
					listIdJson.add(new Integer(userId));
				}
			}
			jsonObject.put("userIds", listIdJson);
		}
		return jsonObject;
	}
	
	private AbsenceBean decodeAbsence(JSONObject absenceJson) {
		
		int userId = this.getIntValue(absenceJson, "userId", IBean.ID_UNDEFINED);
		long begindateTime = this.getLongValue(absenceJson, "begindatetime", 0L);
		long enddateTime = this.getLongValue(absenceJson, "enddatetime", 0L);
		AbsenceBean absenceBean = new AbsenceBean(userId, DateUtils.getNewUTCDate(begindateTime),
				DateUtils.getNewUTCDate(enddateTime), 0);
		return absenceBean;
	}
	private TaskBean decodeTask(JSONObject taskJson, DaoModel daoModel) {

		TaskBean taskBean = new TaskBean(this.getIntValue(taskJson, "id", IBean.ID_UNDEFINED));
		
		long dateTime = this.getLongValue(taskJson, "datetime", 0L);
		taskBean.setDate(DateUtils.getNewUTCDate(dateTime), 0);
		
		JSONArray listUserIds = (JSONArray)taskJson.get("userIds");	
		if (listUserIds != null) {
			for (int i = 0; i < listUserIds.size(); i++) {
				Long userId = (Long)listUserIds.get(i);
				taskBean.addUserId(userId.intValue());
			}
		}
        
		return taskBean;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject encode(HistoryBean historyBean) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("datetime", historyBean.getDate().getTime());
		
		final Map<Integer, Boolean> map = historyBean.getMapUserAndFulfillment();
		if (map != null) {
			
			// list of {userId, fulfillment}
			JSONArray listIdBooleanJson = new JSONArray();
			for (Integer userId : map.keySet()) {
				JSONObject userFulFillObj = new JSONObject();
				userFulFillObj.put("userId", userId);
				userFulFillObj.put("fulfillment", map.get(userId));
				listIdBooleanJson.add(userFulFillObj);
			}
			jsonObject.put("userIdAndFulfillments", listIdBooleanJson);
		}
		
		
		return jsonObject;
	}
	
    private HistoryBean decodeHistory(JSONObject historyJson) {
    	
    	long dateTime = this.getLongValue(historyJson, "datetime", 0L);
    	final Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
    	
    	// list of {userId, fulfillment}
    	JSONArray listIdBooleanJson = (JSONArray)historyJson.get("userIdAndFulfillments");	
    	if (listIdBooleanJson != null) {
    		for (int i = 0; i < listIdBooleanJson.size(); i++) {
				
    			JSONObject userFulFillObj  = (JSONObject)listIdBooleanJson.get(i) ;
				int userId = this.getIntValue(userFulFillObj, "userId", IBean.ID_UNDEFINED);
				boolean fulfillment = this.getBooleanValue(userFulFillObj, "fulfillment", false);
				if (userId != IBean.ID_UNDEFINED) {
				  map.put(userId, fulfillment);
				}
			}
    	}
    	return  new HistoryBean(DateUtils.getNewUTCDate(dateTime), map);
    	
    }
	
	private String getStrValue(JSONObject jsonObject, String key, String defaultValue) {
		if (jsonObject == null || key == null) {
			return null;
		}
		if (jsonObject.containsKey(key)) {
			return (String) jsonObject.get(key);
		}
		else return defaultValue;
	}
	private int getIntValue(JSONObject jsonObject, String key, int defaultValue) {
		if (jsonObject == null || key == null) {
			return IBean.ID_UNDEFINED;
		}
		if (jsonObject.containsKey(key)) {
			Number number = (Number) jsonObject.get(key);
			return number.intValue();
		}
		else return defaultValue;
	}
	private boolean getBooleanValue(JSONObject jsonObject, String key, boolean defaultValue) {
		if (jsonObject == null || key == null) {
			return defaultValue;
		}
		if (jsonObject.containsKey(key)) {
			return(Boolean) jsonObject.get(key);
		}
		else return defaultValue;
	}
	private long getLongValue(JSONObject jsonObject, String key, long defaultValue) {
		if (jsonObject == null || key == null) {
			return IBean.ID_UNDEFINED;
		}
		if (jsonObject.containsKey(key)) {
			Number number = (Number) jsonObject.get(key);
			return number.longValue();
		}
		else return defaultValue;
	}
	
}
