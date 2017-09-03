package com.collectif.ft.croissants.shared.model.bean;

import java.util.Date;

public class AbsenceBean extends AbstractBean implements Comparable<AbsenceBean>{

	private static final long serialVersionUID = 1L;
	
	private  int _userId;
	private  Date _beginDate;
	private  Date _endDate;
	
	// offset en heure / UTC
	private int _clientOffset;
	
	/**
	 * Stocké pour permettre l'identification lors de l'update
	 * ou du delete.
	 * Correspond à la valeur persistée. Non modifiable coté client
	 */
     private  long _initialBeginDateTimeStamp;
	
	//------------------------------------------ accessors
	public int getUserId() {
		return this._userId;
	}
	public Date getBeginDate() {
		return this._beginDate;
	}
	public Date getEndDate() {
		return this._endDate;
	}
	public long getInitialBeginDateTimeStamp()  {
		return this._initialBeginDateTimeStamp;
	}

	public void updateBeginDate(Date beginDate, int clientOffset) {
		this._beginDate = beginDate;
		this._clientOffset = clientOffset;
	}
	public void updateEndDate(Date endDate, int clientOffset) {
		this._endDate = endDate;
		this._clientOffset = clientOffset;
	}
	 public int getOffset() {
		 return this._clientOffset;
	 }
	//------------------------------------------ constructor
	public AbsenceBean() {
		this(IBean.ID_UNDEFINED, null, null, 0);
	}
	/**
	 * A construire uniquement coté server
	 */
	public AbsenceBean( int userId, Date beginDate, Date endDate, int clientOffset) {
		super(IBean.ID_UNDEFINED);
		this._userId = userId;
		this._beginDate = beginDate;
		if (this._beginDate != null) {
		  this._initialBeginDateTimeStamp =this._beginDate.getTime();
		}
		this._endDate = endDate;
		this._clientOffset = clientOffset;
	}
	
	//------------------------------------ overring Comparable
	@Override
	public int compareTo(AbsenceBean o) {
		if (o == null) {
			return 1;
		}
		if (this.getBeginDate() == null) {
			return -1;
		}
		return this.getBeginDate().compareTo(o.getBeginDate());
	}

  



}
