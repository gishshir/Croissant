package com.collectif.ft.croissants.client.widget.user;

import com.collectif.ft.croissants.client.util.IConstants;
import com.collectif.ft.croissants.shared.model.dto.UserScoreDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * Présente le score sous forme d'une suite d'icone
 * @author sylvie
 *
 */
public class ScoreLightPanel extends FlowPanel {
	
	private static final String baseImageUrl = GWT.getModuleBaseURL() + "/images/";
	private static final String scoreImageUrl = baseImageUrl + "score.gif";
	private static final String scoreEmptyImageUrl = baseImageUrl + "scoreEmpty.gif";
	
	//-------------------------------------- constructor
	public ScoreLightPanel() {
	}
	public ScoreLightPanel(UserScoreDto userScore) {
		this();
		this.addStyleName(IConstants.STYLE_USER_SCORE_POPUP);
		this.setDatas(userScore);
	}
	
	
	//-------------------------------------- public methods

	//FIXME gerer plus que 5 scores
	// et score Nok
	public void setDatas (UserScoreDto userScore) {

		int scoreOk = userScore.getScoreOk();
		int scoreNok = userScore.getScoreNok();
		
		// score OK
		for (int i = 0; i <  scoreOk; i++) {
           this.add(this.createImage(scoreImageUrl));			
		}
		//score empty
		for (int i = 0; i <  5-scoreOk; i++) {
	           this.add(this.createImage(scoreEmptyImageUrl));			
			}
	}
	
	//------------------------------------------ private methods
    private Image createImage (String url) {
		
		Image image = new Image(url);
		image.addStyleName(IConstants.STYLE_IMG_NO_MARGING);
		image.addStyleName(IConstants.STYLE_PICTO_16X16);
		return image;
	}

}
