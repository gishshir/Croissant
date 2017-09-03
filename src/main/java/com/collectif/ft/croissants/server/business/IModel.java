package com.collectif.ft.croissants.server.business;

import com.collectif.ft.croissants.shared.model.bean.IBean;

public  interface IModel<T extends IBean> {
	
	public int getId();

	public T asBean();
	
	public void updateFromBean(T bean);
}
