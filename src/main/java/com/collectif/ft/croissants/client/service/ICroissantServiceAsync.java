package com.collectif.ft.croissants.client.service;

import java.util.Date;
import java.util.List;

import com.collectif.ft.croissants.shared.model.bean.AbsenceBean;
import com.collectif.ft.croissants.shared.model.bean.TaskBean;
import com.collectif.ft.croissants.shared.model.bean.UserBean;
import com.collectif.ft.croissants.shared.model.dto.HistoryAndUserDto;
import com.collectif.ft.croissants.shared.model.dto.TaskAndUserDto;
import com.collectif.ft.croissants.shared.model.dto.UserAndAbsencesDto;
import com.collectif.ft.croissants.shared.model.dto.UserAndAlertDto;
import com.collectif.ft.croissants.shared.model.dto.UserAndScoreDto;
import com.collectif.ft.croissants.shared.model.dto.UserHistoryDto;
import com.collectif.ft.croissants.shared.model.dto.UserScoreDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ICroissantServiceAsync {

	void loadListOfFreeUser(AsyncCallback<List<UserBean>> callback);

	void loadNewUser(AsyncCallback<UserBean> callback);

	void updateUser(UserBean userBean, AsyncCallback<UserBean> callback);

	void deleteUser(int userId, AsyncCallback<Void> callback);
	
	void deleteAllUsers(AsyncCallback<Void> callback);

	void loadListOfTaskAndUserDto(int clientOffset, AsyncCallback<List<TaskAndUserDto>> callback);
	
	void loadNewTask(int clientOffset, AsyncCallback<TaskBean> callback);

	void updateTask(TaskBean taskBean, AsyncCallback<TaskBean> callback);

	void deleteTask(int taskId, AsyncCallback<Void> callback);

	void moveUserToTask(int userId, int taskId, AsyncCallback<Void> callback);

	void removeUserFromTask(int userId, AsyncCallback<Void> callback);

	void deleteAllTasks(AsyncCallback<Void> callback);

	void loadUserAndAlert(int userId, AsyncCallback<UserAndAlertDto> callback);

	void updateUserAndAlerts(UserAndAlertDto userAndAlertsBean,
			AsyncCallback<UserAndAlertDto> callback);

	void getListSmileyUrls(AsyncCallback<List<String>> callback);

	void loadListHistoryAndUserDto(int page, AsyncCallback<List<HistoryAndUserDto>> callback);

	void shiftListUsers(int beginTaskId, boolean up, AsyncCallback<Void> callback);

	void loadUserScore(int userId, AsyncCallback<UserScoreDto> callback);

	void loadUserHistory(int userId, AsyncCallback<UserHistoryDto> callback);

	void loadListUserAndScore(AsyncCallback<List<UserAndScoreDto>> callback);

	void loadListOfIncompleteTask(Date beginDate, Date endDate, int clientOffset,
			AsyncCallback<List<TaskAndUserDto>> callback);

	void dispatchFreeUserInIncompleteTask(List<Integer> userIds,
			List<Integer> taskIds, boolean simulation,
			AsyncCallback<List<TaskAndUserDto>> callback);

	void getHistoryPageCount(AsyncCallback<Integer> callback);

	void loadNewAbsence(int userId, int clientOffset,
			AsyncCallback<AbsenceBean> callback);

	void updateAbsence(AbsenceBean absenceBean,
			AsyncCallback<AbsenceBean> callback);

	void deleteAbsence(AbsenceBean absenceBean, AsyncCallback<Boolean> callback);

	void loadListAbsenceForUser(int userId,
			AsyncCallback<UserAndAbsencesDto> callback);

	void updateUserFulfillment(int userId, Date taskDate, boolean fullfilment,
			AsyncCallback<Void> callback);



}
