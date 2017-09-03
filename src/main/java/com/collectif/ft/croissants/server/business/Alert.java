package com.collectif.ft.croissants.server.business;

import java.util.Date;

import com.collectif.ft.croissants.server.business.message.Message;
import com.collectif.ft.croissants.server.business.message.Message.MessageLevel;
import com.collectif.ft.croissants.server.business.message.Message.MessagePriority;
import com.collectif.ft.croissants.shared.model.bean.AlertBean;


public class Alert  extends  AbstractModel<AlertBean>{
	

	//----------------------------------- implements IModel
	@Override
	public AlertBean asBean() {
		AlertBean bean = new AlertBean(this._user.getId(), this._type.name(),
				this._state.name(), this._param, this._active, this._timestampDone);	
		bean.setParam(this._param);
		return bean;
	}

	@Override
	public void updateFromBean(AlertBean bean) {
 
		if (this._user.getId() != bean.getUserId() || !this.getAlerteType().name().equals(bean.getType())) {
			return;
		}
		// seul active/inactif  et param est modifiable par l'utilisateur
        this._active = bean.isActive();
        this._param = bean.getParam();
		
	}

	//===================================== AlerteType
	public enum AlertType{
		ChangeDate(MessageLevel.info, MessagePriority.normal, false),
		DeleteUser(MessageLevel.warn, MessagePriority.normal, false),
		TaskToDo(MessageLevel.info, MessagePriority.urgent, false),
		TaskFree(MessageLevel.warn, MessagePriority.urgent, true);
		
		private final Message.MessageLevel _level;
		public Message.MessageLevel getLevel() {
			return this._level;
		}
		
		private final Message.MessagePriority _priority;
		public MessagePriority getPriority() {
			return this._priority;
		}
		
		// A rearmer periodiquement
		private final boolean _rearmPeriodically;
		public boolean isRearmPeriodically() {
			return this._rearmPeriodically;
		}
		
		private AlertType (MessageLevel level, MessagePriority priority, boolean rearmPeriodically) {
			this._level = level;
			this._priority = priority;
			this._rearmPeriodically = rearmPeriodically;
		}
	}
	
	//====================================== AlerteState
	public enum AlertState {
		running, done, canceled;
	}
	
	private  final User _user;
	private final AlertType _type;
	private boolean _active;
	private AlertState _state = AlertState.running;
	private String _param;
	// timestamp de la tache realisee
	private long _timestampDone = -1;
	
	//------------------------------------------- constructeur
	public Alert(final AlertBean alertBean, User user) {
		this._user = user;
		this._type = Enum.valueOf(AlertType.class, alertBean.getType());
		this._active = alertBean.isActive();
		this._param = alertBean.getParam();
		this._state = (alertBean.getState() == null)?AlertState.running:
				Enum.valueOf(AlertState.class, alertBean.getState());
		this._timestampDone = alertBean.getTimestampDone();
		
		//reinitialisation par defaut
		if (this._state == AlertState.done && this._timestampDone == -1) {
			this._timestampDone = new Date().getTime();
		}
	}
	
	//-----------------------------------------public methods
	public void reArms() {
		this._state = AlertState.running;
	}
	
	//----------------------------------------- accessors
	public long getTimestampDone() {
		return this._timestampDone;
	}
	  public String getParam() {
	    	return this._param;
	    }
	    public void setParam(String param) {
	    	this._param = param;
	    }
	public boolean isActive() {
		return this._active;
	}
	public AlertType getAlerteType() {
		return this._type;
	}
	public AlertState getAlerteState() {
		return this._state;
	}
	public User getUser() {
		return this._user;
	}
	public void setAlerteState(AlertState state) {
		this._state = state;
		if (state == AlertState.done) {
			this._timestampDone = new Date().getTime();
		}
	}
	
	//------------------------------------- public methods
	




}
