package com.collectif.ft.croissants.shared.model.dto;

import java.io.Serializable;
import java.util.Date;

import com.collectif.ft.croissants.shared.model.bean.IBean;

/**
 * Information sur le score d'un utilisateur
 * @author sylvie
 *
 */
public class UserScoreDto implements Serializable, Comparable<UserScoreDto> {
	
	private static final long serialVersionUID = 1L;
	
	private int _userId = IBean.ID_UNDEFINED;
	// Nombre de fois où la tache a été réalisée (fulfillment true)
	private int _scoreOK = 0;
	// Nombre de fois où la tache prévue n'a pas été réalisée (fulfillment false)
	private int _scoreNoK = 0;
	// dernière tache realisée avec success
	private Date _lastRealisedTask;
	
    // note de participation relative arrondie [0,10]
	private int _relativeParticipation = 0;
	
	// note de participation exacte pour la comparaison uniquement
	private float _exactParticipation = 0;
	
	
	// comment
	private String _comment = "";
	
	//---------------------------------------------- accessor
	public String getComment() {
		return this._comment;
	}
	public void setComment(String comment) {
		this._comment = comment;
	}

	public int getUserId() {
		return this._userId;
	}
	public int getScoreOk() {
		return this._scoreOK;
	}
	public int getScoreNok() {
		return this._scoreNoK;
	}
	public void setLastRealisedTask(Date lastRealisedTask) {
		this._lastRealisedTask = lastRealisedTask;
	}
	public Date getLastRealisedTask() {
		return this._lastRealisedTask;
	}

	public void setRelativeParticipation(int relativeParticipation) {
		this._relativeParticipation = relativeParticipation;
	}
	public int getRelativeParticipation() {
		return this._relativeParticipation;
	}
	public void setExactParticipation(float exactParticipation) {
		this._exactParticipation = exactParticipation;
	}
	//----------------------------------------------- constructor
	public UserScoreDto () {}
	public UserScoreDto (int userId) {
		this._userId = userId;
	}
	public UserScoreDto (int userId, int scoreOK, int scoreNoK, Date lastRealisedTask) {
		this(userId);
		this._scoreOK = scoreOK;
		this._scoreNoK = scoreNoK;
		this._lastRealisedTask = lastRealisedTask;
	}
	
	//---------------------------------------------- public methods
	public void addScore (boolean ok) {
		if (ok) {
			this._scoreOK++;
		} else {
			this._scoreNoK++;
		}
	}
	
	
	@Override
	public String toString() {
		return "id: " + this._userId + " - ok: " + this._scoreOK + 
				" - nok: " + this._scoreNoK + " - date: " + this._lastRealisedTask + " - participation: " + this._relativeParticipation;
	}
	
	//--------------------------------------------- override Comparable
	/**
	 * Permet de classer les utilisateur selon leur participation relative et les
	 *  score Nok decroissant
	 */
	@Override
	public int compareTo(UserScoreDto userScore2) {
	
		if (userScore2 == null) {
			return 1;
		}
		// Egalite des participation >> on departage par dernière tache realisée
		if (this._exactParticipation == userScore2._exactParticipation) {
			
			if (this._lastRealisedTask == null && userScore2._lastRealisedTask == null) {
				return 0;
			}
			if (this._lastRealisedTask == null) {
				return -1;
			} else if (userScore2._lastRealisedTask == null) {
				return 1;
			}
			// la date la plus ancienne en premier
			
			//si egalite des date on departage par le scoreNOK
			if (this._lastRealisedTask.equals(userScore2._lastRealisedTask)) {
				// le score Nok le plus haut en premier
				return  (this._scoreOK < userScore2._scoreNoK) ? 1:-1;
			}
			return this._lastRealisedTask.compareTo(userScore2._lastRealisedTask);
		}
			
		// la participation la plus basse en premier
		return (this._exactParticipation < userScore2._exactParticipation)?-1:1; 
				
	}
   
}
