/**
 * 
 */
package com.abubusoft.xenon.game;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.Locale;

import com.abubusoft.xenon.android.XenonLogger;
import com.abubusoft.xenon.android.listener.XenonGestureListener;
import com.abubusoft.xenon.context.XenonBeanContext;

/**
 * <p>
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
@SuppressLint("DefaultLocale")
public abstract class GameStateMachineBuilder {

	/**
	 * <p>
	 * Crea una state machine.
	 * </p>
	 * 
	 * @return
	 */
	public static GameStateMachine build(GameApplicationImpl application, ArrayList<Class<? extends GameState>> stateClazzez) {
		GameStateMachine asm = new GameStateMachine();
		int n = stateClazzez.size();

		for (int i = 0; i < n; i++) {
			extractInfoState(application, asm, stateClazzez.get(i));
		}

		return asm;
	}

	/**
	 * <p>Estrae le informazioni relative 
	 * @param application
	 * @param asm
	 * @param stateClazz
	 */
	protected static <E> void extractInfoState(GameApplicationImpl application, GameStateMachine asm, Class<? extends GameState> stateClazz) {
		GameState state;
		
		state= XenonBeanContext.createInstance(stateClazz);
		GameStateInfo stateInfo = stateClazz.getAnnotation(GameStateInfo.class);

		if (stateInfo == null) {
			String msg = "GameStateInfo defined without @GameStateInfo annotation";
			XenonLogger.fatal(msg);
			throw (new RuntimeException(msg));
		} else {
			String key = stateInfo.key().toLowerCase(Locale.ENGLISH);
			Class<?> gestureClazz = stateInfo.gestureListenerClazz();

			try {
				asm.states.put(key, state);
				
				state.key = key;
				state.stateMachine=asm;
				state.gesturesListener = (XenonGestureListener) XenonBeanContext.createInstance(gestureClazz);
				
				if (stateInfo.initialState())
				{
					asm.setCurrentState(key);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			
		}
	}

	/*
	 * private static Class<?> extractActivitiesClass(Class<?> fsmClass) {
	 * FiniteStateMachine fsm =
	 * fsmClass.getAnnotation(FiniteStateMachine.class); return
	 * fsm.activities(); }
	 * 
	 * private static Class<?> extractInitialState(Class<?> fsmClass) {
	 * FiniteStateMachine fsm =
	 * fsmClass.getAnnotation(FiniteStateMachine.class); return
	 * fsm.initialState(); }
	 * 
	 * private static Class<?> extractFinalState(Class<?> fsmClass) {
	 * FiniteStateMachine fsm =
	 * fsmClass.getAnnotation(FiniteStateMachine.class); return
	 * fsm.finalState(); }
	 * 
	 * private static <T> T buildFSM(Class<T> fsmClass, List<Class<?>>
	 * stateClasses, Class<?> initialStateClass, Class<?> finalStateClass,
	 * Class<?> activitiesClass) { return buildFSM(fsmClass, new
	 * FSMInvocationHandler(stateClasses, initialStateClass, finalStateClass,
	 * activitiesClass)); }
	 */

}
