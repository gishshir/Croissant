package com.collectif.ft.croissants.server.service.alert;

import java.util.Date;

public interface IAlertEventListener {

	public void onUserDeleted(int userId);
	
	// la date de la tache a �t� modifi�e
	// ou
	// l'utilisateur a �t� chang� de tache
	public void onTaskDateChanged(int userId, Date oldDate, Date newDate);
	
	// le delai d'une alert TaskTodo a �t� modifi�
	public void onAlertTaskToDoChanged(int userId);

}
