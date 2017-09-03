package com.collectif.ft.croissants.shared.model.bean;

import java.io.Serializable;
import java.util.Date;


public class UserBean extends AbstractBean implements IBean, Serializable {


	private static final long serialVersionUID = 1L;
		
	private String _login;
	private String _logo;
	private String _email;
	private Date _registration;
	
	//--------------------------------- constructor
	public UserBean() {
		super(IBean.ID_UNDEFINED);
	}
	public UserBean(int id) {
		super(id);
	}
	public UserBean(String login) {
		this();
		this._login = login;
	}
	
	//---------------------------------- accessors
	public Date getRegistration() {
		return this._registration;
	}
	public void setRegistration(Date registration) {
		this._registration = registration;
	}
	public void setLogin(String login) {
		this._login = login;
	}
	public String getLogin() {
		return this._login;
	}
	public String getLogo() {
		return this._logo;
	}
	public void setLogo(String logo) {
		this._logo = logo;
	}
	public String getEmail() {
		return this._email;
	}
	public void setEmail (String email) {
		this._email = email;
	}


}
