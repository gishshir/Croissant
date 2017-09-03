package com.collectif.ft.croissants.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;


public class ShowUserHistoryEvent extends GwtEvent<ShowUserHistoryEventHandler> {

	public final static Type<ShowUserHistoryEventHandler> TYPE = new Type<ShowUserHistoryEventHandler>();
	
	public enum Scope {showScore, hideScore, showHistory}
	
	private final int _userId;
	private final Widget _ankor;
	private final Scope _scope;
	
	//-------------------------------------------- accessors
	public int getUserId() {
		return this._userId;
	}
	public Widget getAnkor() {
		return this._ankor;
	}
	public Scope getScope() {
		return this._scope;
	}
	//--------------------------------------------- constructor
	public ShowUserHistoryEvent(final int userId, Scope scope) {
		this(userId, scope, null);
	}
	public ShowUserHistoryEvent(final int userId, Scope scope, Widget ankor) {
		this._userId = userId;
		this._ankor = ankor;
		this._scope = scope;
	}
	

	//-------------------------------------------- overriding GwtEvent
	
	@Override
	public GwtEvent.Type<ShowUserHistoryEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowUserHistoryEventHandler handler) {
		handler.onShowUserHistory(this);		
	}

}
