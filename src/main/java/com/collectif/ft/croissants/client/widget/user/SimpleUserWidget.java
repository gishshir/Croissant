package com.collectif.ft.croissants.client.widget.user;

import com.collectif.ft.croissants.client.widget.common.AbstractUserWidget;
import com.collectif.ft.croissants.shared.model.bean.UserBean;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SimpleUserWidget extends AbstractUserWidget {


	//----------------------------------------- constructor
	public SimpleUserWidget (String userColorStyle) {
		super(false, false, userColorStyle);
	}
	
	//------------------------------------------ public methods
	public void setDatas(UserBean userBean) {
		super.setDatas(userBean);
	}

	//--------------------------------- overriding  SimpleUserWidget
	@Override
	protected Widget getAdditionalInfoPanel() {
		return this._labelLogin;
	}

	@Override
	protected Widget getVerticalButtonPanel() {
       return null; 
	}
	@Override
	protected Widget getVerticalIconPanel() {
		
		if (this._showPictos) {
			
			if (this._editable) {
				
				VerticalPanel buttonPanel = new VerticalPanel();
				buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				buttonPanel.add(this._image);
				return buttonPanel;
				
			} else {
				return new SimplePanel(this._image);
			}
		}
		return null;
		//return (this._showPictos)? new SimplePanel(this._image):null;
	}

	@Override
	protected void initComposants() {
		super.initComposants();
		
	}

	@Override
	protected void initHandlers() {
		
		
	}
	
	//---------------------------------------------- private methods

	

}
