package com.collectif.ft.croissants.server.business;


public class Email {

	
	private  String _email;
	
	public Email() {
		this._email = null;
	}
	public Email (String email) {
		
		if (email != null) {
			email = email.trim();
			if (email.length() == 0) {
				email = null;
			}
		}
		  this._email = email;
	}
	
	public String getEmail() {
		return this._email;
	}
	
	
}
