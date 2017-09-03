package com.collectif.ft.croissants.client.widget.user;

import com.collectif.ft.croissants.client.event.EditAbsenceEvent;
import com.collectif.ft.croissants.client.event.EditAbsenceEventHandler;
import com.collectif.ft.croissants.client.event.EditUserEvent;
import com.collectif.ft.croissants.client.event.EditUserEventHandler;
import com.collectif.ft.croissants.client.event.ShowUserHistoryEvent;
import com.collectif.ft.croissants.client.event.ShowUserHistoryEvent.Scope;
import com.collectif.ft.croissants.client.event.ShowUserHistoryEventHandler;
import com.collectif.ft.croissants.client.event.UpdateActionType;
import com.collectif.ft.croissants.client.event.UpdateUserEvent;
import com.collectif.ft.croissants.client.event.UpdateUserEventHandler;
import com.collectif.ft.croissants.client.util.IConstants;
import com.collectif.ft.croissants.client.util.InputUtils;
import com.collectif.ft.croissants.client.widget.common.AbstractUserWidget;
import com.collectif.ft.croissants.shared.model.bean.UserBean;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class UserWidget extends AbstractUserWidget {
	

	private Button _btEditUser;
	private Button _btScore;
	private Button _btEditAbsence;
	
	private boolean _editing = false;
	
	private final UpdateUserEventHandler _updateUserEventHandler;
	private final EditUserEventHandler _editUserEventHandler;
	private final ShowUserHistoryEventHandler _showUserHistoryEventHandler;
	private final EditAbsenceEventHandler _editAbsenceEventHandler;
	
	private IDraggableUserWidget _draggableUserWidget;
	
	private TextBox _tblogin;

	//----------------------------------------------------- constructor
	public UserWidget(final UpdateUserEventHandler updateUserEventHandler,
			final EditUserEventHandler editUserEventHandler,
			final ShowUserHistoryEventHandler showUserHistoryEventHandler,
			final EditAbsenceEventHandler editAbsenceEventHandler) {
		super(true, null);
        this._updateUserEventHandler = updateUserEventHandler;
        this._editUserEventHandler = editUserEventHandler;
        this._showUserHistoryEventHandler = showUserHistoryEventHandler;
        this._editAbsenceEventHandler = editAbsenceEventHandler;
	}

	
	//--------------------------------------------------- public methods
	public void editLogin() {
		this.editLogin(true);
	}
	public void cancelEditLogin() {
		if (this._editing) {
			this.editLogin(false);
		}
	}
	@Override
	public void setDatas(UserBean userBean) {
		super.setDatas(userBean);
		if (userBean.getLogin() != null) {
			this._tblogin.setText(userBean.getLogin());
		}

	}
	//-------------------------------------- overriding SimpleUserWidget
	@Override
	protected Panel buildInfoPanel () {
		
        Panel infoPanel = super.buildInfoPanel();
        infoPanel.add(this._tblogin);
		
		return infoPanel;
	}
	@Override
	protected  Widget getAdditionalInfoPanel() {
		return this.getLoginWrapper();
	}
	@Override
	protected  Widget getVerticalButtonPanel() {
		
		// buttons
		final VerticalPanel buttonPanel = new VerticalPanel();
		buttonPanel.add(this._btScore);
		buttonPanel.add(this._btEditAbsence);
		buttonPanel.add(this._btEditUser);
		return buttonPanel;
	}
	@Override
	protected Widget getVerticalIconPanel() {
		return null;
	}
	@Override
	protected void initComposants() {
		super.initComposants();
		this._tblogin = new TextBox();
		this.editLogin(false);
		
		this._image.setTitle(myWording.infoMoveUser());
		
		this._btEditAbsence = new Button();
		this._btEditAbsence.addStyleName(IConstants.STYLE_IMG_SHOW_ABSENCE);
		this._btEditAbsence.setTitle(myWording.infoAbsences());
	
		this._btEditUser = new Button();
		this._btEditUser.addStyleName(IConstants.STYLE_IMG_DETAIL);
		this._btEditUser.setTitle(myWording.infoEditUser());
		
		this._btScore = new Button();
		this._btScore.addStyleName(IConstants.STYLE_IMG_SCORE);
		this._btScore.setTitle(myWording.infoScoreUser());
	}
	@Override
	protected void initHandlers() {
		
		// show score
		this._btScore.addMouseOverHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {

               _showUserHistoryEventHandler.onShowUserHistory(new ShowUserHistoryEvent(_userBean.getId(), Scope.showScore,  _btEditAbsence));
			}
		});
		// hide score
		this._btScore.addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				_showUserHistoryEventHandler.onShowUserHistory(new ShowUserHistoryEvent(_userBean.getId(), Scope.hideScore));
			}
		});
		
		// show history
		this._btScore.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				_showUserHistoryEventHandler.onShowUserHistory(new ShowUserHistoryEvent(_userBean.getId(), Scope.showHistory, _btScore));
			}
		});
		
		// login on change
		this._tblogin.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				
				if (!InputUtils.isDifferents(_tblogin.getText(), _labelLogin.getText())) {
					editLogin(false);
					return;
				}
				
				// mise  a jour du login et fire event
				if (_userBean != null) {
				  _userBean.setLogin(_tblogin.getText());
				  _labelLogin.setText(_userBean.getLogin());
				   editLogin(false);
					
				 _updateUserEventHandler.onUpdateUser(new UpdateUserEvent(_userBean, UpdateActionType.update, _tblogin));
				}
			}
		});
		
		
		// edit absences button onclick
		this._btEditAbsence.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				_editAbsenceEventHandler.onEditAbsences(new EditAbsenceEvent(_userBean.getId(), _btEditAbsence));
			}
		});
		
		// edit user button onclick
		this._btEditUser.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				_editUserEventHandler.onEditUser(new EditUserEvent(_userBean.getId(), _btEditUser));
			}
		});
	}
	//-------------------------------------- package methods
	void setDraggableUserWidget(IDraggableUserWidget draggableUserWidget) {
		this._draggableUserWidget = draggableUserWidget;
	}
	//-------------------------------------- private methods


	private void editLogin(boolean edit) {

		this._editing = edit;
		this._tblogin.setVisible(edit);
		this._labelLogin.setVisible(!edit);
		if (this._draggableUserWidget != null) {
			this._draggableUserWidget.activeDraggable(!edit);
		}
		
		if (edit) {
			  this._tblogin.setText(_labelLogin.getText());
			  this._tblogin.selectAll();
			}
	}

	private Widget getLoginWrapper() {
		
		FocusPanel wrapper = new FocusPanel();
		wrapper.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				editLogin(true);
			}
		});
		wrapper.add(this._labelLogin);
		return wrapper;
	}




}
