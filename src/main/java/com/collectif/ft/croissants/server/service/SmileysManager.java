package com.collectif.ft.croissants.server.service;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SmileysManager {
	
	private static final SmileysManager instance = new SmileysManager();
	public static final SmileysManager getInstance() {
		return instance;
	}
	private SmileysManager() {}
	
	public static final String SMILEYS_DIRECTORY = "/smileys/";
	
	
	private static final Pattern _pattern = Pattern.compile("(\\.jpg$)|(\\.png$)|(\\.gif$)|(\\.jpeg$)");
	
	
	private final static FileFilter mySmileysFilter = new ImageFileFilter();
	
	//-------------------------------------------- public methods
	
  public List<String> getListSmileyUrls(String moduleRootPath) {
	
	
		
		final List<String> list = new ArrayList<String>();
		
		final File dirSmileys = new File(moduleRootPath + SMILEYS_DIRECTORY);
        if (!dirSmileys.exists() || !dirSmileys.isDirectory()) {
        	return list;
        }
        
        File[] tabSmileys = dirSmileys.listFiles(mySmileysFilter);
        if (tabSmileys == null || tabSmileys.length == 0) {
        	return list;
        }
        
        for (File file : tabSmileys) {
			list.add(file.getName());
		}
		
		return list;
	}
	

//============================== INNER CLASS =====================
private static class ImageFileFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		if (!file.exists() || file.isDirectory()) {
			return false;
		}
		
		final String name = file.getName();
		return _pattern.matcher(name).find();
	}
	
}

}
