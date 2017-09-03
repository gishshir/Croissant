package com.collectif.ft.croissants.shared.model.dto;

import java.util.ArrayList;
import java.util.List;

import com.collectif.ft.croissants.shared.model.bean.HistoryBean;
import com.collectif.ft.croissants.shared.model.bean.UserBean;

public class HistoryAndUserDto implements  IDto, Comparable<HistoryAndUserDto> {

	private static final long serialVersionUID = 1L;
	
	private static int maxUserList = 2;
	
	private HistoryBean _historyBean;
	private List<UserBean> _listUserBeans;
	private boolean _editable = false;
	
	//------------------------------------------------ constructor
	public HistoryAndUserDto() {}
	public HistoryAndUserDto (HistoryBean historyBean) {
		this._historyBean = historyBean;
	}
	 //----------------------------------------------- accessors 
	 public List<UserBean>  getListUserBeans () {
		 return this._listUserBeans;
	 }

	 public boolean isEditable() {
		 return this._editable;
	 }
	 public void setEditable(boolean editable) {
		 this._editable = editable;
	 }
	 public HistoryBean getHistoryBean() {
		 return this._historyBean;
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

	
	//----------------------------------------------------- overriding comparable
	@Override
	public int compareTo(HistoryAndUserDto o) {

		if (o == null || o._historyBean == null) {
			return 1;
		}
		return this._historyBean.compareTo(o._historyBean);
	}

}
