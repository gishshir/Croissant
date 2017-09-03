package com.collectif.ft.croissants.client.event;

import com.collectif.ft.croissants.shared.model.bean.UserBean;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

public class UpdateUserEvent extends GwtEvent<UpdateUserEventHandler> {
	
	public final static Type<UpdateUserEventHandler> TYPE = new Type<UpdateUserEventHandler>();
	
	private final UserBean _userBean;
	private final UpdateActionType _updateActionType;
	private final Widget _ankor;
	
    //--------------------------------------- accessors
	public UserBean getUserBean() {
		return this._userBean;
	}
	public UpdateActionType getUpdateActionType() {
		return this._updateActionType;
	}
	public Widget getAnkor () {
		return this._ankor;
	}
	//--------------------------------------- constructor
	public UpdateUserEvent(final UserBean userBean, final UpdateActionType updateActionType, Widget ankor) {
		this._userBean = userBean;
		this._updateActionType = updateActionType;
		this._ankor = ankor;
	}
	public UpdateUserEvent(final UserBean userBean, final UpdateActionType updateActionType) {
		this(userBean, updateActionType, null);
	}

	//------------------------------------------ overriding GwtEvent
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<UpdateUserEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(UpdateUserEventHandler handler) {
		handler.onUpdateUser(this);
	}

}
