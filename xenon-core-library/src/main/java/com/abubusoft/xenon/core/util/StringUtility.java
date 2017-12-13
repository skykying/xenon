/**
 * 
 */
package com.abubusoft.xenon.core.util;

import java.util.Arrays;

/**
 * @author Francesco Benincasa
 * 
 */
public abstract class StringUtility {

	public static String leftPad(String source, int size, char padder)
	{
		int pad = size-source.length();
		if (pad<0) pad=0;
		char[] temp = (new String(new char[pad]) + source).toCharArray();
		Arrays.fill(temp, 0, pad, padder);
		
		return new String(temp);
	}
	
	/**
	 * Check if a String has text. More specifically, returns true if the string
	 * not null, it's length is > 0, and it has at least one non-whitespace
	 * character.
	 * 
	 * <pre>
	 *	StringUtility.hasText(null) = false
	 *	StringUtility.hasText("") = false
	 * 	StringUtility.hasText(" ") = false
	 * 	StringUtility.hasText("12345") = true
	 * 	StringUtility.hasText(" 12345 ") = true
	 * </pre>
	 * 
	 * Parameters: str - the String to check, may be null
	 * 
	 * true if the String is not null, length > 0, and not whitespace only
	 * 
	 * @param source
	 * @param defaultValue
	 * 
	 * @return
	 */
	public static String nvl(String source, String defaultValue) {
		if (source == null || source.trim().length() == 0) {
			return defaultValue;
		}

		return source;
	}

	/**
	 * True se la stringa è vuota
	 * @param source
	 * @return
	 */
	public static boolean isEmpty(String source) {
		if (source == null)
			return true;

		return false;
	}
	
	/**
	 * true se stringa blank o nulla
	 * @param source
	 * @return
	 */
	public static boolean isBlank(String source) {
		if (source == null || source.trim().length() == 0) {
			return true;
		}

		return false;
	}


	/*
	 * IsEmpty/IsBlank
	 * 
	 * DefaultString
	 */
}
