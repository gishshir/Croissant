package com.collectif.ft.croissants.shared.model.dto;

import java.io.Serializable;

import com.collectif.ft.croissants.shared.model.bean.UserBean;
import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Encapsule l'utilisateur et ses alers
 * @author sylvie
 *
 */
public class UserAndAlertDto implements	IsSerializable, Serializable {


	private static final long serialVersionUID = 1L;
	
	private UserBean _userBean;
	
	private boolean _changeDateAlert = true;
	private boolean _deleteUserAlert = true;
	private boolean _taskToDoAlert = true;
	private boolean _taskFreeAlert = true;
	
	private int _delai = 2;

	//---------------------------------- accessors
	public UserBean getUserBean() {
		return this._userBean;
	}
	public void setChangeDateAlert(boolean changeDateAlert) {
		this._changeDateAlert = changeDateAlert;
	}
	public void setDeleteUserAlert(boolean deleteUserAlert) {
		this._deleteUserAlert = deleteUserAlert;
	}
	public void setTaskToDoAlert (boolean taskToDoAlert) {
		this._taskToDoAlert = taskToDoAlert;
	}
	public void setTaskFreeAlert(boolean taskFreeAlert) {
		this._taskFreeAlert = taskFreeAlert;
	}
	public void setDelai(int delai) {
		this._delai = delai;
	}
	public boolean isChangeDateAlert() {
		return this._changeDateAlert;
	}
	public boolean isDeleteUserAlert() {
		return this._deleteUserAlert;
	}
	public boolean isTaskToDoAlert() {
		return this._taskToDoAlert;
	}
	public boolean isTaskFreeAlert() {
		return this._taskFreeAlert;
	}
	public int getDelai() {
		return this._delai;
	}
	//---------------------------------- constructor
	public UserAndAlertDto() {
		this(null);
	}
	public UserAndAlertDto(UserBean userBean) {
      this._userBean = userBean;
	}

}
