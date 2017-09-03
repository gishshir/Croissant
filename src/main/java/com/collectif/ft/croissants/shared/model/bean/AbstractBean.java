package com.collectif.ft.croissants.shared.model.bean;

import java.io.Serializable;

public abstract class AbstractBean implements IBean, Serializable {
	
	private static final long serialVersionUID = 1L;
	private int _id = ID_UNDEFINED;
	
	public AbstractBean(int id) {
		this._id = id;
	}

	//--------------------------------- overriding IBean
	@Override
	public int getId() {
		return this._id;
	}
	
	//-------------------------------- accessors
	public void setId(int id)
	{
		this._id = id;
	}
}
