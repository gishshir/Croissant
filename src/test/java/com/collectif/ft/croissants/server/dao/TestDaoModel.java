package com.collectif.ft.croissants.server.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.junit.Test;

import com.collectif.ft.croissants.server.business.Absence;
import com.collectif.ft.croissants.server.business.Alert;
import com.collectif.ft.croissants.server.business.Alert.AlertState;
import com.collectif.ft.croissants.server.business.Alert.AlertType;
import com.collectif.ft.croissants.server.business.History;
import com.collectif.ft.croissants.server.business.Task;
import com.collectif.ft.croissants.server.business.User;
import com.collectif.ft.croissants.shared.model.bean.AbsenceBean;
import com.collectif.ft.croissants.shared.model.bean.AlertBean;



public class TestDaoModel {
	
	private static final Log log = LogFactory.getLog(TestDaoModel.class);
	
	private static final TestDaoModel instance = new TestDaoModel();
	public static final TestDaoModel getInstance() {
		return instance;
	}
	
	@Test
	public void testEncode() {
		
		log.info("testEncode");
		final DaoModel model = this.buildModel();
		JSONObject jsonObject = DaoModelHelper.getInstance().encode(model);
		assertNotNull(jsonObject);
		
		final String result = jsonObject.toJSONString();
		assertNotNull(result);
		
		log.info(result);
	}
	
	@Test
	public void testDecodeV1v0() {
		
		log.info("testDecode");
		DaoModel model = this.buildModel();
		JSONObject jsonObjectV1v0 = DaoModelHelper.getInstance().encode(model);
		assertNotNull(jsonObjectV1v0);
		
		final String jsonString = jsonObjectV1v0.toJSONString();
		log.info("String to decode: " + jsonString);
		
		model = DaoModelHelper.getInstance().decodeModel(jsonString);
		String result = model.toJson();
		log.info("result of decoding: " + result);
		
		assertNotSame(jsonString, result);
	}
	@Test
	public void testDecodeV1v1() {
		
		log.info("testDecode");
		DaoModel model = this.buildModel();
		JSONObject jsonObjectV1v1 = DaoModelHelper.getInstance().encode(model);
		assertNotNull(jsonObjectV1v1);
		
		final String jsonString = jsonObjectV1v1.toJSONString();
		log.info("String to decode: " + jsonString);
		
		model = DaoModelHelper.getInstance().decodeModel(jsonString);
		String result = model.toJson();
		log.info("result of decoding: " + result);
		
		assertEquals(jsonString, result);
	}
	
	protected DaoModel buildModel() {
		return this.buildModel(DaoModel.VERSION);
	}
	protected DaoModel buildModel(String version) {
		
		DaoModel model = new DaoModel(version);
		
		// list user
		model.getUserList().add(this.buildUser("toto", version));
		model.getUserList().add(this.buildUser("titi", version));
		model.getUserList().add(this.buildUser("tata", version));
		model.getUserList().add(this.buildUser("tutu", version));
		model.getUserList().add(this.buildUser("xxxx", version));
		
		//list task -------------------------------------
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.WEEK_OF_MONTH, 1);
		Task task1 = this.buildTask(calendar.getTime());
		model.getTaskList().add(task1);
		
		calendar.add(Calendar.WEEK_OF_MONTH, 1);
		Task task2 = this.buildTask(calendar.getTime());
		model.getTaskList().add(task2);
		
		calendar.add(Calendar.WEEK_OF_MONTH, 1);
		Task task3 = this.buildTask(calendar.getTime());
		model.getTaskList().add(task3);
		
		// associate user and task
		task1.addUser(model.getUserList().get(0));
		task1.addUser(model.getUserList().get(1));
		
		task2.addUser(model.getUserList().get(2));
		task2.addUser(model.getUserList().get(3));
			
		task3.addUser(model.getUserList().get(4));
		task3.addUser(model.getUserList().get(1));
		
		//list absence ---------------------------------------
		Date beginDate = calendar.getTime();
		calendar.add(Calendar.DAY_OF_YEAR, 40);
		Date endDate = calendar.getTime();
		Absence absence1 = this.buildAbsence(model.getUserList().get(0), beginDate, endDate);
		
		calendar.add(Calendar.DAY_OF_YEAR, 25);
		beginDate = calendar.getTime();
		calendar.add(Calendar.DAY_OF_YEAR, 15);
		endDate = calendar.getTime();
		Absence absence2 = this.buildAbsence(model.getUserList().get(0), beginDate, endDate);
		Absence absence3 = this.buildAbsence(model.getUserList().get(2), beginDate, endDate);
		
		model.getAbsenceList().add(absence1);
		model.getAbsenceList().add(absence2);
		model.getAbsenceList().add(absence3);
		
		// list alert -----------------------------------------------------------------------------
		model.getAlertList().add(this.buildAlert(model.getUserList().get(0), AlertType.TaskToDo));
		model.getAlertList().add(this.buildAlert(model.getUserList().get(0), AlertType.ChangeDate));
		
		model.getAlertList().add(this.buildAlert(model.getUserList().get(1), AlertType.TaskToDo));
		model.getAlertList().add(this.buildAlert(model.getUserList().get(1), AlertType.DeleteUser));
		
		model.getAlertList().add(this.buildAlert(model.getUserList().get(2), AlertType.TaskFree));
		model.getAlertList().add(this.buildAlert(model.getUserList().get(2), AlertType.ChangeDate));
		model.getAlertList().add(this.buildAlert(model.getUserList().get(2), AlertType.DeleteUser));
		
		// History
		model.getHistoryList().add(new History(task1));
		model.getHistoryList().add(new History(task2));
		
		
		return model;
		
	}
	private Absence buildAbsence(User user, Date beginDate, Date endDate) {
		
		AbsenceBean absenceBean = new AbsenceBean(user.getId(), beginDate, endDate, 0);
		Absence absence = new Absence(absenceBean, user);
		return absence;
		
	}
	private Alert buildAlert(User user, Alert.AlertType type) {
		
		AlertBean alertBean = new AlertBean(user.getId(), type.name(), AlertState.running.name(), null, true, -1);
		Alert alert = new Alert(alertBean, user);
		return alert;
	}
	private User buildUser(String login, String version) {
	
		User user = new User(login);
		if (version.equals(DaoModel.VERSION)) {
			user.setRegistration(new Date());
		}
		return user;
	}
	private Task buildTask(Date date) {
		Task task = new Task(date);
		return task;
	}
	

}
