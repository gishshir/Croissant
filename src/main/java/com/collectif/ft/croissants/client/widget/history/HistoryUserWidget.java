package com.collectif.ft.croissants.client.widget.history;

import java.util.Date;

import com.collectif.ft.croissants.client.event.UpdateFulfillmentEvent;
import com.collectif.ft.croissants.client.event.UpdateFulfillmentEventHandler;
import com.collectif.ft.croissants.client.service.AppController;
import com.collectif.ft.croissants.client.text.MyWording;
import com.collectif.ft.croissants.client.util.IConstants;
import com.collectif.ft.croissants.client.widget.common.DialogBoxContainer;
import com.collectif.ft.croissants.client.widget.common.AbstractUserWidget;
import com.collectif.ft.croissants.shared.model.bean.UserBean;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class HistoryUserWidget extends AbstractUserWidget {
	
	private final static MyWording myWording = AppController.getMyWording();
	
	private static final String GROUP_FULLFILEMENT = "radioGroupFullfilment";

	private static final String baseImageUrl = GWT.getModuleBaseURL() + "/images/";
	private static final String okImageUrl = baseImageUrl + "Ok.png";
	private static final String nOkImageUrl = baseImageUrl + "Nok.png";
	
	private Button _btEditFullfilment;
	private final UpdateFulfillmentEventHandler _updateFullfilmentEventHandler;
	
	private DialogBoxContainer dialog ;
	private RadioButton _rbTaskFullfilled;
	private RadioButton _rbTaskNotFullfilled;
	
	
	private  Image _image;
	
	private final Date _date;
	private boolean _fullfilment;

	//----------------------------------------- constructor
//	public HistoryUserWidget ( String userColorStyle) {
//		this(null, false, false, userColorStyle, null);
//	}
	public HistoryUserWidget (Date date, boolean showFulfillment, boolean editable,
			String userColorStyle, UpdateFulfillmentEventHandler editFullfilmentEventHandler) {
		super(showFulfillment, editable, userColorStyle);
		this._date = date;
		this._updateFullfilmentEventHandler = editFullfilmentEventHandler;
	}
	
	//------------------------------------------ public methods
	public void setDatas(UserBean userBean, boolean fulfillment) {
		super.setDatas(userBean);
		this._fullfilment = fulfillment;
		this.updateIcone();
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
				buttonPanel.add(this._btEditFullfilment);
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
		 this._image = new Image(okImageUrl);
		 this._image.addStyleName(IConstants.STYLE_PICTO_16X16);
		 
		 // edit fullfilment if editable
		 if (this._editable) {
		   this._btEditFullfilment = new Button();
		   this._btEditFullfilment.addStyleName(IConstants.STYLE_IMG_DETAIL);
		   this._btEditFullfilment.setTitle(myWording.infoEditFullfilment());
		 }
		
	}

	@Override
	protected void initHandlers() {
		
		//bouton edit fullfilment
		if (this._editable) {
            this._btEditFullfilment.addClickHandler(new ClickHandler() {
		
		  @Override
		  public void onClick(ClickEvent event) {
			showModifyFullfilmentPopup();
		  }
	    });
		}
	}
	
	//---------------------------------------------- private methods
	private void updateIcone(){
		if (this._showPictos) {
			  this._image.setUrl((this._fullfilment)?okImageUrl:nOkImageUrl);
			  this._image.setTitle((this._fullfilment)?myWording.infoTaskOkFulfilment():myWording.infoTaskNokFulfilment());
			}
	}
	private void showModifyFullfilmentPopup() {
		dialog = new DialogBoxContainer(myWording.titleModifyFulfillment(), true);
		dialog.add(this.buildModifyFullfilmentPanel());
	    dialog.showRelativeTo(this._btEditFullfilment);
	}
	private Panel buildModifyFullfilmentPanel() {
		
		this._rbTaskFullfilled = new RadioButton(GROUP_FULLFILEMENT,myWording.radioFullfilled());
		this._rbTaskNotFullfilled = new RadioButton(GROUP_FULLFILEMENT, myWording.radioNotFullfilled());
		this._rbTaskFullfilled.setValue(this._fullfilment);
		this._rbTaskNotFullfilled.setValue(!this._fullfilment);
		
		ClickHandler handler = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doModifyFullfilment(_rbTaskFullfilled.getValue());
			}
		};
		
		this._rbTaskFullfilled.addClickHandler(handler);
		this._rbTaskNotFullfilled.addClickHandler(handler);
			
		VerticalPanel main = new VerticalPanel();
		main.add(this._rbTaskFullfilled);
		main.add(this._rbTaskNotFullfilled);
		return main;
		
	}
	private void doModifyFullfilment(boolean fullfilment) {
		this._fullfilment = fullfilment;
		this.updateIcone();
		dialog.hide();
		this._updateFullfilmentEventHandler.onUpdateFulfillment(
				new UpdateFulfillmentEvent(this._userBean.getId(), this._date,
						this._fullfilment, this._btEditFullfilment));
	}

}
