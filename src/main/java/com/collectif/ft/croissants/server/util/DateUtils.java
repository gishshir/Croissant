package com.collectif.ft.croissants.server.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class DateUtils {
	
	private static final Log log = LogFactory.getLog(DateUtils.class); 
	
	private static final Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	
	//private static final long JOUR_MS = 1000 * 60 * 60 * 24; //24h

	/**
	 * Determine si la date donnée est + vieille ou non de n Jours par rapport au temps present
	 * <br/>
	 * <ul>
	 * <li>timestamp  < now < timestamp + delay : false
	 * <li>timestamp  < timestamp + delay < now : true
	 * </ul>
	 * @param timestamp date à analyser
	 * @param delayInDay
	 * @return
	 */
	public static boolean isDateOldBeforePresent (long timestamp, int delayInDay) {
		
		synchronized (utcCalendar) {	
		
		// date à analyser
		utcCalendar.setTimeInMillis(timestamp);
		// on ajoute n days
		utcCalendar.add(Calendar.DAY_OF_YEAR,  delayInDay);
		
		return isDateTimeInPast(utcCalendar.getTime());
	   }
	}
	
	public static boolean isSameDay (Date date1, Date date2) {
		
		if (date1 == null || date2 == null) {
			return false;
		}
		synchronized (utcCalendar) {	
			
			utcCalendar.setTime(date1);
			int year = utcCalendar.get(Calendar.YEAR);
			int month = utcCalendar.get(Calendar.MONTH);
			int dayInMonth = utcCalendar.get(Calendar.DAY_OF_MONTH);
			
			
			utcCalendar.setTime(date2);
			if (utcCalendar.get(Calendar.YEAR) == year &&
					utcCalendar.get(Calendar.MONTH) == month &&
					  utcCalendar.get(Calendar.DAY_OF_MONTH) == dayInMonth) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Determine si la date donnée est + loin dans le futur de n Jours par rapport au temps present
	 * <br/>
	 * deux controles : moins de 24h * (delayInDay +  1) d'écart et
	 * diff day/year <= delayInDay
	 * @param timestamp date à analyser
	 * @param delayInDay
	 * @return
	 */
	public static boolean isDayDateCloseToPresentDay (Date date, int delayInDay) {
		
		synchronized (utcCalendar) {	
		  Date now = getNewUTCDate();
		  
		  // borne supérieur de l'intervalle
		  utcCalendar.setTime(date);
		  long timestampSup = utcCalendar.getTimeInMillis();
		  
		  // enlever le nombre de jours du delai
		  utcCalendar.add(Calendar.DAY_OF_YEAR, -1 * delayInDay);
		  
		  // mettre 0 heure 0 minute 0 seconde
		  utcCalendar.set(Calendar.HOUR, 0);
		  utcCalendar.set(Calendar.MINUTE, 0);
		  utcCalendar.set(Calendar.SECOND, 0);
		  
		  long timestampInf = utcCalendar.getTimeInMillis();
		  
		  long timestampNow = now.getTime();
		  return (timestampNow > timestampInf) && (timestampNow < timestampSup);
		  
			}
		
	}
	
	/**
	 * Determine si le TS de la date est dans le passé ou le futur
	 * par rapport au temps présent
	 * @param date
	 * @return true si la date est dans le passé
	 */
	public static boolean isDateTimeInPast(Date date) {
					
		return date.getTime() < getNewUTCDate().getTime();
	}
	
	/**
	 * Is date1 strictly after date2
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isDate1AfterDate2 (Date date1, Date date2) {
		if (date1 == null && date2 ==  null) {
			return false;
		}
		if (date1 == null) {
			return false;
		}
		if (date2 == null) {
			return true;
		}
		return date1.getTime() > date2.getTime();
	}
	
	/**
	 * Determine si le jour de la date à évaluer est dans l'intervalle:
	 * [jour beginDate - jour endDate]
	 * Warning : plus restrictif que la condition sur les timestamps
	 * @param beginDate
	 * @param endDate
	 * @param dateToEvaluate
	 * @return
	 */
	public static boolean isDayDateInDayInterval (Date beginDate, Date endDate, Date dateToEvaluate) {
		
		boolean tsInIntervall = isDateInInterval(beginDate, endDate, dateToEvaluate);
		if (tsInIntervall) {
			return true;
		}
		// Condition supplémentaire : le jour de dateToEvaluate doit
		// être différent des jours des bornes beginDate et endDate 
		boolean sameDay = isSameDay(beginDate, dateToEvaluate) || isSameDay(endDate, dateToEvaluate);
		return sameDay;
	}
	
	public static boolean isDateInInterval (Date beginDate, Date endDate, Date dateToEvaluate) {
		
		if (beginDate == null && endDate == null) {
			return true;
		}
		if (beginDate == null || endDate == null || dateToEvaluate == null) {
			return false;
		}
		
		long ts = dateToEvaluate.getTime();
		return ts >= beginDate.getTime() && ts <= endDate.getTime();
		
	}
	public static Date addXDayToDate(Date date, int days) {
		
		if (date == null) {
			return null;
		}
		synchronized (utcCalendar) {
			utcCalendar.setTime(date);
			utcCalendar.add(Calendar.DAY_OF_YEAR, days);
			return utcCalendar.getTime();
		}
		 
		  
	}
	public static int getOffset(Date date) {
		return date.getTimezoneOffset() /60;
	}
	public static int getServerOffset() {
		return getOffset(getNewUTCDate());
	}
	
	
	public static Date getNewUTCDate() {
		
		synchronized (utcCalendar) {
		utcCalendar.setTime(new Date());
		return utcCalendar.getTime();
		}
	}
	public static Date getNewUTCDate(long timestamp) {
		synchronized (utcCalendar) {
		utcCalendar.setTimeInMillis(timestamp);
		return utcCalendar.getTime();
		}
	}
	

	
	//  modification de l'heure de la tache à 23:00 dans le timezone du client
	// a utiliser lors de la creation ou mise à jour des tasks
	// de manière a ce que la tache du jour soit tj considerée comme une tache à venir
	// clientOffset : decalage en heure de la date du client / UTC 
	// par ex pour GMT+1, clientOffset = -1
	public static Date verifyTaskDate(Date taskDate, int clientOffset) {
		
		log.info("verifyTaskDate() - client timezone offset: " + clientOffset);

		synchronized (utcCalendar) {
			
			utcCalendar.setTime(taskDate);
			if (utcCalendar.get(Calendar.HOUR_OF_DAY)  == 23 + clientOffset && utcCalendar.get(Calendar.MINUTE) == 00)
	        {
				return taskDate;
			}
			utcCalendar.set(Calendar.HOUR_OF_DAY, 23 + clientOffset);
			utcCalendar.set(Calendar.MINUTE, 00);
			utcCalendar.set(Calendar.SECOND, 00);
		}
		return utcCalendar.getTime();	
	}
	
	public static void logDate(String comment, Date date) {
		
		synchronized (utcCalendar) {
		
			utcCalendar.setTime(date);
        int year = utcCalendar.get(Calendar.YEAR);
        int month = utcCalendar.get(Calendar.MONTH);
        int day = utcCalendar.get(Calendar.DAY_OF_MONTH);
        int hour = utcCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = utcCalendar.get(Calendar.MINUTE);
        int secondes = utcCalendar.get(Calendar.SECOND);
        log.info(comment + "- year: " + year + " month: " + month + " day: " + day + " hour: " + hour + " minute: " + minute + " seconde: " + secondes);
		}
        
        
	}
	
	


}
