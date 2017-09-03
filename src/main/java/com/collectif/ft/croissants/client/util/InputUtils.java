package com.collectif.ft.croissants.client.util;

import java.util.Date;

public class InputUtils {

	
	public static boolean isDifferents(String text1, String text2) {
		Boolean manageNull = manageNull(text1, text2);
		if (manageNull != null) {
			return manageNull.booleanValue();
		}
		
		return !text1.equals(text2);
	}
	
	public static boolean isDifferents(Date date1, Date date2) {
		Boolean manageNull = manageNull(date1, date2);
		if (manageNull != null) {
			return manageNull.booleanValue();
		}
		
		return !date1.equals(date2);
	}
	
	private static Boolean manageNull(Object obj1, Object obj2)
    {
	
		if (obj1 == null && obj2 == null) {
			return false;
		}
		if (obj1 == null && obj2 != null) {
			return true;
		}
		if (obj1 != null && obj2 == null) {
			return true;
		}
		return null;
    }
	
}
