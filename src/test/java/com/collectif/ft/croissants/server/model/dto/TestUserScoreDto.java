package com.collectif.ft.croissants.server.model.dto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.collectif.ft.croissants.shared.model.dto.UserScoreDto;

public class TestUserScoreDto {
	
	private static final Log log = LogFactory.getLog(TestUserScoreDto.class); 
	
	private static final Calendar calendar = Calendar.getInstance();
	
	@Test
	public void testTriScoreWithoutDate() {
		
		int id = 0;
		
		List<UserScoreDto> listUserScore = new ArrayList<UserScoreDto>();
		
		listUserScore.add(new UserScoreDto(++id, 3, 1, null));
		listUserScore.add(new UserScoreDto(++id, 1, 0, null));	
		listUserScore.add(new UserScoreDto(++id, 2, 0, null));
		listUserScore.add(new UserScoreDto(++id, 3, 0, null));		
		listUserScore.add(new UserScoreDto(++id, 3, 2, null));
		listUserScore.add(new UserScoreDto(++id, 3, 0, null));
		listUserScore.add(new UserScoreDto(++id, 1, 0, null));
		
		log.info("AVANT TRI...");
		this.logList(listUserScore);
		
		Collections.sort(listUserScore);
		
		log.info("");
		log.info("... APRES TRI");
		this.logList(listUserScore);
	}
	
	@Test
	public void testTriScoreWithDate() {
		
		int id = 0;
		
		List<UserScoreDto> listUserScore = new ArrayList<UserScoreDto>();
		
		
		listUserScore.add(new UserScoreDto(++id, 3, 1, null));
		
		calendar.set(2013, 10, 22);
		listUserScore.add(new UserScoreDto(++id, 1, 0,  calendar.getTime()));	
		listUserScore.add(new UserScoreDto(++id, 2, 0, null));
		
		calendar.set(2013, 8, 9);
		listUserScore.add(new UserScoreDto(++id, 3, 0, calendar.getTime()));		
		listUserScore.add(new UserScoreDto(++id, 3, 2, null));
		
		calendar.set(2013, 7, 3);
		listUserScore.add(new UserScoreDto(++id, 3, 0,  calendar.getTime()));
		
		calendar.set(2013, 10, 15);
		listUserScore.add(new UserScoreDto(++id, 1, 0, calendar.getTime()));
		
		log.info("AVANT TRI...");
		this.logList(listUserScore);
		
		Collections.sort(listUserScore);
		
		log.info("");
		log.info("... APRES TRI");
		this.logList(listUserScore);
	}
	
	private void logList(List<UserScoreDto> listUserScore) {
		
		for (UserScoreDto userScoreDto : listUserScore) {
			log.info(userScoreDto.toString());
		}
	}
	
	
	

}
