package com.collectif.ft.croissants.shared.util;

public class FieldValidator {

	public static boolean controlNumeric(final String value) {
		
		boolean success = false;
		try {
		   Integer.parseInt(value);
		   success = true;
		} catch (NumberFormatException e) {
			success = false;
		}
		return success;
	}
	
	public static boolean controlEmail(final String email) {
		if (email == null) return true;
		String myemail  = email.trim();
		if (myemail.length() == 0) {
			return true;
		}
		// TODO a ameliorer avec une regex
		if (myemail.indexOf("@") == -1) {
			return false;
		}
		return true;
	}
	public static boolean controlMinLenght(final String text, int minLength) {
		if (text == null) {
			return false;
		}
		
		return text.length() >= minLength;
	}
}
