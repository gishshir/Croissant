package com.collectif.ft.croissants.server.service.message;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import com.collectif.ft.croissants.server.business.Alert;
import com.collectif.ft.croissants.server.business.Alert.AlertType;
import com.collectif.ft.croissants.server.business.Email;
import com.collectif.ft.croissants.server.business.User;
import com.collectif.ft.croissants.server.service.message.MessageManager.SmtpParams;
import com.collectif.ft.croissants.shared.model.bean.AlertBean;

public class TestMessageManager {

	//private static final Log log = LogFactory.getLog(TestMessageManager.class); 
	private static final MessageManager messageManager = MessageManager.getInstance();
	
	
	private static final Calendar calendar = Calendar.getInstance();
	
	@BeforeClass
	public static void beforeTest() throws Exception {
				
		SmtpParams smtpParams = new SmtpParams("localhost", "xxxx", "xxxx", "croissantmaster@gmail.com", true);
		messageManager.init(smtpParams, "./war/croissant");
		//messageManager.setModuleRootUrl(new URL("http", "gishshir.s156.eatj.com", 80, "/Croissants/croissant"));
	}
	
	@Test
	public void testCreateMessageChangeDate() {
		
		User user = this.createUser("toto", "xxxxt@gmail.fr");
		Alert alert = this.createAlertChangeDate(user);
		calendar.add(Calendar.DAY_OF_MONTH, -3);
		Date oldDate = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, 6);
		Date newDate = calendar.getTime();
		messageManager.createMessageChangeDate(alert, oldDate, newDate);

	    this.pause(5);
	}
	@Test
	public void testCreateMessageChangeNoDate() {
		
		User user = this.createUser("toto", "xxxxt@gmail.fr");
		Alert alert = this.createAlertChangeDate(user);
		calendar.add(Calendar.DAY_OF_MONTH, -3);
		Date oldDate = calendar.getTime();

		Date newDate = null;
		messageManager.createMessageChangeDate(alert, oldDate, newDate);

	    this.pause(5);
	}
	@Test
	public void testCreateMessageDeleteUser() {
		
		User user = this.createUser("toto", "xxxxt@gmail.fr");
		Alert alert = this.createAlertDeleteUser(user);
		messageManager.createMessageDeleteUser(alert);

	    this.pause(5);
	}
	@Test
	public void testCreateMessageTaskToDo() {
		
		User user = this.createUser("toto", "xxxxt@gmail.fr");
		Alert alert = this.createAlertTaskToDo(user);
		messageManager.createMessageTaskToDo(alert, new Date(), 1);
		messageManager.createMessageTaskToDo(alert,  new Date(), 1);

	    this.pause(5);
	}
	
	@Test
	public void testCreateMessageTaskIncomplete() {
		
		User user1 = this.createUser("toto", "xxxxt@gmail.fr");
		Alert alert1 = this.createAlertTaskIncomplete(user1);
		messageManager.createMessageIncompleteTask(alert1, new Date(), true);
	    this.pause(5);
	    
		User user2 = this.createUser("tutu", "xxxxt@gmail.fr");
		Alert alert2 = this.createAlertTaskIncomplete(user2);
		messageManager.createMessageIncompleteTask(alert2, new Date(), false);

	    this.pause(5);
	}
	
	@Test
	public void testCreateMultipleMessage() {
		
		
		User user1 = this.createUser("toto", "xxxxt@gmail.fr");
		User user2 = this.createUser("titi", "yyyy@gmail.fr");
		
		Alert alert1 = this.createAlertChangeDate(user1);
		calendar.add(Calendar.DAY_OF_MONTH, -3);
		Date oldDate = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, 6);
		Date newDate = calendar.getTime();
		messageManager.createMessageChangeDate(alert1, oldDate, newDate);
		
		Alert alert2 = this.createAlertTaskToDo(user1);
		messageManager.createMessageTaskToDo(alert2,  new Date(), 1);
		alert2 = this.createAlertTaskToDo(user2);
		messageManager.createMessageTaskToDo(alert2,  new Date(), 2);
		
		Alert alert3 = this.createAlertDeleteUser(user2);
		messageManager.createMessageDeleteUser(alert3);
		
		 this.pause(15);
	}
	
	
	//------------------------------ private Method -------
	private void pause(int tempoSeconds) {
		try {
			Thread.sleep(tempoSeconds * 1000);
	} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	}
	}
	
	private Alert createAlertTaskToDo(User user) {
		
		AlertBean alertBean = new AlertBean(user.getId(), AlertType.TaskToDo.name(), "1", true);
		Alert alert = new Alert(alertBean, user);
		return alert;
	}
	
	private Alert createAlertDeleteUser(User user) {
		AlertBean alertBean = new AlertBean(user.getId(), AlertType.DeleteUser.name(), "1", true);
		Alert alert = new Alert(alertBean, user);
		return alert;
	}
	
	private Alert createAlertChangeDate(User user) {
		AlertBean alertBean = new AlertBean(user.getId(), AlertType.ChangeDate.name(), "1", true);
		Alert alert = new Alert(alertBean, user);
		return alert;
	}
	private Alert createAlertTaskIncomplete(User user) {
		AlertBean alertBean = new AlertBean(user.getId(), AlertType.TaskFree.name(), "1", true);
		Alert alert = new Alert(alertBean, user);
		return alert;
	}
	private User createUser(String login, String email) {
		
		User user = new User(1);
		user.setLogin(login);
		user.setEmail(new Email(email));
		return user;
	}
}

