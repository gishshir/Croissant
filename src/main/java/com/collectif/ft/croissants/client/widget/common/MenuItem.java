package com.collectif.ft.croissants.client.widget.common;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MenuItem extends SimplePanel {

	private final Button _buttonItem = new Button();
	
	public MenuItem(final String text) {
		this._buttonItem.setText(text);
		this.setWidget(this.buildMainPanel());
	}
	

	//--------------------------------- public methods
	public void setClickHandler(final ClickHandler clickHandler) {
		this._buttonItem.addClickHandler(clickHandler);
	}
	
	public HasClickHandlers getButtonItem() {
		return this._buttonItem;
	}
	
	//--------------------------------- private methods

	private Widget buildMainPanel() {
		return this._buttonItem;
	}
}
