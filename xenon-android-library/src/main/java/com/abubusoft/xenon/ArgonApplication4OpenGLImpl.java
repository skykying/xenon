package com.abubusoft.xenon;

import com.abubusoft.xenon.android.surfaceview.ConfigOptions;
import com.abubusoft.xenon.android.surfaceview.ConfigOptions.ClientVersionType;
import com.abubusoft.xenon.engine.Phase;
import com.abubusoft.xenon.core.config.Config;
import com.abubusoft.xenon.core.logger.ElioLogger;

import android.app.Activity;
import android.content.SharedPreferences;


public abstract class ArgonApplication4OpenGLImpl extends ArgonApplicationImpl<Argon4OpenGL> implements ArgonApplication4OpenGL {
	
	@Override
	public void onConfigReset() {
		ElioLogger.debug("onConfigReset - default");
	}

	/* (non-Javadoc)
	 * @see org.abubu.argon.ArgonApplication4OpenGL#chooseArgonGLConfig()
	 */
	@Override
	public ConfigOptions chooseArgonGLConfig() {
		return ConfigOptions.build().clientVersion(ClientVersionType.OPENGL_ES_2);
	}

	@Override
	public void onStartup() {
		ElioLogger.debug("onStartup - default");
	}
	
	@Override
	public void onSceneRestore(SharedPreferences sharedPreference, boolean firstSceneCreation, boolean preferencesIsChanged, boolean screenIsChanged) {
		ElioLogger.debug("onSceneRestore - default");
	}

	@Override
	public Config retrieveCurrentConfig() {
		ElioLogger.debug("retrieveCurrentConfig - default");
		return null;
	}

	@Override
	public void onPause(Activity currentActivity) {
		ElioLogger.debug("onPause - default");
		
	}

	@Override
	public void onResume(Activity currentActivity) {
		ElioLogger.debug("onResume - default");
		
	}

	@Override
	public void onFramePrepare(Phase phase, long enlapsedTime, float speedAdapter) {
	}

	/**
	 * <p>Camera di default.</p>
	 */
	public Camera camera;

	/* (non-Javadoc)
	 * @see org.abubu.argon.ArgonApplication4OpenGL#setDefaultCamera(org.abubu.argon.Camera)
	 */
	@Override
	public void setDefaultCamera(Camera camera) {
		this.camera=camera;
	}
	
	@Override
	public void onWindowCreate() {

	}

	@Override
	public void onSceneReady(SharedPreferences sharedPreference, boolean firstSceneCreation, boolean preferencesIsChanged, boolean screenIsChanged) {

	}

}
