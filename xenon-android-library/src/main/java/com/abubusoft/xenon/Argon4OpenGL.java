package com.abubusoft.xenon;

import com.abubusoft.kripton.android.Logger;
import com.abubusoft.xenon.android.ArgonActivity4OpenGL;
import com.abubusoft.xenon.android.ArgonView4OpenGL;
import com.abubusoft.xenon.android.ArgonWallpaper;
import com.abubusoft.xenon.android.listener.ArgonGestureDetector;
import com.abubusoft.xenon.android.listener.ArgonGestureListener;
import com.abubusoft.xenon.android.surfaceview16.ArgonGLView;
import com.abubusoft.xenon.engine.Phase;
import com.abubusoft.xenon.misc.FPSCounter;
import com.abubusoft.xenon.misc.FPSLimiter;
import com.abubusoft.xenon.opengl.ArgonGL;
import com.abubusoft.xenon.opengl.ArgonGLConfigChooser;
import com.abubusoft.xenon.opengl.ArgonGLHelper;
import com.abubusoft.xenon.opengl.ArgonGLRenderer;
import com.abubusoft.xenon.settings.ArgonSettings;
import com.abubusoft.xenon.shader.ShaderManager;
import com.abubusoft.xenon.texture.TextureManager;
import com.abubusoft.xenon.vbo.BufferManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.view.Window;
import android.view.WindowManager;

public class Argon4OpenGL extends Argon4BaseImpl<ArgonApplication4OpenGL> implements Argon {

	private static final long serialVersionUID = 4176307344192146991L;

	/**
	 * indica se è la prima volta che si sta creando la scena
	 */
	protected boolean firstSceneCreation;
	
	protected boolean sceneReady;

	/**
	 * Indica che la scena è pronta per essere renderizzata. Serve in particolar 
	 * modo per verificare se gestione o meno del touch
	 * 
	 * @return
	 * 		true se la scena è pronta per essere disegnata.
	 */
	public boolean isSceneReady()
	{
		return sceneReady;
	}
	
	/**
	 * 
	 */
	public void reset() {
		firstSceneCreation = true;
	}

	/**
	 * listener delle gesture
	 */
	public ArgonGestureListener gestureListener;

	/**
	 * costruttore
	 */
	public Argon4OpenGL() {
		super();

		sceneReady=false;
	}

	/**
	 * effettua la configurazione di argon
	 * 
	 */
	public void applySettings() {
		// carica la configurazione

		// impostiamo la camera di default.
		CameraManager.instance().init(settings.viewFrustum);

		// opengl
		ArgonGL.startup(settings.openGL.version, settings.openGL.debug);
		FPSLimiter.maxFrameRate = settings.openGL.maxFPS;

		// gestore delle gesture
		try {
			gestureListener = (ArgonGestureListener) ArgonBeanContext.createInstance(Class.forName(settings.application.gestureListenerClazz.trim()));
		} catch (Exception e) {
			Logger.fatal(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Renderer utilizzato per il rendering sia da wallpaper che da activity
	 */
	public ArgonGLRenderer renderer;

	/**
	 * indica il tempo attuale in mills. Utilizzato in {@link #onDrawFrameBegin()}
	 */
	private long now;

	/**
	 * <p>
	 * Activity corrente nella quale è presente la openglView {@link ArgonView4OpenGL}
	 * </p>
	 */
	public ArgonActivity4OpenGL activity;

	private boolean screenIsChanged;

	private boolean somePreferenceIsChanged;

	/**
	 * Da eseguire durante la creazione dell'activity: imposta i flag per nascondere il titolo della finestra, crea il renderer e lo associa all'activity.
	 * 
	 * @param currentActivity
	 * @throws Exception
	 */
	public void onActivityCreated(ArgonActivity4OpenGL currentActivity) throws Exception {

		try {
			this.activity = currentActivity;
			reset();

			// finestra senza titolo
			currentActivity.requestWindowFeature(Window.FEATURE_NO_TITLE);
			currentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

			// surface per il rendering
			//GLSurfaceViewEGL14  view = new GLSurfaceViewEGL14(currentActivity);
			ArgonGLView  view = ArgonGL.createArgonGLView(currentActivity);

			// abilitiamo se richiesto il debug opengl
			if (settings.openGL.debug) {
				view.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR | GLSurfaceView.DEBUG_LOG_GL_CALLS);
			}

			// impostiamo il safeMode
			renderer = currentActivity.createRenderer();
			renderer.setSafeMode(settings.openGL.safeMode);

			view.setEGLContextClientVersion(2);
			view.setPreserveEGLContextOnPause(true);

			// impostiamo il formato a 32 bit
			//view.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			//view.getHolder().setFormat(PixelFormat.RGBA_8888);
			view.getHolder().setFormat(ArgonGLConfigChooser.build().getPixelFormat());
			view.setRenderer(renderer);

			// inizializza
			//renderer.initialize(view);

			// impostiamo la glSurfaceView per l'activity
			currentActivity.setArgonGLSurfaceView(view);

			// gestore delle gesture, lo impostiamo sull'activity o sul service
			currentActivity.gestureDetector = new ArgonGestureDetector(this, gestureListener);
		} catch (Exception e) {
			// e' un errore che dobbiamo cmq visualizzare. Poi cmq sollevviamo
			// l'eccezione.
			Logger.fatal(e.getMessage());
			e.printStackTrace();
			throw (e);
		}
	}

	/**
	 * Creazione del servizio ed impostazione del renderer
	 * 
	 * @param service
	 */
	public synchronized void onServiceCreated(ArgonWallpaper service)  {

		try {
			reset();
			// renderer = (ArgonRenderer) Class.forName(settings.openGL.rendererClazz).newInstance();
			// renderer=new ArgonRendererV2();
			renderer = service.createRenderer();
			renderer.setSafeMode(settings.openGL.safeMode);

			service.setRenderer(renderer);
			// gestore delle gesture, lo impostiamo sull'activity o sul service
			service.setGestureDetector(new ArgonGestureDetector(this, gestureListener));
			// impostiamo il formato a 32 bit

		} catch (Exception e) {
			Logger.fatal(e.getMessage());
			e.printStackTrace();
			throw new RuntimeException(e);

		}
	}
	
	public synchronized boolean isSceneToCreate()
	{
		return firstSceneCreation || somePreferenceIsChanged || screenIsChanged || previewStatusChanged;
	}

	public synchronized void onSceneCreation() {
		// Logger.info("ARGON onSurfaceChanged");
		// boolean screenIsChanged;
		// boolean somePreferenceIsChanged;

		// lo dobbiamo fare prima di camera manager, ovvero prima che cambi
		// screenInfo

		// la prima volta ha senso, poi
		// screenWidth = width;
		// screenHeight = height;
		// firstSceneCreation=true;

		// QUA c'era tutto il codice prima
		Logger.info("Scene changed firstSceneCreation: %s, somePreferenceIsChanged: %s, screenIsChanged: %s, previewStateIsChanged: %s", firstSceneCreation, somePreferenceIsChanged, screenIsChanged, previewStatusChanged);

		if (isSceneToCreate()) {
			try {
				TextureManager.instance().clearTextures();
				ShaderManager.instance().clearShaders();
				BufferManager.instance().clearBuffers();
				
				Logger.warn("****  Gargabe Collection ****");
				System.gc();
			} catch (Exception e) {
				Logger.error("Error during resource cleaning: %s" + e.getMessage());
				e.printStackTrace();
			}

			// creiamo la scena
			Logger.info("####Create scene");
			application.onSceneCreate(preferences, firstSceneCreation, somePreferenceIsChanged, screenIsChanged);
		} else {

			Logger.info("####Same scene, contextPreservedOnPause " + contextPreservedOnPause);
			if (!contextPreservedOnPause) {
				// qua se ci sono errori non posso far altro che sbottare
				TextureManager.instance().reloadTextures();
				ShaderManager.instance().reloadShaders();
				BufferManager.instance().reloadVertexBuffers();
				Logger.info(" > Reload all resources");
			} else {

				Logger.info(" > No reload needed");
			}

			// carichiamo le varie risorse
			application.onSceneRestore(preferences, firstSceneCreation, somePreferenceIsChanged, screenIsChanged);
		}
		Logger.info("#### ON SCENE READY");
		application.onSceneReady(preferences, somePreferenceIsChanged, somePreferenceIsChanged, screenIsChanged);

		firstSceneCreation = false;
		somePreferenceIsChanged = false;
		screenIsChanged = false;
		previewStatusChanged = false;

		// windowSurfaceCreated=false;
		Logger.info("#### FINE RIGENERO SCENA");

		// azzeriamo il flag, sempre e comunque
		//ElioPreferenceActivityManager.getInstance().resetPreferenceChanged();
		
		// ora la scena è pronta
		sceneReady=true;
	}

	/**
	 * Invocato quando la surfaceView cambia e dobbiamo quindi ridisegnare
	 * 
	 * @param width
	 * @param height
	 */
	public synchronized void onSurfaceChanged(int width, int height) {
		// in questo modo, se sono stati impostati a true, finchè non passiamo per la generazione
		// della scena non verranno messi a false a causa di duplice invocazione di questo metodo.
		screenIsChanged = screenIsChanged || (ArgonGL.screenInfo.width != width) || (ArgonGL.screenInfo.height != height);
		//somePreferenceIsChanged = somePreferenceIsChanged || ElioPreferenceActivityManager.getInstance().isPreferenceChanged();

		// aggiorniamo dimensioni schermo
		ArgonGL.updateScreen(width, height);

		Logger.info("###### Argon4OpenGL onSurfaceChanged Counter ");
		// imposta la camera di default ed imposta screenInfo!
		application.setDefaultCamera(CameraManager.instance().onSurfaceChanged(width, height));
	}

	/**
	 * routine per la fine del frame draw
	 */
	public void onDrawFrameEnd() {
		FPSLimiter.onDrawFrameEnd();
		FPSCounter.onDrawFrameEnd(null);
	}

	/**
	 * routine per l'inizio del frame draw
	 */
	public void onDrawFrameBegin() {
		now = SystemClock.elapsedRealtime();

		FPSCounter.onDrawFrameBegin(now);
		FPSLimiter.onDrawFrameBegin(now);

	}

	/**
	 * Se true indica il contesto viene preservato durante il pause resume. Di default è false.
	 */
	public boolean contextPreservedOnPause = true;

	/**
	 * Indica se stiamo passando da una preview alla visualizzazione vera e propria o viceversa.
	 */
	private boolean previewStatusChanged;

	public boolean isPreviewStatusChanged() {
		return previewStatusChanged;
	}

	// public boolean windowSurfaceCreated;

	/**
	 * eseguito quando viene creata la surface
	 * 
	 */
	public void onSurfaceCreated() {
		Logger.info("###### Argon4OpenGL onSurfaceCreated");

		if (firstSceneCreation) {
			ArgonGL.checkGLVersion();
			Logger.info("@@@ OpenGL version %s",ArgonGL.getVersion());
		}
		
		application.onWindowCreate();

		/*
		 * application.setDefaultCamera(CameraManager.instance().onSurfaceChanged(1024, 1024));
		 * 
		 * // creazione scena TextureManager.instance().clearTextures(context); ShaderManager.instance().clearShaders(); BufferManager.instance().clearVertexBuffers();
		 * 
		 * // creiamo la scena Logger.info("####Create scene"); application.onSceneCreate(preferences, firstSceneCreation, false, false); application.onSceneReady(preferences, false, false, false); Logger.info("> GL Finish");
		 * GLES20.glFinish();
		 */
		// viewStatus = ViewStatusType.SURFACE_ON_PAUSE;
	}

	public synchronized void onDestroy() {
		application.onDestroy(null);
	}

	/**
	 * Creaiamo la view.
	 * 
	 * @param argonView
	 * @throws Exception
	 */
	public void onViewCreated(ArgonView4OpenGL argonView) throws Exception {

		try {
			// abilitiamo se richiesto il debug opengl
			if (settings.openGL.debug) {
				argonView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR | GLSurfaceView.DEBUG_LOG_GL_CALLS);
			}

			renderer = argonView.createRenderer();
			renderer.setSafeMode(settings.openGL.safeMode);

			argonView.setEGLContextClientVersion(2);
			
			argonView.getHolder().setFormat(ArgonGLConfigChooser.build().getPixelFormat());
			argonView.setRenderer(renderer);
			
			argonView.gestureDetector = new ArgonGestureDetector(this, gestureListener);
		} catch (Exception e) {
			// e' un errore che dobbiamo cmq visualizzare. Poi cmq sollevviamo
			// l'eccezione.
			Logger.fatal(e.getMessage());
			e.printStackTrace();
			throw (e);
		}

	}

	/**
	 * Eseguiamo startup dell'applicazione, applichiamo i settings, costruiamo lo screen info e poi invochiamo il metodo parent.
	 * 
	 * @see com.abubusoft.xenon.Argon4BaseImpl#onStartup(android.content.Context, com.abubusoft.xenon.settings.ArgonSettings, android.content.SharedPreferences)
	 */
	@Override
	public void onStartup(Context contextValue, ArgonSettings settingsValue, SharedPreferences preferenceValue) {
		super.onStartup(contextValue, settingsValue, preferenceValue);

		applySettings();
		
		ScreenInfo.build(context, ArgonGL.screenInfo);
		ArgonGLHelper.onStartup();				
		
		Logger.info("Screen resolution %s startX %s - Density %s - Resolution %s", ArgonGL.screenInfo.width, ArgonGL.screenInfo.height, ArgonGL.screenInfo.densityClass, ArgonGL.screenInfo.resolution);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.abubusoft.xenon.Argon#onConfigReset()
	 */
	@Override
	public synchronized void onConfigReset() {
		application.onConfigReset();
	}

	/**
	 * per ogni contesto andrebbe a cancellare qualcosa che deve rimanere valido per gli altri contesti.
	 */
	@Deprecated
	public void onOGLContextDestroy() {
		Logger.info("> Argon4OpenGL onOGLContextDestroy");

		/*
		 * Logger.info("> BEGIN Unbind textures, shaders, vbos");
		 * 
		 * Logger.info("> END Unbind textures, shaders, vbos");
		 */
		/*
		 * TextureManager.instance().clearTextures(); ShaderManager.instance().clearShaders(); BufferManager.instance().clearBuffers();
		 */
	}

	public synchronized void setPreviewStatusChanged(boolean value) {
		previewStatusChanged = value;
	}

	public synchronized void onFrameDraw(Phase phase, long enlapsedTime, float speedAdapter) {
		application.onFrameDraw(phase, enlapsedTime, speedAdapter);
	}

	public synchronized void onFramePrepare(Phase phase, long enlapsedTime, float speedAdapter) {
		application.onFramePrepare(phase, enlapsedTime, speedAdapter);
	}

	/**
	 * 
	 * @param activity
	 */
	public synchronized void onPause(ArgonActivity4OpenGL activity) {
		// viewStatus = ViewStatusType.SURFACE_ON_PAUSE;
		sceneReady=false;
		application.onPause(activity);
	}

	/**
	 * 
	 * @param activity
	 */
	public synchronized void onResume(ArgonActivity4OpenGL activity) {
		application.onResume(activity);
	}

}
