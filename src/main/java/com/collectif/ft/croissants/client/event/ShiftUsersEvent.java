package com.collectif.ft.croissants.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShiftUsersEvent extends GwtEvent<ShiftUsersEventHandler> {

	public final static Type<ShiftUsersEventHandler> TYPE = new Type<ShiftUsersEventHandler>();
	
	private final int _beginTaskId;
	private final boolean _up;
	
	
	//---------------------------------- accessor
	public int getBeginTaskId() {
		return this._beginTaskId;
	}
	public boolean isUp() {
		return this._up;
	}
	
	//---------------------------------- constructor
	public ShiftUsersEvent(int beginTaskId, boolean up) {
		this._beginTaskId = beginTaskId;
		this._up = up;
	}
	
	//-------------------------------------- overriding GwtEvent
	@Override
	public Type<ShiftUsersEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShiftUsersEventHandler handler) {
		handler.onShiftUsers(this);
	}

}
