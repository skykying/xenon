package com.abubusoft.xenon.game.ui;

public class BitmapFontOptions {

	public boolean onlyUppercase;
	
	public char firstChar;
	
	public char lastChar;
	
	/**
	 * <p>Indica la lunghezza massima di caratteri per riga.
	 * I caratteri in più verranno ignorati</p>
	 */
	public int maxLenght;
	
	/**
	 * <p>Build di default:</p>
	 * <ul>
	 *		<li><b>maxLenght</b> = 10</li>
	 * </ul>
	 * @return
	 */
	public static BitmapFontOptions build()
	{
		return new BitmapFontOptions().maxLenght(10);
	}
	
	public BitmapFontOptions firstChar(char value)
	{
		firstChar=value;
		
		return this;
	}
	
	public BitmapFontOptions maxLenght(int value)
	{
		maxLenght=value;
		
		return this;
	}

	
	/**
	 * <p>Tutti i caratteri sono visualizzati in uppercase</p>
	 * 
	 * @param value
	 * @return
	 */
	public BitmapFontOptions onlyUppercase(boolean value)
	{
		onlyUppercase=value;
		
		return this;
	}
	
}
