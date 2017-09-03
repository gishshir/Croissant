package com.collectif.ft.croissants.client.service;

import java.util.logging.Logger;

import com.collectif.ft.croissants.client.event.ErrorEvent;
import com.collectif.ft.croissants.client.event.ErrorEvent.ErrorType;
import com.collectif.ft.croissants.client.event.UpdateAbsenceEvent;
import com.collectif.ft.croissants.client.event.UpdateAbsenceEventHandler;
import com.collectif.ft.croissants.client.util.DateUtils;
import com.collectif.ft.croissants.client.util.WidgetUtils;
import com.collectif.ft.croissants.shared.model.bean.AbsenceBean;
import com.collectif.ft.croissants.shared.model.bean.IBean;
import com.collectif.ft.croissants.shared.model.dto.UserAndAbsencesDto;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class AbsenceUserPresenter implements IPresenter {
	
	private final static Logger log = Logger.getLogger("AbsenceUserPresenter");
	
	private final ICroissantServiceAsync _rpcService;
	private final DisplayAbsence _view;
	private final HandlerManager _eventBus;
	private PresenterCallback _presenterCallback;
	private final Widget _ankor;
	
	private final int _currentUserId;

	//--------------------------------------- constructor
	public AbsenceUserPresenter(ICroissantServiceAsync rpcService, HandlerManager eventBus, DisplayAbsence view,
			int userId, Widget ankor) {
		this._rpcService = rpcService;
		this._eventBus = eventBus;
		this._view = view;
		this._currentUserId = userId;
		this._ankor = ankor;
	}
	
	//--------------------------------------- implementing IPresenter
	@Override
	public void go(HasWidgets container, PresenterCallback presenterCallback) {
		this._presenterCallback = presenterCallback;
		this.bind();
        container.clear();
        container.add(this._view.asWidget());
        
        this.fetchAbsences();
	}

	@Override
	public void refresh(IBean bean) {
		// TODO Auto-generated method stub

	}
	//--------------------------------------------------- private methods
	private void fetchAbsences() {

        this._rpcService.loadListAbsenceForUser(this._currentUserId, new AsyncCallback<UserAndAbsencesDto>() {
			
			@Override
			public void onSuccess(UserAndAbsencesDto userAndAbsencesDto) {
				_view.setUserAndAbsences(userAndAbsencesDto);
				_presenterCallback.onReady();				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				log.severe("onFailure: " + caught.getMessage());
				ErrorEvent errorEvent = new ErrorEvent(caught.getMessage(), null, _ankor);
				errorEvent.setErrorType(ErrorType.stale_datas);
				_eventBus.fireEvent(errorEvent);
			}
		});
	}


	private void bind() {

		// ici on définit les ClickHandler de la vue
		// click on close
		this._view.getCloseButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				_presenterCallback.onEnd();
			}
		});
		
		// clic on add new absence
		this._view.getAddButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doAddAbsence();
			}
		});
		
		// clic on delete all absences
		this._view.getDeleteAllButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				confirmDeleteAllAbsence((Widget)_view.getDeleteAllButton());
			}
		});
		
		// update and delete handler
		this._view.setUpdateAbsenceEventHandler(new UpdateAbsenceEventHandler() {
			
			@Override
			public void onUpdateAbsence(UpdateAbsenceEvent event) {

               if (event == null || event.getUpdateActionType() == null ||
            		   event.getAbsence() == null) {
            	   return;
               }
               switch (event.getUpdateActionType()) {
			      case update: doUpdateAbsence(event.getAbsence());
				      break;

			      case delete: doDeleteAbsence(event.getAbsence());
				    break;
			    }
			}
		});
	}

	private void doAddAbsence() {
		log.config("doAddAbsence()");
		this._rpcService.loadNewAbsence(this._currentUserId, DateUtils.offset, new ServiceCallback<AbsenceBean>(this._eventBus) {

			@Override
			public void onSuccess(AbsenceBean absence) {
				fetchAbsences();
			}
		});
	}
    private void confirmDeleteAllAbsence(final Widget ankor) {
		
		IActionCallback actionCallback = new IActionCallback() {
			
			@Override
			public void onOk() {
				doDeleteAll();
			}
			
			@Override
			public void onCancel() {
				// do nothing			
			}
		};
		
		String[] messages = new String[] {
				"Click OK to  delete all absences '"
		};
	    this.showConfirmDialog("Confirm delete user", messages, ankor, actionCallback);
		

	}
	private void doDeleteAll() {
		log.config("doDeleteAll()");
		// TODO
	}
	private void doDeleteAbsence(AbsenceBean absence) {
		log.config("doDeleteAbsence()");
		this._rpcService.deleteAbsence(absence, new ServiceCallback<Boolean>(this._eventBus) {

			@Override
			public void onSuccess(Boolean result) {
				fetchAbsences();
			}
		});

	}
	private void doUpdateAbsence(AbsenceBean absence) {
		log.config("doUpdateAbsence()");
		this._rpcService.updateAbsence(absence, new ServiceCallback<AbsenceBean>(this._eventBus) {

			@Override
			public void onSuccess(AbsenceBean result) {
				fetchAbsences();
			}
		});
	}
	
	private void showConfirmDialog(String title, String[] messages, Widget ankor,
			IActionCallback actionCallback) {
		
		WidgetUtils.buildDialogBox(title, messages, null, true, actionCallback)
		.showRelativeTo(ankor);
	}
	
	//=============================== INNER CLASS ====================
	public interface DisplayAbsence {
		
		public void setUserAndAbsences(UserAndAbsencesDto userAndAbsences);
		public Widget asWidget();
		public HasClickHandlers getCloseButton();
		public HasClickHandlers getAddButton();
		public HasClickHandlers getDeleteAllButton();
		public void setUpdateAbsenceEventHandler (UpdateAbsenceEventHandler updateAbsenceEventHandler);
	}

}
