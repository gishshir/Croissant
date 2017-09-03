package com.collectif.ft.croissants.client.widget.user;

import com.collectif.ft.croissants.shared.model.dto.UserScoreDto;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * A popup to show the score of the user
 * @author sylvie
 *
 */
public class UserScorePopup extends PopupPanel {
	

	public UserScorePopup (UserScoreDto userScore) {
		
		super(true);
        this.setWidget(new ScoreLightPanel(userScore));		
	}

}
