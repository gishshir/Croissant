package com.collectif.ft.croissants.server.service;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class TestSmileysManager {

	private static final Log log = LogFactory.getLog(TestSmileysManager.class); 
	
	@Test
	public void testGetListSmileyUrls() {
		
		final File root = new File(".\\src\\main\\java\\com\\collectif\\ft\\croissants\\public");
		
		log.info("root: " + root.getAbsolutePath());
		
		List<String> result = SmileysManager.getInstance().getListSmileyUrls(root.getAbsolutePath());
		
		for (String name : result) {
			log.info(name);
		}
		
	}
}
