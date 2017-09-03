package com.collectif.ft.croissants.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

/**
 * Event pour l'affichage de la popup de gestion des absences
 * @author sylvie
 *
 */
public class EditAbsenceEvent extends GwtEvent<EditAbsenceEventHandler> {
	
	public final static Type<EditAbsenceEventHandler> TYPE = new Type<EditAbsenceEventHandler>();

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
	public EditAbsenceEvent(final int userId, Widget ankor) {
		this._userId = userId;
		this._ankor = ankor;
	}
	
	//------------------------------------- overriding GwtEvent
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<EditAbsenceEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(EditAbsenceEventHandler handler) {
		handler.onEditAbsences(this);		
	}

}
