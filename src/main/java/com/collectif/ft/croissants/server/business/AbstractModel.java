package com.collectif.ft.croissants.server.business;

import com.collectif.ft.croissants.shared.model.bean.IBean;

public abstract class AbstractModel<T extends IBean> implements IModel<T> {
	
	private int _id;

	@Override
	public int getId() {
		return this._id;
	}
	
	public AbstractModel() {
		this(IBean.ID_UNDEFINED);
	}
	public AbstractModel(int id) {
		this._id = id;
	}
	
	//---------------------------------------- overriding Object
	@Override
	public boolean equals(Object obj) {
      
		if (this == obj) return true;
		if (obj == null || this.getClass() != obj.getClass()) return false;
		
		
       return  (this.getId() == ((AbstractModel<?>) obj).getId());
	}

	@Override
	public int hashCode() {

        int hash = 7;
        hash = 31 * hash + new Integer(this.getId()).hashCode();
		return hash;
	}

}
