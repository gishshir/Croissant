package com.collectif.ft.croissants.shared.model.dto;

import java.io.Serializable;

/**
 * classe interne au server permettant les calculs de participation
 * @author sylvie
 *
 */
public class UserScoreInternal extends UserScoreDto implements Serializable {


	private static final long serialVersionUID = 1L;
	
	// user name
	private String _userName;
	
	// participation effective (note [0 - 10]
	// champ calculé
	// nbre taches realisees par user / nbre taches realisable en fct de ses absences
	private float _participation = -1;
	
	// nombre de tache realisable par le participant (pendant sa présence)
	private int _countRealizableTask = 0;
	
	// nombre de tache realisée par le participant
	private int _countRealizedTask = 0;
	

	//--------------------------------------------------- accessors
	public String getUserName() {
		return this._userName;
	}
	public float getParticipation() {
		if (this._participation == -1) {
			this.calculateParticipation();
		}
		return this._participation;
	}
	public int getCountRealizableTask() {
		return this._countRealizableTask;
	}
	public int getCountRealizedTask() {
		return this._countRealizedTask;
	}
	public void setCountRealizableTask(int countRealisableTask) {
		this._countRealizableTask = countRealisableTask;
		this.initParticipation();
	}
	//--------------------------------------------------- public methods
	public void incrementsRealizableTask() {
		this._countRealizableTask++;
		this.initParticipation();
	}
	public void incrementsRealizedTask() {
		this._countRealizedTask++;
		this.initParticipation();
	}
	//--------------------------------------------------- constructor
	public UserScoreInternal () {}
	public UserScoreInternal (int userId, String userName) {
		super(userId);
		this._userName = userName;
	}
	
	//--------------------------------------- private methods
	private void initParticipation() {
		this._participation = -1;
	}
	// nbre taches realisees par user * 10 / nbre taches realisable en fct de ses absences
	private void calculateParticipation() {
		//log.info("calculateParticipation() user: " + this.getUserId());
		if (this._countRealizableTask == 0 || this._countRealizedTask == 0) {
			this._participation = 0;
		}
		this._participation =  (this._countRealizableTask == 0)?0:
				((float) (this._countRealizedTask * 10)) / this._countRealizableTask;
//		log.info("realizable : " + this._countRealizableTask + " - realized: " + this._countRealizedTask +
//				" - participation: " + this._participation);
	}
}
