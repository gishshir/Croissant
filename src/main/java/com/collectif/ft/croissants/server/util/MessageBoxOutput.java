package com.collectif.ft.croissants.server.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.collectif.ft.croissants.server.business.message.Message;

/**
 * Boite contenant les messages � expedier
 * @author sylvie
 *
 */
public class MessageBoxOutput {

	private static final MessageBoxOutput instance = new MessageBoxOutput();
	public static final MessageBoxOutput getInstance() {
		return instance;
	}
	
	//private static final Log log = LogFactory.getLog(MessageBoxOutput.class);

	private MessageBoxOutput() {}
	
	// -------------------------------------- public methods
	public void addMessage(Message message) {
		// TODO
	}
	public void deleteMessage(Message message) {
		// TODO
	}
}
