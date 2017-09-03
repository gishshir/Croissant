package com.collectif.ft.croissants.server.business;

import java.util.Date;

import com.collectif.ft.croissants.shared.model.bean.UserBean;


public class User extends AbstractModel<UserBean> {

	private static int compteur = 0;
	
	private String _login;
	private String _logo = null;
	private Email _email;
	private boolean _free = true;
	// date de creation de l'utilisateur.
	// modifiable. utilisée pour le calcul de la participation
	private Date _registration;
	
	//------------------------------------- accessors
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
	public Email getEmail() {
		return this._email;
	}
	public void setEmail (Email email) {
		this._email = email;
	}
	public void setFree(boolean free) {
		this._free = free;
	}
	public boolean isFree() {
		return this._free;
	}
	
	//--------------------------------- constructor
	public User() {
		super(++compteur);
	}
	public User(int id) {
		super(id);
		compteur = Math.max(compteur, id);
	}
	public User(String login) {
		this();
		this._login = login;
	}
	
	//------------------------------------- overriding IModel
	@Override
	public UserBean asBean() {

        final UserBean userBean = new UserBean(this.getId());
        userBean.setEmail((this._email==null)?null:this._email.getEmail());
        userBean.setLogin(this._login);
        userBean.setLogo(this._logo);
        userBean.setRegistration(this._registration);
        
		return userBean;
	}

	@Override
	public void updateFromBean(UserBean userBean) {
		
		this._email = (userBean.getEmail()==null)?null:new Email(userBean.getEmail());
		this._login = userBean.getLogin();
		this._logo = userBean.getLogo();
		this._registration = userBean.getRegistration();
	}
	
    //---------------------------------------------- public methods
	public boolean hasEmail() {
		
		return this._email != null && this._email.getEmail() != null && !this._email.getEmail().isEmpty();
	}

}
