package com.collectif.ft.croissants.server.business.message;

/**
 * Bloc constitutif d'un email.
 * @author sylvie
 *
 */
public class MessageBlock {
	
	public enum MessageType { html, image, sautLigne, lien}
	
	public enum AlignBlock {left, center, right}
	
	private final MessageType _messageType;
	public MessageType getMessageType() {
		return this._messageType;
	}
	
	private final String _value;
	// largeur en nbre de cell du block dans le tableau final
	private final int _colspan;
	
	private  String _style = "color:magenta; font-weight:bold;";
	private AlignBlock _alignBlock = null;

	//-------------------------------------- public method
	public void alignCenter() {
		this._alignBlock = AlignBlock.center;
	}
	public AlignBlock getAlignBlock() {
		return this._alignBlock;
	}
	public void setStyle(String style) {
		this._style = style;
	}
	public String getHtmlValue(String id) {
		
		switch (this._messageType) {

		case html: return "<div style=\"" + this._style + "\">" +
                this._value + "</div>";

		case image: return "<img src=\"cid:" + id + "\"/>";
		
		case lien : return "<div style=\"font-size: 0.8em;\"><a href=\"" + this._value + "\">" + this._value  + "</a></div>";
			
		}
		
		return null;
	}
	public int getColSpan() {
		return this._colspan;
	}
	public String getValue() {
		return this._value;
	}
	//-------------------------------------- constructor
	public MessageBlock (final MessageType type, final String value) {
		this(type, value, 1);
	}
	public MessageBlock (final MessageType type, final String value, final int colspan) {
		this._value = value;
		this._colspan = colspan;
		this._messageType = type;
	}
	//--------------------------------------------------- overriding Object
	@Override
	public String toString() {

		if (this._messageType == MessageType.html || this._messageType == MessageType.lien) {
			return this._value;
		}
		return "";
	}
	
	

}
