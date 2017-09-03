package com.collectif.ft.croissants.client.service;

import java.util.List;
import java.util.logging.Logger;

import com.collectif.ft.croissants.client.event.ErrorEvent;
import com.collectif.ft.croissants.client.event.UpdateFulfillmentEvent;
import com.collectif.ft.croissants.client.event.UpdateFulfillmentEventHandler;
import com.collectif.ft.croissants.shared.model.bean.IBean;
import com.collectif.ft.croissants.shared.model.dto.HistoryAndUserDto;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class GlobalHistoryPresenter implements IPresenter {
	
	private final static Logger log = Logger.getLogger("GlobalHistoryPresenter");

	
	private final ICroissantServiceAsync _rpcService;
	private final DisplayGlobalHistoy _view;
	private final HandlerManager _eventBus;
	
	private int _oldPage = 0;
	private int _currentPage = 1;
	private int _maxPage;
	
	//--------------------------------- constructor
	public GlobalHistoryPresenter (ICroissantServiceAsync rpcService, HandlerManager eventBus, DisplayGlobalHistoy view) {
			this._rpcService = rpcService;
			this._eventBus = eventBus;
			this._view = view;
	}
	
	//--------------------------------------- overriding IPresenter
	/**
	 * Attache la vue au container
	 * Récupère les history auprès du service et les transmet  a la vue
	 */
	@Override
	public void go(HasWidgets container, PresenterCallback presenterCallback) {
		this.bind();
        container.clear();
        container.add(this._view.asWidget());
        this.fetchAll();
	}

	@Override
	public void refresh(IBean bean) {
		// TODO Auto-generated method stub

	}
	
	
	//--------------------------------------------- private methods
	private void bind() {
		
		// modification de la realisation
		this._view.setUpdateFulfillmentEventHandler(new UpdateFulfillmentEventHandler() {
			
			@Override
			public void onUpdateFulfillment(UpdateFulfillmentEvent event) {
				doUpdatefulfillment(event);
			}
		});
		
		// pagination
		this._view.getFirstPageButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				_currentPage = 1;
				fetchListHistoryOnCurrentPage();
			}
		});
		
		this._view.getBackPageButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				_currentPage--;
				_currentPage = (_currentPage < 1)?1:_currentPage;
				fetchListHistoryOnCurrentPage();
			}
		});
		
		this._view.getForwardPageButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				_currentPage++;
				_currentPage = (_currentPage > _maxPage)?_maxPage:_currentPage;
				fetchListHistoryOnCurrentPage();
			}
		});
		
		this._view.getLastPageButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				_currentPage = _maxPage;
				fetchListHistoryOnCurrentPage();
			}
		});
		
	}


	private void fetchAll() {

		this._oldPage = 0;
		this._currentPage = 1;
		this._maxPage = 1;
		
		this._rpcService.getHistoryPageCount(new ServiceCallback<Integer>(this._eventBus) {

			@Override
			public void onSuccess(final Integer maxCount) {
				_maxPage = maxCount;
				fetchListHistoryOnCurrentPage();
			}
		});
			
	}
	
	private void fetchListHistoryOnCurrentPage () {
		
		if (this._oldPage == this._currentPage) {
			return;
		}
        this._rpcService.loadListHistoryAndUserDto(this._currentPage, new ServiceCallback<List<HistoryAndUserDto>>(this._eventBus) {

			@Override
			public void onSuccess(List<HistoryAndUserDto> result) {
				_view.setHistoryDatas(_currentPage, _maxPage, result);
				_oldPage = _currentPage;
			}
		});
	}
	
	private void doUpdatefulfillment(final UpdateFulfillmentEvent event) {
		
		this._rpcService.updateUserFulfillment(event.getUserId(), event.getDate(),
				event.isFullfilment(), new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						// do nothing
						
					}
					@Override
					public void onFailure(Throwable caught) {
						log.severe("onFailure: " + caught.getMessage());
						_eventBus.fireEvent(new ErrorEvent(caught.getMessage(), null, event.getAnkor()));
						
						// en cas d'erreur rétablir
						fetchListHistoryOnCurrentPage();
					}
		});
	}

	//====================================== INNER CLASS ========
	
	public interface DisplayGlobalHistoy {
		
		
		// la vue est un composite 
		public Widget asWidget();
		
		public void setHistoryDatas (int currentPage, int maxPage,   final List<HistoryAndUserDto> historyBeanList);
		
		public void setUpdateFulfillmentEventHandler(UpdateFulfillmentEventHandler handler);
		
		public HasClickHandlers getFirstPageButton();
		public HasClickHandlers getBackPageButton();
		public HasClickHandlers getForwardPageButton();
		public HasClickHandlers getLastPageButton();
		
		
	}

}
