package com.collectif.ft.croissants.client.event;

import com.collectif.ft.croissants.client.widget.absence.AbsenceUserWidget;
import com.collectif.ft.croissants.shared.model.bean.AbsenceBean;
import com.google.gwt.event.shared.GwtEvent;

public class UpdateAbsenceEvent extends GwtEvent<UpdateAbsenceEventHandler> {
	
	public final static Type<UpdateAbsenceEventHandler> TYPE = new Type<UpdateAbsenceEventHandler>();
	
	private final AbsenceBean _absenceBean;
	private final UpdateActionType _updateActionType;
	private final AbsenceUserWidget _absenceUserWidget;
	
	//------------------------------------------ accessor
	public AbsenceBean getAbsence () {
		return this._absenceBean;
	}
	public UpdateActionType getUpdateActionType() {
		return this._updateActionType;
	}
	public AbsenceUserWidget getAbsenceUserWidget() {
		return this._absenceUserWidget;
	}
	//------------------------------------------ constructor
	public UpdateAbsenceEvent (AbsenceBean absenceBean,   AbsenceUserWidget absenceUserWidget,  UpdateActionType updateActionType) {
		this._absenceBean = absenceBean;
		this._absenceUserWidget = absenceUserWidget;
		this._updateActionType = updateActionType;
	}

	//----------------------------------------- overriding GwtEvent
	@Override
	public Type<UpdateAbsenceEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(UpdateAbsenceEventHandler handler) {
		handler.onUpdateAbsence(this);		
	}


}
