package com.collectif.ft.croissants.client.event;

import java.util.Date;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

public class UpdateFulfillmentEvent extends GwtEvent<UpdateFulfillmentEventHandler> {
	
	public final static Type<UpdateFulfillmentEventHandler> TYPE = new Type<UpdateFulfillmentEventHandler>();

	private final int _userId;
	private final Date _date;
	private final boolean _fullfilment;
	private final Widget _ankor;
	
	//-------------------------------------------- accessors
	public int getUserId() {
			return this._userId;
	}
	public Widget getAnkor() {
			return this._ankor;
	}
	public boolean isFullfilment() {
		return this._fullfilment;
	}
	public Date getDate() {
		return this._date;
	}
	//--------------------------------------------- constructor
	public UpdateFulfillmentEvent(final int userId, Date date, boolean fullfilment, Widget ankor) {
		this._userId = userId;
		this._date = date;
		this._fullfilment = fullfilment;
		this._ankor = ankor;
	}
		
	//-------------------------------------------- overriding GwtEvent
	@Override
	public Type<UpdateFulfillmentEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(UpdateFulfillmentEventHandler handler) {
		handler.onUpdateFulfillment(this);
	}

}
