package com.abubusoft.xenon.game;

import com.abubusoft.xenon.Xenon4OpenGL;
import com.abubusoft.xenon.context.XenonBeanContext;
import com.abubusoft.xenon.context.XenonBeanType;

/**
 * <p>
 * Contiene la scatola del gioco, ovvero la gestione dei punteggi e degli achievements raggiunti.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class GameConfig {

	/**
	 * punteggio
	 */
	//@PreferenceConfig(crypted=true)
	public int score = 0;
	
	/**
	 * <p>Obiettivi raggiunti</p>
	 */
	//@PreferenceConfig
	public String achievements[];
	
	/**
	 * partite giocate
	 */
	//@PreferenceConfig(crypted=true)
	public int playedGames = 0;
		

	/**
	 * <p>
	 * Indica se fare il login automatico con google+
	 * </p>
	 */
	//@PreferenceConfig
	public boolean googleAutoSignin = true;
	
	/*
	 * boolean mPrimeAchievement = false; boolean mHumbleAchievement = false; boolean mLeetAchievement = false; boolean mArrogantAchievement = false; int mBoredSteps = 0; int
	 * mEasyModeScore = -1; int mHardModeScore = -1;
	 */

	/*
	 * boolean isEmpty() { return !mPrimeAchievement && !mHumbleAchievement && !mLeetAchievement && !mArrogantAchievement && mBoredSteps == 0 && mEasyModeScore < 0 &&
	 * mHardModeScore < 0; }
	 */

	/**
	 * <p>Salva su disco</p>
	 */
	public void save() {
		Xenon4OpenGL argon = XenonBeanContext.getBean(XenonBeanType.XENON);
		//writePreferences(xenon.preferences);
	}

	/**
	 * <p>Carica da disco</p>
	 */
	public void load() {
		Xenon4OpenGL argon = XenonBeanContext.getBean(XenonBeanType.XENON);
		//readPreferences(xenon.preferences);
	}
}