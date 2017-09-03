package com.collectif.ft.croissants.client.widget.user;

import gwtquery.plugins.draggable.client.DraggableOptions.RevertOption;
import gwtquery.plugins.draggable.client.events.DragStartEvent;
import gwtquery.plugins.draggable.client.events.DragStopEvent;
import gwtquery.plugins.draggable.client.events.DragStopEvent.DragStopEventHandler;
import gwtquery.plugins.draggable.client.gwt.DraggableWidget;

import java.util.logging.Logger;

import com.collectif.ft.croissants.client.util.IConstants;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;

public class DraggableUserWidget extends DraggableWidget<UserWidget> implements IDraggableUserWidget{
	
	private final static Logger log = Logger.getLogger("DraggableUserWidget");

	
	public DraggableUserWidget(UserWidget userWidget) {
		userWidget.setDraggableUserWidget(this);
		this.configureDragBehavior();
		this.configureDragHandlers();
		this.initWidget(userWidget);
	}
	
	

	//----------------------------------------------- overriding IDraggableUserWidget

	@Override
	public void activeDraggable(boolean activeDraggable) {
		this.setDisabledDrag(!activeDraggable);
	}
	//----------------------------------------------- private methods
	private void configureDragBehavior () {
		//configure the drag behavior (see next paragraph)
		this.setDraggingCursor(Cursor.MOVE);
		this.setDraggingZIndex(200);
		this.setDraggingOpacity((float)0.8);
	    //revert the dragging display on its original position is not drop occured
		this.setRevert(RevertOption.ON_INVALID_DROP);
		this.setHandle(IConstants.STYLE_USER_WIDGET);
		this.setContainment(IConstants.STYLE_BOUNDARY);
		//this.useCloneAsHelper();

	}
	
	private void configureDragHandlers () {

        this.addDragStartHandler(new DragStartEvent.DragStartEventHandler() {
			
			@Override
			public void onDragStart(DragStartEvent event) {
				log.config("onDragStart");
				event.getDraggableWidget().addStyleName(IConstants.STYLE_DRAGGING);
			}
		});
        
        this.addDragStopHandler(new DragStopEventHandler() {
			
			@Override
			public void onDragStop(DragStopEvent event) {
				// since we use original widged, reinit position
				event.getDraggableWidget().getElement().getStyle().setTop(0, Unit.PX);
				event.getDraggableWidget().getElement().getStyle().setLeft(0, Unit.PX);
				event.getDraggableWidget().removeStyleName(IConstants.STYLE_DRAGGING);
			}
		});
	}



}
