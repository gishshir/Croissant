package com.collectif.ft.croissants.server.service;

import static org.junit.Assert.assertNotNull;

import java.util.Date;

import com.collectif.ft.croissants.shared.model.bean.TaskBean;
import com.collectif.ft.croissants.shared.model.bean.UserBean;

public abstract class AbstractTests {
	

	protected CroissantServiceImpl service;
	protected void initService ( CroissantServiceImpl service) {
		this.service = service;
	}
		
	protected TaskBean createTask (Date date) throws Exception {
		
		TaskBean taskBean = service.loadNewTask(0);
		assertNotNull("result not null", taskBean);	
			
		taskBean.setDate(date, 0);
		return service.updateTask(taskBean);
			
		
	}
	
	protected UserBean createUser (String login)  throws Exception {
		
		UserBean userBean = service.loadNewUser();
		assertNotNull("result not null", userBean);
		
		userBean.setLogin(login);
		return service.updateUser(userBean);
	}
}
