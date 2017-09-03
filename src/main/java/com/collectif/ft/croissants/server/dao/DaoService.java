package com.collectif.ft.croissants.server.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.collectif.ft.croissants.server.util.DateUtils;

public class DaoService {

	
	private final String BACKUP_DIRECTORY = "backup";
	private final String BACKUP_FILENAME = "daomodel.json";
	
	private static final DaoService instance = new DaoService();
	public static final DaoService getInstance() {
		return instance;
	}
	
	private static final Log log = LogFactory.getLog(DaoService.class);

	private DaoService() {}
	
	public void startSaveTimer() {
		
	}
	
	public boolean save(String contextRootPathname, DaoModel daoModel) {
		
		log.info("save(): " + contextRootPathname);
		Date now = DateUtils.getNewUTCDate();
		daoModel.setSaveDate(now);
		final String dataJson = daoModel.toJson();
		
		String backupFile = this.getBackupFilePathname(contextRootPathname);
		
		boolean result = this._save(dataJson, backupFile);	
		
		// deuxième sauvegarde avec timestamp 
		backupFile += "." + now.getTime();
		this._save(dataJson, backupFile);
		
		return result;
	}
	
	public boolean _save(String dataJson, String backupFile) {
		
		
		boolean result = true;
		FileWriter writer = null;
		try {
			writer = new FileWriter(backupFile);
			writer.write(dataJson);
		} catch (IOException e) {
			log.error("error in save() methods : " + e.getMessage());
			result = false;
		}
		finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException ignored) {}
			}
		}
		
		return result;		
	}
	
	public DaoModel restore(String contextRootPathname) {
		
		File backupFile = this.getBackupFile(contextRootPathname);
		if (!backupFile.exists()) {
			return null;
		}
		
		
		final StringBuilder sb = new StringBuilder();
	    BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(getBackupFilePathname(contextRootPathname)));
			String line = null;
			while((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ignored) {}
			}
		}
		if (sb.length() == 0) {
			return new DaoModel();
		}
		
		return DaoModelHelper.getInstance().decodeModel(sb.toString());
		
	}
	

	
	//------------------------------------------------- private methods
	private String getBackupDirPathname(String contextRootPathname) {
		File backupDirFile = new File(contextRootPathname, BACKUP_DIRECTORY);
		this.controlOrCreateBackupDirPathname(backupDirFile);
		return backupDirFile.getAbsolutePath();
	}
	private File getBackupFile(String contextRootPathname) {
		return new File(getBackupDirPathname(contextRootPathname), BACKUP_FILENAME);
	}
	private String getBackupFilePathname(String contextRootPathname) {
		return this.getBackupFile(contextRootPathname).getAbsolutePath();
	}
	private void controlOrCreateBackupDirPathname(File backupDirFile) {
		
		if (!backupDirFile.exists()) {
			backupDirFile.mkdirs();
		}
	}
}
