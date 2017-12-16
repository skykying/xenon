package com.abubusoft.xenon.game;

import java.util.HashMap;

import android.content.SharedPreferences;

import com.abubusoft.xenon.android.XenonLogger;

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
			XenonLogger.error(msg);
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
			XenonLogger.error(msg);
			throw (new RuntimeException(msg));
		}
	}

	/**
	 * <p>
	 * </p>
	 * 
	 */
	GameStateMachine() {
		states = new HashMap<>();
	}

	/**
	 * <p>
	 * Eseguie onCreateScene per lo stato corrente
	 * </p>
	 * 
	 * @param firstSceneCreation
	 * @param preferencesIsChanged
	 * @param screenIsChanged
	 */
	public void onSceneCreate(boolean firstSceneCreation, boolean preferencesIsChanged, boolean screenIsChanged) {
		currentState.onSceneCreate(firstSceneCreation, preferencesIsChanged, screenIsChanged);
	}

	/**
	 * <p>
	 * Esegue onSameScene per lo stato corrente
	 * </p>
	 * 
	 * @param firstSceneCreation
	 * @param preferencesIsChanged
	 * @param screenIsChanged
	 */
	public void onSceneRestore(boolean firstSceneCreation, boolean preferencesIsChanged, boolean screenIsChanged) {
		currentState.onSceneRestore(firstSceneCreation, preferencesIsChanged, screenIsChanged);
	}

	public void onSceneReady(boolean firstSceneCreation, boolean preferencesIsChanged, boolean screenIsChanged) {
		currentState.onSceneReady(firstSceneCreation, preferencesIsChanged, screenIsChanged);
	}
}
