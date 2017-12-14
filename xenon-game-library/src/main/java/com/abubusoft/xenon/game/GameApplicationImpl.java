package com.abubusoft.xenon.game;

import java.util.ArrayList;

import org.abubu.argon.ArgonApplication4OpenGLImpl;
import org.abubu.argon.engine.Phase;
import org.abubu.argon.game.hub.GameAchievementsManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Activity;
import android.content.SharedPreferences;

public abstract class GameApplicationImpl extends ArgonApplication4OpenGLImpl {

	/**
	 * <p>
	 * state machine
	 * </p>
	 */
	public GameStateMachine stateMachine;

	/**
	 * 
	 */
	public GameActivity activity;

	@Override
	public void onPause(Activity currentActivity) {

	}

	@Override
	public void onResume(Activity currentActivity) {
		activity = (GameActivity) currentActivity;

		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());

		/*
		 * if (resultCode != ConnectionResult.SUCCESS) { Toast.makeText(getContext(), "isGooglePlayServicesAvailable SUCCESS", Toast.LENGTH_LONG).show(); } else {
		 * GooglePlayServicesUtil.getErrorDialog(resultCode, currentActivity, 1); }
		 */
		if (resultCode != ConnectionResult.SUCCESS) {
			GooglePlayServicesUtil.getErrorDialog(resultCode, currentActivity, 1);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.abubu.argon.ArgonApplication4OpenGL#onCreateScene(android.content .SharedPreferences, boolean, boolean, boolean)
	 */
	@Override
	public void onSceneCreate(SharedPreferences sharedPreference, boolean firstSceneCreation, boolean preferencesIsChanged, boolean screenIsChanged) {
		// prima di tutto creiamo la scena
		stateMachine.onSceneCreate(sharedPreference, firstSceneCreation, preferencesIsChanged, screenIsChanged);

		// poi avviamo lo start dello stato corrente
		stateMachine.start();
	}

	@Override
	public void onSceneRestore(SharedPreferences sharedPreference, boolean firstSceneCreation, boolean preferencesIsChanged, boolean screenIsChanged) {
		stateMachine.onSceneRestore(sharedPreference, firstSceneCreation, preferencesIsChanged, screenIsChanged);
	}
	
	

	@Override
	public void onSceneReady(SharedPreferences sharedPreference, boolean firstSceneCreation, boolean preferencesIsChanged, boolean screenIsChanged) {
		stateMachine.onSceneReady(sharedPreference, firstSceneCreation, preferencesIsChanged, screenIsChanged);
		
	}

	@Override
	public void onFrameDraw(Phase phase, long enlapsedTime, float speedAdapter) {
		stateMachine.currentState.onFrameDraw(enlapsedTime, speedAdapter);
		stateMachine.update();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.abubu.argon.ArgonApplication#onStartup()
	 */
	@Override
	public void onStartup() {
		ArrayList<Class<? extends GameState>> list = defineGameStates(new ArrayList<Class<? extends GameState>>());

		stateMachine = GameStateMachineBuilder.build(this, list);
		onGameStartup();
		defineAchievements(GameAchievementsManager.instance());
	}

	protected abstract ArrayList<Class<? extends GameState>> defineGameStates(ArrayList<Class<? extends GameState>> list);

	/**
	 * <p>
	 * Eseguito all'avvio del gioco. Metodo fatto per evitare di dover fare l'override e poi inserire il super.onStartup del metodo startup.
	 * </p>
	 */
	protected abstract void onGameStartup();

	/**
	 * <p>
	 * Definisce gli achivements del gioco.
	 * </p>
	 * 
	 * @param gameAchievementsManager
	 */
	protected abstract void defineAchievements(GameAchievementsManager gameAchievementsManager);

	/**
	 * <p>
	 * Eseguito il login
	 * </p>
	 */
	public abstract void onSignInSucceeded();

}
