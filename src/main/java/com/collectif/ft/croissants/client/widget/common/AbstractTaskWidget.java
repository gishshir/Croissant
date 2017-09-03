package com.collectif.ft.croissants.client.widget.common;

import java.util.ArrayList;
import java.util.List;

import com.collectif.ft.croissants.client.util.DateUtils;
import com.collectif.ft.croissants.client.util.IConstants;
import com.collectif.ft.croissants.shared.model.dto.IDto;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.datepicker.client.DateBox;

public abstract class AbstractTaskWidget<T extends IDto> extends Composite {

private static final int userMaxNumber = 2;
	
	protected List<CaseUserWidget> _listCaseUserWidgets = new ArrayList<CaseUserWidget>();
	protected final DateBox _dateBox = new DateBox();
	
	//------------------------------------- constructor
		public AbstractTaskWidget() {
	
			this.initComposants();
			this.initHandlers();
			this.initWidget(this.buildMainPanel());
		}
		
	//---------------------------------------------- public methods
	public abstract void setDatas (T dto);
	public void clean() {
		for (CaseUserWidget caseUserWidget : this._listCaseUserWidgets) {
			caseUserWidget.clean();
		}
	}
			
	//--------------------------------------------------- private methods
	
	private Panel buildMainPanel() {

		final HorizontalPanel globalTaskPanel = new HorizontalPanel();
		globalTaskPanel.addStyleName(IConstants.STYLE_TASK_GLOBAL_WIDGET);
		//globalTaskPanel.add(this._btDeleteTask);
		
		HorizontalPanel mainTaskPanel = new HorizontalPanel();
		mainTaskPanel.setSpacing(IConstants.MIN_SPACING);
		mainTaskPanel.addStyleName(IConstants.STYLE_TASK_WIDGET);
		mainTaskPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		
		mainTaskPanel.add(this._dateBox);
		
		// create case widget
		for (int i = 0; i < userMaxNumber; i++) {
			CaseUserWidget caseUserWidget = new CaseUserWidget();
			mainTaskPanel.add(caseUserWidget);
			this._listCaseUserWidgets.add(caseUserWidget);
			
		}
		
		globalTaskPanel.add(mainTaskPanel);
		
		return globalTaskPanel;
	}


	private void initHandlers() {
			// TODO Auto-generated method stub
			
		}


	private void initComposants() {
		this._dateBox.setFormat(new DateBox.DefaultFormat(DateUtils.dateTimeFormat));	
		this._dateBox.addStyleName(IConstants.STYLE_TASK_DATEBOX);
		this._dateBox.addStyleName(IConstants.STYLE_TASK_DATEBOX_NOPAST);
		this._dateBox.setEnabled(false);
	}

	
	 //============================================ INNER CLASS ===========
    public static class CaseUserWidget extends SimplePanel  {
    	    	
    	public CaseUserWidget () {
    		this.addStyleName(IConstants.STYLE_CASE_USER_WIDGET);
    	}
    	public void setUserWidget (AbstractUserWidget simpleUserWidget) {
    		this.add(simpleUserWidget);
    	}
    	protected void clean() {
    		this.clear();
    	}
    }

}
