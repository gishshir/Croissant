package com.collectif.ft.croissants.client.event;

import com.collectif.ft.croissants.shared.model.bean.IBean;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

public class MoveUserEvent extends GwtEvent<MoveUserEventHandler> {
	

	public final static Type<MoveUserEventHandler> TYPE = new Type<MoveUserEventHandler>();
	
	private final int _userId;
	private final MoveToEnum _moveType;
	// only if MoveType.toTask
	private final int _taskId;
	
	private final Widget _ankor;

	
	//---------------------------------------- accessor
	public int getUserId() {
		return this._userId;
	}
	public int getTaskId() {
		return this._taskId;
	}
	public MoveToEnum getMoveType() {
		return this._moveType;
	}
	public Widget getAnkor() {
		return this._ankor;
	}
	
	//---------------------------------------- constructor
	public MoveUserEvent(final int userId, final MoveToEnum moveType, Widget ankor) {
		this(userId, moveType, IBean.ID_UNDEFINED, ankor);
	}
	public MoveUserEvent(final int userId, final MoveToEnum moveType, final int taskId, Widget ankor) {
		this._userId = userId;
		this._moveType = moveType;
		this._taskId = taskId;
		this._ankor = ankor;
	}
	
	public MoveUserEvent(final int userId, final MoveToEnum moveType, final int taskId) {
		this(userId, moveType, taskId, null);
	}
	
	//---------------------------------------- overriding GwtEvent
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<MoveUserEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MoveUserEventHandler handler) {
		handler.onMoveUser(this);
	}

}
