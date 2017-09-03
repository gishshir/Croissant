package com.collectif.ft.croissants.client.widget.common;

import com.collectif.ft.croissants.client.util.IConstants;
import com.collectif.ft.croissants.client.util.WidgetUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;


public abstract class InputWidget extends Composite {

	private final Label _label = new Label();
	private final TextBox _textBox = new TextBox();
	private final Label _labelWrongValue = new Label();
	private boolean _inputOK = false;
	
	public InputWidget(String label, String wrongValue, String labelWith, boolean inverse) {

      this._label.setText(label);
      this._labelWrongValue.setText(wrongValue);
      this._labelWrongValue.addStyleName(IConstants.STYLE_WRONG_VALUE);
      this.initHandlers();
      initWidget(WidgetUtils.buildLabelAndTextboxPanel(this._label, this._textBox, labelWith, this._labelWrongValue, inverse));
	}
	
	protected abstract boolean verifyInput(TextBox textBox);
	private void initHandlers () {
		
		this._textBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				_inputOK = verifyInput(_textBox);
				_labelWrongValue.setVisible(!_inputOK);
			}
		});
	}
	
	public void setTextBoxStyleName(String styleName) {
		this._textBox.addStyleName(styleName);
	}
	public void setTextBoxWidth(String width) {
		this._textBox.setWidth(width);
	}
	public String getValue() {
		if (this._inputOK) {
			return this._textBox.getValue();
		}
		return "";
	}
	public void setValue(String value) {
		this._textBox.setText(value);
		this._inputOK = this.verifyInput(this._textBox);
	}
	
}
