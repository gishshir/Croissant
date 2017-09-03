package com.collectif.ft.croissants.shared.model.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class HistoryBean  extends AbstractBean implements IBean, Serializable, Comparable<HistoryBean> {


	private static final long serialVersionUID = 1L;
	
	private  Date _date;
	// map userId - task fulfillment
	private  Map<Integer, Boolean> _mapUserAndFulfillment;
	
	
	//-------------------------------------- accessors
	public Date getDate() {
		return this._date;
	}
	public Map<Integer, Boolean> getMapUserAndFulfillment() {
		return this._mapUserAndFulfillment;
	}
	public boolean isFulfillment(int userId) {
		return this._mapUserAndFulfillment.get(userId);
	}

    //-------------------------------------- constructor
	public HistoryBean() {
		this( null, null);
	}
	
	public HistoryBean ( Date date,  Map<Integer, Boolean> mapUserAndFulfillment) {
		super(IBean.ID_UNDEFINED);
		this._date = date;
		this._mapUserAndFulfillment = mapUserAndFulfillment;
	}
	
	//------------------------------------- public methods
     public void updateUserFulfillment(int userId, boolean fulfillment) {
    	 if (this._mapUserAndFulfillment.containsKey(userId)) {
    		 this._mapUserAndFulfillment.put(userId, fulfillment);
    	 }
     }
     
     //------------------------------------ overriding comparable
	@Override
	public int compareTo(HistoryBean o) {
		
		if (o == null || o._date == null) {
			return 1;
		}
		return this._date.compareTo(o._date);
	}
 

}
