package com.collectif.ft.croissants.client.util;

import com.collectif.ft.croissants.client.service.IActionCallback;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class WidgetUtils {
	
	private final static String STYLE = "style";
	public final static String TOP = "top";
	public final static String LEFT = "left";

	
	public final static DialogBox buildDialogBox(final String title, final String[] messages, 
			final Widget widget, final boolean withCancel,
			final IActionCallback actionCallback) {

		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText(title);
		dialogBox.setAnimationEnabled(true);

		final VerticalPanel vPanelInfo = new VerticalPanel();
		vPanelInfo.setSpacing(IConstants.MIN_SPACING);
		
		if (messages != null) {			
			for (int i = 0; i < messages.length; i++) {
				vPanelInfo.add(new Label(messages[i]));
			}
		}
   
		if (widget != null) {
		  vPanelInfo.add(widget);
		}
		
		
		final HorizontalPanel panelButton = new HorizontalPanel();
		panelButton.setSpacing(IConstants.MIN_SPACING);
		panelButton.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		
		final Button closeButton = new Button("ok");
		closeButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				dialogBox.hide();			
				if (actionCallback != null) {
					actionCallback.onOk();
				}
			}
		});
		panelButton.add(closeButton);
		
		if (withCancel) {
		final Button cancelButton = new Button("annuler");
		cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				dialogBox.hide();	
				if (actionCallback != null) {
					actionCallback.onCancel();
				}
		
			}
		});
		panelButton.add(cancelButton);
		}


		
		vPanelInfo.add(panelButton);
		vPanelInfo.setCellHorizontalAlignment(panelButton,HasHorizontalAlignment.ALIGN_RIGHT);


		dialogBox.setWidget(vPanelInfo);
		return dialogBox;
	}
	
    public static void addStyleAttributeWithPrefix (final Element element, final String attributePrefix, final String attributeValue) {
		
		if (attributeValue == null || attributeValue.trim().length() == 0) {
			_removeStyleAttributFromPrefix(element, attributePrefix);
			return;
		}
		
		final String styleAttributes = element.getAttribute(STYLE);

		if (styleAttributes == null || styleAttributes.length() == 0) {
			element.setAttribute(STYLE, attributePrefix + ": " + attributeValue + "; ");
			return;
		}
		
		boolean findAttribute = false;
		final StringBuilder sb = new StringBuilder();
    	final String[] tokens = styleAttributes.split(";");
    	for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i].trim();

			if (token.equals(";") || token.equals("")) {
				continue;
			}
			if (token.startsWith(attributePrefix)) {
				// replace
				findAttribute = true;
				sb.append(attributePrefix + ": " + attributeValue + "; ");
			}
			else {
				sb.append(token + "; ");
			}	
		}
    	
    	// new attribute
    	if(!findAttribute) {
    		sb.append(attributePrefix + ": " + attributeValue + "; ");
    	}
    	
    	element.setAttribute(STYLE, sb.toString());
		
	}
    
    private static void _removeStyleAttributFromPrefix(final Element element, String attributePrefix) {
    	
    	final String styleAttributes = element.getAttribute(STYLE);
    	if (styleAttributes == null || styleAttributes.length() == 0) {
    		return;
    	}
    	final StringBuilder sb = new StringBuilder();
    	final String[] tokens = styleAttributes.split(";");
    	for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i].trim();
			if (token.equals(";") || token.equals("")) {
				continue;
			}
			if (!token.startsWith(attributePrefix)) {
				sb.append(token + "; ");
			}
		}
    	element.setAttribute("style", sb.toString());
    }
    
    public static Panel buildLabelAndTextboxPanel (Label label, TextBox textbox, String labelWith, Label wrongValue, boolean inverse) {
    	
    	label.setWidth(labelWith);
    	
    	HorizontalPanel panel = new HorizontalPanel();
    	panel.setSpacing(IConstants.MIN_SPACING);
    	panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    
    	if (inverse) {
    		 panel.add(textbox);
    		 panel.add(label);
    	}
    	else {
    	  panel.add(label);
    	  panel.add(textbox);
    	}
    	if (wrongValue != null) {
    		panel.add(wrongValue);
    		wrongValue.setVisible(false);
    	}
    	
    	return panel;
    }
    
    
    public static Panel buildLabelAndWidgetPanel (Label label, Widget widget, String labelWith) {
    	
    	label.setWidth(labelWith);
    	
    	HorizontalPanel panel = new HorizontalPanel();
    	panel.setSpacing(IConstants.MIN_SPACING);
    	panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
  
    	  panel.add(label);
    	  panel.add(widget);
    	
    	return panel;
    }
    

}
