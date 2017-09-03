package com.collectif.ft.croissants.server.service.alert;

import java.util.Date;

public interface IAlertEventListener {

	public void onUserDeleted(int userId);
	
	// la date de la tache a été modifiée
	// ou
	// l'utilisateur a été changé de tache
	public void onTaskDateChanged(int userId, Date oldDate, Date newDate);
	
	// le delai d'une alert TaskTodo a été modifié
	public void onAlertTaskToDoChanged(int userId);

}
