package com.abubusoft.xenon.game;

import com.abubusoft.xenon.android.XenonLogger;
import com.abubusoft.xenon.android.listener.XenonGestureListener;

/**
 * @author Francesco Benincasa
 * 
 */
public abstract class GameState {

	/**
	 * definito mediante il builder
	 */
	public String key;

	/**
	 * <p>
	 * listener per le gesture.
	 * </p>
	 */
	public XenonGestureListener gesturesListener;
	
	/**
	 * riferimento alla macchina degli stati. Viene referenziata quando lo stato viene creato.
	 */
	public GameStateMachine stateMachine;
	
	public void next(String nextStateKey) 
	{
		stateMachine.next(nextStateKey);
	}

	/**
	 * Crea la scena in base alle preference e ad argonContext
	 *
	 * @param firstSceneCreation
	 * @param preferencesIsChanged
	 *            se true indica che qualche preferenza è cambiata
	 * @param screenIsChanged
	 **/
	public void onSceneCreate(boolean firstSceneCreation, boolean preferencesIsChanged, boolean screenIsChanged) {

	}

	/**
	 * <p>
	 * La scena non è cambiata, ma bisogna ricaricare lo schermo. Le texture e
	 * gli shader sono stati già ricaricati. Carica le risorse associate alla
	 * scena.
	 * </p>
	 *
	 * @param firstSceneCreation
	 * @param preferencesIsChanged
	 * @param screenIsChanged
	 */
	public void onSceneRestore(boolean firstSceneCreation, boolean preferencesIsChanged, boolean screenIsChanged) {

	}

	public void onDetroyScene() {

	}

	/**
	 * <p>
	 * Disegna il frame.
	 * </p>
	 * 
	 * @param enlapsedTime
	 *            tempo trascorso tra un frame ed un altro
	 * @param speedAdapter
	 *            data una velocità espressa in termini di m/s, questo parametro
	 *            consente di adattare gli spostamenti in bae al tempo passato.
	 */
	public void onFrameDraw(long enlapsedTime, float speedAdapter) {

	}

	/**
	 * <p>
	 * Eseguito quando stiamo uscendo da questo stato.
	 * </p>
	 * 
	 * @param nextState
	 * 		prossimo stato
	 */
	public void onEnd(GameState nextState) {
		XenonLogger.debug("gameState %s onEnd", key);
	}

	/**
	 * <p>Eseguito quando stiamo entrando nello stato.</p>
	 * @param previousState
	 */
	public void onStart(GameState previousState) {
		XenonLogger.debug("gameState %s onStart", key);
	}

	public void onSceneReady(boolean firstSceneCreation, boolean preferencesIsChanged, boolean screenIsChanged) {
		
	}

}
