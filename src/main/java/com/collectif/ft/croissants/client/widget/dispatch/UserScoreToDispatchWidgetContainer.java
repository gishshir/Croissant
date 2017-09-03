package com.collectif.ft.croissants.client.widget.dispatch;

import java.util.ArrayList;
import java.util.List;

import com.collectif.ft.croissants.client.event.ShowUserHistoryEventHandler;
import com.collectif.ft.croissants.client.util.IConstants;
import com.collectif.ft.croissants.shared.model.dto.UserAndScoreDto;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Contient une liste de UserToDispatchWidget
 * liste des utilisateurs free classé par score croissant
 * 
 *  Utilisé dans la liste de redistribution des user free
 * @author sylvie
 *
 */
public class UserScoreToDispatchWidgetContainer extends Composite {

	private Panel _main = new SimplePanel();
    private VerticalPanel _widgetTable = new VerticalPanel();
    
    private ShowUserHistoryEventHandler _showUserHistoryEventHandler;
	
	//------------------------------------ constructor
	public UserScoreToDispatchWidgetContainer() {
		this.initComposants();
		this.initHandlers();
		this.initWidget(this.buildMainPanel());
	}
	
	//------------------------------------ public methods
	public void setShowUserHistoryEventHandler(final ShowUserHistoryEventHandler showUserHistoryEventHandler) {
		this._showUserHistoryEventHandler = showUserHistoryEventHandler;
	}
	public void setDatas (List<UserAndScoreDto> listUserAndScoreDto) {
		
		this.clean();
		if (listUserAndScoreDto == null) {
			return;
		}
		
		for (UserAndScoreDto userAndScoreDto : listUserAndScoreDto) {
			
			final UserScoreToDispatchWidget scoreWidget = new UserScoreToDispatchWidget(this._showUserHistoryEventHandler);
			scoreWidget.setDatas(userAndScoreDto);
			this._widgetTable.add(scoreWidget);
		}
		
	}
	public void clean() {
		this._widgetTable.clear();
	}
	/**
	 * Retourne la liste des ids des utilisateurs selectionnés
	 * @return
	 */
	public List<Integer> getListOfSelectedUsers() {
		
		final List<Integer> list = new ArrayList<Integer>();
		
		int count = this._widgetTable.getWidgetCount();
		for (int i = 0; i < count; i++) {
			Widget widget = this._widgetTable.getWidget(i);
			if (widget == null) {
				continue;
			}
			UserScoreToDispatchWidget userScoreToDispatchWidget = (UserScoreToDispatchWidget)widget;
			if (userScoreToDispatchWidget.isUserSelected()) {
				list.add(userScoreToDispatchWidget.getUserId());
			}
		}
		
		return list;
	}
	//------------------------------------- private methods

	private Widget buildMainPanel() {
		this._main.add(this._widgetTable);
		this._main.getElement().setId(IConstants.STYLE_DISPATCH_USER_CONTAINER);
		
		this._widgetTable.getElement().setId(IConstants.STYLE_DISPATCH_USER_LIST);
		return this._main;
	}

	private void initHandlers() {
		// TODO Auto-generated method stub
		
	}

	private void initComposants() {
		// TODO Auto-generated method stub
	}

}
