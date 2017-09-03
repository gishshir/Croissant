package com.collectif.ft.croissants.client.view;



import java.util.List;

import com.collectif.ft.croissants.client.service.EditUserPresenter.DisplayUser;
import com.collectif.ft.croissants.client.util.DateUtils;
import com.collectif.ft.croissants.client.util.IConstants;
import com.collectif.ft.croissants.client.util.WidgetUtils;
import com.collectif.ft.croissants.client.widget.common.InputWidget;
import com.collectif.ft.croissants.client.widget.user.GallerySmileyWidget;
import com.collectif.ft.croissants.shared.model.bean.UserBean;
import com.collectif.ft.croissants.shared.model.dto.UserAndAlertDto;
import com.collectif.ft.croissants.shared.util.FieldValidator;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class EditUserView extends Composite implements DisplayUser {
	
	private final static String LABEL_WIDTH = "80px";

	private final HorizontalPanel _main = new HorizontalPanel();
	
	private Button _btCancel = new Button("cancel");
	private Button _btValidate = new Button("Validate");
	private Button _btDeleteUser = new Button("Delete User");
	
    private GallerySmileyWidget _galleryWidget = new GallerySmileyWidget();
    private DisclosurePanel _panelAlerts = new DisclosurePanel("Alerts");
    
    private CheckBox _cbChangeDateAlert = new CheckBox("change of date");
    private CheckBox _cbDeleteUserAlert = new CheckBox("user deleted");
    private CheckBox _cbTaskToDoAlert = new CheckBox("task to do in ");
    private CheckBox _cbTaskFreeAlert = new CheckBox("task free");
    
    private UserAndAlertDto _userAlertsBean;
    private final DateBox _dateRegistration = new DateBox();
    
    
    private InputWidget _inputLogin = new InputWidget("login: ", "must have 3 characters at least!",LABEL_WIDTH, false) {
		
		@Override
		protected boolean verifyInput(TextBox textBox) {
			boolean result = FieldValidator.controlMinLenght(textBox.getText(), 3);
			_btValidate.setEnabled(result);
			return result;
		}
	};
	
	private InputWidget _inputEmail = new InputWidget("email: ", "wrong format of email", LABEL_WIDTH, false) {
		
		@Override
		protected boolean verifyInput(TextBox textBox) {
			boolean result = FieldValidator.controlEmail(textBox.getText());
			_btValidate.setEnabled(result);
			return result;
		}
	};
	
	private InputWidget _inputDelaiDays = new InputWidget(" days", "is not a numeric!", LABEL_WIDTH, true) {
		
		@Override
		protected boolean verifyInput(TextBox textBox) {
			boolean result = FieldValidator.controlNumeric(textBox.getText());
			_btValidate.setEnabled(result);
			return result;
		}
	};
	

	
	//------------------------------------------- constructor
	public EditUserView() {
		this.initComposants();
		this.initWidget(this.buildMainPanel());
	}
	
	//-------------------------------------------- overriding DisplayUser
	@Override
	public Widget getLoginWidget() {
		return this._inputLogin;
	}

	
	@Override
	public HasClickHandlers getCancelButton() {
		return this._btCancel;
	}

	@Override
	public HasClickHandlers getValidateButton() {
		return this._btValidate;
	}
	@Override
	public HasClickHandlers getDeleteUserButton() {
		return this._btDeleteUser;
	}
	
	@Override
	public UserAndAlertDto getUser() {
	
		this.populateBeanFromWidget(this._userAlertsBean);
		return this._userAlertsBean;
	}


	@Override
	public void setUser(UserAndAlertDto userAndAlertsBean) {

       if (userAndAlertsBean == null ) {
    	   return;
       }
       this._userAlertsBean = userAndAlertsBean;
       
       final UserBean userBean = userAndAlertsBean.getUserBean();
       
       this._inputLogin.setValue(userBean.getLogin());
       if (userBean.getEmail() != null) {
         this._inputEmail.setValue(userBean.getEmail());
       }
       
       //registration date
       this._dateRegistration.setValue(userBean.getRegistration());
       
       // alertes
       this._cbChangeDateAlert.setValue(userAndAlertsBean.isChangeDateAlert());
       this._cbDeleteUserAlert.setValue(userAndAlertsBean.isDeleteUserAlert());
       this._cbTaskToDoAlert.setValue(userAndAlertsBean.isTaskToDoAlert());
       this._cbTaskFreeAlert.setValue(userAndAlertsBean.isTaskFreeAlert());
       this._inputDelaiDays.setValue(Integer.toString(userAndAlertsBean.getDelai()));
       
       this.selectLogo();
       this.displayPanelAlerts();
		
	}
	
	@Override
	public void setGallery(List<String> listUrls) {
		this._galleryWidget.setImages(listUrls);
		this.selectLogo();
	}
	//------------------------------------------ private methods
	private void selectLogo() {
		if (this._userAlertsBean != null) {
		  String logo = this._userAlertsBean.getUserBean().getLogo();
		  if (logo != null) {
		    this._galleryWidget.setSelectedLogo(logo);
		  }
		}
	}
	private Panel buildMainPanel() {
		
		final VerticalPanel panelEdition = new VerticalPanel();
		
		// panel info
		final VerticalPanel panelInfo = new VerticalPanel();
		panelInfo.add(this._inputLogin);
		panelInfo.add(this._inputEmail);
		
		// date registration
		panelInfo.add(WidgetUtils.buildLabelAndWidgetPanel(new Label("registration : "), this._dateRegistration, LABEL_WIDTH));
		
		panelEdition.add(panelInfo);
		
		// panel alertes
		panelEdition.add(this.buildPanelAlerts());
		
		// panel button
		final HorizontalPanel panelButton = new HorizontalPanel();
		panelButton.setSpacing(IConstants.MIN_SPACING);
		panelButton.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		
		panelButton.add(this._btDeleteUser);
		panelButton.add(this._btCancel);
		panelButton.add(this._btValidate);
		
		panelEdition.add(panelButton);
		panelEdition.setCellHorizontalAlignment(panelButton,HasHorizontalAlignment.ALIGN_RIGHT);
		
		// main panel
		this._main.add(panelEdition);
		this._main.add(this._galleryWidget);
		
		return this._main;
	}
	private void displayPanelAlerts() {
		// si au moins une alerte active >> expand alerts panel
		if (this._cbChangeDateAlert.getValue() || this._cbDeleteUserAlert.getValue() ||
	        this._cbTaskToDoAlert.getValue() ||
	        this._cbTaskFreeAlert.getValue()) {
	        this._panelAlerts.setOpen(true);
	     } else {
	    	 this._panelAlerts.setOpen(false);
	     }
	}
	private Widget buildPanelAlerts() {
		
		
		VerticalPanel panel = new VerticalPanel();
		
		HorizontalPanel panelTaskToDo = new HorizontalPanel();
		panelTaskToDo.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		panelTaskToDo.add(this._cbTaskToDoAlert);
		
		panelTaskToDo.add (this._inputDelaiDays);		
		
		panel.add(panelTaskToDo);
		panel.add(this._cbTaskFreeAlert);
		panel.add(this._cbChangeDateAlert);
		panel.add(this._cbDeleteUserAlert);
				
		this._panelAlerts.add(panel);
		return this._panelAlerts;
	}


	private void initComposants() {
		
		//this._tbLogin.addStyleName(IConstants.STYLE_EDIT_USER_TEXTBOX);
		this._inputLogin.setTextBoxStyleName(IConstants.STYLE_EDIT_USER_TEXTBOX);
		this._inputEmail.setTextBoxStyleName(IConstants.STYLE_EDIT_USER_TEXTBOX);
		
		this._cbDeleteUserAlert.setValue(false);
		this._cbDeleteUserAlert.setValue(false);
		this._cbTaskToDoAlert.setValue(true);
		this._cbTaskFreeAlert.setValue(true);
		this._inputDelaiDays.setValue("1");
		this._inputDelaiDays.setTextBoxStyleName(IConstants.STYLE_EDIT_USER_TEXTBOX);
		this._inputDelaiDays.setTextBoxWidth("15px");
		
		this._dateRegistration.setFormat(new DateBox.DefaultFormat(DateUtils.dateTimeFormat));	
	}

	private void populateBeanFromWidget(UserAndAlertDto userAndAlertsBean) {
		
		final UserBean userBean = this._userAlertsBean.getUserBean();
		userBean.setLogin(this._inputLogin.getValue());
		userBean.setEmail(this._inputEmail.getValue());
		userBean.setLogo(this._galleryWidget.getSelectedLogo());
		userBean.setRegistration(this._dateRegistration.getValue());
		
        this._userAlertsBean.setChangeDateAlert(this._cbChangeDateAlert.getValue());
        this._userAlertsBean.setDeleteUserAlert(this._cbDeleteUserAlert.getValue());
        this._userAlertsBean.setTaskToDoAlert(this._cbTaskToDoAlert.getValue());
        this._userAlertsBean.setTaskFreeAlert(this._cbTaskFreeAlert.getValue());
        this._userAlertsBean.setDelai(Integer.parseInt(this._inputDelaiDays.getValue()));
        
        
	}


	

}
