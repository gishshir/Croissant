package com.collectif.ft.croissants.client;

import com.collectif.ft.croissants.client.service.AppController;
import com.collectif.ft.croissants.client.service.ICroissantService;
import com.collectif.ft.croissants.client.service.ICroissantServiceAsync;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CroissantEntryPoint implements EntryPoint {
	

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		
		final  ICroissantServiceAsync service = GWT.create(ICroissantService.class);
		final HandlerManager eventBus = new HandlerManager(null);
		AppController appViewer = new AppController(service, eventBus);
		appViewer.go(RootPanel.get("divView"), null);
		}
}
