package com.collectif.ft.croissants.shared.model.dto;

import java.io.Serializable;


import com.collectif.ft.croissants.shared.model.bean.UserBean;
/**
 * Combinaisaon d'un utilisateur et des informations sur son score
 * @author sylvie
 *
 */
public class UserAndScoreDto implements Serializable {


	private static final long serialVersionUID = 1L;
	
	private UserBean _userBean;
	private UserScoreDto _userScoreDto;
	
	// utilisateur free
	private boolean _freeUser = true;
	
	//--------------------------------------------- accessors
	public UserBean getUser() {
		return this._userBean;
	}
	public UserScoreDto getScore() {
		return this._userScoreDto;
	}
	public boolean isFreeUser() {
		return this._freeUser;
	}
	//--------------------------------------------- constructor
	public UserAndScoreDto() {}
	public UserAndScoreDto (UserBean userBean, UserScoreDto userScoreDto, boolean freeUser) {
		this._userBean = userBean;
		this._userScoreDto = userScoreDto;
		this._freeUser = freeUser;
	}
}
