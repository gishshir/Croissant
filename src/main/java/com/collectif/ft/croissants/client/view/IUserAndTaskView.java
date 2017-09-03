package com.collectif.ft.croissants.client.view;

import com.collectif.ft.croissants.client.widget.user.DraggableUserWidget;
import com.collectif.ft.croissants.client.widget.user.UserWidget;
import com.collectif.ft.croissants.shared.model.bean.UserBean;

public interface IUserAndTaskView {

	
	public DraggableUserWidget createDraggableUserWidget(UserBean userBean) ;
	public UserWidget getUserWidgetById(int userBeanId);
	
	public void cancelAllUserEditing();
}
