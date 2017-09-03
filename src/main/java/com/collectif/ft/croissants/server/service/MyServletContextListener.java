package com.collectif.ft.croissants.server.service;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.collectif.ft.croissants.server.dao.DaoService;
import com.collectif.ft.croissants.server.service.alert.AlertManager;
import com.collectif.ft.croissants.server.service.message.MessageManager;

public class MyServletContextListener implements ServletContextListener {
	
	private static final Log log = LogFactory.getLog(MyServletContextListener.class);


	@Override
	public void contextDestroyed(ServletContextEvent event) {
		
		log.info("contextDestroyed()");
		AlertManager.getInstance().cancelTimer();
		MessageManager.getInstance().cancelTimer();
		CroissantManager.getInstance().cancelTimer();
		ServletContext context = event.getServletContext();
		DaoService.getInstance().save(context.getRealPath(""), ModelInMemory.getInstance().getDaoModel());
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {

		log.info("contextInitialized()");
		ServletContext context = event.getServletContext();
		CroissantManager.getInstance().restoreAndControlDaoModel(context.getRealPath(""));
	}
	


}
