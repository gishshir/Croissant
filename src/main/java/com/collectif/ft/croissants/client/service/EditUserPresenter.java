package com.collectif.ft.croissants.client.service;

import java.util.List;
import java.util.logging.Logger;

import com.collectif.ft.croissants.client.event.ErrorEvent;
import com.collectif.ft.croissants.client.event.ErrorEvent.ErrorType;
import com.collectif.ft.croissants.client.event.UpdateActionType;
import com.collectif.ft.croissants.client.event.UpdateUserEvent;
import com.collectif.ft.croissants.shared.model.bean.IBean;
import com.collectif.ft.croissants.shared.model.bean.UserBean;
import com.collectif.ft.croissants.shared.model.dto.UserAndAlertDto;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

/**
 * Pilote la vue EditUserView
 * @author sylvie
 *
 */
public class EditUserPresenter extends AbstractPresenter implements IPresenter {
	
	private final static Logger log = Logger.getLogger("EditUserPresenter");
	
	private final ICroissantServiceAsync _rpcService;
	private final DisplayUser _view;
	private final HandlerManager _eventBus;
	private PresenterCallback _presenterCallback;
	private final Widget _ankor;
	
	private final int _currentUserId;
	private UserBean _userBean;

	//--------------------------------------------- overriding IPresenter
	@Override
	public void go(HasWidgets container, PresenterCallback presenterCallback) {

		this._presenterCallback = presenterCallback;
		this.bind();
        container.clear();
        container.add(this._view.asWidget());
        
        this.fetchUser();
	}
	
	@Override
	public void refresh(IBean bean) {
		// TODO Auto-generated method stub		
	}
	
	//------------------------------------------ constructor
	public EditUserPresenter(ICroissantServiceAsync rpcService, HandlerManager eventBus, DisplayUser view,
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
		
		// click on cancel
		this._view.getCancelButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				_presenterCallback.onEnd();
			}
		});
		
		
		// click on validate
		this._view.getValidateButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doUpdateUser();
			}
		});
		
		// click on delete user
		this._view.getDeleteUserButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				confirmDeleteUser(_userBean, _view.getLoginWidget());
			}
		});
		
	}
	
	private void confirmDeleteUser(final UserBean userBean, final Widget ankor) {
		
		IActionCallback actionCallback = new IActionCallback() {
			
			@Override
			public void onOk() {
				doDeleteUser(_currentUserId, ankor);
			}
			
			@Override
			public void onCancel() {
				// do nothing			
			}
		};
		
		String[] messages = new String[] {
				"Click OK to  delete user '" + userBean.getLogin() + "'",
				"",
				"Warning: all user history will be lost!"
		};
	    this.showConfirmDialog("Confirm delete user", messages, ankor, actionCallback);
		

	}
	
	private void doDeleteUser(int userId,  final Widget ankor) {
		
		this._rpcService.deleteUser(userId, new ServiceCallback<Void>(this._eventBus, ankor, null) {
			
			@Override
			public void onSuccess(Void result) {
				_presenterCallback.onEnd();
				_eventBus.fireEvent(new UpdateUserEvent(_userBean, UpdateActionType.delete));
			}
		});

	}
	
	private void doUpdateUser() {
		
		
		final UserAndAlertDto userAndAlertsBean = this._view.getUser();
		
		this._rpcService.updateUserAndAlerts(userAndAlertsBean, new ServiceCallback<UserAndAlertDto>(this._eventBus, (Widget)this._view.getValidateButton(), null) {

			@Override
			public void onSuccess(UserAndAlertDto result) {
				_presenterCallback.onEnd();
				_eventBus.fireEvent(new UpdateUserEvent(result.getUserBean(), UpdateActionType.update));
			}
		});
		
	}
	
	private void fetchUser() {
		
		this._rpcService.loadUserAndAlert(this._currentUserId, new AsyncCallback<UserAndAlertDto>() {
			
			@Override
			public void onSuccess(UserAndAlertDto userAndAlertDto) {
				
				 _userBean = userAndAlertDto.getUserBean();
				  _view.setUser(userAndAlertDto);
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
		
		this._rpcService.getListSmileyUrls(new ServiceCallback<List<String>>(this._eventBus, null, null) {

			@Override
			public void onSuccess(List<String> list) {
				_view.setGallery(list);	
			}
		});
		
	}
	
	//========================== INNER CLASS ===================
	
	/**
	 * Edition d'un user
	 * Vue pilotee par le Presenter
	 * @author sylvie
	 *
	 */
	public interface DisplayUser {
		
		public Widget asWidget();
		
		public HasClickHandlers getCancelButton();
		public HasClickHandlers getValidateButton();
		public HasClickHandlers getDeleteUserButton();
		
		public void setUser (UserAndAlertDto userAndAlertsBean);
		public void setGallery (List<String> listUrls);
		
		public UserAndAlertDto getUser();
		
		public Widget getLoginWidget();
		
	}


}
