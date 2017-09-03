package com.collectif.ft.croissants.server.service;

import org.junit.Test;

import com.collectif.ft.croissants.server.dao.TestDaoService;

public class TestCroissantManager {
	
	@Test
	public void testRestoreAndControlDaoModel () {
		
		String contextRootPath = TestDaoService.CONTEXT_ROOT_PATHNAME;
		CroissantManager.getInstance().restoreAndControlDaoModel(contextRootPath);
	}
}
