package com.collectif.ft.croissants.client.widget.common;

import com.collectif.ft.croissants.client.util.IConstants;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

public class ButtonPrinc extends Composite implements HasClickHandlers {

	
	private Button _btButton;
	private final Panel _panelText = new SimplePanel();

	public ButtonPrinc() {

		this("");
	}
	
	public ButtonPrinc(final String text) {
		this.initWidget(this.buildContent(text));
	}
	
	public void setEnabled(boolean enabled){
		this._btButton.setEnabled(enabled);
		if (enabled) {
			this._btButton.removeStyleName(IConstants.STYLE_DISABLED);
		} else {
			this._btButton.addStyleName(IConstants.STYLE_DISABLED);
		}
	}
	//--------------------------------------------- private methods
	
	private Panel buildContent(String text) {
		
		final Panel panel = new FlowPanel();
		panel.addStyleName(IConstants.STYLE_PANEL_BUTTONPRINC);
		
		this._panelText.addStyleName(IConstants.STYLE_BUTTONPRINC_TEXT);
		this._panelText.add(new Label(text));
		panel.add(this._panelText);
		
		this._btButton = new Button("");
		this._btButton.setStyleName(IConstants.STYLE_BUTTONPRINC);
		
		panel.add(_btButton);
		
		return panel;
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return this._btButton.addClickHandler(handler);
	}

}
