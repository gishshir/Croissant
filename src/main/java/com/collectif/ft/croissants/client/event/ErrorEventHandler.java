package com.collectif.ft.croissants.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface ErrorEventHandler extends EventHandler {

	public void onError( ErrorEvent event);
}
