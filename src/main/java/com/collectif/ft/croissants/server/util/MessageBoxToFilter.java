package com.collectif.ft.croissants.server.util;

import com.collectif.ft.croissants.server.business.message.Message;

/**
 * Boite contenant les messages lors de leur creation avant filtrage
 * @author sylvie
 *
 */
public class MessageBoxToFilter {

	private static final MessageBoxToFilter instance = new MessageBoxToFilter();
	public static final MessageBoxToFilter getInstance() {
		return instance;
	}
	
//	private static final Log log = LogFactory.getLog(MessageBoxToFilter.class);

	private MessageBoxToFilter() {}
	
	// -------------------------------------- public methods
	public void addMessage(Message message) {
			// TODO
	}
	public void deleteMessage(Message message) {
			// TODO
	}
}
