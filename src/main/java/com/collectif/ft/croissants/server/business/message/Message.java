package com.collectif.ft.croissants.server.business.message;

import java.io.Serializable;
import java.util.Date;

import com.collectif.ft.croissants.server.util.DateUtils;

/**
 * Message à envoyer à un utilisateur par email
 * @author sylvie
 *
 */
public class Message implements Serializable{
	

	private static final long serialVersionUID = 1L;

	
	// =================================== MessageState
	public enum MessageState {
		created, waiting, send, error, canceled;
	}

	// =================================== MessagePriority
	public enum MessagePriority {
		urgent, normal, slow;
	}
	
	
	//==================================== MessageLevel
	public enum MessageLevel {
		error, warn, info;
	}
	
	//==================================== Message
	
	private final Date _date;
	private final String _subject;
	
	private final MessageContent _messageContent;
	private final MessagePriority _priority;
	private final MessageLevel _level;
	
	private final String _email;
	
	private MessageState _state = MessageState.created;
	
	//-------------------------------------------------- accessors
	public MessageState getState() {
		return this._state;
	}
	public void setState(final MessageState state) {
		this._state = state;
	}
	public MessageContent getContent() {
		return this._messageContent;
	}
	public String getEmail() {
		return this._email;
	}
	public MessageLevel getLevel() {
		return this._level;
	}	
	public MessagePriority getPriority() {
		return this._priority;
	}
	public Date getDate() {
		return this._date;
	}
	public String getSubject() {
		return this._subject;
	}
	//-------------------------------------------------- constructor
	public Message(final String email, String subject,
			final MessageContent content, final MessagePriority priority, final MessageLevel level) {
		this._date = DateUtils.getNewUTCDate();
		this._subject = subject;
		this._email = email;
		this._messageContent = content;
		this._level = level;
		this._priority = priority;
	}
	
	//-------------------------------------------------- overriding object
	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
		sb.append("date: " + this._date);
		sb.append(" email: " + this._email);
		sb.append(" subject: " + this._subject);
		sb.append(" content: " + this._messageContent);
		return sb.toString();
	}
}
