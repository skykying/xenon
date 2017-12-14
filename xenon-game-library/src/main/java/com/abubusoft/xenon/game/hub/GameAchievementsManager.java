/**
 * 
 */
package com.abubusoft.xenon.game.hub;

import java.util.ArrayList;

/**
 * @author Francesco Benincasa
 * 
 */
public class GameAchievementsManager {

	private static final GameAchievementsManager instance = new GameAchievementsManager();

	public static final GameAchievementsManager instance() {
		return instance;
	}

	ArrayList<GameAchievement> list;

	private GameAchievementsManager() {
		list = new ArrayList<>();
	}

	/**
	 * aggiunge elemento
	 * 
	 * @param id
	 * @param name
	 * @param checker
	 */
	public void defineAchievement(int uidResourceId, int nameResourceId, int descResourceId, GameAchievementChecker checker) {
		GameAchievement item = new GameAchievement(uidResourceId, descResourceId, descResourceId);
		item.checker = checker;

		list.add(item);
	}

}
