package com.collectif.ft.croissants.client.widget.user;

import gwtquery.plugins.draggable.client.gwt.DraggableWidget;
import gwtquery.plugins.droppable.client.DroppableOptions.AcceptFunction;
import gwtquery.plugins.droppable.client.DroppableOptions.DroppableTolerance;
import gwtquery.plugins.droppable.client.events.DragAndDropContext;
import gwtquery.plugins.droppable.client.events.DropEvent;
import gwtquery.plugins.droppable.client.gwt.DroppableWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.collectif.ft.croissants.client.event.EditAbsenceEventHandler;
import com.collectif.ft.croissants.client.event.EditUserEventHandler;
import com.collectif.ft.croissants.client.event.MoveToEnum;
import com.collectif.ft.croissants.client.event.MoveUserEvent;
import com.collectif.ft.croissants.client.event.MoveUserEventHandler;
import com.collectif.ft.croissants.client.event.ShowUserHistoryEventHandler;
import com.collectif.ft.croissants.client.event.UpdateUserEventHandler;
import com.collectif.ft.croissants.client.service.AppController;
import com.collectif.ft.croissants.client.service.UserAndTaskPresenter.DisplayUserList;
import com.collectif.ft.croissants.client.text.MyWording;
import com.collectif.ft.croissants.client.util.IConstants;
import com.collectif.ft.croissants.client.view.IUserAndTaskView;
import com.collectif.ft.croissants.client.widget.common.MyButton;
import com.collectif.ft.croissants.client.widget.common.SimpleContainer;
import com.collectif.ft.croissants.shared.model.bean.UserBean;
import com.collectif.ft.croissants.shared.model.dto.UserScoreDto;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class UserContainer extends SimpleContainer implements DisplayUserList {
	
	private final static Logger log = Logger.getLogger("UserContainer");	
	
	private final static MyWording myWording = AppController.getMyWording();
	
	private final DroppableUserWidgetPanel _userWidgetListPanel = new DroppableUserWidgetPanel();
	
	private final MyButton _btDeleteAllUsers = new MyButton(myWording.buttonDeleteAllUsers());
	private final MyButton _btAddUser = new MyButton(myWording.buttonAddUser());
	
	private UserScorePopup _popupUserScore;

    private MoveUserEventHandler _moveUserEventHandler;
	
	//------------------------------------------- constructor
	public UserContainer(final IUserAndTaskView mainView) {
       
		super(mainView);
		this.initComposants();
		this.add(this.buildMainPanel());	
	}

	//--------------------------------------- overriding DisplayUserList

	@Override
	public HasClickHandlers getDeleteAllUsersButton() {
		return this._btDeleteAllUsers;
	}

	/**
	 * Populate the container with a list of UserBean
	 * @param userBeanList
	 */
	@Override
	public void setUserDatas(List<UserBean> userBeanList) {
		this.clean();
		
		if (userBeanList == null || userBeanList.isEmpty()) {
			return;
		}
		
		for (UserBean userBean : userBeanList) {
			this._userWidgetListPanel.addWidget(this._mainView.createDraggableUserWidget(userBean));
		}
	}

   @Override
   public List<HasClickHandlers> getAddUserButtons() {
		
	   final List<HasClickHandlers> listAddUserButtons = new ArrayList<HasClickHandlers>(2);
	   listAddUserButtons.add(this._btAddUser);
	   return listAddUserButtons;
	}
   @Override
	public void editUserLogin(int userId) {
		
		UserWidget userWidget = this._mainView.getUserWidgetById(userId);
		if (userWidget != null) {
			userWidget.editLogin();
		}
	}
   @Override
	public void setUpdateUserEventHandler(UpdateUserEventHandler handler) {
		//NA
	}

	@Override
	public void setMoveUserEventHandler(MoveUserEventHandler handler) {
		this._moveUserEventHandler = handler;
	}

	@Override
	public void setEditUserEventHandler(EditUserEventHandler handler) {
		// NA
	}
	
	

	@Override
	public void setEditAbsenceEventHandler(EditAbsenceEventHandler handler) {
		// NA		
	}

	@Override
	public void setShowUserHistoryEventHandler(
			ShowUserHistoryEventHandler handler) {
		// NA	
	}

	@Override
	public void showUserScore(UserScoreDto userScore, Widget ankor) {
		
		this.hideUserScore();
		
        this._popupUserScore = new UserScorePopup(userScore);
        this._popupUserScore.showRelativeTo(ankor);
	}
	
	@Override
	public void hideUserScore() {
		if (this._popupUserScore != null && this._popupUserScore.isShowing()) {
			this._popupUserScore.hide();
		}
	}


	//------------------------------------------ private methods

	private Widget buildMainPanel() {
		
		final VerticalPanel vpanel = new VerticalPanel();
		vpanel.setSpacing(IConstants.MIN_SPACING);
		
		// barre de boutons
		final HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setSpacing(IConstants.MIN_SPACING);
		buttonPanel.add(this._btDeleteAllUsers);
		buttonPanel.add(this._btAddUser);
		vpanel.add(buttonPanel);
		
		// user panel
		final Panel mainUserPanel = new FlowPanel();
		this.initContainer(mainUserPanel);
	
		vpanel.add(mainUserPanel);
		
		return vpanel;
	}

     private void clean() {
			this._userWidgetListPanel.clean();
	}
	private void initContainer(final Panel panel) {
		
		panel.getElement().setId(IConstants.STYLE_USER_CONTAINER);
		
		this._userWidgetListPanel.setStyleName(IConstants.STYLE_USERWIDGET_LIST);
		panel.add(this._userWidgetListPanel);

	}
	
	private void initComposants() {
		this._btAddUser.setTitle(myWording.buttonAddUserTitle());
		this._btDeleteAllUsers.setTitle(myWording.buttonDeleteAllUsersTitle());
		this._btDeleteAllUsers.setEnabled(true);
	}

	 //============================= INNER CLASS =================================
    private class DroppableUserWidgetPanel extends DroppableWidget<FlowPanel> {
    
    	private final FlowPanel _panel = new FlowPanel();
    	
    	private DroppableUserWidgetPanel() {
    		this.configureDroppableBehaviour();
			this.initWidget(this._panel);
		}
    	
    	
    	private void clean() {
    		this._panel.clear();
    	}
    	
    	private void addWidget(Widget widget) {
    		this._panel.add(widget);
    	}
    	
    	private void dropAction(DraggableWidget<UserWidget> draggableUserWidget) {			
			_panel.add(draggableUserWidget);
			final UserWidget userWidget = draggableUserWidget.getOriginalWidget();
			UserContainer.this._moveUserEventHandler.onMoveUser(
					new MoveUserEvent(userWidget.getUserBean().getId(), MoveToEnum.toBox, userWidget));
    	}
    	
    	private void configureDroppableBehaviour () {
    			
    			this.setTolerance(DroppableTolerance.POINTER);
    			this.setActiveClass(IConstants.STYLE_DROPPABLE);
    			this.setDroppableHoverClass(IConstants.STYLE_DROPPABLE_OVER);
    			this.addDropHandler(new DropEvent.DropEventHandler() {

    				@Override
    				public void onDrop(DropEvent event) {
    					log.config("onDrop() in panel");	
    					// casting is safe because onAccept before
    					@SuppressWarnings("unchecked")
    					DraggableWidget<UserWidget> draggableUserWidget = (DraggableWidget<UserWidget>)event.getDraggableWidget();
    					dropAction(draggableUserWidget);

    				}		
    			});
    			
    			this.setAccept(new AcceptFunction() {
    				
    				@Override
    				public boolean acceptDrop(DragAndDropContext context) {
    					    					
    					DraggableWidget<?> draggableWidget = context.getDraggableWidget();
    					if (draggableWidget.getParent() == _panel) {
    						return false;
    					}
    					
    			        //accept only UserWidget
    			        boolean accept =  draggableWidget.getOriginalWidget() instanceof UserWidget;
    			        log.config("accepted: " + accept);
    			        return accept;

    				}
    			});
    		}
    }




}
