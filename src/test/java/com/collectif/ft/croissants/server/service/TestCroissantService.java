package com.collectif.ft.croissants.server.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.collectif.ft.croissants.server.util.DateUtils;
import com.collectif.ft.croissants.shared.model.bean.AbsenceBean;
import com.collectif.ft.croissants.shared.model.bean.IBean;
import com.collectif.ft.croissants.shared.model.bean.TaskBean;
import com.collectif.ft.croissants.shared.model.bean.UserBean;
import com.collectif.ft.croissants.shared.model.dto.TaskAndUserDto;
import com.collectif.ft.croissants.shared.model.dto.UserAndAbsencesDto;
import com.collectif.ft.croissants.shared.model.dto.UserAndAlertDto;

public class TestCroissantService extends AbstractTests {
	
	private static final Log log = LogFactory.getLog(TestCroissantService.class); 
	
	private final Calendar calendar = Calendar.getInstance();
	private final static int offset = DateUtils.getServerOffset();
	
	

	//===========================================================
	//------------------------------- UserBean ------------------
	//===========================================================
	@Before
	public void init() throws Exception {
		
		CroissantServiceImpl service = Mockito.spy(new CroissantServiceImpl());
		super.initService(service);
		
		ServletConfig config = Mockito.mock(ServletConfig.class);
		Mockito.when(config.getInitParameter(Mockito.eq(CroissantServiceImpl.PARAM_ACTIVE_SEND_MAIL))).thenReturn("true");
		Mockito.when(config.getInitParameter(Mockito.eq(CroissantServiceImpl.PARAM_SMTP_SERVER))).thenReturn("localhost");
		Mockito.when(config.getInitParameter(Mockito.eq(CroissantServiceImpl.PARAM_SMTP_LOGIN))).thenReturn("xxx");
		Mockito.when(config.getInitParameter(Mockito.eq(CroissantServiceImpl.PARAM_SMTP_PWD))).thenReturn("xxx");
		Mockito.when(config.getInitParameter(Mockito.eq(CroissantServiceImpl.PARAM_SMTP_FROM))).thenReturn("xxx");
		Mockito.when(config.getInitParameter(Mockito.eq(CroissantServiceImpl.PARAM_PERIOD_TIMER_ALERT_TASKTODO))).thenReturn("2");
		Mockito.when(config.getInitParameter(Mockito.eq(CroissantServiceImpl.PARAM_INTERVAL_BETWEEN_TASK))).thenReturn("7");
			
		ServletContext mockServletContext = Mockito.mock(ServletContext.class);
		Mockito.when(mockServletContext.getRealPath(Mockito.anyString())).thenReturn("/doc/");
		
		Mockito.doReturn(mockServletContext).when(service).getServletContext();
		
		service.init(config);
		
		this.clearDaoModel(service);
	}
	

	private void clearDaoModel(CroissantServiceImpl service) throws Exception {
		
		service.deleteAllTasks();
		service.deleteAllUsers();
	}
	
	@Test
	public void testLoadListOfFreeUserBean()  throws Exception {
		
		log.info("=== testLoadListOfFreeUserBean ===");
		
		List<UserBean> result = service.loadListOfFreeUser();
		this.logAndAssertListUserBeans(result);
	}
	

	@Test
	public void testLoadNewUser()  throws Exception {
		
		log.info("=== testLoadNewUser ===");
		
		UserBean result = service.loadNewUser();
		assertNotNull("result not null", result);
		
		assertTrue("id != undefined", result.getId() != IBean.ID_UNDEFINED);
	}
	
	@Test
	public void testUpdateUser() throws Exception {
		
		log.info("=== testUpdateUser ===");
		
		UserBean userBean =this.createUser("toto");
		assertEquals("login is toto", userBean.getLogin(), "toto");
		
		List<UserBean> result = service.loadListOfFreeUser();
		this.logAndAssertListUserBeans(result);
			
	}
	
	@Test
	public void testDeleteUser()  throws Exception {
		
		log.info("=== testUpdateUser ===");
		
		UserBean userBean =this.createUser("titi");
		assertEquals("login is titi", userBean.getLogin(), "titi");
		assertTrue("userId is defined", userBean.getId() != IBean.ID_UNDEFINED);
		
		service.deleteUser(userBean.getId());
			
	}
	
	// supprimer un utilisateur qui n'existe pas
	@Test(expected=Exception.class)
	public void testDeleteUnknownUser()  throws Exception {
		
		log.info("=== testDeleteUnknownUser ===");
		
		service.deleteUser(1000);
	}
	

	//===========================================================
	//---------------------------- Tasks ------------------------
	//===========================================================
	
	@Test
	public void testLoadListOfTaskAndUserDto()  throws Exception {
		
		log.info("=== testLoadListOfTaskAndUserDto ===");
		
		List<TaskAndUserDto> result = service.loadListOfTaskAndUserDto(-1);
		this.logAndAssertListTaskAndUserDto(result);
		
	}
	
	@Test
	public void testLoadNewTask()  throws Exception {
		
		log.info("=== testLoadNewTask ===");
		
		TaskBean result = service.loadNewTask(offset);
		assertNotNull("result not null", result);
		
		assertTrue("id != undefined", result.getId() != IBean.ID_UNDEFINED);
		
		Date createdDate = result.getDate();
		assertNotNull("task date cannot be null!!", createdDate);
		
		 DateUtils.logDate("*** created date ****", createdDate);
		 log.info("created date: " + createdDate.toString());
		 
		 calendar.setTime(createdDate);
		 
		 assertTrue("hour must be 23", calendar.get(Calendar.HOUR_OF_DAY) == 23);
		 assertTrue("minute must be 50", calendar.get(Calendar.MINUTE) == 00);
		 assertTrue("seconde must be 00", calendar.get(Calendar.SECOND) == 0);
	}
	
	@Test
	public void testUpdateTask() throws Exception {

        log.info("=== testUpdateTask ===");
		
        Date date = calendar.getTime();
        
        DateUtils.logDate("*** initial date ****", date);
		TaskBean taskBean = this.createTask(date);
		
		Date createdDate = taskBean.getDate();
		assertNotNull("task date cannot be null!!", createdDate);

	    DateUtils.logDate("*** created date ****", createdDate);
	    
	    calendar.set(Calendar.YEAR, 2015);
		calendar.set(Calendar.DAY_OF_MONTH, 7);
		calendar.set(Calendar.HOUR_OF_DAY, 12);
		calendar.set(Calendar.MINUTE, 23);
		calendar.set(Calendar.SECOND, 31);
		taskBean.setDate(calendar.getTime(), offset);
		service.updateTask(taskBean);
		
		List<TaskAndUserDto> result = service.loadListOfTaskAndUserDto(-1);
		this.logAndAssertListTaskAndUserDto(result);
		
		assertTrue("nb task must be 1", result.size() == 1);
		TaskAndUserDto taskAndUserDto = result.get(0);
		
		Date updatedDate = taskAndUserDto.getTaskBean().getDate();
		assertNotNull("task date cannot be null!!", updatedDate);
		
		 DateUtils.logDate("*** updated date ****", updatedDate);
		 calendar.setTime(updatedDate);
		 
		 assertTrue("day must be 7", calendar.get(Calendar.DAY_OF_MONTH) == 7);
		 assertTrue("hour must be 23", calendar.get(Calendar.HOUR_OF_DAY) == 23);
		 assertTrue("minute must be 50", calendar.get(Calendar.MINUTE) == 00);
		 assertTrue("seconde must be 50", calendar.get(Calendar.SECOND) == 00);
	}
	
	@Test
	public void testDeleteTask()  throws Exception {
		
		 log.info("=== testDeleteTask ===");
			
		 TaskBean taskBean = this.createTask(calendar.getTime());
		 assertNotNull("task not null", taskBean);
		 
		 service.deleteTask(taskBean.getId());
		 
		 taskBean = this.createTask(calendar.getTime());
		 assertNotNull("task not null", taskBean);
		 calendar.set(Calendar.YEAR, -1);
		 taskBean.setDate(calendar.getTime(), 0);
		 assertNotNull("task not null", taskBean);
		 
		 service.deleteTask(taskBean.getId());
	}
	

	
	//===========================================================
	//-----------------------User and Alerts ---------------------
	//===========================================================
	@Test
	public void testLoadUserAndAlertDto()  throws Exception {
		
		log.info("=== testLoadUserAndAlertDto ===");
		
		UserBean userBean1 = this.createUser("tata");
		assertNotNull("userBean1 not null", userBean1);	
		
		UserAndAlertDto userAndAlertDto = service.loadUserAndAlert(userBean1.getId());
		assertNotNull("userAndAlertDto not null", userAndAlertDto);
		
        UserBean userBean2 = userAndAlertDto.getUserBean();
        assertNotNull("userBean2 not null", userBean2);	
        assertEquals("ids are equals", userBean1.getId(), userBean2.getId());
        assertEquals("logins are equals", userBean1.getLogin(), userBean2.getLogin());
        
        
        
	}
	@Test
	public void testUpdateUserAndAlerts()  throws Exception {
		
		log.info("=== testUpdateUserAndAlerts ===");
		
		UserBean userBean1 = this.createUser("tata");
		assertNotNull("userBean1 not null", userBean1);	
		
		UserAndAlertDto userAndAlertDto = service.loadUserAndAlert(userBean1.getId());
		assertNotNull("userAndAlertDto not null", userAndAlertDto);      

        // update login and email
        userAndAlertDto.getUserBean().setLogin("koko");
        userAndAlertDto.getUserBean().setEmail("koko@laposte.net");
        userAndAlertDto.setChangeDateAlert(true);
        userAndAlertDto.setDeleteUserAlert(true);
        userAndAlertDto.setTaskToDoAlert(false);
        
        service.updateUserAndAlerts(userAndAlertDto);
        
        //verification
		UserAndAlertDto result = service.loadUserAndAlert(userBean1.getId());
		assertNotNull("result not null", result); 
		UserBean userBean2 = userAndAlertDto.getUserBean();
	    assertNotNull("userBean2 not null", userBean2);	
	    assertEquals("ids are equals", userBean1.getId(), userBean2.getId());
	    assertEquals("logins are equals", "koko", userBean2.getLogin());
	    assertEquals("emails are equals", "koko@laposte.net", userBean2.getEmail());
	    assertTrue("changeDateAlert true", result.isChangeDateAlert());
	    assertTrue("deleteUserAlert true", result.isDeleteUserAlert());
	    assertFalse("taskToDoAlert", result.isTaskToDoAlert());
	    
	    log.info("User id: " + userBean2.getId() + " - login: " + userBean2.getLogin() + " - email: " + userBean2.getEmail());
		
	}
	
	//===========================================================
	//-----------------------User and Absence--------------------
	//===========================================================
	@Test
	public void testLoadNewAbsence() throws Exception {
		
		log.info("=== testLoadNewAbsence ===");
		UserBean user1 = this.createUser("titi");
		AbsenceBean absenceBean = service.loadNewAbsence(user1.getId(), offset);
	    this.logAndAssertAbsence(absenceBean, user1);
	}
	
	@Test
	public void testLoadListAbsenceForUser() throws Exception {
		
		log.info("=== testLoadListAbsenceForUser ===");
		UserBean user1 = this.createUser("titi");
		
		service.loadNewAbsence(user1.getId(), offset);
		this.sleep(100);
		service.loadNewAbsence(user1.getId(), offset);
		this.sleep(100);
		service.loadNewAbsence(user1.getId(), offset);
		
		UserAndAbsencesDto result = service.loadListAbsenceForUser(user1.getId());
		assertNotNull("result cannot be null", result);
		this.logAndAssertListAbsence(result.getListAbsences(), user1, 3);
		
	}
	@Test
    public void testUpdateAbsence() throws Exception {
    	
    	log.info("=== testUpdateAbsence ===");
		UserBean user1 = this.createUser("toto");
		
		service.loadNewAbsence(user1.getId(), offset);
		this.sleep(100);
		service.loadNewAbsence(user1.getId(), offset);
		this.sleep(100);
		service.loadNewAbsence(user1.getId(), offset);
		
		UserAndAbsencesDto userAndAbsenceDto = service.loadListAbsenceForUser(user1.getId());
		assertNotNull("result cannot be null", userAndAbsenceDto);
		
		AbsenceBean absenceBean = userAndAbsenceDto.getListAbsences().get(1);
	     this.logAndAssertAbsence(absenceBean, user1);
		 
		 
		calendar.setTime(DateUtils.getNewUTCDate());
		calendar.add(Calendar.MONTH, 3);
		absenceBean.updateBeginDate(calendar.getTime(), offset);
		calendar.add(Calendar.DAY_OF_YEAR, 10);
		absenceBean.updateEndDate(calendar.getTime(), offset);
		
		AbsenceBean result = service.updateAbsence(absenceBean);
		 this.logAndAssertAbsence(result, user1);
    }
	@Test
	public void testDeleteAbsence() throws Exception {
		
		log.info("=== testDeleteAbsence ===");
		UserBean user1 = this.createUser("tata");
		
		service.loadNewAbsence(user1.getId(), offset);
		this.sleep(100);
		service.loadNewAbsence(user1.getId(), offset);
		this.sleep(100);
		service.loadNewAbsence(user1.getId(), offset);
		
		UserAndAbsencesDto userAndAbsenceDto = service.loadListAbsenceForUser(user1.getId());
		assertNotNull("userAndAbsenceDto cannot be null!", userAndAbsenceDto);
		List<AbsenceBean> list = userAndAbsenceDto.getListAbsences();
		assertNotNull("list not null", list);
		assertTrue("list.size == 3", list.size() == 3);
		this.logAndAssertListAbsence(list, user1, 3);
		
		AbsenceBean absenceBean = list.get(1);
		log.info("to delete:");
		this.logAndAssertAbsence(absenceBean, user1);
		long tsToDelete = absenceBean.getInitialBeginDateTimeStamp();
		service.deleteAbsence(absenceBean);
		
		userAndAbsenceDto = service.loadListAbsenceForUser(user1.getId());
		assertNotNull("userAndAbsenceDto cannot be null!", userAndAbsenceDto);
		list = userAndAbsenceDto.getListAbsences();
		this.logAndAssertListAbsence(list, user1, 2);
		
		// on s'assure que l'Absence a bien disparu
		for (AbsenceBean absenceBean2 : list) {
			assertTrue("ts", tsToDelete != absenceBean2.getInitialBeginDateTimeStamp());
		}
		
	}
	
	//===========================================================
	//-----------------------User and Tasks ---------------------
	//===========================================================
	
	@Test
	public void testMoveUserToTask ()  throws Exception {
		
		log.info("=== testMoveUserToTask ===");
		
		TaskBean task1 = this.createTask(calendar.getTime());
		UserBean user1 = this.createUser("titi");
		
		service.moveUserToTask(user1.getId(), task1.getId());
		
		List<TaskAndUserDto> list1 = service.loadListOfTaskAndUserDto(-1);
		this.logAndAssertListTaskAndUserDto(list1);
		
		calendar.add(Calendar.MONTH, 1);
		TaskBean task2 = this.createTask(calendar.getTime());
		service.moveUserToTask(user1.getId(), task2.getId());
		
		List<TaskAndUserDto> list2 = service.loadListOfTaskAndUserDto(-1);
		this.logAndAssertListTaskAndUserDto(list2);
	}
	@Test
	public void testDeleteTaskWithUsers()  throws Exception {
		
		log.info("=== testDeleteTaskWithUsers ===");
		
		TaskBean task1 = this.createTask(calendar.getTime());
		UserBean user1 = this.createUser("titi");
		
		service.moveUserToTask(user1.getId(), task1.getId());
		
		service.deleteTask(task1.getId());
		
		 task1 = this.createTask(calendar.getTime());
		 calendar.set(Calendar.YEAR, -1);
		 task1.setDate(calendar.getTime(), 0);
		 service.moveUserToTask(user1.getId(), task1.getId());
		 assertNotNull("task not null", task1);
		 
		 service.deleteTask(task1.getId());
		
	}
	
	@Test
	public void testShiftDownListTasks() throws Exception {
		
		log.info("=== testShiftDownListTasks ===");
		
		calendar.setTime(new Date());
	
		// on construit une liste de 5 taches avec users sauf tache 4 vide
		
		// 1ère task
		calendar.add(Calendar.DAY_OF_MONTH, 7);
		this.createTaskAndTwoUsers(calendar.getTime(), "titi1", "titi2");
		
		// 2ème task : begin shifting
		calendar.add(Calendar.DAY_OF_MONTH, 7);
		TaskBean beginTask = this.createTaskAndTwoUsers(calendar.getTime(), "toto1", "toto2");
		
		// 3ème task
		calendar.add(Calendar.DAY_OF_MONTH, 7);
		this.createTaskAndTwoUsers(calendar.getTime(), "tata1", "tatat2");
		
		// 4ème task empty
		calendar.add(Calendar.DAY_OF_MONTH, 7);
		this.createTask(calendar.getTime());
		
		// 5ème task
		calendar.add(Calendar.DAY_OF_MONTH, 7);
		this.createTaskAndTwoUsers(calendar.getTime(), "tutu1", "tutu2");
		
		List<TaskAndUserDto> list1 =  service.loadListOfTaskAndUserDto(-1);
		assertNotNull("list not null", list1);
		
		log.info("AVANT SHIFT DOWN");
		this.logAndAssertListTaskAndUserDto(list1);
		
		// test shift down à partir de la deuxième
		service.shiftListUsers(beginTask.getId(), false);
		
		List<TaskAndUserDto> list2 =  service.loadListOfTaskAndUserDto(-1);
		assertNotNull("list not null", list1);
		
		log.info("APRES SHIFT DOWN");
		this.logAndAssertListTaskAndUserDto(list2);
		
		// seule la deuxième tache doit être vide
		for (int i = 0; i < list2.size(); i++) {
			TaskAndUserDto taskAndUserDto = list2.get(i);
			
			if (i == 1) {
				assertTrue("task 2 doit etre vide!", taskAndUserDto.getTaskBean().isEmpty());
			} else {
				assertFalse("task != 2 ne doit pas etre vide!", taskAndUserDto.getTaskBean().isEmpty());
			}
			
		}
		
	}
	
	@Test
	public void testShiftUpListTasks() throws Exception {
		
		log.info("=== testShiftUpListTasks ===");
		
		calendar.setTime(new Date());
	
		// on construit une liste de 5 taches avec users sauf tache 2 vide
		calendar.add(Calendar.DAY_OF_MONTH, 7);
		this.createTaskAndTwoUsers(calendar.getTime(), "titi1", "titi2");
		
		// 2ème task empty
		calendar.add(Calendar.DAY_OF_MONTH, 7);
		this.createTask(calendar.getTime());
		
		// 3ème task
		calendar.add(Calendar.DAY_OF_MONTH, 7);
		this.createTaskAndTwoUsers(calendar.getTime(), "toto1", "toto2");
		
		// 4ème task
		calendar.add(Calendar.DAY_OF_MONTH, 7);
		TaskBean beginTask = this.createTaskAndTwoUsers(calendar.getTime(), "tata1", "tatat2");
		
		// 5ème task
		calendar.add(Calendar.DAY_OF_MONTH, 7);
		this.createTaskAndTwoUsers(calendar.getTime(), "tutu1", "tutu2");
		
		List<TaskAndUserDto> list1 =  service.loadListOfTaskAndUserDto(-1);
		assertNotNull("list not null", list1);
		
		log.info("");
		log.info("AVANT SHIFT UP");
		this.logAndAssertListTaskAndUserDto(list1);
		
		// test shift down à partir de la quatrième
		service.shiftListUsers(beginTask.getId(), true);
		
		List<TaskAndUserDto> list2 =  service.loadListOfTaskAndUserDto(-1);
		assertNotNull("list not null", list1);
		
		log.info("");
		log.info("APRES SHIFT UP");
		this.logAndAssertListTaskAndUserDto(list2);
		
		// seule la quatrième tache doit être vide
		for (int i = 0; i < list2.size(); i++) {
			TaskAndUserDto taskAndUserDto = list2.get(i);
			
			if (i == 3) {
				assertTrue("task 2 doit etre vide!", taskAndUserDto.getTaskBean().isEmpty());
			} else {
				assertFalse("task != 2 ne doit pas etre vide!", taskAndUserDto.getTaskBean().isEmpty());
			}
			
		}
		
	}
	//----------------------------------------- private methods
	private void sleep (long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void logAndAssertListAbsence (List<AbsenceBean> list, UserBean user, int count) {
		
		assertNotNull("list not null", list);
		assertTrue("list.size", list.size() == count);
		
		for (AbsenceBean absenceBean : list) {
		   this.logAndAssertAbsence(absenceBean, user);	
		}
	}
	private void logAndAssertAbsence(AbsenceBean absenceBean, UserBean user) {
		
		assertNotNull("absence", absenceBean);
		assertTrue("absence.userId", absenceBean.getUserId() == user.getId());
		
		assertNotNull("absence.beginDate", absenceBean.getBeginDate());
		assertNotNull("absence.endDate", absenceBean.getEndDate());
		assertEquals("absence.initialBeginDate", absenceBean.getBeginDate().getTime(), absenceBean.getInitialBeginDateTimeStamp());
	
		log.info("Absence - userId: " + user.getId() + " - ts: " + absenceBean.getInitialBeginDateTimeStamp());
	}
	private TaskBean createTaskAndTwoUsers (Date date, String login1, String login2) throws Exception {
		
		TaskBean task = this.createTask(date);
		UserBean user1 = this.createUser(login1);
		UserBean user2 = this.createUser(login2);
		
		service.moveUserToTask(user1.getId(), task.getId());
		service.moveUserToTask(user2.getId(), task.getId());
		
		return task;
	}

	private void logAndAssertListUserBeans(List<UserBean> list) {
		
		assertNotNull("result not null", list);
		
		for (UserBean userBean : list) {
			log.info("User id: " + userBean.getId() + " - login: " + userBean.getLogin());
		}
	}
	
	private void logAndAssertListTaskAndUserDto(List<TaskAndUserDto> list) {
		
		assertNotNull("result not null", list);
		
		for (TaskAndUserDto taskAndUserDto : list) {
			
			log.info("");
			TaskBean taskBean = taskAndUserDto.getTaskBean();
			assertNotNull("taskBean not null", taskBean);
			log.info("Task id: " + taskBean.getId() + " - date: " + taskBean.getDate());
			
			List<UserBean> listUsers = taskAndUserDto.getListUserBeans();

			if (listUsers != null) {
			for (UserBean userBean : listUsers) {
				log.info(" >> user id: " + userBean.getId() + " - login: " + userBean.getLogin());
			}
			}
		}
	}
	
	
	
		
}
