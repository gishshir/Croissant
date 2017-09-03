package com.collectif.ft.croissants.client.service;

import java.util.logging.Logger;

import com.collectif.ft.croissants.client.event.ErrorEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public abstract class ServiceCallback<T> implements AsyncCallback<T> {
	
	private final static Logger log = Logger.getLogger("ServiceCallback");
	
	private final HandlerManager _eventBus;
	private final Widget _ankor;
	private final IActionCallback _actionCallback;
	
	public ServiceCallback (HandlerManager eventBus, Widget ankor, IActionCallback actionCallback) {
		this._eventBus = eventBus;
		this._ankor = ankor;
		this._actionCallback = actionCallback;
	}

	public ServiceCallback (HandlerManager eventBus) {
		this(eventBus, null, null);
	}

	@Override
	public void onFailure(Throwable caught) {
		log.severe("onFailure: " + caught.getMessage());
		_eventBus.fireEvent(new ErrorEvent(caught.getMessage(), _actionCallback, _ankor));
	}

}
