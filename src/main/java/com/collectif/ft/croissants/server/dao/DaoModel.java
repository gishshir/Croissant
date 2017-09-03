package com.collectif.ft.croissants.server.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.collectif.ft.croissants.server.business.Absence;
import com.collectif.ft.croissants.server.business.Alert;
import com.collectif.ft.croissants.server.business.History;
import com.collectif.ft.croissants.server.business.Task;
import com.collectif.ft.croissants.server.business.User;

public class DaoModel {
	
	protected static final String VERSION = "1.1";
	
    // date de la derniere sauvegarde
	private Date _saveDate;
	private List<User> _users = new ArrayList<User>();
	private List<Task> _tasks = new ArrayList<Task>();
	private List<Alert> _alerts = new ArrayList<Alert>();
	private List<History> _histories = new ArrayList<History>();
	private List<Absence> _absences = new ArrayList<Absence>();

	private String _version = VERSION;
	
	public DaoModel() {}
	// only for tests
	DaoModel(String version) {
		this._version = version;
	}
	
	//------------------------------------------ accessors
	public String getVersion() {
		return this._version;
	}
	public List<Absence> getAbsenceList() {
		return this._absences;
	}
	public List<User> getUserList() {
		return this._users;
	}
	public  List<Task> getTaskList() {
		return this._tasks;
	}
	public List<Alert> getAlertList() {
		return this._alerts;
	}
	public Date getSaveDate() {
		return this._saveDate;
	}
	public List<History> getHistoryList() {
		return this._histories;
	}
	public void setSaveDate(Date date) {
		this._saveDate = date;
	}

	
	public void clean() {
		this._users.clear();
		this._tasks.clear();
		this._alerts.clear();
		this._histories.clear();
		this._absences.clear();
	}
	
	public String toJson() {
		return DaoModelHelper.getInstance().encode(this).toJSONString(); 
	}

		
	public User getUser(int id) {
		
		for (User user : this._users) {
			if (user.getId() == id) {
				return user;
			}
		}
		return null;
	}
}
