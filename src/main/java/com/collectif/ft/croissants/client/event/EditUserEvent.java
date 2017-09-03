package com.collectif.ft.croissants.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

public class EditUserEvent extends GwtEvent<EditUserEventHandler> {
	
	public final static Type<EditUserEventHandler> TYPE = new Type<EditUserEventHandler>();

	private final int _userId;
	private final Widget _ankor;
	
	
	//-------------------------------------------- accessors
	public int getUserId() {
		return this._userId;
	}
	public Widget getAnkor() {
		return this._ankor;
	}
	
	//--------------------------------------------- constructor
	public EditUserEvent(final int userId, Widget ankor) {
		this._userId = userId;
		this._ankor = ankor;
	}
	

	//-------------------------------------------- overriding GwtEvent
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<EditUserEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(EditUserEventHandler handler) {
		handler.onEditUser(this);
	}

}
