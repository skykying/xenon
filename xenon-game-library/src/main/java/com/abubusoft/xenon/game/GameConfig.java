package com.abubusoft.xenon.game;

import org.abubu.argon.Argon4OpenGL;
import org.abubu.argon.ArgonBeanContext;
import org.abubu.argon.ArgonBeanType;
import org.abubu.elio.config.ConfigBase;
import org.abubu.elio.config.PreferenceConfig;

/**
 * <p>
 * Contiene la scatola del gioco, ovvero la gestione dei punteggi e degli achievements raggiunti.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class GameConfig extends ConfigBase {

	/**
	 * punteggio
	 */
	@PreferenceConfig(crypted=true)
	public int score = 0;
	
	/**
	 * <p>Obiettivi raggiunti</p>
	 */
	@PreferenceConfig
	public String achievements[];
	
	/**
	 * partite giocate
	 */
	@PreferenceConfig(crypted=true)
	public int playedGames = 0;
		

	/**
	 * <p>
	 * Indica se fare il login automatico con google+
	 * </p>
	 */
	@PreferenceConfig
	public boolean googleAutoSignin = false;
	
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
		Argon4OpenGL argon = ArgonBeanContext.getBean(ArgonBeanType.ARGON);
		writePreferences(argon.preferences);
	}

	/**
	 * <p>Carica da disco</p>
	 */
	public void load() {
		Argon4OpenGL argon = ArgonBeanContext.getBean(ArgonBeanType.ARGON);
		readPreferences(argon.preferences);
	}
}