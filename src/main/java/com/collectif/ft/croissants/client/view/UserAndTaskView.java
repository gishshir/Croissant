package com.collectif.ft.croissants.client.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.collectif.ft.croissants.client.event.EditAbsenceEventHandler;
import com.collectif.ft.croissants.client.event.EditUserEventHandler;
import com.collectif.ft.croissants.client.event.MoveUserEventHandler;
import com.collectif.ft.croissants.client.event.ShiftUsersEventHandler;
import com.collectif.ft.croissants.client.event.ShowUserHistoryEventHandler;
import com.collectif.ft.croissants.client.event.UpdateTaskEventHandler;
import com.collectif.ft.croissants.client.event.UpdateUserEventHandler;
import com.collectif.ft.croissants.client.service.UserAndTaskPresenter.DisplayUserAndTaskList;
import com.collectif.ft.croissants.client.util.IConstants;
import com.collectif.ft.croissants.client.widget.task.TaskContainer;
import com.collectif.ft.croissants.client.widget.user.DraggableUserWidget;
import com.collectif.ft.croissants.client.widget.user.UserContainer;
import com.collectif.ft.croissants.client.widget.user.UserWidget;
import com.collectif.ft.croissants.shared.model.bean.TaskBean;
import com.collectif.ft.croissants.shared.model.bean.UserBean;
import com.collectif.ft.croissants.shared.model.dto.TaskAndUserDto;
import com.collectif.ft.croissants.shared.model.dto.UserScoreDto;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Display : interface from Presenter point of view
 * IUserAndTaskView : interface from Widget point of view
 * @author sylvie
 *
 */
public class UserAndTaskView extends Composite implements DisplayUserAndTaskList, IUserAndTaskView{
	
	private final FocusPanel main = new FocusPanel();

	private final UserContainer _userContainer = new UserContainer(this);
	private final TaskContainer _taskContainer = new TaskContainer(this);
	
	private UpdateUserEventHandler _updateUserEventHandler;
	private EditUserEventHandler _editUserEventHandler;
	private ShowUserHistoryEventHandler _showUserHistoryEventHandler;
	private EditAbsenceEventHandler _ediAbsenceEventHandler;
	
	private Map<Integer, UserWidget> _mapUserWidget = new HashMap<Integer, UserWidget>();
	
	//---------------------------------------- constructor
	public UserAndTaskView() {
		this.initWidget(this.buildMainPanel());
	}
	
	//---------------------------------------- overriding Display
	@Override
	public void setMoveUserEventHandler(MoveUserEventHandler handler) {
		this._userContainer.setMoveUserEventHandler(handler);
		this._taskContainer.setMoveUserEventHandler(handler);
	}

	
	//---------------------------------------- overriding IUserAndTaskView
	
	@Override
	public DraggableUserWidget createDraggableUserWidget(UserBean userBean) {
	 	final UserWidget userWidget = new UserWidget(this._updateUserEventHandler, 
	 			this._editUserEventHandler, this._showUserHistoryEventHandler,
	 			this._ediAbsenceEventHandler);
		userWidget.setDatas(userBean);
		this._mapUserWidget.put(userBean.getId(), userWidget);
		return new DraggableUserWidget(userWidget);
	}
	@Override
	public UserWidget getUserWidgetById(int userBeanId) {
		return this._mapUserWidget.get(userBeanId);
	}
	@Override
	public void cancelAllUserEditing() {
		for (UserWidget userWidget : this._mapUserWidget.values()) {
			userWidget.cancelEditLogin();
		}
	}
	@Override
	public void refresh(UserBean userBean) {
	   UserWidget userWidget = this.getUserWidgetById(userBean.getId());
	   if(userWidget != null) {
		   userWidget.setDatas(userBean);
	   }
	}
	@Override
	public void refresh(TaskBean taskBean) {
	  this._taskContainer.setTaskDatas(taskBean);
	}

	//----------------------------------------- overriding UserAndTaskPresenter.DisplayUserList
	@Override
	public void setUserDatas(List<UserBean> userBeanList) {
		this._userContainer.setUserDatas(userBeanList);
	}

	@Override
	public List<HasClickHandlers> getAddUserButtons() {
		return this._userContainer.getAddUserButtons();
	}

	@Override
	public void setUpdateUserEventHandler(UpdateUserEventHandler handler) {
		this._updateUserEventHandler = handler;

	}

	@Override
	public void setEditAbsenceEventHandler(EditAbsenceEventHandler handler) {
		this._ediAbsenceEventHandler = handler;
	}

	@Override
	public void editUserLogin(int userId) {
		this._userContainer.editUserLogin(userId);
	}
	@Override
	public HasClickHandlers getDeleteAllUsersButton() {
		return this._userContainer.getDeleteAllUsersButton();
	}
	
	@Override
	public void setEditUserEventHandler(EditUserEventHandler handler) {
		this._editUserEventHandler = handler;
	}
	
	@Override
	public void setShowUserHistoryEventHandler(
			ShowUserHistoryEventHandler handler) {
		this._showUserHistoryEventHandler = handler;		
	}


	@Override
	public void showUserScore(UserScoreDto userScore, Widget ankor) {

          this._userContainer.showUserScore(userScore, ankor);
	}
	@Override
	public void hideUserScore() {
		this._userContainer.hideUserScore();
	}


	
	//----------------------------------------- overriding UserAndTaskPresenter.DisplayTaskList
	@Override
	public List<HasClickHandlers> getAddTaskButtons() {
		return this._taskContainer.getAddTaskButtons();
	}
	
	@Override
	public void setTaskDatas(List<TaskAndUserDto> taskBeanList) {
		this._taskContainer.setTaskDatas(taskBeanList);
	}
	
	@Override
	public void setUpdateTaskEventHandler(UpdateTaskEventHandler handler) {
		this._taskContainer.setUpdateTaskEventHandler(handler);		
	}
	@Override
	public void setShiftUsersEventHandler(ShiftUsersEventHandler handler) {
		this._taskContainer.setShiftUsersEventHandler(handler);		
	}
	@Override
	public HasClickHandlers getDeleteAllTasksButton() {
		return this._taskContainer.getDeleteAllTasksButton();
	}
	//-------------------------------------- private methods
	private Panel buildMainPanel() {
		
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setSpacing(IConstants.MIN_SPACING);
		vPanel.getElement().setId(IConstants.STYLE_MAIN_VIEW);
				
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(IConstants.MIN_SPACING);
		hPanel.setWidth(IConstants.MAX_SIZE);
		hPanel.getElement().setId(IConstants.STYLE_MAIN_USER_TASK_VIEW);
		
		hPanel.add(this._userContainer);
		hPanel.add(this._taskContainer);
		
		vPanel.add(hPanel);
		
		this.main.add(vPanel);
		
		return main;
	}



}
