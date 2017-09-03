package com.collectif.ft.croissants.server.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.collectif.ft.croissants.server.util.DateUtils;
import com.collectif.ft.croissants.shared.model.bean.TaskBean;

public class Task extends AbstractModel<TaskBean> implements Comparable<Task> {

	private static int compteur = 0;
	private static int maxUserList = 2;
	
	private Date _date;
	 private List<User> _listUsers; 
	 
	 //---------------------------------------------- constructor
	 public Task() {
		 this(DateUtils.getNewUTCDate());
	 }
	public Task(int id) {
			super(id);
			compteur = Math.max(compteur, id);
	}
	 public Task(final Date date) {
		 super(++compteur);
		 this._date = date;
	 }
	 //----------------------------------------------- accessors
	 public void addUser(User user) {
		 if (this._listUsers == null) {
			 this._listUsers = new ArrayList<User>(maxUserList);
		 }
		 if (!this._listUsers.contains(user)) {
			 this._listUsers.add(user);
			 user.setFree(false);
		 }
	 }
	 	 
	 public List<User> getListUsers () {
		 return this._listUsers;
	 }

	 public void setDate (Date date) {
		 this._date = date;
	 }
	 public Date getDate() {
		 return this._date;
	 }
	//--------------------------------------- overriding AbstractModel
	@Override
	public TaskBean asBean() {

            final TaskBean taskBean =
            		new TaskBean(this.getId(), this._date, 
            				DateUtils.isDateTimeInPast(this._date));
            
            if (this._listUsers != null) {
              for (User user : this._listUsers) {
				taskBean.addUserId(user.getId());
			  }
            }
			return taskBean;
	}

	@Override
	public void updateFromBean(TaskBean bean) {

            this._date = bean.getDate();		
	}
	 //-------------------------------------------- public methods
	public void update(TaskBean bean) {

            this._date = bean.getDate();
			
	}
	 public boolean isEmpty() {
		 return this.getListUserSize() == 0;
	 }

	 public boolean isSpaceForAnotherUser() {
		 return this.getListUserSize() < maxUserList;
	 }
	 public void setUserBeanInAnyFreePlace(User user) {
		 this.addUser(user);
	 }
	public boolean removeUserFromTask (User user) {
		if (user == null || this._listUsers == null) {
			return false;
		}
		boolean result = this._listUsers.remove(user);
		if(result) {
			user.setFree(true);
		}
		return result;
	}
	public boolean containsUser(User user) {
		if (user == null || this._listUsers == null) {
			return false;
		}
		return this._listUsers.contains(user);
	}
	//------------------------------------------------ private methods
	 private int getListUserSize() {
		 if (this._listUsers == null || this._listUsers.isEmpty()) {
			 return 0;
		 }
		 return this._listUsers.size();
	 }
	 
	//---------------------------------------- overriding Comparable
	@Override
	public int compareTo(Task o) {
		if (o == null) {
			return 1;
		}
		if (this.getDate() == null) {
			return -1;
		}
		return this.getDate().compareTo(o.getDate());
	}


}
