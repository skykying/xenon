/**
 * 
 */
package com.abubusoft.xenon.game.hub;

/**
 * <p>
 * Tipo di client google.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public enum GameClientType {
	// We expose these constants here because we don't want users of this class
	// to have to know about GameHelper at all.
	CLIENT_GAMES(GameHelper.CLIENT_GAMES),
	//CLIENT_APPSTATE(GameHelper.CLIENT_APPSTATE),
	CLIENT_PLUS(GameHelper.CLIENT_PLUS),
	CLIENT_ALL(GameHelper.CLIENT_ALL);
	
	private GameClientType(int val)
	{
		value=val;
	}
	
	public int value;
}
