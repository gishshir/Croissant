package com.collectif.ft.croissants.client.util;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

public class DateUtils {

	public final static DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("dd MMMM yyyy");
	public final static DateTimeFormat smallDateTimeFormat = DateTimeFormat.getFormat("dd MMM yy");
	/**
	 * The getTimezoneOffset() method returns the time difference between UTC time and local time, in minutes.
     *
     * For example, If your time zone is GMT+2, -120 will be returned.
     * 
     * @return TZ offset in hour
	 */
	public final static int offset = new Date().getTimezoneOffset() / 60;
	
	
	public static String getLabelDate(Date date) {
		if (date ==  null) {
			return "";
		}
		return dateTimeFormat.format(date);
	}
	public static String getSmallLabelDate(Date date) {
		if (date ==  null) {
			return "";
		}
		return smallDateTimeFormat.format(date);
	}
	
	public static boolean isSameDay (Date date1, Date date2) {
		
		if (date1 == null || date2 == null) {
			return false;
		}
		
		if (date1.getDate() == date2.getDate() && date1.getMonth() == date2.getMonth() &&
				date1.getYear() == date2.getYear()) {
			return true;
		}
		return false;
	}
}
