package com.collectif.ft.croissants.client.widget.user;

import java.util.ArrayList;
import java.util.List;

import com.collectif.ft.croissants.client.util.IConstants;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * List des smileys pour EditUserView
 * @author sylvie
 *
 */
public class GallerySmileyWidget extends Composite {
	
	private static final String baseImageUrl = GWT.getModuleBaseURL() + "/smileys/";
	
	private final Panel _main = new SimplePanel();
	private final VerticalPanel _panelImages = new VerticalPanel();
	
	public GallerySmileyWidget() {
		this.initWidget(this.buildMainPanel());
	}
	//-------------------------------------- public methods
	public void setImages(List<String> listUrls) {
		
		if (listUrls == null || listUrls.isEmpty()) {
			return;
		}
		this.clear();
		// on construit dynamiquement la liste des images
		for (String logo : listUrls) {
			PanelImage panelImage = new PanelImage(logo);
			this._panelImages.add(panelImage);
		}
	}
	//-------------------------------------- public methods
	public void setSelectedLogo(String logo) {
		
		if (logo == null) {
			return;
		}
		for (int i = 0; i < this._panelImages.getWidgetCount(); i++) {
			Widget widget = this._panelImages.getWidget(i);
			
			PanelImage panel = (PanelImage)widget;
			if (panel.getLogo().equals(logo)) {
              this.selectPanelImage(panel);
              break;
			}	
		}
	}
	public String getSelectedLogo() {
		
		for (int i = 0; i < this._panelImages.getWidgetCount(); i++) {
			Widget widget = this._panelImages.getWidget(i);
			
			PanelImage panel = (PanelImage)widget;
			if (panel.isSelected()) {
               return panel.getLogo();
			}	
		}
		return null;
	}
	//-------------------------------------- private methods
	private void clear() {
		this._panelImages.clear();
	}
	private String buildUrl(String name) {
		return baseImageUrl + name;
	}
	private Panel buildMainPanel() {
		
		this._panelImages.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this._panelImages.setSpacing(IConstants.MIN_SPACING);
		
		this._main.addStyleName(IConstants.STYLE_GALLERY_WIDGET);
		this._main.add(this._panelImages);
		
		return this._main;
	}

	private void selectPanelImage (PanelImage selectedPanel) {
		
		selectedPanel.setSelected(true);
		for (int i = 0; i < this._panelImages.getWidgetCount(); i++) {
			Widget widget = this._panelImages.getWidget(i);
			
			PanelImage panel = (PanelImage)widget;
			if (panel != selectedPanel) {
				panel.setSelected(false);
			}
			
		}
	}
	//=========================== INNER CLASS ==================
	private class PanelImage extends SimplePanel {
		
		private boolean _selected = false;
		private final String _logo;
		
		boolean isSelected() {
			return this._selected;
		}
		String getLogo() {
			return this._logo;
		}
		void setSelected(boolean selected) {
			this._selected = selected;
			if (selected) {
			  this.addStyleName(IConstants.STYLE_GALLERY_IMG_SELECTED);
			}
			else {
				 this.removeStyleName(IConstants.STYLE_GALLERY_IMG_SELECTED);
			}
		}
		PanelImage(String logo) {
			this._logo = logo;
			final Image image = new Image(buildUrl(logo));
			this.add(image);
			this.addStyleName(IConstants.STYLE_GALLERY_IMG);
			this.initHandler();
		}
		
		private void initHandler() {
			this.addDomHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					selectPanelImage(PanelImage.this);
				}
			}, ClickEvent.getType() );
		}
	}

}
