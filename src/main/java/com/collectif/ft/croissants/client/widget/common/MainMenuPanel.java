package com.collectif.ft.croissants.client.widget.common;

import com.collectif.ft.croissants.client.event.ChangeViewEvent;
import com.collectif.ft.croissants.client.service.AppController;
import com.collectif.ft.croissants.client.text.MyWording;
import com.collectif.ft.croissants.client.util.IConstants;
import com.collectif.ft.croissants.client.view.ViewToDisplayEnum;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainMenuPanel extends Composite {
	
	private final static MyWording myWording = AppController.getMyWording();
	
	private final HandlerManager _eventBus;
	
	private final VerticalPanel _mainPanel = new VerticalPanel();
	
	private final ButtonPrinc _btGoToHistoryView = new ButtonPrinc(myWording.buttonGoToGlobalHistoryView());
	private final ButtonPrinc _btGoToTaskView = new ButtonPrinc(myWording.buttonGoToTaskView());
	private final ButtonPrinc _btGoToDispatchUserView = new ButtonPrinc(myWording.buttonGoToDispatchUserView());
	

	//-------------------------------------------- constructor
	public MainMenuPanel ( HandlerManager eventBus) {

		    this._eventBus = eventBus;
			this.initComposants();
			this.initHandlers();
			this.initWidget(this.buildMainPanel());
	}

	public void displayButton(ViewToDisplayEnum viewToDisplay) {
		
		this._btGoToHistoryView.setEnabled(true);
		this._btGoToTaskView.setEnabled(true);
		this._btGoToDispatchUserView.setEnabled(true);
		
		switch (viewToDisplay) {
		case globalHistory:	
			this._btGoToHistoryView.setEnabled(false);
			break;
		case userAndTask:	
			this._btGoToTaskView.setEnabled(false);
			break;
		case dispatchUser:
			this._btGoToDispatchUserView.setEnabled(false);
			break;
			
		}		
	}	
	

	//------------------------------------------- private methods
	private Widget buildMainPanel() {

         this._mainPanel.setHeight(IConstants.MAX_SIZE);
         
         this._mainPanel.add(this._btGoToTaskView);
         this._mainPanel.add(this._btGoToHistoryView);
         this._mainPanel.add(this._btGoToDispatchUserView);
         
		return this._mainPanel;
	}

	private void initHandlers() {

        this._btGoToHistoryView.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				_eventBus.fireEvent(new ChangeViewEvent(ViewToDisplayEnum.globalHistory));
			}
		});
        this._btGoToDispatchUserView.addClickHandler(new ClickHandler() {
			
 			@Override
 			public void onClick(ClickEvent event) {
 				_eventBus.fireEvent(new ChangeViewEvent(ViewToDisplayEnum.dispatchUser));
 			}
 		});
        this._btGoToTaskView.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				_eventBus.fireEvent(new ChangeViewEvent(ViewToDisplayEnum.userAndTask));
			}
		});
	}


	private void initComposants() {
		this.displayButton(ViewToDisplayEnum.userAndTask);
		
		this._btGoToTaskView.setTitle(myWording.titleButtonGoToTaskView());
		this._btGoToHistoryView.setTitle(myWording.titleButtonGoToGlobalHistoryView());
		this._btGoToDispatchUserView.setTitle(myWording.titleButtonGoToDispatchUserView());
	}
	
}
