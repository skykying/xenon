/**
 * 
 */
package com.abubusoft.xenon.game.hub;

import com.abubusoft.xenon.game.GameConfig;

/**
 * @author Francesco Benincasa
 *
 */
public interface GameAchievementChecker {

	/**
	 * <p>Indica se l'achivement è stato raggiunto o meno.</p>
	 * 
	 * @return
	 */
	boolean check(GameAchievement achievement, GameConfig config);
}
