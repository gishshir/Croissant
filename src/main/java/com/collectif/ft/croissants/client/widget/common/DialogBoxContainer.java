package com.collectif.ft.croissants.client.widget.common;

import java.util.Iterator;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class DialogBoxContainer extends DialogBox implements HasWidgets {


	//------------------------------------ constructor
	public DialogBoxContainer(final String title, final boolean modal) {
		
		 this.setText(title);
		 this.setAnimationEnabled(true);
	}
	
	//------------------------------------ overriding HasWidgets
	@Override
	public void add(Widget w) {
		super.add(w);
	}

	@Override
	public void clear() {
		super.clear();
	}

	@Override
	public Iterator<Widget> iterator() {
		return super.iterator();
	}

	@Override
	public boolean remove(Widget w) {
		return super.remove(w);
	}
	


}
