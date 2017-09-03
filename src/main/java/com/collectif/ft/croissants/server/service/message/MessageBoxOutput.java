package com.collectif.ft.croissants.server.service.message;

import java.util.ArrayList;
import java.util.List;

import com.collectif.ft.croissants.server.business.message.Message;
import com.collectif.ft.croissants.server.business.message.Message.MessageState;

/**
 * Message prets � partir par ordre de priorit�
 * @author sylvie
 *
 */
class MessageBoxOutput {

	private final List<Message> _listMessageUrgent =  new ArrayList<Message>();
	private final List<Message> _listMessageMedium =  new ArrayList<Message>();
	private final List<Message> _listMessageSlow =  new ArrayList<Message>();

	//------------------------------------------ public method
	public void addMessage(Message message) {
		
		message.setState(MessageState.waiting);
		switch (message.getPriority()) {
		case urgent: this._listMessageUrgent.add(message);
			break;
		case normal: this._listMessageMedium.add(message);
			break;
		case slow: this._listMessageSlow.add(message);
			break;
		}
	}
	
	public boolean isEmpty() {
		return this._listMessageMedium.isEmpty() && this._listMessageUrgent.isEmpty() &&
				this._listMessageSlow.isEmpty();
	}
	public Message depileNextMessage() {
		
		if (!this._listMessageUrgent.isEmpty()) {
			return this.depileFromList(this._listMessageUrgent);
		}
		else if (!this._listMessageMedium.isEmpty()) {
			return this.depileFromList(this._listMessageMedium);
		}
		else if (!this._listMessageSlow.isEmpty()) {
			return this.depileFromList(this._listMessageSlow);
		}
		return null;
	}
	
	//---------------------------------------- private methods
	private Message depileFromList(List<Message> list) {
		
		return list.remove(0);
	}
}
