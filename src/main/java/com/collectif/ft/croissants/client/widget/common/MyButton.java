package com.collectif.ft.croissants.client.widget.common;

import com.collectif.ft.croissants.client.util.IConstants;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

public class MyButton extends Composite implements HasClickHandlers {
	
	private Button _btButton;
	private final Panel _panelImage = new SimplePanel();

	public MyButton() {

		this.initWidget(this.buildContent());
	}
	
	public MyButton(final String text) {
        this();
        this._btButton.setText(text);
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
	
	private Panel buildContent() {
		
		final Panel panel = new FlowPanel();
		panel.addStyleName(IConstants.STYLE_PANEL_BUTTON);
		
		this._panelImage.addStyleName(IConstants.STYLE_BUTTON_IMAGE);
		panel.add(this._panelImage);
		
		this._btButton = new Button("");
		this._btButton.setStyleName(IConstants.STYLE_BUTTON);
		
		panel.add(_btButton);
		
		return panel;
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return this._btButton.addClickHandler(handler);
	}
}
