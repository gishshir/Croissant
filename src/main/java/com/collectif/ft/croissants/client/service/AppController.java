package com.collectif.ft.croissants.client.service;

import java.util.logging.Logger;

import com.collectif.ft.croissants.client.event.ChangeViewEvent;
import com.collectif.ft.croissants.client.event.ChangeViewEventHandler;
import com.collectif.ft.croissants.client.event.EditAbsenceEvent;
import com.collectif.ft.croissants.client.event.EditAbsenceEventHandler;
import com.collectif.ft.croissants.client.event.EditUserEvent;
import com.collectif.ft.croissants.client.event.EditUserEventHandler;
import com.collectif.ft.croissants.client.event.ErrorEvent;
import com.collectif.ft.croissants.client.event.ErrorEvent.ErrorType;
import com.collectif.ft.croissants.client.event.ErrorEventHandler;
import com.collectif.ft.croissants.client.event.ShowUserHistoryEvent;
import com.collectif.ft.croissants.client.event.ShowUserHistoryEvent.Scope;
import com.collectif.ft.croissants.client.event.ShowUserHistoryEventHandler;
import com.collectif.ft.croissants.client.event.UpdateUserEvent;
import com.collectif.ft.croissants.client.event.UpdateUserEventHandler;
import com.collectif.ft.croissants.client.text.MyWording;
import com.collectif.ft.croissants.client.util.WidgetUtils;
import com.collectif.ft.croissants.client.view.AbsenceUserView;
import com.collectif.ft.croissants.client.view.DispatchFreeUserView;
import com.collectif.ft.croissants.client.view.EditUserView;
import com.collectif.ft.croissants.client.view.GlobalHistoryView;
import com.collectif.ft.croissants.client.view.HistoryUserView;
import com.collectif.ft.croissants.client.view.UserAndTaskView;
import com.collectif.ft.croissants.client.view.ViewToDisplayEnum;
import com.collectif.ft.croissants.client.widget.common.DialogBoxContainer;
import com.collectif.ft.croissants.client.widget.common.MainMenuPanel;
import com.collectif.ft.croissants.shared.model.bean.IBean;
import com.collectif.ft.croissants.shared.model.dto.UserAndAlertDto;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Presenter principal de l'application charg� des �changes inter-view
 * @author sylvie
 *
 */
public class AppController implements IPresenter, ValueChangeHandler<String> {
	
	private final static Logger log = Logger.getLogger("AppController");
	
	private final static MyWording myWording = GWT.create(MyWording.class);
	public static final MyWording getMyWording() {
		return myWording;
	}
	
	private final HandlerManager _eventBus;
	private final ICroissantServiceAsync _rpcService;
	private MainMenuPanel _mainMenu;
	private Panel _viewContainer;
	
	private IPresenter _currentPresenter;
	
	//------------------------------------------- constructor
	public AppController(final ICroissantServiceAsync rpcService, final HandlerManager eventBus) {
		this._rpcService = rpcService;
		this._eventBus = eventBus;
		this.bind();
	}

	//-------------------------------------- overriding IPresenter
	@Override
	public void go(HasWidgets container, PresenterCallback callback) {
		
		log.info("go()");
		
		this.buildMainPanel(container);
		
		if ("".equals(History.getToken())) {
			History.newItem(ViewToDisplayEnum.userAndTask.name());
		} else {
			History.fireCurrentHistoryState();
		}
	}
	
	/**
	 * La vue de l'application est composee
	 * - panel de menu
	 * - panel de vue
	 * @param container fourni par l'entry point
	 */
	private void  buildMainPanel(HasWidgets container) {
		
		HorizontalPanel main = new HorizontalPanel();
		
		
		this._viewContainer = new SimplePanel();
		main.add(this._viewContainer);
		
		this._mainMenu = new MainMenuPanel(this._eventBus);
		main.add(this._mainMenu);
		
		container.add(main);
	}

	
	@Override
	public void refresh(IBean bean) {

		this.refresh(ViewToDisplayEnum.userAndTask, bean);
     
	}
	//------------------------------------- overriding ValueChangeHandler
	/**
	 * Determine le choix de la vue courante
	 */
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		final String token = event.getValue();
		log.info("onValueChange() - token: " + token);
		ViewToDisplayEnum viewToDisplay = (token == null || token.length() == 0)?ViewToDisplayEnum.userAndTask:ViewToDisplayEnum.valueOf(ViewToDisplayEnum.class, token);
		
		this.goToView(viewToDisplay);
	}
	
	//---------------------------------- private methods
	private void refresh (ViewToDisplayEnum viewToDisplay, IBean bean) {
		
		log.info("refresh() - viewToDisplay: " + viewToDisplay + " - bean: " + ((bean != null)?bean.getId():""));
		if (this._currentPresenter != null) {
			this._currentPresenter.refresh(bean);
		}
		else {
			this.goToView(viewToDisplay);
		}
	}
	private void goToView (ViewToDisplayEnum viewToDisplay) {
		
			
			log.info("goToView() - viewToDisplay: " + viewToDisplay);
			// creation du presenter et de la vue associée

			this._mainMenu.displayButton(viewToDisplay);
			switch (viewToDisplay) {
			
			   case userAndTask: this._currentPresenter = new UserAndTaskPresenter(this._rpcService, this._eventBus, new UserAndTaskView());
				break;
				
			   case globalHistory: this._currentPresenter = new GlobalHistoryPresenter(this._rpcService, this._eventBus, new GlobalHistoryView());
			      break;
			     
			   case dispatchUser: this._currentPresenter = new DispatchFreeUserPresenter(this._rpcService, this._eventBus, new DispatchFreeUserView());
				  break;
			}
			
			if (this._currentPresenter != null) {
				this._currentPresenter.go(this._viewContainer, new PresenterCallback() {
					
					@Override
					public void onReady() {
						// do nothing						
					}
					
					@Override
					public void onEnd() {
						// do nothing	
					}
				});
			}	
	}
	
	

	private void bind() {
		
		log.info("bind()");
		
		//registration to app-wide history management
		History.addValueChangeHandler(this);
		
		// registration of business handlers

		// change view
		this._eventBus.addHandler(ChangeViewEvent.TYPE, new ChangeViewEventHandler() {
			
			@Override
			public void onChangeView(ChangeViewEvent event) {
				goToView(event.getViewToDisplay());
			}
		});
		
		// Error handler
		this._eventBus.addHandler(ErrorEvent.TYPE, new ErrorEventHandler() {
			
			@Override
			public void onError(ErrorEvent event) {
				
				IActionCallback actionCallback = event.getActionCallback();
				if (event.getErrorType() == ErrorType.stale_datas && event.getActionCallback() == null) {
					actionCallback = new OkActionCallback() {
						
						@Override
						public void onOk() {
							refresh(null);
						}
					};
				} 
				
				showError(event.getErrorMessage(), actionCallback, event.getWidget());		
			}
		});
		
		// Edit user
		this._eventBus.addHandler(EditUserEvent.TYPE, new EditUserEventHandler() {
			
			@Override
			public void onEditUser(EditUserEvent event) {
				doEditUser(event.getUserId(), event.getAnkor());
			}
		});
		
		// Edit absences
		this._eventBus.addHandler(EditAbsenceEvent.TYPE, new EditAbsenceEventHandler() {
			
			@Override
			public void onEditAbsences(EditAbsenceEvent event) {
				doEditAbsences(event.getUserId(), event.getAnkor());
			}
		});
		
		// Update user
		this._eventBus.addHandler(UpdateUserEvent.TYPE, new UpdateUserEventHandler() {
			
			@Override
			public void onUpdateUser(UpdateUserEvent event) {
				
				switch (event.getUpdateActionType()) {
				case update:	doRefreshUser(event.getUserBean().getId());
					break;

				case delete: refresh(ViewToDisplayEnum.userAndTask, null);
					break;
				}
				
			}
		});
		
		// display user history
		this._eventBus.addHandler(ShowUserHistoryEvent.TYPE, new ShowUserHistoryEventHandler() {
			
			@Override
			public void onShowUserHistory(ShowUserHistoryEvent event) {

               if (event != null && event.getScope() == Scope.showHistory) {
            	   doDisplayUserHistory(event.getUserId(), event.getAnkor());
               }
				
			}
		});
		
	}
	
	
	private void doRefreshUser(final int userId) {
		
		this._rpcService.loadUserAndAlert(userId, new AsyncCallback<UserAndAlertDto>() {
			
			@Override
			public void onSuccess(UserAndAlertDto userAndAlertDto) {
				refresh(ViewToDisplayEnum.userAndTask, userAndAlertDto.getUserBean());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				showError("error in loadUserAndAlertDto");
			}
		});
	}
	
	private void doEditAbsences (final int userId, Widget ankor) {
		log.info("doEditAbsences()");
		
		IPresenter presenter = new AbsenceUserPresenter(this._rpcService, this._eventBus, new AbsenceUserView(), userId, ankor);
		final DialogBoxContainer dialog = new DialogBoxContainer("Editing absences...", true);
	
		presenter.go(dialog, new PresenterCallback() {
			
			@Override
			public void onReady() {
				dialog.center();
			}
			
			@Override
			public void onEnd() {
				dialog.hide();
			}

		});	
	}
	
	private void doEditUser(final int userId, Widget ankor) {
		log.info("doEditUser()");
		
		IPresenter presenter = new EditUserPresenter(this._rpcService, this._eventBus, new EditUserView(), userId, ankor);
		final DialogBoxContainer dialog = new DialogBoxContainer("Editing user...", true);
		
		presenter.go(dialog, new PresenterCallback() {
			
			@Override
			public void onReady() {
				dialog.center();
			}
			
			@Override
			public void onEnd() {
				dialog.hide();
			}

		});	
	}
	


	private void doDisplayUserHistory(final int userId, Widget ankor) {
		
		log.info("doDisplayUserHistory()");
		
		IPresenter presenter = new HistoryUserPresenter(this._rpcService, this._eventBus, new HistoryUserView(), userId, ankor);
		final DialogBoxContainer dialog = new DialogBoxContainer("User history...", true);
		
		presenter.go(dialog,  new PresenterCallback() {
			
			@Override
			public void onReady() {
				dialog.center();
			}
			
			@Override
			public void onEnd() {
				dialog.hide();
			}
		});
		
	}
	
	
	
	private void showError(String errorMessage) {
		this.showError(errorMessage, null, null);
	}
	private void showError(String errorMessage, IActionCallback actionCallback, Widget ankor) {
		
		DialogBox dialogBox = WidgetUtils.buildDialogBox("Error", new String[] {errorMessage}, null, false, actionCallback);
		if (ankor == null) {
			dialogBox.center();
			dialogBox.show();
		} else {
			dialogBox.showRelativeTo(ankor);
		}
	}



}
