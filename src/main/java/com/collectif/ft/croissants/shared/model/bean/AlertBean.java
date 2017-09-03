package com.collectif.ft.croissants.shared.model.bean;



public class AlertBean extends AbstractBean implements IBean {

	private static final long serialVersionUID = 1L;
	
	private final int _userId;
	
	private boolean _active;
	private final String _type;
	private String _state = null;
	private String _param;
	private long _timestampDone = -1;
	//-------------------------------------- accessors
	public String getType() {
		return this._type;
	}
	public String getState() {
		return this._state;
	}
	public int getUserId() {
		return this._userId;
	}
	public boolean isActive() {
		return this._active;
	}
    public void setState(String state) {
    	this._state = state;
    }
    public String getParam() {
    	return this._param;
    }
    public void setParam(String param) {
    	this._param = param;
    }
    public long getTimestampDone() {
		return this._timestampDone;
	}
	//--------------------------------------- constructor
    public AlertBean(int userId, String type,  String param, final boolean active) {
    	this(userId, type, null, param, active, -1);
    }
	public AlertBean(int userId, String type, final String state, String param, final boolean active, final long timestampDone) {
		super(IBean.ID_UNDEFINED);
		this._userId = userId;
		this._type = type;
		this._active = active;
		this._state = state;
		this._param = param;
		this._timestampDone = timestampDone;
	}

	//--------------------------------------- public method



}
