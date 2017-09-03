package com.collectif.ft.croissants.client.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.collectif.ft.croissants.client.event.ChangeViewEvent;
import com.collectif.ft.croissants.client.event.ShowUserHistoryEvent;
import com.collectif.ft.croissants.client.event.ShowUserHistoryEventHandler;
import com.collectif.ft.croissants.client.util.DateUtils;
import com.collectif.ft.croissants.client.view.ViewToDisplayEnum;
import com.collectif.ft.croissants.shared.model.bean.IBean;
import com.collectif.ft.croissants.shared.model.dto.TaskAndUserDto;
import com.collectif.ft.croissants.shared.model.dto.UserAndScoreDto;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class DispatchFreeUserPresenter implements IPresenter {

	private final static Logger log = Logger.getLogger("DispatchFreeUserPresenter");

	
	private final ICroissantServiceAsync _rpcService;
	private final DisplayFreeUser _view;
	private final HandlerManager _eventBus;
	
	
	//--------------------------------- constructor
	public DispatchFreeUserPresenter (ICroissantServiceAsync rpcService, 
				HandlerManager eventBus, DisplayFreeUser view) {
		this._rpcService = rpcService;
		this._eventBus = eventBus;
		this._view = view;
	}
	//---------------------------------------- overriding IPresenter
	@Override
	public void go(HasWidgets container, PresenterCallback callback) {
		this.bind();
        container.clear();
        container.add(this._view.asWidget());
        this.fetchAll();
	}

	@Override
	public void refresh(IBean bean) {
		this.fetchAll();
	}
	
	//--------------------------------------------- private methods

	private void bind() {

		
	   // Simulate dispatch handler
       this._view.getSimulateDispatchButton().addClickHandler(new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			doSimulateDispatch();
		}
	   });
       
       // validate dispatch handler
       this._view.getValidateDispatchButton().addClickHandler(new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			doValidateDispatch();
		}
	   });
       
       // cancel dispatch handler
       this._view.getCancelDispatchButton().addClickHandler(new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			doCancelDispatch();
		}
	  });
       
      // show History handler
    	this._view.setShowUserHistoryEventHandler(new ShowUserHistoryEventHandler() {
    			
    			@Override
    			public void onShowUserHistory(ShowUserHistoryEvent event) {
    				
    				if (event == null || event.getScope() == null) {
    					return;
    				}
    				
    				switch (event.getScope()) {
    				case showHistory: _eventBus.fireEvent(event);
    				break;
    			
    				}		
    				
    			}
    		});
		
	}
	
	private void doSimulateDispatch() {

		 log.config("doSimulateDispatch()");
         final List<Integer> selectedUserIds = this._view.getListOfSelectedUsers();
         final List<Integer> taskIds = this._view.getListOfIncompleteTasks();
         
         this._rpcService.dispatchFreeUserInIncompleteTask(selectedUserIds, taskIds, true, new ServiceCallback<List<TaskAndUserDto>>(this._eventBus) {

			@Override
			public void onSuccess(List<TaskAndUserDto> result) {
				_view.setListIncompleteTask(result);
			}
		});
	}
	private void doValidateDispatch() {

		 log.config("doValidateDispatch()");
		 final List<Integer> selectedUserIds = this._view.getListOfSelectedUsers();
         final List<Integer> taskIds = this._view.getListOfIncompleteTasks();
         
         this._rpcService.dispatchFreeUserInIncompleteTask(selectedUserIds, taskIds, false, new ServiceCallback<List<TaskAndUserDto>>(this._eventBus) {

			@Override
			public void onSuccess(List<TaskAndUserDto> result) {
				log.config("validation done successfully !!");
				_eventBus.fireEvent(new ChangeViewEvent(ViewToDisplayEnum.userAndTask));
			}
		});
	}
	
	private void doCancelDispatch() {
		// on réinitialise les taches incomplètes
		this.fetchIncompleteTask();
	}

	private void fetchAll() {
		this.fetchUseAndScore();
		this.fetchIncompleteTask();		
	}
	private void fetchUseAndScore() {
		
		this._rpcService.loadListUserAndScore(new ServiceCallback<List<UserAndScoreDto>>(this._eventBus) {
			
			@Override
			public void onSuccess(List<UserAndScoreDto> result) {
				_view.setListUserAndScore(result);
			}
	
		});
	}
	
	private void fetchIncompleteTask() {
		
		Date beginDate = null;
		Date endDate = null;
		this._rpcService.loadListOfIncompleteTask(beginDate, endDate, DateUtils.offset,
				new ServiceCallback<List<TaskAndUserDto>>(this._eventBus) {

			@Override
			public void onSuccess(List<TaskAndUserDto> result) {
				_view.setListIncompleteTask(result);
			}
		});
	}
	
	//====================================== INNER CLASS
	
	public interface DisplayFreeUser {
		
		
		// la vue est un composite 
		public Widget asWidget();
		
		public void setListUserAndScore (List<UserAndScoreDto> listUserAndScoreDto);
		
		public void setListIncompleteTask (List<TaskAndUserDto> listTaskAndUserDto);
		
		public List<Integer> getListOfSelectedUsers();
		
		public List<Integer> getListOfIncompleteTasks();
		
		public HasClickHandlers getSimulateDispatchButton();
		public HasClickHandlers getValidateDispatchButton();
		public HasClickHandlers getCancelDispatchButton();
		
		public void setShowUserHistoryEventHandler(ShowUserHistoryEventHandler handler);
		
		
	}

}
