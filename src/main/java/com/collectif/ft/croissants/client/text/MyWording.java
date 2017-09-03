package com.collectif.ft.croissants.client.text;

import com.google.gwt.i18n.client.Constants;

public interface MyWording extends Constants {

	String buttonAddTask();
	String buttonAddTaskTitle();
	String buttonAddUser();
	String buttonAddUserTitle();
	String buttonDeleteAllUsers();
	String buttonDeleteAllUsersTitle();
	String buttonDeleteAllTasks();
	String buttonDeleteAllTasksTitle();
	
	String buttonSimulateDispatchUser();
	String buttonValidateDispatchUser();
	String buttonCancelDispatchUser();
	String buttonSimulateDispatchUserTitle();
	String buttonValidateDispatchUserTitle();
	String buttonCancelDispatchUserTitle();
	String labelDispatchFreeUserTitle();
	
	String infoEditUser();
	String infoScoreUser();
	String infoEditTask();
	String infoDeleteUser();
	String infoDeleteAbsence();
	String infoDeleteTask();
	String infoMoveUser();
	String infoEditFullfilment();
	
	String buttonGoToTaskView();
	String titleButtonGoToTaskView();
	String buttonGoToGlobalHistoryView();
	String titleButtonGoToGlobalHistoryView();
	String buttonGoToDispatchUserView();
	String titleButtonGoToDispatchUserView();
	String labelGlobalHistoryTitle();
	
	String infoTaskOkFulfilment();
	String infoTaskNokFulfilment();
	
	String infoUpShiftUsers();
	String infoDownShiftUsers();
	String infoAbsences();
	String infoParticipation();
	String infoLastRealizedTask();
	
	String radioFullfilled();
	String radioNotFullfilled();
	String titleModifyFulfillment();
	String infoUserNotFree();
	
	String labelLowParticipation();
	String labelHighParticipation();
}
