package com.collectif.ft.croissants.client.service;

import java.util.List;
import java.util.logging.Logger;

import com.collectif.ft.croissants.client.event.EditAbsenceEvent;
import com.collectif.ft.croissants.client.event.EditAbsenceEventHandler;
import com.collectif.ft.croissants.client.event.EditUserEvent;
import com.collectif.ft.croissants.client.event.EditUserEventHandler;
import com.collectif.ft.croissants.client.event.MoveUserEvent;
import com.collectif.ft.croissants.client.event.MoveUserEventHandler;
import com.collectif.ft.croissants.client.event.ShiftUsersEvent;
import com.collectif.ft.croissants.client.event.ShiftUsersEventHandler;
import com.collectif.ft.croissants.client.event.ShowUserHistoryEvent;
import com.collectif.ft.croissants.client.event.ShowUserHistoryEventHandler;
import com.collectif.ft.croissants.client.event.UpdateTaskEvent;
import com.collectif.ft.croissants.client.event.UpdateTaskEventHandler;
import com.collectif.ft.croissants.client.event.UpdateUserEvent;
import com.collectif.ft.croissants.client.event.UpdateUserEventHandler;
import com.collectif.ft.croissants.client.util.DateUtils;
import com.collectif.ft.croissants.client.widget.task.TaskWidget;
import com.collectif.ft.croissants.shared.model.bean.IBean;
import com.collectif.ft.croissants.shared.model.bean.TaskBean;
import com.collectif.ft.croissants.shared.model.bean.UserBean;
import com.collectif.ft.croissants.shared.model.dto.TaskAndUserDto;
import com.collectif.ft.croissants.shared.model.dto.UserScoreDto;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

/**
 * Pilote la vue User and Task list
 * @author sylvie
 *
 */
public class UserAndTaskPresenter extends AbstractPresenter implements IPresenter {
	
	private final static Logger log = Logger.getLogger("UserAndTaskPresenter");
	

	private final ICroissantServiceAsync _rpcService;
	private final DisplayUserAndTaskList _view;
	private final HandlerManager _eventBus;
	
	private boolean _updateTaskRunning = false;
	
	private final IActionCallback _fetchAllActionCallback = new OkActionCallback() {
			
			@Override
			public void onOk() {
				fetchAll();
			}
		};

	
	//--------------------------------- constructor
	public UserAndTaskPresenter (ICroissantServiceAsync rpcService, HandlerManager eventBus, DisplayUserAndTaskList view) {
		this._rpcService = rpcService;
		this._eventBus = eventBus;
		this._view = view;
	}
	
	
	//---------------------------------- overriding IPresenter
	/**
	 * Attache la vue au container
	 * Récupère les datas auprès du service et les transmet  a la vue
	 */
	@Override
	public void go(HasWidgets container, PresenterCallback presenterCallback) {
		this.bind();
        container.clear();
        container.add(this._view.asWidget());
        this.fetchAll();
	}
	
	@Override
	public void refresh(IBean bean) {
		if (bean == null) {
			this.fetchAll();
		}
		else if (bean instanceof UserBean) {
		    this._view.refresh((UserBean)bean);	
		} else if (bean instanceof TaskBean) {
			 this._view.refresh((TaskBean)bean);	
		}
	}
	
	//------------------------------------- private methods
    private void fetchAll() {
    	fetchTaskBeans();
		fetchUserBeans(IBean.ID_UNDEFINED);
    }
	private void fetchTaskBeans() {

         this._rpcService.loadListOfTaskAndUserDto(DateUtils.offset,
        		 new ServiceCallback<List<TaskAndUserDto>>(this._eventBus) {

			@Override
			public void onSuccess(List<TaskAndUserDto> taskBeanList) {			
				_view.setTaskDatas(taskBeanList);
			}
		});
         
		
	}
	

	private void fetchUserBeans(final int userIdToEdit) {

		this._rpcService.loadListOfFreeUser(new ServiceCallback<List<UserBean>>(this._eventBus) {
			
			@Override
			public void onSuccess(List<UserBean> userBeanList) {
				_view.setUserDatas(userBeanList);
				if (userIdToEdit != IBean.ID_UNDEFINED) {
				  _view.editUserLogin(userIdToEdit);
				}
			}

		});
		
	}

	private void bind() {

		// ici on définit les ClickHandler de la vue
		
		
		// Add new User click handler
		final ClickHandler addUserClickHandler = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doAddUser();
			}
		};
		final List<HasClickHandlers> listAddUserButtons = this._view.getAddUserButtons();
		for (HasClickHandlers userButton : listAddUserButtons) {
			userButton.addClickHandler(addUserClickHandler);
		}
		
		// delete all users click handler
		this._view.getDeleteAllUsersButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				confirmDeleteAllUsers((Widget)_view.getDeleteAllUsersButton());
			}
		});
		
		// update User handler
		this._view.setUpdateUserEventHandler(new UpdateUserEventHandler() {
			
			@Override
			public void onUpdateUser(UpdateUserEvent event) {
				if (event == null || event.getUpdateActionType() == null || event.getUserBean() == null) {
					return ;
				}
				switch (event.getUpdateActionType()) {
				   case update:	doUpdateUserLogin(event.getUserBean(),  event.getAnkor());			
					break;

				}
			}
		});
		
		// show History handler
		this._view.setShowUserHistoryEventHandler(new ShowUserHistoryEventHandler() {
			
			@Override
			public void onShowUserHistory(ShowUserHistoryEvent event) {
				
				if (event == null || event.getScope() == null) {
					return;
				}
				
				switch (event.getScope()) {
				case hideScore:  doManageUserScore(event.getUserId(), false, event.getAnkor());
					break;

				case showScore: doManageUserScore(event.getUserId(), true, event.getAnkor());
				break;
				
				case showHistory: _eventBus.fireEvent(event);
				break;
			
				}
				
				
			}
		});
		
		// edit absence handler
		this._view.setEditAbsenceEventHandler(new EditAbsenceEventHandler() {
			
			@Override
			public void onEditAbsences(EditAbsenceEvent event) {
				_eventBus.fireEvent(event);
			}
		});
		
		
		// edit User handler
		this._view.setEditUserEventHandler(new EditUserEventHandler() {
			
			@Override
			public void onEditUser(EditUserEvent event) {
				_eventBus.fireEvent(event);
			}
		});
		
		// Add new Task click handler
		final ClickHandler addTaskClickHandler = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doAddTask();
			}
		};
		final List<HasClickHandlers> listAddTaskButtons = this._view.getAddTaskButtons();
		for (HasClickHandlers addTaskButton : listAddTaskButtons) {
			addTaskButton.addClickHandler(addTaskClickHandler);
		}
		
		// delete all task click handler
		this._view.getDeleteAllTasksButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				confirmDeleteAllTasks((Widget)_view.getDeleteAllTasksButton());
			}
		});
		
		// update Task handler
		this._view.setUpdateTaskEventHandler(new UpdateTaskEventHandler() {
			
			@Override
			public void onUpdateTask(UpdateTaskEvent event) {
				if (event == null || event.getUpdateActionType() == null || event.getTaskBean() == null) {
					return ;
				}
				switch (event.getUpdateActionType()) {
				   case update:	doUpdateTask(event.getTaskBean(), event.getTaskWidget());			
					break;
				   case delete:	confirmDeleteTask(event.getTaskBean(), event.getTaskWidget().getFormattedDate(),
						   event.getTaskWidget());		
					break;
				}
			}
		});
		

		
		// move user handler
		this._view.setMoveUserEventHandler(new MoveUserEventHandler() {
			
			@Override
			public void onMoveUser(MoveUserEvent event) {

				log.config("onMoveUser()");
               if (event == null || event.getMoveType() == null || event.getUserId() == IBean.ID_UNDEFINED) {
            	   return;
               }
               
               switch (event.getMoveType()) {
               	case toBox: doRemoveUserFromTask(event.getUserId(), event.getAnkor());
				   break;
				   
            	case toTask: doMoveUserToTask(event.getUserId(), event.getTaskId(), event.getAnkor());
 				   break;
			    }
				
			}
		});
		
		// shift users handler
		this._view.setShiftUsersEventHandler(new ShiftUsersEventHandler() {
			
			@Override
			public void onShiftUsers(ShiftUsersEvent event) {
				log.config("onShiftTasks()");
				if (event == null || event.getBeginTaskId() == IBean.ID_UNDEFINED) {
					return;
				}
				doShiftTask(event.getBeginTaskId(), event.isUp());
			}
		});
	}
	
	
	//-------------------------------------- private actions
   private void confirmDeleteAllUsers(Widget ankor) {
		
		IActionCallback actionCallback = new IActionCallback() {
			
			@Override
			public void onOk() {
				doDeleteAllUsers();
			}
			
			@Override
			public void onCancel() {
				// do nothing			
			}
		};
		
		String[] messages = new String[] {
				"Click OK to  delete all users",
				"",
				"Warning: all users history will be lost!"
		};
	    this.showConfirmDialog("Confirm delete all users", messages, ankor, actionCallback);
		

	}
	private void doDeleteAllUsers() {
		
		this._rpcService.deleteAllUsers(new ServiceCallback<Void>(_eventBus) {

			@Override
			public void onSuccess(Void result) {
				fetchAll();
			}
		});
	}
	 private void confirmDeleteAllTasks(Widget ankor) {
			
			IActionCallback actionCallback = new IActionCallback() {
				
				@Override
				public void onOk() {
					doDeleteAllTasks();
				}
				
				@Override
				public void onCancel() {
					// do nothing			
				}
			};
			
			String[] messages = new String[] {
					"Click OK to  delete all tasks '",
					"",
					"The users w'll become free and the history w'll be saved!"
			};
		    this.showConfirmDialog("Confirm delete all tasks", messages, ankor, actionCallback);
			

		}
	private void doDeleteAllTasks() {
		
		this._rpcService.deleteAllTasks(new ServiceCallback<Void>(_eventBus) {


			@Override
			public void onSuccess(Void result) {
				fetchAll();
			}
		});
	}
//	private void confirmDeleteUser(final UserBean userBean, final Widget ankor) {
//		
//		IActionCallback actionCallback = new IActionCallback() {
//			
//			@Override
//			public void onOk() {
//				doDeleteUser(userBean.getId(), ankor);
//			}
//			
//			@Override
//			public void onCancel() {
//				// do nothing			
//			}
//		};
//		
//		String[] messages = new String[] {
//				"Click OK to  delete user '" + userBean.getLogin() + "'",
//				"",
//				"Warning: all user history will be lost!"
//		};
//	    this.showConfirmDialog("Confirm delete user", messages, ankor, actionCallback);
//		
//
//	}
	
//	private void doDeleteUser(int userId,  final Widget ankor) {
//		
//		this._rpcService.deleteUser(userId, new ServiceCallback<Void>(this._eventBus, ankor, this._fetchAllActionCallback) {
//			
//			@Override
//			public void onSuccess(Void result) {
//				fetchAll();
//			}
//		});
//
//	}
	

	
	private void doRemoveUserFromTask(int userId, Widget ankor) {
		
		 this._rpcService.removeUserFromTask(userId, new ServiceCallback<Void>(_eventBus, ankor, this._fetchAllActionCallback) {

				@Override
				public void onSuccess(Void result) {
					 fetchAll();
				}
			});
	}
	private void doMoveUserToTask(int userId, int taskId, Widget ankor) {
		
		this._rpcService.moveUserToTask(userId, taskId, new ServiceCallback<Void>(_eventBus, ankor, this._fetchAllActionCallback) {


			@Override
			public void onSuccess(Void result) {
				 fetchAll();
			}
		   });
	}

   private void confirmDeleteTask(final TaskBean taskBean, String formattedDate, final  Widget ankor) {
		
		IActionCallback actionCallback = new IActionCallback() {
			
			@Override
			public void onOk() {
				doDeleteTask(taskBean.getId(), ankor);
			}
			
			@Override
			public void onCancel() {
				// do nothing			
			}
		};
		
		String[] messages = new String[] {
				"Click OK to  delete task '" + formattedDate + "'",
				"",
				"The users w'll become free and the history w'll be saved!"
		};
	    this.showConfirmDialog("Confirm delete task", messages, ankor, actionCallback);
		

	}
	private void doDeleteTask(int taskId, final Widget ankor) {

   
		 this._rpcService.deleteTask(taskId, new ServiceCallback<Void>(this._eventBus, ankor, this._fetchAllActionCallback) {

			 @Override
				public void onSuccess(Void result) {
					fetchAll();
				}	 
		 });
        
	}
	

	
	// mise a jour des informations d'un utilisateur
	private void doUpdateUserLogin (final UserBean userBean, final Widget ankor) {
		
		this._rpcService.updateUser(userBean, new ServiceCallback<UserBean>(_eventBus, ankor, this._fetchAllActionCallback) {

			@Override
			public void onSuccess(UserBean result) {
				refresh(result);
			}
		});
		
	}
	
	// déplace les utilisateurs d'une serie de tache vers le haut ou vers le bas
	private void doShiftTask(final int beginTaskId, final boolean up) {

         this._rpcService.shiftListUsers(beginTaskId, up, new ServiceCallback<Void>(_eventBus) {

			@Override
			public void onSuccess(Void result) {
				fetchTaskBeans();
			}
		});
	}
	
	// mise a  jour de la date d'une task
	private void doUpdateTask(final TaskBean taskBean, final TaskWidget taskWidget) {

		 if (taskBean == null) {
			 return;
		 }
		 if (this._updateTaskRunning) {
			 return;
		 }
		 
    	 
    	 final IActionCallback actionCallback =
    		  new OkActionCallback() {

					@Override
					public void onOk() {
						fetchTaskBeans();
						taskWidget.setDateInError(false);
						_updateTaskRunning = false;
					}					
				};
    	 
		 
		 this._updateTaskRunning = true;
         this._rpcService.updateTask(taskBean, new ServiceCallback<TaskBean>(this._eventBus, taskWidget, actionCallback) {
        	 
        	 @Override
        	public void onFailure(Throwable caught) {
        		 taskWidget.setDateInError(true);
        		 super.onFailure(caught);
        	 }
			@Override
			public void onSuccess(TaskBean result) {
					actionCallback.onOk();
			}
		});
	}
	
	// creation d'un nouvel utilisateur
	private void doAddUser() {

       this._rpcService.loadNewUser(new ServiceCallback<UserBean>(_eventBus) {

		@Override
		public void onSuccess(UserBean userBean) {
			fetchUserBeans(userBean.getId());
		}
	  });
	}
	
	// creation d'une task
	private void doAddTask() {
		
		this._rpcService.loadNewTask(DateUtils.offset,
				new ServiceCallback<TaskBean>(_eventBus) {

			@Override
			public void onSuccess(TaskBean taskBean) {
				fetchTaskBeans();
			}
		});
	}
	
	// show user score
	private void doManageUserScore(final int userId, final boolean show, final  Widget ankor) {
		
	   if (show) {
	     this._rpcService.loadUserScore(userId, new ServiceCallback<UserScoreDto>(_eventBus) {

		  @Override
		   public void onSuccess(UserScoreDto userScore) {
			 _view.showUserScore(userScore,   ankor);
		  }
	      });
	   } else {
		   _view.hideUserScore();
	   }
	}
	
//
//	private void doDeleteUser(int userId,  final Widget ankor) {
//		
//		this._rpcService.deleteUser(userId, new ServiceCallback<Void>(this._eventBus, ankor, this._fetchAllActionCallback) {
//			
//			@Override
//			public void onSuccess(Void result) {
//				fetchAll();
//			}
//		});
//
//	}

	//========================== INNER CLASS ===================
	
	public interface DisplayUserAndTaskList extends DisplayUserList, DisplayTaskList {
		public void refresh(UserBean userBean);
		public void refresh(TaskBean taskBean);
	}
	/**
	 * Interface de la vue que pilote le Presenter
	 * @author sylvie
	 *
	 */
	public interface DisplayUserList {
		
		// la vue est un composite 
		public Widget asWidget();
		public void setUserDatas (final List<UserBean> userBeanList);
				
		public List<HasClickHandlers> getAddUserButtons();
		public HasClickHandlers getDeleteAllUsersButton();
	
		public void setEditUserEventHandler(EditUserEventHandler handler);
		public void setUpdateUserEventHandler(UpdateUserEventHandler handler);
		public void setShowUserHistoryEventHandler(ShowUserHistoryEventHandler handler);
		public void setEditAbsenceEventHandler(EditAbsenceEventHandler handler);
		public void editUserLogin(int userId);
		public void showUserScore(UserScoreDto userScore, Widget ankor);
		public void hideUserScore();
		
		public void setMoveUserEventHandler(MoveUserEventHandler handler);
	}
	
	public interface DisplayTaskList {
		
		public Widget asWidget();
		public void setTaskDatas (final List<TaskAndUserDto> taskBeanList);
		public List<HasClickHandlers> getAddTaskButtons();
		public HasClickHandlers getDeleteAllTasksButton();
		
		public void setUpdateTaskEventHandler(UpdateTaskEventHandler handler);
		
		public void setMoveUserEventHandler(MoveUserEventHandler handler);
		
		public void setShiftUsersEventHandler(ShiftUsersEventHandler handler);

	
	}

}
