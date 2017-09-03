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
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("MyService")
public interface ICroissantService extends RemoteService {
	
	public void updateUserFulfillment(int userId, Date taskDate, boolean fullfilment) throws Exception;
	
	public UserAndAbsencesDto loadListAbsenceForUser(int userId) throws Exception;
	
	public AbsenceBean loadNewAbsence(int userId, int clientOffset) throws Exception;
	
	public AbsenceBean updateAbsence (AbsenceBean absenceBean) throws Exception;
	
	public boolean deleteAbsence (AbsenceBean absenceBean) throws Exception;
	
	public List<TaskAndUserDto> dispatchFreeUserInIncompleteTask (List<Integer> userIds, List<Integer> taskIds, boolean simulation)  throws Exception ;
	
	public List<TaskAndUserDto> loadListOfIncompleteTask(Date beginDate, Date endDate, int clientOffset) throws Exception;
	
	public List<UserAndScoreDto> loadListUserAndScore ()  throws Exception;
	
	public void shiftListUsers(int beginTaskId, boolean up) throws Exception;

	public UserAndAlertDto loadUserAndAlert (int userId) throws Exception;
	
	public UserScoreDto loadUserScore(int userId) throws Exception;
	
	public UserHistoryDto loadUserHistory(int userId) throws Exception;
	
	public UserAndAlertDto updateUserAndAlerts (UserAndAlertDto userAndAlertsBean) throws Exception;
	
	public List<UserBean> loadListOfFreeUser() throws Exception;
	
	public UserBean loadNewUser()  throws Exception;
	
	public UserBean updateUser (UserBean userBean) throws Exception;
	
	public void deleteUser (int userId) throws Exception;
	
	public List<TaskAndUserDto> loadListOfTaskAndUserDto(int clientOffset) throws Exception;

	public TaskBean loadNewTask(int clientOffset) throws Exception;
	
	public TaskBean updateTask (TaskBean taskBean) throws Exception;
	
	public void deleteTask (int taskId) throws Exception;
	
	public void moveUserToTask(int userId, int taskId)  throws Exception;
	
	public 	void removeUserFromTask(int userId)  throws Exception;

	public void deleteAllUsers() throws Exception;
	
	public void deleteAllTasks() throws Exception;
	
	public List<String> getListSmileyUrls() throws Exception;
	
	public int getHistoryPageCount()  throws Exception;
	
	public List<HistoryAndUserDto> loadListHistoryAndUserDto(int page) throws Exception;
	
	
}
