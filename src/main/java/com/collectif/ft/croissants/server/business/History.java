package com.collectif.ft.croissants.server.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.collectif.ft.croissants.shared.model.bean.HistoryBean;

/**
 * Garde l'historique d'une tache realisée avec la liste des utilisateurs concernés
 * Pour chaque utilisateur, il est possible de modifier si il a rempli ou non la tache assignée
 * @author sylvie
 *
 */
public class History extends AbstractModel<HistoryBean> implements Comparable<History>{
	
	private final Date _date;
	// map userId - UserFulfillment [User - fulfillment]
	private final Map<Integer, UserFulfillment> _mapUserFulfillment = new HashMap<Integer,History.UserFulfillment>();
	
	//-------------------------------------- accessors
	public Date getDate() {
		return this._date;
	}
	public List<UserFulfillment> getListUserFulfillment() {
		return new ArrayList<UserFulfillment>(this._mapUserFulfillment.values());
	}
	public UserFulfillment getUserFulfillment(int userId) {
		return this._mapUserFulfillment.get(userId);
	}
	//------------------------------------- public methods
	public boolean contains (int userId) {
		return this._mapUserFulfillment.containsKey(userId);
	}
	public void removeUser(int userId) {
		this._mapUserFulfillment.remove(userId);
	}
	public boolean isEmpty() {
		return this._mapUserFulfillment.isEmpty();
	}
	/**
	 * True si au moins un utilisateur a rempli la tache
	 * @return
	 */
	public boolean hasBeenFulfilled() {
		
		for (UserFulfillment userFulfillment : this._mapUserFulfillment.values()) {
			if (userFulfillment.isFulfillment()) {
				return true;
			}
		}
		return false;
	}
	public void updateUserFulfillment(int userId, boolean fulfillment) {
		UserFulfillment userFulfillment = this.getUserFulfillment(userId);
		if (userFulfillment != null) {
			userFulfillment._fulfillment = fulfillment;
		}
	}
	//-------------------------------------- constructor
	public History (Task task) {
		this._date = task.getDate();
		for (User user : task.getListUsers()) {
			this._mapUserFulfillment.put(user.getId(), new UserFulfillment(user));
		}
	}
	
	public History (Date date, final Map<Integer, UserFulfillment> mapUserFulfillment) {
		this._date = date;
		this._mapUserFulfillment.putAll(mapUserFulfillment);
	}

	//-------------------------------------- implements AbstractModel
	@Override
	public HistoryBean asBean() {
 
		final Map<Integer, Boolean> map = new HashMap<Integer, Boolean>(this._mapUserFulfillment.size());
		for (Integer userId: this._mapUserFulfillment.keySet()) {
			UserFulfillment userFulfillment = this._mapUserFulfillment.get(userId);
			map.put(userId, userFulfillment._fulfillment);
		}
		
		return new HistoryBean(this._date, map);

	}

	@Override
	public void updateFromBean(HistoryBean bean) {

          if (bean == null || bean.getDate() == null || bean.getDate().getTime() != this._date.getTime()) {
        	  return;
          }
		
          Map<Integer, Boolean> mapUserFulfillment =  bean.getMapUserAndFulfillment();
          for (Integer userId : mapUserFulfillment.keySet()) {
			
        	  UserFulfillment userFulfillment = this._mapUserFulfillment.get(userId);
        	  if (userFulfillment != null) {
        		  userFulfillment._fulfillment = mapUserFulfillment.get(userId);
        	  }
		  }
	}
	


	//----------------------------------------------- implements Comparable
	@Override
	public int compareTo(History o) {
		
		if (o == null || o._date == null) {
			return 1;
		}
		return this._date.compareTo(o._date);
	}
	
	//============================================= INNER CLASS
	public static class UserFulfillment {
		
		private final User _user;
		// tache réalisée ou non par l'utilisateur
		private boolean _fulfillment;
		
		public User getUser() {
			return this._user;
		}
		public boolean isFulfillment() {
			return this._fulfillment;
		}
		
		public UserFulfillment(final User user) {
			this(user, true);
		}
		public UserFulfillment(final User user, boolean fulfillment) {
			this._user = user;
			this._fulfillment = fulfillment;
		}
	}





}
