package com.collectif.ft.croissants.shared.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.collectif.ft.croissants.shared.model.bean.UserBean;



public class UserHistoryDto implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	private UserBean _userBean;
	
	// Map of Date / fulfillment
	private Map<Date, Boolean> _mapOfDateAndFulfillment = new HashMap<Date, Boolean>();
	
	//---------------------------------------------- accessor
	public UserBean getUserBean() {
			return this._userBean;
	}
	
	//--------------------------------------------- constructor
	public UserHistoryDto() {}
	public UserHistoryDto (UserBean userBean) {
		this._userBean = userBean;
	}
	
	//-------------------------------------------- public methods
	public void addDate (Date date, boolean fulfillment) {
		this._mapOfDateAndFulfillment.put(date, fulfillment);
	}
	public List<Date> getDateList () {
		
		final List<Date> dateList = new ArrayList<Date>(this._mapOfDateAndFulfillment.keySet());
		 Collections.sort(dateList);
		 return dateList;
	}
	public boolean isDateFulfilled (Date date) {
		if( this._mapOfDateAndFulfillment.containsKey(date)) {
			return this._mapOfDateAndFulfillment.get(date);
		}
		return false;
	}
    
}
