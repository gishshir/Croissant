package com.collectif.ft.croissants.shared.model.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TaskBean extends AbstractBean implements IBean, Serializable, Comparable<TaskBean> {

	private static final long serialVersionUID = 1L;

	private static int maxUserList = 2;
	
	private Date _date;
	// offset en heure / UTC
	private int _clientOffset;
	private List<Integer> _listUserIds;
	private boolean _beforeToday;
	
	 //---------------------------------------------- constructor
	 public TaskBean() {
		 this(IBean.ID_UNDEFINED,  new Date(), false);
	 }
	 public TaskBean(final int id) {
		 super(id);
	 }
	 public TaskBean(final int id, final Date date,  boolean beforeToday) {
		 super(id);
		 this._date = date;
		 this._beforeToday = beforeToday;
	 }
	 //----------------------------------------------- accessors
	 // nombre de place libre / nombre total
	 public int getFreeSpace() {
		return (this.isEmpty())?maxUserList:
				maxUserList - this._listUserIds.size(); 
	 }
	 public boolean isDateBeforeToday() {
		 return this._beforeToday;
	 }
	 public void setBeforeToday(boolean beforeToday) {
		 this._beforeToday = beforeToday;
	 }
	 public int getOffset() {
		 return this._clientOffset;
	 }
	 public void setDate (Date date, int offset) {
		 this._date = date;
		 this._clientOffset = offset;
	 }
	 public Date getDate() {
		 return this._date;
	 }
	 public List<Integer> getListUserIds() {
		 return this._listUserIds;
	 }
	 public void addUserId(int userId) {
		 if (this._listUserIds == null) {
			 this._listUserIds = new ArrayList<Integer>(maxUserList);
		 }
		 this._listUserIds.add(userId);
	 }
	//--------------------------------- public method
	public void update (TaskBean dayBean) {
		this._date = dayBean.getDate();				
	}
	 public boolean isEmpty() {
		 return _listUserIds == null || _listUserIds.isEmpty();
	 }
	//------------------------------------------------- overriding Comparable
	@Override
	public int compareTo(TaskBean o) {
		if (o == null) {
			return 1;
		}
		if (this.getDate() == null) {
			return -1;
		}
		return this.getDate().compareTo(o.getDate());
	}
	

}
