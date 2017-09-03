package com.collectif.ft.croissants.client.widget.dispatch;

import java.util.ArrayList;
import java.util.List;

import com.collectif.ft.croissants.client.util.IConstants;
import com.collectif.ft.croissants.shared.model.dto.TaskAndUserDto;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * liste de taches incomplete  pour la vue de redistribution des participants libres
 * @author sylvie
 *
 */
public class IncompleteTaskContainer extends Composite {

	private Panel _main = new SimplePanel();
    private VerticalPanel _widgetTable = new VerticalPanel();
	
	//------------------------------------ constructor
	public IncompleteTaskContainer() {
		this.initComposants();
		this.initHandlers();
		this.initWidget(this.buildMainPanel());
	}
	
	//------------------------------------ public methods
	public void setDatas (List<TaskAndUserDto> listTaskAndUserDto) {
			
			this.clean();
			if (listTaskAndUserDto == null) {
				return;
			}
			
			for (TaskAndUserDto taskAndUserDto : listTaskAndUserDto) {
				
				final IncompleteTaskWidget incompleteTaskWidget = new IncompleteTaskWidget();
				incompleteTaskWidget.setDatas(taskAndUserDto);
				this._widgetTable.add(incompleteTaskWidget);
			}
			
	}

	public List<Integer> getListOfIncompleteTasks() {
		
		
		final List<Integer> list = new ArrayList<Integer>();
		
		int count = this._widgetTable.getWidgetCount();
		for (int i = 0; i < count; i++) {
			Widget widget = this._widgetTable.getWidget(i);
			if (widget == null) {
				continue;
			}
			IncompleteTaskWidget incompleteTaskWidget = (IncompleteTaskWidget)widget;
			list.add(incompleteTaskWidget.getTaskId());
			
		}
		
		return list;
	}
	//------------------------------------- private methods
	private void clean() {
		this._widgetTable.clear();
	}
		private Widget buildMainPanel() {
			this._main.add(this._widgetTable);
			this._main.getElement().setId(IConstants.STYLE_INCOMPLETE_TASK_CONTAINER);
			
			this._widgetTable.getElement().setId(IConstants.STYLE_INCOMPLETE_TASK_LIST);
			return this._main;
		}

		private void initHandlers() {
			// TODO Auto-generated method stub
			
		}

		private void initComposants() {
			// TODO Auto-generated method stub
		}
}
