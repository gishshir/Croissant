package com.collectif.ft.croissants.client.widget.user;

import gwtquery.plugins.draggable.client.gwt.DraggableWidget;
import gwtquery.plugins.droppable.client.DroppableOptions.AcceptFunction;
import gwtquery.plugins.droppable.client.DroppableOptions.DroppableTolerance;
import gwtquery.plugins.droppable.client.events.DragAndDropContext;
import gwtquery.plugins.droppable.client.events.DropEvent;
import gwtquery.plugins.droppable.client.gwt.DroppableWidget;

import com.collectif.ft.croissants.client.util.IConstants;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class DroppableUserWidget extends DroppableWidget<SimplePanel> {
	

    private final CaseUserWidget _caseUserWidget = new CaseUserWidget();
    private final IDroppableUserWidgetListener _listener;
    private final int _index;
	
	public DroppableUserWidget(int index, IDroppableUserWidgetListener listener) {
		this._index = index;
		this._listener = listener;
		this.configureDroppableBehaviour();
		this.initWidget(this._caseUserWidget);
	}
	
	public void setDraggableUserWidget (DraggableUserWidget draggableUserWidget) {
		
		this._caseUserWidget.setDraggableUserWidget(draggableUserWidget);
	}
	
	//------------------------------------------------ private methods
	private void dropAction(DraggableUserWidget draggableUserWidget) {
		
		this.setDraggableUserWidget(draggableUserWidget);
		this._listener.onDrop(draggableUserWidget, _index);
	}
    private void configureDroppableBehaviour () {
		
		this.setTolerance(DroppableTolerance.POINTER);
		this.setActiveClass(IConstants.STYLE_DROPPABLE);
		this.setDroppableHoverClass(IConstants.STYLE_DROPPABLE_OVER);
		
		this.addDropHandler(new DropEvent.DropEventHandler() {

			@Override
			public void onDrop(DropEvent event) {
				
				// casting is safe because onAccept before
				DraggableUserWidget draggableUserWidget = (DraggableUserWidget)event.getDraggableWidget();
				dropAction(draggableUserWidget);
							}		
		});
		
		this.setAccept(new AcceptFunction() {
			
			@Override
			public boolean acceptDrop(DragAndDropContext context) {
				
				if (!_caseUserWidget.isEmpty()) {
					return false;
				}
				if (!_listener.isDropEnabled()) {
					return false;
				}
				
				DraggableWidget<?> draggabelWidget = context.getDraggableWidget();
		        //accept only UserWidget
		        boolean accept =  draggabelWidget.getOriginalWidget() instanceof UserWidget;
		        return accept;

			}
		});
	}
  //============================================ INNER CLASS ===========
    private class CaseUserWidget extends SimplePanel  {
    	
    	private UserWidget _userWidget = null;

    	public CaseUserWidget () {
    		this.addStyleName(IConstants.STYLE_CASE_USER_WIDGET);

    	}
    	
    	@Override
    	public boolean remove(Widget w) {
    		 this._userWidget = null;
    		return super.remove(w);
    	}
    	
    	public boolean isEmpty() {
    		return this._userWidget == null;
    	}
    	
    	public void setDraggableUserWidget(DraggableWidget<UserWidget> draggableUserWidget) {
    		this.setWidget(draggableUserWidget);
    		this._userWidget = draggableUserWidget.getOriginalWidget();
    		
    		// not draggable if task in past
    		if (!_listener.isDropEnabled()) {
    			draggableUserWidget.setDisabledDrag(true);
    		}
    	}
    }

}


