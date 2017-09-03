package com.collectif.ft.croissants.client.widget.dispatch;

import java.util.List;

import com.collectif.ft.croissants.client.widget.common.AbstractTaskWidget;
import com.collectif.ft.croissants.shared.model.bean.IBean;
import com.collectif.ft.croissants.shared.model.bean.TaskBean;
import com.collectif.ft.croissants.shared.model.bean.UserBean;
import com.collectif.ft.croissants.shared.model.dto.TaskAndUserDto;

/**
 * Represente une tache incomplete pour la vue de redistribution des participants libres
 * @author sylvie
 *
 */
public class IncompleteTaskWidget extends AbstractTaskWidget<TaskAndUserDto> {
	
	private int _taskId = IBean.ID_UNDEFINED;

	//---------------------------------------------- public methods
	public void setDatas (TaskAndUserDto taskAndUserDto) {
				
		this.clean();
		if (taskAndUserDto == null || taskAndUserDto.getTaskBean() == null) {
			return;
		}
		TaskBean taskBean = taskAndUserDto.getTaskBean();
		this._taskId = taskBean.getId();
		this._dateBox.setValue(taskBean.getDate());
		
		//List des users
		List<UserBean> listUsers = taskAndUserDto.getListUserBeans();
		if (listUsers != null && !listUsers.isEmpty()) {
			
			for (int i = 0; i < listUsers.size(); i++) {
				UserBean userBean = listUsers.get(i);

				DispatchUserWidget disptachUserWidget = new DispatchUserWidget();
				disptachUserWidget.setDatas(userBean);
				
				CaseUserWidget caseUserWidget = this._listCaseUserWidgets.get(i);
				caseUserWidget.setUserWidget(disptachUserWidget);
			}
		}
		
	}
	
	public int getTaskId() {
		return this._taskId;
	}



}
