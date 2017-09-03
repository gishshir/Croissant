package com.collectif.ft.croissants.client.widget.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.collectif.ft.croissants.client.event.MoveUserEventHandler;
import com.collectif.ft.croissants.client.event.ShiftUsersEventHandler;
import com.collectif.ft.croissants.client.event.UpdateTaskEventHandler;
import com.collectif.ft.croissants.client.service.AppController;
import com.collectif.ft.croissants.client.service.UserAndTaskPresenter.DisplayTaskList;
import com.collectif.ft.croissants.client.text.MyWording;
import com.collectif.ft.croissants.client.util.IConstants;
import com.collectif.ft.croissants.client.view.IUserAndTaskView;
import com.collectif.ft.croissants.client.widget.common.MyButton;
import com.collectif.ft.croissants.client.widget.common.SimpleContainer;
import com.collectif.ft.croissants.client.widget.user.DraggableUserWidget;
import com.collectif.ft.croissants.shared.model.bean.TaskBean;
import com.collectif.ft.croissants.shared.model.bean.UserBean;
import com.collectif.ft.croissants.shared.model.dto.TaskAndUserDto;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TaskContainer extends SimpleContainer implements DisplayTaskList{
	
	private final static Logger log = Logger.getLogger("TaskContainer");
	
	private final static MyWording myWording = AppController.getMyWording();
	
	private final VerticalPanel _taskWidgetTable = new VerticalPanel();
	
	private Map<Integer, TaskWidget> _mapTaskWidget = new HashMap<Integer, TaskWidget>();
	
	private UpdateTaskEventHandler _updateTaskEventHandler;
	private MoveUserEventHandler _moveUserEventHandler;
	private ShiftUsersEventHandler _shiftUsersEventHandler;
	
	private final MyButton _btDeleteAllTasks = new MyButton(myWording.buttonDeleteAllTasks());
	private final MyButton _btAddTask = new MyButton(myWording.buttonAddTask());
	
	//------------------------------------- constructor
	public TaskContainer(final IUserAndTaskView mainView) {
		super(mainView);
		this.initComposants();
		this.add(this.buildMainPanel());
	}

	//-------------------------------------- public methods
	public void setTaskDatas(TaskBean taskBean) {
		TaskWidget taskWidget = this._mapTaskWidget.get(taskBean.getId());
		if (taskWidget != null) {
			taskWidget.setDatas(taskBean);
		}
	}
	
	//-------------------------------------- overriding DisplayTask

	@Override
	public void setTaskDatas(List<TaskAndUserDto> taskBeanList) {
		this.clean();
		
		if (taskBeanList == null || taskBeanList.isEmpty()) {
			return;
		}
		
		for (TaskAndUserDto taskAndUserDto : taskBeanList) {
			this.createTaskWidget(taskAndUserDto);
		}
	}
    @Override
    public List<HasClickHandlers> getAddTaskButtons(){
    	final List<HasClickHandlers> listAddUserButtons = new ArrayList<HasClickHandlers>(2);
 	   listAddUserButtons.add(this._btAddTask);
 	   return listAddUserButtons;
	}
	@Override
	public void setUpdateTaskEventHandler(UpdateTaskEventHandler handler) {
		this._updateTaskEventHandler = handler;
		
	}
	@Override
	public void setMoveUserEventHandler(MoveUserEventHandler handler) {
		this._moveUserEventHandler = handler;
	}

	@Override
	public HasClickHandlers getDeleteAllTasksButton() {
		return this._btDeleteAllTasks;
	}
	

	@Override
	public void setShiftUsersEventHandler(ShiftUsersEventHandler handler) {
		this._shiftUsersEventHandler = handler;
	}



	//-------------------------------------- private methods
	public void clean() {
		this._taskWidgetTable.clear();
		this._mapTaskWidget.clear();
	}

	
	private void initComposants() {
		this._btAddTask.setTitle(myWording.buttonAddTaskTitle());
		this._btDeleteAllTasks.setTitle(myWording.buttonDeleteAllTasksTitle());
		this._btDeleteAllTasks.setEnabled(true);
	}
	private Panel buildMainPanel() {
		

		
		final VerticalPanel vpanel = new VerticalPanel();
		vpanel.setSpacing(IConstants.MIN_SPACING);
		
		// barre de boutons
		final HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setSpacing(IConstants.MIN_SPACING);
		buttonPanel.add(this._btAddTask);
		buttonPanel.add(this._btDeleteAllTasks);
		
		vpanel.add(buttonPanel);

		// init Task main panel
		final Panel mainTaskPanel = new FlowPanel();
		this.initContainer(mainTaskPanel);
			
		vpanel.add(mainTaskPanel);
		
		return vpanel;
	}
	
	private void initContainer(final Panel panel) {
		
		panel.getElement().setId(IConstants.STYLE_TASK_CONTAINER);
		
		// init task widget liste
		this._taskWidgetTable.getElement().setId(IConstants.STYLE_TASK_WIDGET_LIST);
		this._taskWidgetTable.setSpacing(IConstants.MIN_SPACING);
		panel.add(this._taskWidgetTable);
	}
	
	 private void createTaskWidget(TaskAndUserDto taskAndUserDto) {
		 log.config("createTaskWidget(): " + taskAndUserDto.getTaskBean().getDate());
		 final TaskWidget taskWidget = 
				 new TaskWidget(this._updateTaskEventHandler,
				           this._moveUserEventHandler,
				               this._shiftUsersEventHandler);
		 this._mapTaskWidget.put(taskAndUserDto.getTaskBean().getId(), taskWidget);
		 
		 List<DraggableUserWidget> listDraggableUserWidget = null;
		 final List<UserBean> listUserBeans = taskAndUserDto.getListUserBeans();
         if (listUserBeans != null) {
        	 
        	 listDraggableUserWidget = new ArrayList<DraggableUserWidget>(listUserBeans.size());
        	 for (UserBean userBean : listUserBeans) {
				
				 final DraggableUserWidget draggableUserWidget 
				 = (userBean != null)? this._mainView.createDraggableUserWidget(userBean):null;
				 listDraggableUserWidget.add(draggableUserWidget);
				 
				
			}
         }
    
		 taskWidget.setDatas(taskAndUserDto, listDraggableUserWidget);
		
		 _taskWidgetTable.add(taskWidget);
	 }


}
