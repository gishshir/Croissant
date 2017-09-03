package com.collectif.ft.croissants.client.view;

import java.util.List;

import com.collectif.ft.croissants.client.event.UpdateAbsenceEventHandler;
import com.collectif.ft.croissants.client.service.AbsenceUserPresenter.DisplayAbsence;
import com.collectif.ft.croissants.client.util.DateUtils;
import com.collectif.ft.croissants.client.util.IConstants;
import com.collectif.ft.croissants.client.util.WidgetUtils;
import com.collectif.ft.croissants.client.widget.absence.AbsenceUserWidget;
import com.collectif.ft.croissants.client.widget.history.HistoryUserWidget;
import com.collectif.ft.croissants.client.widget.user.SimpleUserWidget;
import com.collectif.ft.croissants.shared.model.bean.AbsenceBean;
import com.collectif.ft.croissants.shared.model.dto.UserAndAbsencesDto;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * Vue de la liste des absences d'un participant
 * @author sylvie
 *
 */
public class AbsenceUserView extends Composite implements DisplayAbsence {

	private final VerticalPanel _main = new VerticalPanel();
	private final VerticalPanel _listAbsenceWidget = new VerticalPanel();
	
	private final CaseUserWidget _caseUserWidget = new CaseUserWidget();
	private final Button _btAddAbsence = new Button("Add absence");
	private final Button _btDeleleAll = new Button("Delete all");
	private final Button _btClose = new Button("close");
	
    private final DateBox _dateRegistration = new DateBox();
    
	private UpdateAbsenceEventHandler _updateAbsenceEventHandler;
	
	
	//------------------------------------------- constructor
	public AbsenceUserView() {
			this.initComposants();
			this.initWidget(this.buildMainPanel());
	}

	//-------------------------------------------- implements IDisplayAbsence
	public void setUserAndAbsences(UserAndAbsencesDto userAndAbsences) {
		this.clean();
		
		SimpleUserWidget userWidget =
				new SimpleUserWidget(IConstants.STYLE_USER_ONEHISTORY_WIDGET);
		userWidget.setDatas(userAndAbsences.getUserBean());
		this._caseUserWidget.setUserWidget(userWidget);
		
		this._dateRegistration.setValue(userAndAbsences.getUserBean().getRegistration());
		
		List<AbsenceBean> list = userAndAbsences.getListAbsences();
		if (list == null || list.isEmpty()) {
			return;
		}
		
		for (AbsenceBean absenceBean : list) {
			AbsenceUserWidget widget = new AbsenceUserWidget(this._updateAbsenceEventHandler);
			widget.setDatas(absenceBean);
			this._listAbsenceWidget.add(widget);
		}
	}
	
	@Override
	public HasClickHandlers getAddButton() {
		return this._btAddAbsence;
	}

	@Override
	public HasClickHandlers getDeleteAllButton() {
		return this._btDeleleAll;
	}
	@Override
	public HasClickHandlers getCloseButton() {
		return this._btClose;
	}

	@Override
	public void setUpdateAbsenceEventHandler(
			UpdateAbsenceEventHandler updateAbsenceEventHandler) {
		this._updateAbsenceEventHandler = updateAbsenceEventHandler;
		
	}

	//-------------------------------------------- private methods
	private void clean() {
		this._caseUserWidget.clean();
		this._listAbsenceWidget.clear();
	}
	private Widget buildMainPanel() {

		final HorizontalPanel panelButton = new HorizontalPanel();
		panelButton.setSpacing(IConstants.MIN_SPACING);
		panelButton.add(this._btAddAbsence);
		panelButton.add(this._btDeleleAll);
				
		// UserWidget
		this._main.add(this._caseUserWidget);
		// date registration
		this._main.add(WidgetUtils.buildLabelAndWidgetPanel(new Label("registration : "), this._dateRegistration, "80px"));
		this._main.add(panelButton);
        this._main.add(this._listAbsenceWidget);
        this._main.add(this._btClose);
        this._main.setCellHorizontalAlignment(this._btClose, HasHorizontalAlignment.ALIGN_RIGHT);
		
		return this._main;
	}

	private void initComposants() {
		this._btDeleleAll.setEnabled(false);
		this._dateRegistration.setEnabled(false);
		this._dateRegistration.setFormat(new DateBox.DefaultFormat(DateUtils.dateTimeFormat));	
		
	}
	 //============================================ INNER CLASS ===========
    private class CaseUserWidget extends SimplePanel  {
    	    	
    	private void setUserWidget (SimpleUserWidget userWidget) {
    		this.add(userWidget);
    	}
    	private void clean() {
    		this.clear();
    	}
    }




}
