package com.collectif.ft.croissants.client.view;

import java.util.List;

import com.collectif.ft.croissants.client.event.UpdateFulfillmentEventHandler;
import com.collectif.ft.croissants.client.service.AppController;
import com.collectif.ft.croissants.client.service.GlobalHistoryPresenter.DisplayGlobalHistoy;
import com.collectif.ft.croissants.client.text.MyWording;
import com.collectif.ft.croissants.client.util.IConstants;
import com.collectif.ft.croissants.client.widget.common.DialogBoxContainer;
import com.collectif.ft.croissants.client.widget.history.HistoryWidget;
import com.collectif.ft.croissants.shared.model.dto.HistoryAndUserDto;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Vue de l'historique des taches réalisée
 * Présentation par ordre chronologique
 * Ensemble de HistoryWidget
 * @author sylvie
 *
 */
public class GlobalHistoryView extends Composite implements DisplayGlobalHistoy {
	
	private final static MyWording myWording = AppController.getMyWording();
	
	private final VerticalPanel _main =  new VerticalPanel();
	private final Panel _container = new VerticalPanel();
	private final Panel _historyWidgetTable = new FlowPanel();
	
	private final HorizontalPanel _paginationPanel = new HorizontalPanel();
	private final Button _btFirst = new Button();
	private final Button _btBack = new Button();
	private final Button _btForward = new Button();
	private final Button _btLast = new Button();
	private final Label _labelCurrentPagination = new Label("x/x");
	
    private final Label _labelTitle = new Label(myWording.labelGlobalHistoryTitle());
    
    private UpdateFulfillmentEventHandler _updateFullfilmentEventHandler;
	
	//------------------------------------- constructor
	public GlobalHistoryView() {
		
		this.initComposants();
		this.initWidget(this.buildMainPanel());
	}
	//-------------------------------------------- overriding DisplayGlobalHistoy
	@Override
	public void setHistoryDatas(int currentPage, int maxPage, List<HistoryAndUserDto> historyBeanList) {
		this.clean();

		if (historyBeanList == null || historyBeanList.isEmpty()) {
			return;
		}
		
		this._labelCurrentPagination.setText(currentPage + " / " + maxPage);
		
		for (HistoryAndUserDto historyAndUserDto : historyBeanList) {
			HistoryWidget historyWidget = new HistoryWidget(this._updateFullfilmentEventHandler);
			historyWidget.setDatas(historyAndUserDto);
			this._historyWidgetTable.add(historyWidget);
		}
	}

	@Override
	public HasClickHandlers getFirstPageButton() {
		return this._btFirst;
	}
	@Override
	public HasClickHandlers getBackPageButton() {
		return this._btBack;
	}
	@Override
	public HasClickHandlers getForwardPageButton() {
		return this._btForward;
	}
	@Override
	public HasClickHandlers getLastPageButton() {
		return this._btLast;
	}


	@Override
	public void setUpdateFulfillmentEventHandler(
			UpdateFulfillmentEventHandler handler) {
		this._updateFullfilmentEventHandler = handler;
		
	}
	


	//---------------------------------------------------- public methods
	public void clean() {
		this._historyWidgetTable.clear();
	}

	//---------------------------------------------------- private methods
	private Panel buildMainPanel() {
					
		this._container.add(this.buildPaginationPanel());
		this._container.add(this._historyWidgetTable);
		
		this._main.add(this._labelTitle);
		this._main.add(this._container);
		return this._main;
	}

	private Panel buildPaginationPanel() {
		
		this._paginationPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this._paginationPanel.add(this._btFirst);
		this._paginationPanel.add(this._btBack);
		this._paginationPanel.add(this._labelCurrentPagination);
		this._paginationPanel.add(this._btForward);
		this._paginationPanel.add(this._btLast);
		
		return this._paginationPanel;
		
	}

	private void initComposants() {
		
		this._main.getElement().setId(IConstants.STYLE_MAIN_GLOBAL_HISTORY_VIEW);
		this._container.getElement().setId(IConstants.STYLE_GLOBAL_HISTORY_CONTAINER);
		this._historyWidgetTable.getElement().setId(IConstants.STYLE_HISTORY_WIDGET_LIST);
		this._labelTitle.getElement().setId(IConstants.STYLE_GLOBAL_HISTORY_TITLE);
		
		this._paginationPanel.getElement().setId(IConstants.STYLE_HISTORY_PAGINATION_PANEL_BUTTON);
		this._btFirst.addStyleName(IConstants.STYLE_IMG_FIRST);
		this._btBack.addStyleName(IConstants.STYLE_IMG_BACK);
		this._btForward.addStyleName(IConstants.STYLE_IMG_FORWARD);
		this._btLast.addStyleName(IConstants.STYLE_IMG_LAST);
	}


	

}
