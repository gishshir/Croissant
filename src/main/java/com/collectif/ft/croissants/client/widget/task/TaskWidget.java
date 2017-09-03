package com.collectif.ft.croissants.client.widget.task;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.mortbay.log.Log;

import com.collectif.ft.croissants.client.event.MoveToEnum;
import com.collectif.ft.croissants.client.event.MoveUserEvent;
import com.collectif.ft.croissants.client.event.MoveUserEventHandler;
import com.collectif.ft.croissants.client.event.ShiftUsersEvent;
import com.collectif.ft.croissants.client.event.ShiftUsersEventHandler;
import com.collectif.ft.croissants.client.event.UpdateActionType;
import com.collectif.ft.croissants.client.event.UpdateTaskEvent;
import com.collectif.ft.croissants.client.event.UpdateTaskEventHandler;
import com.collectif.ft.croissants.client.service.AppController;
import com.collectif.ft.croissants.client.text.MyWording;
import com.collectif.ft.croissants.client.util.DateUtils;
import com.collectif.ft.croissants.client.util.IConstants;
import com.collectif.ft.croissants.client.util.InputUtils;
import com.collectif.ft.croissants.client.widget.user.DraggableUserWidget;
import com.collectif.ft.croissants.client.widget.user.DroppableUserWidget;
import com.collectif.ft.croissants.client.widget.user.IDroppableUserWidgetListener;
import com.collectif.ft.croissants.client.widget.user.UserWidget;
import com.collectif.ft.croissants.shared.model.bean.TaskBean;
import com.collectif.ft.croissants.shared.model.bean.UserBean;
import com.collectif.ft.croissants.shared.model.dto.TaskAndUserDto;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;

public class TaskWidget extends Composite implements IDroppableUserWidgetListener {

	private final static Logger log = Logger.getLogger("TaskWidget");
	
	private final static MyWording myWording = AppController.getMyWording();
	
	private final DateBox _dateBox = new DateBox();
	private final DroppableUserWidget _droppableUserWidget1 = new DroppableUserWidget(1, this);
	private final DroppableUserWidget _droppableUserWidget2 = new DroppableUserWidget(2, this);
	
	private TaskAndUserDto _taskAndUserDto;
	private final UpdateTaskEventHandler _updateTaskEventHandler;
	private final MoveUserEventHandler _moveUserEventHandler;
	private final ShiftUsersEventHandler _shiftUsersEventHandler;
	
	private final Button _btDeleteTask = new Button();
	private final Button _btUpTaskShift = new Button();
	private final Button _btDownTaskShift = new Button();
	
	//-------------------------------------------- constructor
	public TaskWidget (final UpdateTaskEventHandler updateTaskEventHandler,
			final MoveUserEventHandler moveUserEventHandler,
			final ShiftUsersEventHandler shiftTasksEventHandler) {
		
		this._updateTaskEventHandler = updateTaskEventHandler;
		this._moveUserEventHandler = moveUserEventHandler;
		this._shiftUsersEventHandler = shiftTasksEventHandler;
		
		this.initComposants();
		this.initHandlers();
		this.initWidget(this.buildMainPanel());
	}
	
	//------------------------------------------- overriding IDroppableUserWidgetListener
	@Override
	public void onDrop(DraggableUserWidget draggableUserWidget, int index) {
		
		final UserWidget userWidget = draggableUserWidget.getOriginalWidget();
		this._moveUserEventHandler.onMoveUser(new MoveUserEvent(userWidget.getUserBean().getId(), 
				MoveToEnum.toTask, this._taskAndUserDto.getTaskBean().getId(), userWidget));
		
	}
	
	/**
	 * Drop impossible si taskDate in the past
	 */
	@Override
	public boolean isDropEnabled() {
		return  !this._taskAndUserDto.getTaskBean().isDateBeforeToday();
	}


	//------------------------------------------- public methods

	/**
	 * Populate widget with bean
	 * 	
	 */
	public void setDatas(TaskAndUserDto taskAndUserDto, List<DraggableUserWidget> listDraggableUserWidget) {
		this._taskAndUserDto = taskAndUserDto;		
		
		this.setDatas(taskAndUserDto.getTaskBean());
		
		if (listDraggableUserWidget != null) {
			if (listDraggableUserWidget.size() > 0) {
				this._droppableUserWidget1.setDraggableUserWidget(listDraggableUserWidget.get(0));
			}
			if (listDraggableUserWidget.size() > 1) {
				this._droppableUserWidget2.setDraggableUserWidget(listDraggableUserWidget.get(1));
			}
		}	
		
		// up and down
		this._btUpTaskShift.setVisible(taskAndUserDto.isUpShiftEnabled());
		this._btDownTaskShift.setVisible(taskAndUserDto.isDownShiftEnabled());

	}
	public void setDatas (TaskBean taskBean) {
		
		Date taskDate = taskBean.getDate();
		log.config("taskDate: " + taskBean.toString());
		this._dateBox.setValue(taskDate);
		
		boolean past = taskBean.isDateBeforeToday();
		this._dateBox.setEnabled(!past);
		this._dateBox.addStyleName((past)?IConstants.STYLE_TASK_DATEBOX_PAST:IConstants.STYLE_TASK_DATEBOX_NOPAST);
        this._dateBox.removeStyleName((past)?IConstants.STYLE_TASK_DATEBOX_NOPAST:IConstants.STYLE_TASK_DATEBOX_PAST);
		
	}
	
	public void setDateInError (boolean error) {
		        
        if (error) {
        	this._dateBox.addStyleName(IConstants.STYLE_TASK_DATEBOX_ERROR);
        } else {
        	this._dateBox.removeStyleName(IConstants.STYLE_TASK_DATEBOX_ERROR);
        }
	}
	public String getFormattedDate() {
		return this._dateBox.getTextBox().getText();
	}
	//-------------------------------------- package methods
	void updateTaskBeanAfterDrop (UserBean userBean) {
		if (this._taskAndUserDto != null ){
				this._taskAndUserDto.addUserBean(userBean);	
		}
	}
	//-------------------------------------- private methods
	private Panel buildMainPanel() {
		
		// button
		VerticalPanel buttonPanel = new VerticalPanel();
		buttonPanel.add(this._btDeleteTask);
		buttonPanel.add(this._btUpTaskShift);
		buttonPanel.add(this._btDownTaskShift);
		
		final HorizontalPanel globalTaskPanel = new HorizontalPanel();
		globalTaskPanel.addStyleName(IConstants.STYLE_TASK_GLOBAL_WIDGET);
		globalTaskPanel.add(buttonPanel);
		
		HorizontalPanel mainTaskPanel = new HorizontalPanel();
		mainTaskPanel.setSpacing(IConstants.MIN_SPACING);
		mainTaskPanel.addStyleName(IConstants.STYLE_TASK_WIDGET);
		mainTaskPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		
		mainTaskPanel.add(this._dateBox);
		mainTaskPanel.add(this._droppableUserWidget1);
		mainTaskPanel.add(this._droppableUserWidget2);
		
		globalTaskPanel.add(mainTaskPanel);
		
		return globalTaskPanel;
	}
	
	private void initComposants () {	
		this._dateBox.setFormat(new DateBox.DefaultFormat(DateUtils.dateTimeFormat));	
		this._dateBox.setTitle(myWording.infoEditTask());
		this._dateBox.addStyleName(IConstants.STYLE_TASK_DATEBOX);
		
		this._btDeleteTask.addStyleName(IConstants.STYLE_IMG_DELETE_USER);
		this._btDeleteTask.setTitle(myWording.infoDeleteTask());
		
		this._btUpTaskShift.addStyleName(IConstants.STYLE_IMG_UP);
		this._btUpTaskShift.setTitle(myWording.infoUpShiftUsers());
		
		this._btDownTaskShift.addStyleName(IConstants.STYLE_IMG_DOWN);
		this._btDownTaskShift.setTitle(myWording.infoDownShiftUsers());
	}
	
	private void initHandlers() {
		
		// change the date
		this._dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				
				if (DateUtils.isSameDay(_taskAndUserDto.getTaskBean().getDate(), event.getValue())) {
					return;
				}
				//memorise date and offset
				_taskAndUserDto.getTaskBean().setDate(event.getValue(), DateUtils.offset);
				
				_updateTaskEventHandler.onUpdateTask(new UpdateTaskEvent(_taskAndUserDto.getTaskBean(), TaskWidget.this,  UpdateActionType.update));
			}
		});
		
		// delete the task
		this._btDeleteTask.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				_updateTaskEventHandler.onUpdateTask(new UpdateTaskEvent(_taskAndUserDto.getTaskBean(), TaskWidget.this,  UpdateActionType.delete));
			}
		});
		
		// move tasks up
		this._btUpTaskShift.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				_shiftUsersEventHandler.onShiftUsers(new ShiftUsersEvent(_taskAndUserDto.getTaskBean().getId(), true));
			}
		});
		
		// move tasks down
		this._btDownTaskShift.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				_shiftUsersEventHandler.onShiftUsers(new ShiftUsersEvent(_taskAndUserDto.getTaskBean().getId(), false));
			}
		});
	}


	
}
