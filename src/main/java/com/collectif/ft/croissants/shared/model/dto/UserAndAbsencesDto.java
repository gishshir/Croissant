package com.collectif.ft.croissants.shared.model.dto;

import java.io.Serializable;
import java.util.List;

import com.collectif.ft.croissants.shared.model.bean.AbsenceBean;
import com.collectif.ft.croissants.shared.model.bean.UserBean;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * User & liste de ses absences
 * @author sylvie
 *
 */
public class UserAndAbsencesDto implements IsSerializable, Serializable {

	private static final long serialVersionUID = 1L;
	
	private UserBean _userBean;
	private List<AbsenceBean> _listAbsences;
	
	//---------------------------------- accessors
	public UserBean getUserBean() {
	 return this._userBean;
	}
	public void setListAbsences(List<AbsenceBean> listAbsences) {
		this._listAbsences = listAbsences;
	}
	public List<AbsenceBean> getListAbsences () {
		return this._listAbsences;
	}
	//---------------------------------- constructor
	public UserAndAbsencesDto() {
			this(null);
	}
	public UserAndAbsencesDto(UserBean userBean) {
	      this._userBean = userBean;
	}

}
