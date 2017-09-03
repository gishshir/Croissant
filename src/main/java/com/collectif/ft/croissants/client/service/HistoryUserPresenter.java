package com.collectif.ft.croissants.client.service;

import java.util.logging.Logger;

import com.collectif.ft.croissants.client.event.ErrorEvent;
import com.collectif.ft.croissants.client.event.ErrorEvent.ErrorType;
import com.collectif.ft.croissants.shared.model.bean.IBean;
import com.collectif.ft.croissants.shared.model.dto.UserHistoryDto;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;


/**
 * Pilote la vue HistoryUserView
 * @author sylvie
 *
 */
public class HistoryUserPresenter implements IPresenter {
	
	
	private final static Logger log = Logger.getLogger("EditUserPresenter");
	
	private final ICroissantServiceAsync _rpcService;
	private final DisplayUserHistory _view;
	private final HandlerManager _eventBus;
	private PresenterCallback _presenterCallback;
	private final Widget _ankor;
	
	private final int _currentUserId;

	
	//--------------------------------------------- overriding IPresenter
	@Override
	public void go(HasWidgets container, PresenterCallback presenterCallback) {
		
		this._presenterCallback = presenterCallback;
		this.bind();
        container.clear();
        container.add(this._view.asWidget());
        
        this.fetchHistory();
	}




	@Override
	public void refresh(IBean bean) {
		// TODO Auto-generated method stub

	}
	
	//------------------------------------------ constructor
		public HistoryUserPresenter(ICroissantServiceAsync rpcService, HandlerManager eventBus, DisplayUserHistory view,
				int userId, Widget ankor) {
			this._rpcService = rpcService;
			this._eventBus = eventBus;
			this._view = view;
			this._currentUserId = userId;
			this._ankor = ankor;
		}
		

		//----------------------------------------- private methods
		private void bind() {

			// ici on définit les ClickHandler de la vue
			
			// click on close

			this._view.getCloseButton().addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					_presenterCallback.onEnd();
				}
			});
		}
		

		private void fetchHistory() {
			
			this._rpcService.loadUserHistory(this._currentUserId,  new AsyncCallback<UserHistoryDto>() {
				
				@Override
				public void onSuccess(UserHistoryDto userHistory) {
					
					  _view.setHistory(userHistory);
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

		//======================================================= INNER CLASS
		
		/**
		 * Vue : historique d'un utilisateur
		 * @author sylvie
		 *
		 */
		public interface DisplayUserHistory {
			
			public Widget asWidget();
			
			public HasClickHandlers getCloseButton();
			
			public void setHistory (UserHistoryDto userHistory);
			
			
		}
}
