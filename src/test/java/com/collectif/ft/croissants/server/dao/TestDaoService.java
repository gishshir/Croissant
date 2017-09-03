package com.collectif.ft.croissants.server.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
public class TestDaoService {
	
	private static final Log log = LogFactory.getLog(TestDaoService.class);
	
	private static final DaoService daoService = DaoService.getInstance();
	
	public static final String CONTEXT_ROOT_PATHNAME  = "src\\test\\files";

	@Test
	public void testSave() {
		
		log.info("testSave()");
		this.save();
		
	}
	@Test
	public void testRestore() {
		
		log.info("testRestore()");
		// prerequis
		this.save();
		
		DaoModel daoModel = daoService.restore(new File(CONTEXT_ROOT_PATHNAME).getAbsolutePath());
		assertNotNull(daoModel);
		
		log.info("result: " + daoModel.toJson());
		
	}
		
	private void save() {

		final DaoModel daoModel = TestDaoModel.getInstance().buildModel();
		assertNotNull(daoModel);
		
		boolean result = daoService.save(new File(CONTEXT_ROOT_PATHNAME).getAbsolutePath(), daoModel);
		assertTrue(result);
	}
}
