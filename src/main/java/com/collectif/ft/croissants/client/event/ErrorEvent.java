package com.collectif.ft.croissants.client.event;

import com.collectif.ft.croissants.client.service.IActionCallback;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

public class ErrorEvent extends GwtEvent<ErrorEventHandler> {
	
	public final static Type<ErrorEventHandler> TYPE = new Type<ErrorEventHandler>();
	
	public enum ErrorType{stale_datas, others}
	
	private final String _errorMessage;
	private final IActionCallback _actionCallack;
	private final Widget _widget;
	private ErrorType _errorType = ErrorType.others;
	
	//------------------------------------------- accessor
	public String getErrorMessage() {
		return this._errorMessage;
	}
	public IActionCallback getActionCallback() {
		return this._actionCallack;
	}
	public Widget getWidget() {
		return this._widget;
	}
	public void setErrorType(ErrorType errorType) {
		this._errorType = errorType;
	}
	public ErrorType getErrorType() {
		return this._errorType;
	}
	//------------------------------------------- constructor
	public ErrorEvent(final String errorMessage) {
		this(errorMessage, null, null);
	}
	public ErrorEvent(final String errorMessage, final IActionCallback actionCallack, Widget widget) {
		this._errorMessage = errorMessage;
		this._actionCallack = actionCallack;
		this._widget = widget;
	}

	//------------------------------------------- overriding GwtEvent
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ErrorEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ErrorEventHandler handler) {
		handler.onError(this);		
	}

}
