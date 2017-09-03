package com.collectif.ft.croissants.client.widget.absence;

import java.util.Date;

import com.collectif.ft.croissants.client.event.UpdateAbsenceEvent;
import com.collectif.ft.croissants.client.event.UpdateAbsenceEventHandler;
import com.collectif.ft.croissants.client.event.UpdateActionType;
import com.collectif.ft.croissants.client.text.MyWording;
import com.collectif.ft.croissants.client.util.DateUtils;
import com.collectif.ft.croissants.client.util.IConstants;
import com.collectif.ft.croissants.shared.model.bean.AbsenceBean;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * Représente une absence sous la forme d'un intervalle de dates
 * Possibilité de modifier les dates ou de supprimer l'absence
 * @author sylvie
 *
 */
public class AbsenceUserWidget extends Composite {
	
	protected final static MyWording myConstants = GWT.create(MyWording.class);

	private final HorizontalPanel _main = new HorizontalPanel();
	private final DateBox _beginDateBox = new DateBox();
	private final DateBox _endDateBox = new DateBox();
    private final Button _btDeleteAbsence = new Button();
    
	private AbsenceBean _absenceBean;
	//----------------------------------- constructor
	public AbsenceUserWidget(UpdateAbsenceEventHandler updateAbsenceEventHandler) {
		this.initWidget(this.buildMainPanel());
		this.initHandlers(updateAbsenceEventHandler);
		this.initComposants();
	}

	//---------------------------------------- public methods
	public void setDatas(AbsenceBean absenceBean) {
		
		this._absenceBean = absenceBean;
		this._beginDateBox.setValue(absenceBean.getBeginDate());
		this._endDateBox.setValue(absenceBean.getEndDate());
	}
	//---------------------------------------- private methods
	private Widget buildMainPanel() {
		this._main.setSpacing(IConstants.MIN_SPACING);
		this._main.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this._main.add(new Label("from"));
		this._main.add(this._beginDateBox);
		this._main.add(new Label("to"));
		this._main.add(this._endDateBox);
		this._main.add(this._btDeleteAbsence);
		
		return this._main;
	}
	
	private void initHandlers(final UpdateAbsenceEventHandler updateAbsenceEventHandler) {
		
		// modify begin date
		this._beginDateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				
				if (DateUtils.isSameDay(_absenceBean.getBeginDate(), event.getValue())) {
					return;
				}
				//memorise date and offset
				_absenceBean.updateBeginDate(event.getValue(), DateUtils.offset);
				updateAbsenceEventHandler.onUpdateAbsence(
				   new UpdateAbsenceEvent(_absenceBean, AbsenceUserWidget.this, UpdateActionType.update));
			  }
		});
		
		// modify end date
		this._endDateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
					
				@Override
				public void onValueChange(ValueChangeEvent<Date> event) {
						
					if (DateUtils.isSameDay(_absenceBean.getEndDate(), event.getValue())) {
							return;
					}
					//memorise date and offset
					_absenceBean.updateEndDate(event.getValue(), DateUtils.offset);
					updateAbsenceEventHandler.onUpdateAbsence(
						new UpdateAbsenceEvent(_absenceBean, AbsenceUserWidget.this, UpdateActionType.update));
					}
				});
		
		// Suppression
		this._btDeleteAbsence.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				updateAbsenceEventHandler.onUpdateAbsence(
						new UpdateAbsenceEvent(_absenceBean, AbsenceUserWidget.this, UpdateActionType.delete));
					}
			
		});
		
	}
	private void initComposants() {
		this._btDeleteAbsence.addStyleName(IConstants.STYLE_IMG_DELETE_ABSENCE);
		this._btDeleteAbsence.setTitle(myConstants.infoDeleteAbsence());
		
		this._beginDateBox.setFormat(new DateBox.DefaultFormat(DateUtils.dateTimeFormat));
		this._endDateBox.setFormat(new DateBox.DefaultFormat(DateUtils.dateTimeFormat));
		
	}
}
