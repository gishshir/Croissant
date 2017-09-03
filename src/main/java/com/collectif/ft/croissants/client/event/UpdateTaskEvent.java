package com.collectif.ft.croissants.client.event;

import com.collectif.ft.croissants.client.widget.task.TaskWidget;
import com.collectif.ft.croissants.shared.model.bean.TaskBean;
import com.google.gwt.event.shared.GwtEvent;

public class UpdateTaskEvent extends GwtEvent<UpdateTaskEventHandler> {
	

	public final static Type<UpdateTaskEventHandler> TYPE = new Type<UpdateTaskEventHandler>();
	
	private final TaskBean _taskBean;
	private final UpdateActionType _updateActionType;
	private final TaskWidget _taskWidget;
	
	//-------------------------------------------- accessor
	public TaskBean getTaskBean() {
		return this._taskBean;
	}
	
	public TaskWidget getTaskWidget() {
		return this._taskWidget;
	}
	
	public UpdateActionType getUpdateActionType() {
		return this._updateActionType;
	}
	
	//-------------------------------------------- constructor
	public UpdateTaskEvent (final TaskBean taskBean, final TaskWidget taskWidget, final UpdateActionType updateActionType) {
		this._taskBean = taskBean;
		this._taskWidget = taskWidget;
		this._updateActionType = updateActionType;
	}

	//--------------------------------------------- overriding GwtEvent
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<UpdateTaskEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(UpdateTaskEventHandler handler) {
		handler.onUpdateTask(this);		
	}

}
