package com.collectif.ft.croissants.server.business;

import java.util.Date;

import com.collectif.ft.croissants.server.util.DateUtils;
import com.collectif.ft.croissants.shared.model.bean.AbsenceBean;
import com.collectif.ft.croissants.shared.model.bean.IBean;

public class Absence implements IModel<AbsenceBean> {
	
	private final User _user;
	private Date _beginDate;
	private Date _endDate;
	
	//------------------------------------------ accessors
		public User getUser() {
			return this._user;
		}
		public Date getBeginDate() {
			return this._beginDate;
		}
		public Date getEndDate() {
			return this._endDate;
		}

	//-----------------------------------implementing IModel
	@Override
	public int getId() {
		return IBean.ID_UNDEFINED;
	}
	@Override
	public AbsenceBean asBean() {
		return new AbsenceBean(this.getUser().getId(), this.getBeginDate(), this.getEndDate(),  DateUtils.getServerOffset());		
	}

	@Override
	public void updateFromBean(AbsenceBean bean) {
		if (bean == null || bean.getUserId() != this.getUser().getId()) {
			return;
		}
		this._beginDate = bean.getBeginDate();
		this._endDate = bean.getEndDate();
	}

	//--------------------------------- constructor
	public Absence (User user) {
		this._user = user;
	}
	public Absence (AbsenceBean absenceBean, User user) {
		this._user = user;
		this.updateFromBean(absenceBean);
	}

}
