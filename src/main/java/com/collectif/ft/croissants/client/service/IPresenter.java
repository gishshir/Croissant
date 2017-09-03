package com.collectif.ft.croissants.client.service;

import com.collectif.ft.croissants.shared.model.bean.IBean;
import com.google.gwt.user.client.ui.HasWidgets;

public interface IPresenter {

	public  void go(HasWidgets container, PresenterCallback callback);
	
	public void refresh(IBean bean);
	
	
	//============================== INNER CLASS
	public interface PresenterCallback {
		
		public void onReady();
		public void onEnd();
	}
}
