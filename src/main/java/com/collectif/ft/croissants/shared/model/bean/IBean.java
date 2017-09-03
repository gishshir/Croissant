package com.collectif.ft.croissants.shared.model.bean;

import com.google.gwt.user.client.rpc.IsSerializable;

public interface IBean extends IsSerializable{
	
	public final static int ID_UNDEFINED = -1;

	public int getId();
	
}
