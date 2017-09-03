package com.collectif.ft.croissants.client.view;

import java.util.List;

import com.collectif.ft.croissants.client.event.ShowUserHistoryEventHandler;
import com.collectif.ft.croissants.client.service.AppController;
import com.collectif.ft.croissants.client.service.DispatchFreeUserPresenter.DisplayFreeUser;
import com.collectif.ft.croissants.client.text.MyWording;
import com.collectif.ft.croissants.client.util.IConstants;
import com.collectif.ft.croissants.client.widget.common.MyButton;
import com.collectif.ft.croissants.client.widget.dispatch.IncompleteTaskContainer;
import com.collectif.ft.croissants.client.widget.dispatch.UserScoreToDispatchWidgetContainer;
import com.collectif.ft.croissants.shared.model.dto.TaskAndUserDto;
import com.collectif.ft.croissants.shared.model.dto.UserAndScoreDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Vue permettant la redistribution controlees des utilisateurs dans les taches incompletes
 * <ul>
 * <li> DispatchWidgetContainer  : liste des utilisateurs free classé par score croissant
 * <li> TaskIncompleteContainer : liste des taches à completer
 * </ul>
 * @author sylvie
 *
 */
public class DispatchFreeUserView extends Composite implements DisplayFreeUser {
	
	private final static MyWording myWording = AppController.getMyWording();
	private static final String imageUrl = GWT.getModuleBaseURL() + "/images/arrow.gif";
	
	private final VerticalPanel _main = new VerticalPanel();
	private final Image _arrowImage = new Image(imageUrl);
	
	private enum ButtonState{
		init, simulate
	}
	
	private final UserScoreToDispatchWidgetContainer _dispatchWidgetContainer = new UserScoreToDispatchWidgetContainer();
	private final IncompleteTaskContainer _incompleteTaskContainer = new IncompleteTaskContainer();
	
	private final Label _labelTitle = new Label(myWording.labelDispatchFreeUserTitle());
	
	private final MyButton _btDispatch = new MyButton(myWording.buttonSimulateDispatchUser());
	private final MyButton _btValidate = new MyButton(myWording.buttonValidateDispatchUser());
	private final MyButton _btCancel = new MyButton(myWording.buttonCancelDispatchUser());
	

	private ButtonState _buttonState = ButtonState.init;
	
	//------------------------------------------- constructor
	public DispatchFreeUserView() {
		this.initComposants();
		this.initHandlers();
		this.initWidget(this.buildMainPanel());
	}
	
	//----------------------------------------- implements DisplayFreeUser

	@Override
	public void setShowUserHistoryEventHandler(ShowUserHistoryEventHandler handler) {
		this._dispatchWidgetContainer.setShowUserHistoryEventHandler(handler);
	}

	@Override
	public void setListUserAndScore(
			List<UserAndScoreDto> listUserAndScoreDto) {
		this._dispatchWidgetContainer.setDatas(listUserAndScoreDto);
	}
	@Override
	public void setListIncompleteTask(List<TaskAndUserDto> listTaskAndUserDto) {
		this._incompleteTaskContainer.setDatas(listTaskAndUserDto);
	}

	@Override
	public List<Integer> getListOfSelectedUsers() {
		return this._dispatchWidgetContainer.getListOfSelectedUsers();
	}

	@Override
	public List<Integer> getListOfIncompleteTasks() {
		return this._incompleteTaskContainer.getListOfIncompleteTasks();
	}

	@Override
	public HasClickHandlers getSimulateDispatchButton() {
		return this._btDispatch;
	}

	@Override
	public HasClickHandlers getValidateDispatchButton() {
		return this._btValidate;
	}

	@Override
	public HasClickHandlers getCancelDispatchButton() {
		return this._btCancel;
	}
	//------------------------------------------ private method
	private void displayButton() {
		
		
		switch (this._buttonState) {
		case init:
			this._btCancel.setEnabled(false);
			this._btDispatch.setEnabled(true);
			this._btValidate.setEnabled(false);
			break;
		case simulate:
			this._btCancel.setEnabled(true);
			this._btDispatch.setEnabled(false);
			this._btValidate.setEnabled(true);
			break;
		}
	}
	private void initHandlers() {

      final ClickHandler handlerInit = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {

			_buttonState = ButtonState.init;
			displayButton();
		}
	  };
      final ClickHandler handlerSimulate = new ClickHandler() {
  		
		@Override
		public void onClick(ClickEvent event) {

			_buttonState = ButtonState.simulate;
			displayButton();
		}
	  };
	  
	  this._btValidate.addClickHandler(handlerInit);
	  this._btCancel.addClickHandler(handlerInit);
	  this._btDispatch.addClickHandler(handlerSimulate);
	 
	}
	private Widget buildMainPanel() {
		
		//buttons
		HorizontalPanel panelButton = new HorizontalPanel();
		panelButton.setSpacing(IConstants.MIN_SPACING);
		panelButton.add(this._btDispatch);
		panelButton.add(this._btValidate);
		panelButton.add(this._btCancel);
		
		// container
		HorizontalPanel panelContainer = new HorizontalPanel();
		panelContainer.setSpacing(IConstants.MIN_SPACING);
		
		
		// Panel arrow
		
		SimplePanel panelArrowContainer = new SimplePanel();
		panelArrowContainer.setWidth("100px");
		panelArrowContainer.setHeight("100%");
		
		Label labelLowParticipation = new Label(myWording.labelLowParticipation());
		labelLowParticipation.addStyleName(IConstants.STYLE_DISPATCH_USER_ARROW_LABEL);
		Label labelHighParticipation = new Label(myWording.labelHighParticipation());
		labelHighParticipation.addStyleName(IConstants.STYLE_DISPATCH_USER_ARROW_LABEL);


		VerticalPanel panelArrow = new VerticalPanel();
		panelArrow.getElement().setId(IConstants.STYLE_DISPATCH_USER_ARROW_WIDGET);
		panelArrow.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		panelArrow.add(labelLowParticipation);
		this._arrowImage.getElement().setId(IConstants.STYLE_DISPATCH_USER_ARROW);
		panelArrow.add(this._arrowImage);
		panelArrow.add(labelHighParticipation);
		panelArrowContainer.add(panelArrow);
		
		panelContainer.add(panelArrowContainer);
		panelContainer.add(this._dispatchWidgetContainer);
		panelContainer.add(this._incompleteTaskContainer);
		
		this._main.setSpacing(IConstants.MIN_SPACING);
		this._main.add(this._labelTitle);
		this._main.add(panelButton);
		this._main.add(panelContainer);
		return this._main;
	}

	private void initComposants() {
		
		this._main.getElement().setId(IConstants.STYLE_MAIN_DISPATCH_USER_VIEW);
		this._labelTitle.getElement().setId(IConstants.STYLE_DISPATCH_VIEW_TITLE);
		
		this._btDispatch.setTitle(myWording.buttonSimulateDispatchUserTitle());
		this._btValidate.setTitle(myWording.buttonValidateDispatchUserTitle());
		this._btCancel.setTitle(myWording.buttonCancelDispatchUserTitle());

		this.displayButton();
	}



}
