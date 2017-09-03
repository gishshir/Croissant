package com.collectif.ft.croissants.client.widget.user;


public interface IDroppableUserWidgetListener {

	public boolean isDropEnabled();
	
	public void onDrop (DraggableUserWidget draggableUserWidget, int index);
}
