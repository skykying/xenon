package com.abubusoft.xenon.game;

import java.util.HashMap;

import org.abubu.elio.logger.ElioLogger;

import android.content.SharedPreferences;

/**
 * <p>
 * Rappresenta lo stato di avanzamento del gioco. E' stato creata come classe
 * normale al fine di poterla estendere.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class GameStateMachine {

	/**
	 * <p>
	 * elenco degli stati
	 * </p>
	 */
	protected HashMap<String, GameState> states;

	/**
	 * stato corrente della state machine
	 */
	public GameState currentState;

	/**
	 * prossimo stato della state machine
	 */
	protected GameState nextState;

	/**
	 * <p>
	 * Passa al prossimo stato, mediante la sua key
	 * </p>
	 * 
	 * @param key
	 */
	public void next(String key) {
		nextState = states.get(key);

		if (nextState == null) {
			String msg = String.format("GameStateInfo %s does not exist!", key);
			ElioLogger.error(msg);
			throw (new RuntimeException(msg));
		}

	}

	/**
	 * <p>
	 * Avvia la state machine partendo dallo stato iniziale
	 * </p>
	 */
	public void start() {
		currentState.onStart(null);
	}

	/**
	 * <p>
	 * Se presente, avanza allo stato successivo.
	 * </p>
	 * 
	 * @return
	 */
	public boolean update() {
		if (nextState != null) {
			currentState.onEnd(nextState);

			nextState.onStart(currentState);

			currentState = nextState;
			nextState = null;

			return true;
		}

		return false;
	}

	/**
	 * <p>
	 * Imposta lo stato corrente.
	 * </p>
	 * 
	 * @param key
	 */
	void setCurrentState(String key) {
		currentState = states.get(key);

		if (currentState == null) {
			String msg = String.format("GameStateInfo %s does not exist!", key);
			ElioLogger.error(msg);
			throw (new RuntimeException(msg));
		}
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param sceneDrawerHandlerValue
	 * @param gesturesHandlerValue
	 */
	GameStateMachine() {
		states = new HashMap<>();
	}

	/**
	 * <p>
	 * Eseguie onCreateScene per lo stato corrente
	 * </p>
	 * 
	 * @param sharedPreference
	 * @param firstSceneCreation
	 * @param preferencesIsChanged
	 * @param screenIsChanged
	 */
	public void onSceneCreate(SharedPreferences sharedPreference, boolean firstSceneCreation, boolean preferencesIsChanged, boolean screenIsChanged) {
		currentState.onSceneCreate(sharedPreference, firstSceneCreation, preferencesIsChanged, screenIsChanged);
	}

	/**
	 * <p>
	 * Esegue onSameScene per lo stato corrente
	 * </p>
	 * 
	 * @param sharedPreference
	 * @param firstSceneCreation
	 * @param preferencesIsChanged
	 * @param screenIsChanged
	 */
	public void onSceneRestore(SharedPreferences sharedPreference, boolean firstSceneCreation, boolean preferencesIsChanged, boolean screenIsChanged) {
		currentState.onSceneRestore(sharedPreference, firstSceneCreation, preferencesIsChanged, screenIsChanged);
	}

	public void onSceneReady(SharedPreferences sharedPreference, boolean firstSceneCreation, boolean preferencesIsChanged, boolean screenIsChanged) {
		currentState.onSceneReady(sharedPreference, firstSceneCreation, preferencesIsChanged, screenIsChanged);
	}
}
