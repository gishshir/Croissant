package com.collectif.ft.croissants.client.widget.dispatch;

import java.util.Date;

import com.collectif.ft.croissants.client.event.ShowUserHistoryEvent;
import com.collectif.ft.croissants.client.event.ShowUserHistoryEventHandler;
import com.collectif.ft.croissants.client.event.ShowUserHistoryEvent.Scope;
import com.collectif.ft.croissants.client.text.MyWording;
import com.collectif.ft.croissants.client.util.DateUtils;
import com.collectif.ft.croissants.client.util.IConstants;
import com.collectif.ft.croissants.client.widget.user.ScoreLightPanel;
import com.collectif.ft.croissants.shared.model.bean.IBean;
import com.collectif.ft.croissants.shared.model.bean.UserBean;
import com.collectif.ft.croissants.shared.model.dto.UserAndScoreDto;
import com.collectif.ft.croissants.shared.model.dto.UserScoreDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Représente un utilisateur et son score
 * Possibilité de choisir ou non le user par case à cocher
 * Utilisé dans la liste de redistribution des user free
 * @author sylvie
 *
 */
public class UserScoreToDispatchWidget extends Composite {
	
	private final static MyWording myWording = GWT.create(MyWording.class);
	
	private final CheckBox _cbSelect = new CheckBox();
	private final DispatchUserWidget _dispatchUserWidget = new DispatchUserWidget();
	private final ScoreLightPanel _scoreLightPanel = new ScoreLightPanel();
	private final Label _labelLastTaskDate = new Label();
	private final Label _labelParticipation = new Label();
	
	private final Button _btShowHistory = new Button();
	

	private int _userId = IBean.ID_UNDEFINED;
	
	//------------------------------------------- constructor
	public UserScoreToDispatchWidget(final ShowUserHistoryEventHandler showUserHistoryEventHandler) {
		this.initComposants();
		this.initHandlers(showUserHistoryEventHandler);
		this.initWidget(this.buildMainPanel());
	}
	//---------------------------------------------- public methods
	public void setDatas (UserAndScoreDto userAndScoreDto) {
			
		// User information
		UserBean userBean = userAndScoreDto.getUser();
		UserScoreDto userScore = userAndScoreDto.getScore();
		
		this._userId = userBean.getId();
		this._dispatchUserWidget.setDatas(userBean);
		
		// score information
		this._scoreLightPanel.setDatas(userScore);
		if (userScore.getComment() != null) {
			this._scoreLightPanel.setTitle(userScore.getComment());
		}
		
		// last task date information
		Date lastRealizedTask = userScore.getLastRealisedTask();
		this._labelLastTaskDate.setText("(" + DateUtils.getSmallLabelDate(lastRealizedTask) + ")");
		
		// participation
		this._labelParticipation.setText("[" +userScore.getRelativeParticipation() + "]");
		if (userScore.getComment() != null) {
			this._labelParticipation.setTitle(userScore.getComment());
		}
		
		// user not free >> no select
		if (!userAndScoreDto.isFreeUser()) {
			this._cbSelect.setValue(false);
			this._cbSelect.setEnabled(false);
			this._cbSelect.setTitle(myWording.infoUserNotFree());
		}
	}

	public boolean isUserSelected() {
		return this._cbSelect.getValue();
	}
	public int getUserId() {
		return this._userId;
	}
	//--------------------------------------------- private methods
	private Widget buildMainPanel() {

		final HorizontalPanel userScorePanel = new HorizontalPanel();
		userScorePanel.setSpacing(IConstants.MIN_SPACING);
		userScorePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		userScorePanel.add(this._cbSelect);
		userScorePanel.add(this._dispatchUserWidget);
		userScorePanel.add(this._labelParticipation);
		userScorePanel.add(this._scoreLightPanel);
		userScorePanel.add(this._labelLastTaskDate);
        userScorePanel.add(this._btShowHistory);
		
		return userScorePanel;
	}

	private void initHandlers(final ShowUserHistoryEventHandler showUserHistoryEventHandler) {

		// show history
		this._btShowHistory.addClickHandler(new ClickHandler() {
					
			@Override
			public void onClick(ClickEvent event) {
				showUserHistoryEventHandler.onShowUserHistory(new ShowUserHistoryEvent(_userId, Scope.showHistory, _btShowHistory));
			}
		});
	}

	private void initComposants() {
		this._cbSelect.setValue(true);
		this._labelLastTaskDate.addStyleName(IConstants.STYLE_DISPATCH_USER_LABEL);
		this._labelParticipation.addStyleName(IConstants.STYLE_DISPATCH_USER_LABEL);
		
		this._labelLastTaskDate.setTitle(myWording.infoLastRealizedTask());
		this._labelParticipation.setTitle(myWording.infoParticipation());
		
		this._btShowHistory.addStyleName(IConstants.STYLE_IMG_DETAIL);
		this._btShowHistory.setTitle(myWording.infoScoreUser());
	}




}
