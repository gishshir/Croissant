package com.collectif.ft.croissants.client.widget.history;

import java.util.List;

import com.collectif.ft.croissants.client.event.UpdateFulfillmentEventHandler;
import com.collectif.ft.croissants.client.util.IConstants;
import com.collectif.ft.croissants.client.widget.common.AbstractTaskWidget;
import com.collectif.ft.croissants.shared.model.bean.HistoryBean;
import com.collectif.ft.croissants.shared.model.bean.UserBean;
import com.collectif.ft.croissants.shared.model.dto.HistoryAndUserDto;
import com.google.gwt.user.client.ui.Button;

/**
 * Représente une tache réalisée avec ses participants
 * Pour chaque participant on indique si la tache a été réalisée ou non
 * Widget readonly sauf 48 après task où il est possible de déterminer si la tache assignée à chacun
 * a réellement été réalisée.
 * @author sylvie
 *
 */
public class HistoryWidget extends AbstractTaskWidget<HistoryAndUserDto> {
	
	private final UpdateFulfillmentEventHandler _editFullfilmentEventHandler;
	
	//---------------------------------------------- constructor
	public HistoryWidget(UpdateFulfillmentEventHandler editFullfilmentEventHandler) {
		this._editFullfilmentEventHandler = editFullfilmentEventHandler;
	}
	
	//---------------------------------------------- override AbstractTaskWidget
	@Override
	public void setDatas (HistoryAndUserDto historyAndUserDto) {
				
		this.clean();
		if (historyAndUserDto == null || historyAndUserDto.getHistoryBean() == null) {
			return;
		}
		HistoryBean historyBean = historyAndUserDto.getHistoryBean();
		this._dateBox.setValue(historyBean.getDate());
		
		//List des users
		List<UserBean> listUsers = historyAndUserDto.getListUserBeans();
		if (listUsers != null && !listUsers.isEmpty()) {
			
			for (int i = 0; i < listUsers.size(); i++) {
				UserBean userBean = listUsers.get(i);
				boolean fulfillment = historyBean.isFulfillment(userBean.getId());

				boolean editable = historyAndUserDto.isEditable();
				HistoryUserWidget historyUserWidget =
						new HistoryUserWidget(historyBean.getDate(), true, editable, IConstants.STYLE_USER_HISTORY_WIDGET,
								(editable)?this._editFullfilmentEventHandler:null);
				historyUserWidget.setDatas(userBean, fulfillment);
				
				CaseUserWidget caseUserWidget = this._listCaseUserWidgets.get(i);
				caseUserWidget.setUserWidget(historyUserWidget);
			}
		}
		
	}
	

}
