package com.abubusoft.xenon.game.hub;

public class GameAchievement {
	public final int uidResourceId;

	public final int resourceName;

	public GameAchievementChecker checker;

	public final int resourceDesc;

	GameAchievement(int resourceIdValue, int resourceNameValue, int resourceDescValue) {
		uidResourceId = resourceIdValue;
		resourceName = resourceNameValue;
		resourceDesc = resourceDescValue;
	}

}
