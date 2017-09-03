package com.collectif.ft.croissants.client.service;

import com.collectif.ft.croissants.client.util.WidgetUtils;
import com.collectif.ft.croissants.shared.model.bean.IBean;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractPresenter implements IPresenter {


	protected void showConfirmDialog(String title, String[] messages, Widget ankor,
			IActionCallback actionCallback) {
		
		WidgetUtils.buildDialogBox(title, messages, null, true, actionCallback)
		.showRelativeTo(ankor);
	}

}
