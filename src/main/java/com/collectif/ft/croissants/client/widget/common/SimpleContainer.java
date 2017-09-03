package com.collectif.ft.croissants.client.widget.common;

import com.collectif.ft.croissants.client.view.IUserAndTaskView;
import com.google.gwt.user.client.ui.FlowPanel;

public abstract class SimpleContainer extends FlowPanel {
	
	protected final IUserAndTaskView _mainView;
	
	//-------------------------------------------------- constructor
	public SimpleContainer(final IUserAndTaskView mainView) {
		this._mainView = mainView;
	}

	//------------------------------------------------- protected methods

	
	private  void cancelEdit() {
		this._mainView.cancelAllUserEditing();
	}
}
