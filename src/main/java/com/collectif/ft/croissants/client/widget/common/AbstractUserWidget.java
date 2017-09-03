package com.collectif.ft.croissants.client.widget.common;

import com.collectif.ft.croissants.client.text.MyWording;
import com.collectif.ft.croissants.client.util.IConstants;
import com.collectif.ft.croissants.shared.model.bean.UserBean;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractUserWidget extends Composite {

	
	private static final String baseImageUrl = GWT.getModuleBaseURL() + "/smileys/";
	private static final String defaultImageUrl = baseImageUrl + "userDefault.png";
	
	protected final static MyWording myWording = GWT.create(MyWording.class);

	protected final Image _image = new Image(defaultImageUrl);
	protected final Label _labelLogin = new Label("login");

	protected final boolean _showPictos;
	protected final boolean _editable;
	
	protected UserBean _userBean;
	

	//----------------------------------------------------- constructor
	public AbstractUserWidget(boolean showPictos, String userColorStyle) {
		this(showPictos, false, userColorStyle);
	}
	public AbstractUserWidget(boolean showPictos, boolean editable, String userColorStyle) {
		
		this._showPictos = showPictos;
		this._editable = editable;
		this.initComposants();
		this.initHandlers();
		this.initWidget(this.buildMainPanel(userColorStyle));
	}

	//--------------------------------------------------- public methods
	public UserBean getUserBean() {
		return this._userBean;
	}
	/**
	 * Populate widget with bean
	 * 	
	 */
	public void setDatas(UserBean userBean) {
		this._userBean = userBean;
		if (userBean.getLogin() != null) {
			this._labelLogin.setText(userBean.getLogin());
		}
		if (userBean.getLogo() != null && userBean.getLogo().length() > 0) {
			this._image.setUrl(baseImageUrl + userBean.getLogo());
		}
	}
	




	//-------------------------------------- private methods
	private Panel buildMainPanel(String userColorStyle) {
		
		final SimplePanel main = new SimplePanel();
		main.addStyleName(IConstants.STYLE_USER_GLOBAL_WIDGET);
		
		final HorizontalPanel hPanel = new HorizontalPanel();
		
		// icone
		final Widget iconPanel = this.getVerticalIconPanel();
		if (iconPanel != null) {
			hPanel.add(iconPanel);
		}
		
		Panel mainUserPanel = new SimplePanel();
		mainUserPanel.addStyleName(IConstants.STYLE_USER_WIDGET);
		if (userColorStyle != null) {
			mainUserPanel.addStyleName(userColorStyle);
		}
		mainUserPanel.add(this.buildInfoPanel());
		
		hPanel.add(mainUserPanel);
		
		// buttons
		final Widget buttonPanel = this.getVerticalButtonPanel();
        if (buttonPanel != null) {
		    hPanel.add(buttonPanel);
        }
		
		main.add(hPanel);
		return main;
	}
	
	protected Panel buildInfoPanel () {
		
		Panel infoPanel = new FlowPanel();
		infoPanel.addStyleName(IConstants.STYLE_USER_INFO_PANEL);
		
		infoPanel.add(this._image);
		Widget additionalWidget = this.getAdditionalInfoPanel();
		if (additionalWidget != null) {
		infoPanel.add(additionalWidget);
		}
		
		return infoPanel;
	}
	
	protected  void initComposants() {
		this._labelLogin.setVisible(true);
	}
	protected abstract Widget getAdditionalInfoPanel();
	protected abstract Widget getVerticalButtonPanel();
	protected abstract Widget getVerticalIconPanel();

	protected abstract void initHandlers();

}
