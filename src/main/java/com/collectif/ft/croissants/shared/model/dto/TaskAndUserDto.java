package com.collectif.ft.croissants.shared.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.collectif.ft.croissants.shared.model.bean.TaskBean;
import com.collectif.ft.croissants.shared.model.bean.UserBean;
import com.google.gwt.user.client.rpc.IsSerializable;

public class TaskAndUserDto implements  IDto, IsSerializable, Serializable, Comparable<TaskAndUserDto> {

	private static final long serialVersionUID = 1L;
	
	private static int maxUserList = 2;
	
	// nbre maximal d'utilisateur pour une tache
	 private TaskBean _taskBean;
	 private List<UserBean> _listUserBeans;
	 
	 // lancer un décalage vers le haut ou vers le bas
	 // autorisé seulement si une tache libre existe dans la liste
	 private boolean _upShiftEnabled = false;
	 private boolean _downShiftEnabled = false;
	 
	 //---------------------------------------------- constructor
	 public TaskAndUserDto() {}
	 public TaskAndUserDto(TaskBean taskBean, List<UserBean> listUserBeans) {
        this._taskBean = taskBean;
        this._listUserBeans = listUserBeans;
	 }

	 //----------------------------------------------- accessors 
	 public List<UserBean>  getListUserBeans () {
		 return this._listUserBeans;
	 }

	 public void setUpShiftEnabled(boolean upEnabled) {
		 this._upShiftEnabled = upEnabled;
	 }
	 public boolean isUpShiftEnabled() {
		 return this._upShiftEnabled;
	 }
	 public void setDownShiftEnabled(boolean downEnabled) {
		 this._downShiftEnabled = downEnabled;
	 }
	 public boolean isDownShiftEnabled() {
		 return this._downShiftEnabled;
	 }
	 public TaskBean getTaskBean() {
		 return this._taskBean;
	 }
	 public void addUserBean(UserBean userBean) {
		 if (userBean == null) {
			 return;
		 }
		 if (this._listUserBeans == null) {
			 this._listUserBeans = new ArrayList<UserBean>(maxUserList);
		 }
		 this._listUserBeans.add(userBean);
	 }
	 //-------------------------------------------- public methods
	 public boolean isEmpty() {
		 return this.getListUserBeanSize() == 0;
	 }
	 public boolean isSpaceForAnotherUser() {
		 return this.getListUserBeanSize() < maxUserList;
	 }
	 public void setUserBeanInAnyFreePlace(UserBean userBean) {
		this._listUserBeans.add(userBean);
	 }
	public boolean removeUserFromTask (UserBean userBean) {

			if (userBean == null || this._listUserBeans == null) {
				return false;
			}

			return this._listUserBeans.remove(userBean);
			
		}
	//------------------------------------------------ private methods
	 private int getListUserBeanSize() {
		 if (this._listUserBeans == null || this._listUserBeans.isEmpty()) {
			 return 0;
		 }
		 return this._listUserBeans.size();
	 }

	 //----------------------------------------- overriding Comparable
	@Override
	public int compareTo(TaskAndUserDto o) {
		if (o == null || o._taskBean == null) {
			return 1;
		}
		return this._taskBean.compareTo(o._taskBean);
	}

}
