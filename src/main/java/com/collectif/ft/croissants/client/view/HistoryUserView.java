package com.collectif.ft.croissants.client.view;

import java.util.Date;

import com.collectif.ft.croissants.client.service.AppController;
import com.collectif.ft.croissants.client.service.HistoryUserPresenter.DisplayUserHistory;
import com.collectif.ft.croissants.client.text.MyWording;
import com.collectif.ft.croissants.client.util.DateUtils;
import com.collectif.ft.croissants.client.util.IConstants;
import com.collectif.ft.croissants.client.widget.user.SimpleUserWidget;
import com.collectif.ft.croissants.shared.model.dto.UserHistoryDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * Panel listant l'historique d'un utilisateur
 * @author sylvie
 *
 */
public class HistoryUserView extends Composite implements DisplayUserHistory {
	
	private final static MyWording myWording = AppController.getMyWording();
	
	private static final String baseImageUrl = GWT.getModuleBaseURL() + "/images/";
	private static final String okImageUrl = baseImageUrl + "Ok.png";
	private static final String nOkImageUrl = baseImageUrl + "Nok.png";
	

	private final CaseUserWidget _caseUserWidget = new CaseUserWidget();
	private final VerticalPanel _listDate = new VerticalPanel();
	
	private Button _btClose = new Button("close");

	
	//---------------------------------- constructor
	public HistoryUserView() {
		this.initWidget(this.buildMainPanel());
	}

	
	//--------------------------------- overriding DisplayUserHistory

	@Override
	public HasClickHandlers getCloseButton() {
		return this._btClose;
	}

	@Override
	public void setHistory(UserHistoryDto userHistory) {
		this._caseUserWidget.clean();
		this._listDate.clear();
		
		SimpleUserWidget historyUserWidget =
                new SimpleUserWidget(IConstants.STYLE_USER_ONEHISTORY_WIDGET);
		historyUserWidget.setDatas(userHistory.getUserBean());
		
		this._caseUserWidget.setHistoryUserWidget(historyUserWidget);
		
		for (Date date : userHistory.getDateList()) {

			HorizontalPanel panelDate = new HorizontalPanel();
			panelDate.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
			panelDate.add(this.buildImage( userHistory.isDateFulfilled(date)));
			panelDate.add(this.buildtDateBox(date));
			
			
			this._listDate.add(panelDate);
		}
	}
	
	
	//---------------------------------- private methods
	private Panel buildMainPanel() {
		
		final VerticalPanel main = new VerticalPanel();
		main.setSpacing(IConstants.MIN_SPACING);
		
		// UserWidget
		main.add(this._caseUserWidget);
		
		// list of dates
		this._listDate.setSpacing(IConstants.MIN_SPACING);
		main.add(this._listDate);
		
		// panel button
		final HorizontalPanel panelButton = new HorizontalPanel();
		panelButton.setSpacing(IConstants.MIN_SPACING);
		panelButton.setWidth(IConstants.MAX_SIZE);
		panelButton.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		
		main.add(panelButton);
		
		panelButton.add(this._btClose);
		return main;
	}
	
	private Image buildImage(boolean fulfillment) {
		
		Image image = new Image();
		image.setUrl((fulfillment)?okImageUrl:nOkImageUrl);
		image.setTitle((fulfillment)?myWording.infoTaskOkFulfilment():myWording.infoTaskNokFulfilment());
		image.addStyleName(IConstants.STYLE_PICTO_16X16);
		return image;
	}
	
	private DateBox buildtDateBox(Date date) {
		
		DateBox dateBox = new DateBox();
		dateBox.setFormat(new DateBox.DefaultFormat(DateUtils.dateTimeFormat));	
		dateBox.addStyleName(IConstants.STYLE_TASK_DATEBOX);
		dateBox.addStyleName(IConstants.STYLE_TASK_DATEBOX_NOPAST);
		dateBox.setEnabled(false);
		dateBox.setValue(date);
		return dateBox;
	}
	
	 //============================================ INNER CLASS ===========
    private class CaseUserWidget extends SimplePanel  {
    	    	
    	private void setHistoryUserWidget (SimpleUserWidget historyUserWidget) {
    		this.add(historyUserWidget);
    	}
    	private void clean() {
    		this.clear();
    	}
    }
}
