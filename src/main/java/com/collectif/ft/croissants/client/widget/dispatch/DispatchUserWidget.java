package com.collectif.ft.croissants.client.widget.dispatch;

import com.collectif.ft.croissants.client.util.IConstants;
import com.collectif.ft.croissants.client.widget.common.AbstractUserWidget;
import com.google.gwt.user.client.ui.Widget;
/**
 * Widget utilisé pour présenter la liste des user free dans l'ordre du score croissant
 * @author sylvie
 *
 */
public class DispatchUserWidget extends AbstractUserWidget {

	
	//------------------------------- constructor
	public DispatchUserWidget() {
		super(false, IConstants.STYLE_USER_DISPATCH_WIDGET);
	}

	
	//----------------------------------- overriding SimpleUserWidget
	@Override
	protected Widget getAdditionalInfoPanel() {
		return this._labelLogin;
	}

	@Override
	protected Widget getVerticalButtonPanel() {
		return null;
	}

	@Override
	protected Widget getVerticalIconPanel() {
		return null;
	}

	@Override
	protected void initHandlers() {
	}


}
