package com.collectif.ft.croissants.client.event;

import com.collectif.ft.croissants.client.view.ViewToDisplayEnum;
import com.google.gwt.event.shared.GwtEvent;

public class ChangeViewEvent extends GwtEvent<ChangeViewEventHandler> {
	
	public final static Type<ChangeViewEventHandler> TYPE = new Type<ChangeViewEventHandler>();
	
	private ViewToDisplayEnum _viewToDisplay;
	
	//------------------------------------------ accessors
	public ViewToDisplayEnum getViewToDisplay() {
		return this._viewToDisplay;
	}
	
	//------------------------------------------- constructor
	public ChangeViewEvent(ViewToDisplayEnum viewToDisplay) {
		this._viewToDisplay = viewToDisplay;
	}
	
	//------------------------------------------- overriding GwtEvent
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ChangeViewEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ChangeViewEventHandler handler) {
		handler.onChangeView(this);
	}

}
