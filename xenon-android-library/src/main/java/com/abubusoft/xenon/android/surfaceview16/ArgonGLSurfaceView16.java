/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.abubusoft.xenon.android.surfaceview16;

import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

import com.abubusoft.xenon.ArgonBeanContext;
import com.abubusoft.xenon.ArgonBeanType;
import com.abubusoft.xenon.opengl.ArgonGLConfigChooser;
import com.abubusoft.xenon.opengl.ArgonGLRenderer;
import com.abubusoft.xenon.opengl.AsyncOperationManager;
import com.abubusoft.xenon.settings.ArgonSettings;
import com.abubusoft.kripton.android.Logger;

import android.content.Context;
import android.opengl.EGL14;
import android.opengl.EGLExt;
import android.opengl.GLDebugHelper;
import android.opengl.GLSurfaceView.Renderer;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

/**
 * An implementation of SurfaceView that uses the dedicated surface for displaying OpenGL rendering.
 * <p>
 * A GLSurfaceView provides the following features:
 * <p>
 * <ul>
 * <li>Manages a surface, which is a special piece of memory that can be composited into the Android view system.
 * <li>Manages an EGL display, which enables OpenGL to render into a surface.
 * <li>Accepts a user-provided Renderer object that does the actual rendering.
 * <li>Renders on a dedicated thread to decouple rendering performance from the UI thread.
 * <li>Supports both on-demand and continuous rendering.
 * <li>Optionally wraps, traces, and/or error-checks the renderer's OpenGL calls.
 * </ul>
 *
 * <div class="special reference">
 * <h3>Developer Guides</h3>
 * <p>
 * For more information about how to use OpenGL, read the <a href="{@docRoot}guide/topics/graphics/opengl.html">OpenGL</a> developer guide.
 * </p>
 * </div>
 *
 * <h3>Using GLSurfaceView</h3>
 * <p>
 * Typically you use GLSurfaceView by subclassing it and overriding one or more of the View system input event methods. If your application does not need to override event methods then GLSurfaceView can be used as-is. For the most part
 * GLSurfaceView behavior is customized by calling "set" methods rather than by subclassing. For example, unlike a regular View, drawing is delegated to a separate Renderer object which is registered with the GLSurfaceView using the
 * {@link #setRenderer(Renderer)} call.
 * <p>
 * <h3>Initializing GLSurfaceView</h3>
 * All you have to do to initialize a GLSurfaceView is call {@link #setRenderer(Renderer)}. However, if desired, you can modify the default behavior of GLSurfaceView by calling one or more of these methods before calling setRenderer:
 * <ul>
 * <li>{@link #setDebugFlags(int)}
 * <li>{@link #setEGLConfigChooser(boolean)}
 * <li>{@link #setEGLConfigChooser(ArgonConfigChooser16)}
 * <li>{@link #setEGLConfigChooser(int, int, int, int, int, int)}
 * <li>{@link #setGLWrapper(GLWrapper)}
 * </ul>
 * <p>
 * <h4>Specifying the android.view.Surface</h4>
 * By default GLSurfaceView will create a PixelFormat.RGB_888 format surface. If a translucent surface is required, call getHolder().setFormat(PixelFormat.TRANSLUCENT). The exact format of a TRANSLUCENT surface is device dependent, but it
 * will be a 32-bit-per-pixel surface with 8 bits per component.
 * <p>
 * <h4>Choosing an EGL Configuration</h4>
 * A given Android device may support multiple EGLConfig rendering configurations. The available configurations may differ in how may channels of data are present, as well as how many bits are allocated to each channel. Therefore, the first
 * thing GLSurfaceView has to do when starting to render is choose what EGLConfig to use.
 * <p>
 * By default GLSurfaceView chooses a EGLConfig that has an RGB_888 pixel format, with at least a 16-bit depth buffer and no stencil.
 * <p>
 * If you would prefer a different EGLConfig you can override the default behavior by calling one of the setEGLConfigChooser methods.
 * <p>
 * <h4>Debug Behavior</h4>
 * You can optionally modify the behavior of GLSurfaceView by calling one or more of the debugging methods {@link #setDebugFlags(int)}, and {@link #setGLWrapper}. These methods may be called before and/or after setRenderer, but typically
 * they are called before setRenderer so that they take effect immediately.
 * <p>
 * <h4>Setting a Renderer</h4>
 * Finally, you must call {@link #setRenderer} to register a {@link Renderer}. The renderer is responsible for doing the actual OpenGL rendering.
 * <p>
 * <h3>Rendering Mode</h3>
 * Once the renderer is set, you can control whether the renderer draws continuously or on-demand by calling {@link #setRenderMode}. The default is continuous rendering.
 * <p>
 * <h3>Activity Life-cycle</h3>
 * A GLSurfaceView must be notified when the activity is paused and resumed. GLSurfaceView clients are required to call {@link #onPause()} when the activity pauses and {@link #onResume()} when the activity resumes. These calls allow
 * GLSurfaceView to pause and resume the rendering thread, and also allow GLSurfaceView to release and recreate the OpenGL display.
 * <p>
 * <h3>Handling events</h3>
 * <p>
 * To handle an event you will typically subclass GLSurfaceView and override the appropriate method, just as you would with any other View. However, when handling the event, you may need to communicate with the Renderer object that's
 * running in the rendering thread. You can do this using any standard Java cross-thread communication mechanism. In addition, one relatively easy way to communicate with your renderer is to call {@link #queueEvent(Runnable)}. For example:
 * 
 * <pre class="prettyprint">
 * class MyGLSurfaceView extends GLSurfaceView {
 * 
 * 	private MyRenderer mMyRenderer;
 * 
 * 	public void start() {
 *         mMyRenderer = ...;
 *         setRenderer(mMyRenderer);
 *     }
 * 
 * 	public boolean onKeyDown(int keyCode, KeyEvent event) {
 * 		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
 * 			queueEvent(new Runnable() {
 * 				// This method will be called on the rendering
 * 				// thread:
 * 				public void run() {
 * 					mMyRenderer.handleDpadCenter();
 * 				}
 * 			});
 * 			return true;
 * 		}
 * 		return super.onKeyDown(keyCode, event);
 * 	}
 * }
 * </pre>
 *
 */
public class ArgonGLSurfaceView16 extends ArgonGLView implements SurfaceHolder.Callback {
	
	private abstract class BaseConfigChooser implements ArgonConfigChooser16 {
		protected int[] mConfigSpec;

		public BaseConfigChooser(int[] configSpec) {
			mConfigSpec = filterConfigSpec(configSpec);
		}

		public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
			int[] num_config = new int[1];
			
			//recupera il numero di configurazioni ammesse dal device
			if (!egl.eglChooseConfig(display, mConfigSpec, null, 0, num_config)) {
				throw new IllegalArgumentException("eglChooseConfig failed");
			}

			int numConfigs = num_config[0];

			if (numConfigs <= 0) {
				throw new IllegalArgumentException("No configs match configSpec");
			}

			EGLConfig[] configs = new EGLConfig[numConfigs];
			if (!egl.eglChooseConfig(display, mConfigSpec, configs, numConfigs, num_config)) {
				throw new IllegalArgumentException("eglChooseConfig#2 failed");
			}
			EGLConfig config = chooseConfig(egl, display, configs);
			if (config == null) {
				throw new IllegalArgumentException("No config chosen");
			}
			return config;
		}

		abstract EGLConfig chooseConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs);

		private int[] filterConfigSpec(int[] configSpec) {
			if (mEGLContextClientVersion != 2 && mEGLContextClientVersion != 3) {
				return configSpec;
			}
			/*
			 * We know none of the subclasses define EGL_RENDERABLE_TYPE. And we know the configSpec is well formed.
			 */
			int len = configSpec.length;
			int[] newConfigSpec = new int[len + 2];
			System.arraycopy(configSpec, 0, newConfigSpec, 0, len - 1);
			newConfigSpec[len - 1] = EGL10.EGL_RENDERABLE_TYPE;
			if (mEGLContextClientVersion == 2) {
				newConfigSpec[len] = EGL14.EGL_OPENGL_ES2_BIT; /* EGL_OPENGL_ES2_BIT */
			} else {
				newConfigSpec[len] = EGLExt.EGL_OPENGL_ES3_BIT_KHR; /* EGL_OPENGL_ES3_BIT_KHR */
			}
			newConfigSpec[len + 1] = EGL10.EGL_NONE;
			return newConfigSpec;
		}
	}
	
	private class DefaultContextFactory implements EGLContextFactory {
		
		public int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

		public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig config) {
			int[] attrib_list = { EGL_CONTEXT_CLIENT_VERSION, mEGLContextClientVersion, EGL10.EGL_NONE };

			EGLContext context=egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, attrib_list);
			contextCounter.addAndGet(1);					
						
			ArgonSettings settings=ArgonBeanContext.getBean(ArgonBeanType.ARGON_SETTINGS);
			
			if (settings.openGL.asyncMode) {
				AsyncOperationManager.instance().init(egl, context, display, config);
			} else {
				AsyncOperationManager.instance().init();
			}

			
			Logger.warn("DefaultContextFactory > createContext %s , [contexts #%s, surfaces #%s]",context, contextCounter.get(), surfaceCounter.get());
			
			return context;
		}

		public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
			ArgonSettings settings=ArgonBeanContext.getBean(ArgonBeanType.ARGON_SETTINGS);
			
			// distrugge il context secondario, usato per le operazioni
			// async
			if (settings.openGL.asyncMode) {
				if (!AsyncOperationManager.instance().destroy(egl)) {
					Logger.error("display: texture loader context: " + context);
					Logger.info("tid=" + Thread.currentThread().getId());
					Logger.error("eglDestroyContex %s", egl.eglGetError());
				}
			}

			
			if (!egl.eglDestroyContext(display, context)) {
				Logger.error("DefaultContextFactory, display:" + display + " context: " + context);
				if (LOG_THREADS) {
					Logger.info("DefaultContextFactory tid=" + Thread.currentThread().getId());
				}
				EglHelper.throwEglException("eglDestroyContex", egl.eglGetError());
			}
			
			contextCounter.addAndGet(-1);
			Logger.warn("DefaultContextFactory > destroyContext %s [contexts #%s, surfaces #%s]",context,contextCounter.get(), surfaceCounter.get());
		}
	}
	private class DefaultWindowSurfaceFactory implements EGLWindowSurfaceFactory {
		
		public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig config, Object nativeWindow) {
			EGLSurface surface = null;
			try {
				surface = egl.eglCreateWindowSurface(display, config, nativeWindow, null);
						
				surfaceCounter.addAndGet(1);
				Logger.warn("DefaultWindowSurfaceFactory > eglCreateWindowSurface %s [contexts #%s, surfaces #%s]",surface,contextCounter.get(), surfaceCounter.get());
			} catch (IllegalArgumentException e) {
				// This exception indicates that the surface flinger surface
				// is not valid. This can happen if the surface flinger surface has
				// been torn down, but the application has not yet been
				// notified via SurfaceHolder.Callback.surfaceDestroyed.
				// In theory the application should be notified first,
				// but in practice sometimes it is not. See b/4588890
				Logger.error("eglCreateWindowSurface %s", e);
			}
			return surface;
		}

		public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {									
			egl.eglDestroySurface(display, surface);
			
			surfaceCounter.addAndGet(-1);
			Logger.warn("DefaultWindowSurfaceFactory > eglDestroySurface %s [contexts #%s, surfaces #%s]",surface,contextCounter.get(), surfaceCounter.get());
		}
	}

	/**
	 * An interface for customizing the eglCreateContext and eglDestroyContext calls.
	 * <p>
	 * This interface must be implemented by clients wishing to call {@link ArgonGLSurfaceView16#setEGLContextFactory(EGLContextFactory)}
	 */
	public interface EGLContextFactory {
		EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig);

		void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context);
	}
	/**
	 * An EGL helper class.
	 */

	private static class EglHelper {
		public static String formatEglError(String function, int error) {
			return function + " failed: " + error;// + EGLLogWrapper.getErrorString(error);
		}

		public static void logEglErrorAsWarning(String tag, String function, int error) {
			Logger.warn(formatEglError(function, error));
		}

		public static void throwEglException(String function, int error) {
			String message = formatEglError(function, error);
			if (LOG_THREADS) {
				Logger.error("EglHelper, throwEglException tid=" + Thread.currentThread().getId() + " " + message);
			}
			throw new RuntimeException(message);
		}

		EGL10 mEgl;

		EGLConfig mEglConfig;

		EGLContext mEglContext;

		EGLDisplay mEglDisplay;

		EGLSurface mEglSurface;
		
		private WeakReference<ArgonGLSurfaceView16> mGLSurfaceViewWeakRef;

		public EglHelper(WeakReference<ArgonGLSurfaceView16> glSurfaceViewWeakRef) {
			mGLSurfaceViewWeakRef = glSurfaceViewWeakRef;
		}

		/**
		 * Create a GL object for the current EGL context.
		 * 
		 * @return
		 */
		GL createGL() {

			GL gl = mEglContext.getGL();
			ArgonGLSurfaceView16 view = mGLSurfaceViewWeakRef.get();
			if (view != null) {
				if (view.mGLWrapper != null) {
					gl = view.mGLWrapper.wrap(gl);
				}

				if ((view.mDebugFlags & (DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS)) != 0) {
					int configFlags = 0;
					Writer log = null;
					if ((view.mDebugFlags & DEBUG_CHECK_GL_ERROR) != 0) {
						configFlags |= GLDebugHelper.CONFIG_CHECK_GL_ERROR;
					}
					if ((view.mDebugFlags & DEBUG_LOG_GL_CALLS) != 0) {
						log = new LogWriter();
					}
					gl = GLDebugHelper.wrap(gl, configFlags, log);
				}
			}
			return gl;
		}

		/**
		 * Create an egl surface for the current SurfaceHolder surface. If a surface already exists, destroy it before creating the new surface.
		 *
		 * @return true if the surface was created successfully.
		 */
		public boolean createSurface() {
			if (LOG_EGL) {
				Logger.warn("EglHelper, createSurface()  tid=" + Thread.currentThread().getId());
			}
			/*
			 * Check preconditions.
			 */
			if (mEgl == null) {
				throw new RuntimeException("egl not initialized");
			}
			if (mEglDisplay == null) {
				throw new RuntimeException("eglDisplay not initialized");
			}
			if (mEglConfig == null) {
				throw new RuntimeException("mEglConfig not initialized");
			}

			/*
			 * The window size has changed, so we need to create a new surface.
			 */
			destroySurfaceImp();

			/*
			 * Create an EGL surface we can render into.
			 */
			ArgonGLSurfaceView16 view = mGLSurfaceViewWeakRef.get();
			if (view != null) {
				mEglSurface = view.mEGLWindowSurfaceFactory.createWindowSurface(mEgl, mEglDisplay, mEglConfig, view.getHolder());
			} else {
				mEglSurface = null;
			}

			if (mEglSurface == null || mEglSurface == EGL10.EGL_NO_SURFACE) {
				int error = mEgl.eglGetError();
				if (error == EGL10.EGL_BAD_NATIVE_WINDOW) {
					Logger.error("EglHelper, createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
				}
				return false;
			}

			/*
			 * Before we can issue GL commands, we need to make sure the context is current and bound to a surface.
			 */
			if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
				/*
				 * Could not make the context current, probably because the underlying SurfaceView surface has been destroyed.
				 */
				logEglErrorAsWarning("EGLHelper", "eglMakeCurrent", mEgl.eglGetError());
				return false;
			}
			Logger.info("EglHelper, createWindowSurface eglMakeCurrent.");

			return true;
		}

		public void destroySurface() {
			if (LOG_EGL) {
				Logger.warn("EglHelper, destroySurface()  tid=" + Thread.currentThread().getId());
			}
			destroySurfaceImp();
		}

		private void destroySurfaceImp() {
			if (mEglSurface != null && mEglSurface != EGL10.EGL_NO_SURFACE) {
				mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
				ArgonGLSurfaceView16 view = mGLSurfaceViewWeakRef.get();
				if (view != null) {
					view.mEGLWindowSurfaceFactory.destroySurface(mEgl, mEglDisplay, mEglSurface);
				}
				mEglSurface = null;
			}
		}
		/**
		 * xcesco: esco e distruggo il display
		 */
		public void disconnectDisplay()
		{
			if (mEglDisplay != null) {		
				Logger.info("disconnectDisplay %s", mEglDisplay);
				// xcesco: non rimuoviamo il display
				mEgl.eglTerminate(mEglDisplay);
				mEglDisplay = null;
			}
		}
		public void finish() {
			if (LOG_EGL) {
				Logger.warn("EglHelper, finish() tid=" + Thread.currentThread().getId());
			}
			if (mEglContext != null) {
				ArgonGLSurfaceView16 view = mGLSurfaceViewWeakRef.get();
				if (view != null) {
					view.mEGLContextFactory.destroyContext(mEgl, mEglDisplay, mEglContext);
				}
				mEglContext = null;
			}
			if (mEglDisplay != null) {				
				// xcesco: non rimuoviamo il display
				//mEgl.eglTerminate(mEglDisplay);
				//mEglDisplay = null;
			}
		}
		/**
		 * Initialize EGL for a given configuration spec.
		 * 
		 * @param configSpec
		 */
		public void start() {
			if (LOG_EGL) {
				Logger.warn("EglHelper, start() tid=%s", Thread.currentThread().getId());
			}
			/*
			 * Get an EGL instance
			 */
			// V1
			mEgl = (EGL10) EGLContext.getEGL();

			/*
			 * xcesco: Get to the default display. 
			 */
			if (mEglDisplay == null) {
				mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
				Logger.info("mEglDisplay INITIALIZATED %s", mEglDisplay);

				if (mEglDisplay == EGL10.EGL_NO_DISPLAY) {
					throw new RuntimeException("eglGetDisplay failed");
				}

				/*
				 * We can now initialize EGL for that display
				 */
				int[] version = new int[2];

				if (!mEgl.eglInitialize(mEglDisplay, version)) {
					Logger.warn("eglInitialize failed - %s %s",version[0], version[1]);
				}				
			} else {
				Logger.info("mEglDisplay RESUED %s", mEglDisplay);
			}
			ArgonGLSurfaceView16 view = mGLSurfaceViewWeakRef.get();
			if (view == null) {
				mEglConfig = null;
				mEglContext = null;
			} else {
				mEglConfig = view.mEGLConfigChooser.chooseConfig(mEgl, mEglDisplay);

				/*
				 * Create an EGL context. We want to do this as rarely as we can, because an EGL context is a somewhat heavy object.
				 */
				mEglContext = view.mEGLContextFactory.createContext(mEgl, mEglDisplay, mEglConfig);
			}
			if (mEglContext == null || mEglContext == EGL10.EGL_NO_CONTEXT) {
				mEglContext = null;
				throwEglException("createContext");
			}
			if (LOG_EGL) {
				Logger.warn("EglHelper, createContext " + mEglContext + " tid=" + Thread.currentThread().getId());
			}

			mEglSurface = null;
		}
		/**
		 * Display the current render surface.
		 * 
		 * @return the EGL error code from eglSwapBuffers.
		 */
		public int swap() {
			if (!mEgl.eglSwapBuffers(mEglDisplay, mEglSurface)) {
				return mEgl.eglGetError();
			}
			return EGL10.EGL_SUCCESS;
		}
		private void throwEglException(String function) {
			throwEglException(function, mEgl.eglGetError());
		}

	}
	/**
	 * An interface for customizing the eglCreateWindowSurface and eglDestroySurface calls.
	 * <p>
	 * This interface must be implemented by clients wishing to call {@link ArgonGLSurfaceView16#setEGLWindowSurfaceFactory(EGLWindowSurfaceFactory)}
	 */
	public interface EGLWindowSurfaceFactory {
		/**
		 * @return null if the surface cannot be constructed.
		 */
		EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig config, Object nativeWindow);

		void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface);
	}
	/**
	 * A generic GL Thread. Takes care of initializing EGL and GL. Delegates to a Renderer instance to do the actual drawing. Can be configured to render continuously or on request.
	 *
	 * All potentially blocking synchronization is done through the sGLThreadManager object. This avoids multiple-lock ordering issues.
	 *
	 */
	class GLThread extends Thread {
		private EglHelper mEglHelper;

		private ArrayList<Runnable> mEventQueue = new ArrayList<Runnable>();

		private boolean mExited;

		private boolean mFinishedCreatingEglSurface;

		/**
		 * Set once at thread construction time, nulled out when the parent view is garbage called. This weak reference allows the GLSurfaceView to be garbage collected while the GLThread is still alive.
		 */
		private WeakReference<ArgonGLSurfaceView16> mGLSurfaceViewWeakRef;

		private boolean mHasSurface;

		private boolean mHaveEglContext;

		private boolean mHaveEglSurface;

		private int mHeight;

		private boolean mPaused;

		private boolean mRenderComplete;

		private int mRenderMode;

		private boolean mRequestPaused;

		private boolean mRequestRender;

		// Once the thread is started, all accesses to the following member
		// variables are protected by the sGLThreadManager monitor
		private boolean mShouldExit;

		private boolean mShouldReleaseEglContext;

		private boolean mSizeChanged = true;

		private boolean mSurfaceIsBad;

		private boolean mWaitingForSurface;

		private int mWidth;
		GLThread(WeakReference<ArgonGLSurfaceView16> glSurfaceViewWeakRef) {
			super();
			mWidth = 0;
			mHeight = 0;
			mRequestRender = true;
			mRenderMode = RENDERMODE_CONTINUOUSLY;
			mGLSurfaceViewWeakRef = glSurfaceViewWeakRef;
		}
		public boolean ableToDraw() {
			return mHaveEglContext && mHaveEglSurface && readyToDraw();
		}
		/**
		 * xcesco: disconnette il display al momento di uscire
		 */
		private void disconnectDisplay() {
			mEglHelper.disconnectDisplay();			
		}
		public int getRenderMode() {
			synchronized (sGLThreadManager) {
				return mRenderMode;
			}
		}
		private void guardedRun() throws InterruptedException {
			mEglHelper = new EglHelper(mGLSurfaceViewWeakRef);
			mHaveEglContext = false;
			mHaveEglSurface = false;
			try {
				GL10 gl = null;
				boolean createEglContext = false;
				boolean createEglSurface = false;
				boolean createGlInterface = false;
				boolean lostEglContext = false;
				boolean sizeChanged = false;
				boolean wantRenderNotification = false;
				boolean doRenderNotification = false;
				boolean askedToReleaseEglContext = false;
				int w = 0;
				int h = 0;
				Runnable event = null;

				while (true) {
					synchronized (sGLThreadManager) {
						while (true) {
							if (mShouldExit) {								
								return;
							}

							if (!mEventQueue.isEmpty()) {
								event = mEventQueue.remove(0);
								break;
							}

							// Update the pause state.
							boolean pausing = false;
							if (mPaused != mRequestPaused) {
								pausing = mRequestPaused;
								mPaused = mRequestPaused;
								sGLThreadManager.notifyAll();
								if (LOG_PAUSE_RESUME) {
									Logger.info("GLThread, mPaused is now " + mPaused + " tid=" + getId());
								}
							}

							// Do we need to give up the EGL context?
							if (mShouldReleaseEglContext) {
								if (LOG_SURFACE) {
									Logger.info("GLThread, releasing EGL context because asked to tid=" + getId());
								}
								stopEglSurfaceLocked();
								stopEglContextLocked();
								mShouldReleaseEglContext = false;
								askedToReleaseEglContext = true;
							}

							// Have we lost the EGL context?
							if (lostEglContext) {
								stopEglSurfaceLocked();
								stopEglContextLocked();
								lostEglContext = false;
							}

							// When pausing, release the EGL surface:
							if (pausing && mHaveEglSurface) {
								if (LOG_SURFACE) {
									Logger.info("GLThread, releasing EGL surface because paused tid=" + getId());
								}
								stopEglSurfaceLocked();
							}

							// When pausing, optionally release the EGL Context:
							if (pausing && mHaveEglContext) {
								ArgonGLSurfaceView16 view = mGLSurfaceViewWeakRef.get();
								boolean preserveEglContextOnPause = view == null ? false : view.mPreserveEGLContextOnPause;
								if (!preserveEglContextOnPause || sGLThreadManager.shouldReleaseEGLContextWhenPausing()) {
									stopEglContextLocked();
									if (LOG_SURFACE) {
										Logger.info("GLThread, releasing EGL context because paused tid=" + getId());
									}
								}
							}

							// When pausing, optionally terminate EGL:
							if (pausing) {
								if (sGLThreadManager.shouldTerminateEGLWhenPausing()) {
									mEglHelper.finish();
									if (LOG_SURFACE) {
										Logger.info("GLThread, terminating EGL because paused tid=" + getId());
									}
								}
							}

							// Have we lost the SurfaceView surface?
							if ((!mHasSurface) && (!mWaitingForSurface)) {
								if (LOG_SURFACE) {
									Logger.info("GLThread, noticed surfaceView surface lost tid=" + getId());
								}
								if (mHaveEglSurface) {
									stopEglSurfaceLocked();
								}
								mWaitingForSurface = true;
								mSurfaceIsBad = false;
								sGLThreadManager.notifyAll();
							}

							// Have we acquired the surface view surface?
							if (mHasSurface && mWaitingForSurface) {
								if (LOG_SURFACE) {
									Logger.info("GLThread, noticed surfaceView surface acquired tid=" + getId());
								}
								mWaitingForSurface = false;
								sGLThreadManager.notifyAll();
							}

							if (doRenderNotification) {
								if (LOG_SURFACE) {
									Logger.info("GLThread, sending render notification tid=" + getId());
								}
								wantRenderNotification = false;
								doRenderNotification = false;
								mRenderComplete = true;
								sGLThreadManager.notifyAll();
							}

							// Ready to draw?
							if (readyToDraw()) {

								// If we don't have an EGL context, try to acquire one.
								if (!mHaveEglContext) {
									if (askedToReleaseEglContext) {
										askedToReleaseEglContext = false;
									} else if (sGLThreadManager.tryAcquireEglContextLocked(this)) {
										try {
											mEglHelper.start();
										} catch (RuntimeException t) {
											sGLThreadManager.releaseEglContextLocked(this);
											throw t;
										}
										mHaveEglContext = true;
										createEglContext = true;

										sGLThreadManager.notifyAll();
									}
								}

								if (mHaveEglContext && !mHaveEglSurface) {
									mHaveEglSurface = true;
									createEglSurface = true;
									createGlInterface = true;
									sizeChanged = true;
								}

								if (mHaveEglSurface) {
									if (mSizeChanged) {
										sizeChanged = true;
										w = mWidth;
										h = mHeight;
										wantRenderNotification = true;
										if (LOG_SURFACE) {
											Logger.info("GLThread, noticing that we want render notification tid=" + getId());
										}

										// Destroy and recreate the EGL surface.
										createEglSurface = true;

										mSizeChanged = false;
									}
									mRequestRender = false;
									sGLThreadManager.notifyAll();
									break;
								}
							}

							// By design, this is the only place in a GLThread thread where we wait().
							if (LOG_THREADS) {
								Logger.info("GLThread, waiting tid=" + getId() + " mHaveEglContext: " + mHaveEglContext + " mHaveEglSurface: " + mHaveEglSurface + " mFinishedCreatingEglSurface: " + mFinishedCreatingEglSurface
										+ " mPaused: " + mPaused + " mHasSurface: " + mHasSurface + " mSurfaceIsBad: " + mSurfaceIsBad + " mWaitingForSurface: " + mWaitingForSurface + " mWidth: " + mWidth + " mHeight: " + mHeight
										+ " mRequestRender: " + mRequestRender + " mRenderMode: " + mRenderMode);
							}
							sGLThreadManager.wait();
						}
					} // end of synchronized(sGLThreadManager)

					if (event != null) {
						event.run();
						event = null;
						continue;
					}

					if (createEglSurface) {
						if (LOG_SURFACE) {
							Logger.warn("GLThread, egl createSurface");
						}
						if (mEglHelper.createSurface()) {
							synchronized (sGLThreadManager) {
								mFinishedCreatingEglSurface = true;
								sGLThreadManager.notifyAll();
							}
						} else {
							synchronized (sGLThreadManager) {
								mFinishedCreatingEglSurface = true;
								mSurfaceIsBad = true;
								sGLThreadManager.notifyAll();
							}
							continue;
						}
						createEglSurface = false;
					}

					if (createGlInterface) {
						gl = (GL10) mEglHelper.createGL();

						sGLThreadManager.checkGLDriver(gl);
						createGlInterface = false;
					}

					if (createEglContext) {
						if (LOG_RENDERER) {
							Logger.warn("GLThread, onSurfaceCreated");
						}
						ArgonGLSurfaceView16 view = mGLSurfaceViewWeakRef.get();
						if (view != null) {
							try {
								// Logger.info("onSurfaceCreated");
								view.mRenderer.onSurfaceCreated();
							} finally {
								// Trace.traceEnd(Trace.TRACE_TAG_VIEW);
							}
						}
						createEglContext = false;
					}

					if (sizeChanged) {
						if (LOG_RENDERER) {
							Logger.warn("GLThread, onSurfaceChanged(" + w + ", " + h + ")");
						}
						ArgonGLSurfaceView16 view = mGLSurfaceViewWeakRef.get();
						if (view != null) {
							try {
								// Trace.traceBegin(Trace.TRACE_TAG_VIEW, "onSurfaceChanged");
								view.mRenderer.onSurfaceChanged(w, h);
							} finally {
								// Trace.traceEnd(Trace.TRACE_TAG_VIEW);
							}
						}
						sizeChanged = false;
					}

					if (LOG_RENDERER_DRAW_FRAME) {
						Logger.warn("GLThread, onDrawFrame tid=" + getId());
					}
					{
						ArgonGLSurfaceView16 view = mGLSurfaceViewWeakRef.get();
						if (view != null) {
							try {
								// Trace.traceBegin(Trace.TRACE_TAG_VIEW, "onDrawFrame");
								view.mRenderer.onDrawFrame();
							} finally {
								// Trace.traceEnd(Trace.TRACE_TAG_VIEW);
							}
						}
					}
					
					
					
					int swapError = mEglHelper.swap();
					switch (swapError) {
					case EGL10.EGL_SUCCESS:
						break;
					case EGL11.EGL_CONTEXT_LOST:
						if (LOG_SURFACE) {
							Logger.info("GLThread, egl context lost tid=" + getId());
						}
						lostEglContext = true;
						break;
					default:
						// Other errors typically mean that the current surface is bad,
						// probably because the SurfaceView surface has been destroyed,
						// but we haven't been notified yet.
						// Log the error to help developers understand why rendering stopped.
						EglHelper.logEglErrorAsWarning("GLThread", "eglSwapBuffers", swapError);

						synchronized (sGLThreadManager) {
							mSurfaceIsBad = true;
							sGLThreadManager.notifyAll();
						}
						break;
					}

					if (wantRenderNotification) {
						doRenderNotification = true;
					}
				}

			} finally {
				/*
				 * clean-up everything...
				 */
				synchronized (sGLThreadManager) {
					stopEglSurfaceLocked();
					stopEglContextLocked();
					if (destroyDisplayOnExit)
					{
						disconnectDisplay();
					}
				}
			}
		}
		public void onPause() {
			synchronized (sGLThreadManager) {
				if (LOG_PAUSE_RESUME) {
					Logger.info("GLThread, onPause tid=" + getId());
				}
				mRequestPaused = true;
				sGLThreadManager.notifyAll();
				while ((!mExited) && (!mPaused)) {
					if (LOG_PAUSE_RESUME) {
						Logger.info("Main Thread, onPause waiting for mPaused.");
					}
					try {
						sGLThreadManager.wait();
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}
		public void onResume() {
			synchronized (sGLThreadManager) {
				if (LOG_PAUSE_RESUME) {
					Logger.info("GLThread, onResume tid=" + getId());
				}
				mRequestPaused = false;
				mRequestRender = true;
				mRenderComplete = false;
				sGLThreadManager.notifyAll();
				while ((!mExited) && mPaused && (!mRenderComplete)) {
					if (LOG_PAUSE_RESUME) {
						Logger.info("Main Thread, onResume waiting for !mPaused.");
					}
					try {
						sGLThreadManager.wait();
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}
		public void onWindowResize(int w, int h) {
			synchronized (sGLThreadManager) {
				mWidth = w;
				mHeight = h;
				mSizeChanged = true;
				mRequestRender = true;
				mRenderComplete = false;
				sGLThreadManager.notifyAll();

				// Wait for thread to react to resize and render a frame
				while (!mExited && !mPaused && !mRenderComplete && ableToDraw()) {
					if (LOG_SURFACE) {
						Logger.info("Main Thread, onWindowResize waiting for render complete from tid=" + getId());
					}
					try {
						sGLThreadManager.wait();
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}
		/**
		 * Queue an "event" to be run on the GL rendering thread.
		 * 
		 * @param r
		 *            the runnable to be run on the GL rendering thread.
		 */
		public void queueEvent(Runnable r) {
			if (r == null) {
				throw new IllegalArgumentException("r must not be null");
			}
			synchronized (sGLThreadManager) {
				mEventQueue.add(r);
				sGLThreadManager.notifyAll();
			}
		}
		private boolean readyToDraw() {
			return (!mPaused) && mHasSurface && (!mSurfaceIsBad) && (mWidth > 0) && (mHeight > 0) && (mRequestRender || (mRenderMode == RENDERMODE_CONTINUOUSLY));
		}
		public void requestExitAndWait() {
			// don't call this from GLThread thread or it is a guaranteed
			// deadlock!
			synchronized (sGLThreadManager) {
				mShouldExit = true;
				sGLThreadManager.notifyAll();
				while (!mExited) {
					try {
						sGLThreadManager.wait();
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}
		public void requestReleaseEglContextLocked() {
			mShouldReleaseEglContext = true;
			sGLThreadManager.notifyAll();
		}
		public void requestRender() {
			synchronized (sGLThreadManager) {
				mRequestRender = true;
				sGLThreadManager.notifyAll();
			}
		}
		@Override
		public void run() {
			setName("GLThread " + getId());
			if (LOG_THREADS) {
				Logger.warn("GLThread starting tid=" + getId());
			}

			try {
				guardedRun();
			} catch (InterruptedException e) {
				// fall thru and exit normally
				Logger.fatal(e.getMessage());
				e.printStackTrace();
			} finally {
				sGLThreadManager.threadExiting(this);
			}
		}
		public void setRenderMode(int renderMode) {
			if (!((RENDERMODE_WHEN_DIRTY <= renderMode) && (renderMode <= RENDERMODE_CONTINUOUSLY))) {
				throw new IllegalArgumentException("renderMode");
			}
			synchronized (sGLThreadManager) {
				mRenderMode = renderMode;
				sGLThreadManager.notifyAll();
			}
		}
		/*
		 * This private method should only be called inside a synchronized(sGLThreadManager) block.
		 */
		private void stopEglContextLocked() {
			if (mHaveEglContext) {
				mEglHelper.finish();
				mHaveEglContext = false;
				sGLThreadManager.releaseEglContextLocked(this);
			}
		}
		/*
		 * This private method should only be called inside a synchronized(sGLThreadManager) block.
		 */
		private void stopEglSurfaceLocked() {
			if (mHaveEglSurface) {
				mHaveEglSurface = false;
				mEglHelper.destroySurface();
			}
		}

		// End of member variables protected by the sGLThreadManager monitor.

		public void surfaceCreated() {
			synchronized (sGLThreadManager) {
				if (LOG_THREADS) {
					Logger.info("GLThread, surfaceCreated tid=" + getId());
				}
				mHasSurface = true;
				mFinishedCreatingEglSurface = false;
				sGLThreadManager.notifyAll();
				while (mWaitingForSurface && !mFinishedCreatingEglSurface && !mExited) {
					try {
						sGLThreadManager.wait();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}

		public void surfaceDestroyed() {
			synchronized (sGLThreadManager) {
				if (LOG_THREADS) {
					Logger.info("GLThread, surfaceDestroyed tid=" + getId());
				}
				mHasSurface = false;
				sGLThreadManager.notifyAll();
				while ((!mWaitingForSurface) && (!mExited)) {
					try {
						sGLThreadManager.wait();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}

	}
	private static class GLThreadManager {
		// private static String TAG = "GLThreadManager";

		private static final int kGLES_20 = 0x20000;

		private static final String kMSM7K_RENDERER_PREFIX = "Q3Dimension MSM7500 ";

		private GLThread mEglOwner;

		private boolean mGLESDriverCheckComplete;

		private int mGLESVersion;

		/**
		 * This check was required for some pre-Android-3.0 hardware. Android 3.0 provides support for hardware-accelerated views, therefore multiple EGL contexts are supported on all Android 3.0+ EGL drivers.
		 */
		private boolean mGLESVersionCheckComplete;

		private boolean mLimitedGLESContexts;

		private boolean mMultipleGLESContextsAllowed;
		public synchronized void checkGLDriver(GL10 gl) {
			if (!mGLESDriverCheckComplete) {
				checkGLESVersion();
				String renderer = gl.glGetString(GL10.GL_RENDERER);
				if (mGLESVersion < kGLES_20) {
					mMultipleGLESContextsAllowed = !renderer.startsWith(kMSM7K_RENDERER_PREFIX);
					notifyAll();
				}
				mLimitedGLESContexts = !mMultipleGLESContextsAllowed;
				if (LOG_SURFACE) {
					Logger.warn("checkGLDriver renderer = \"" + renderer + "\" multipleContextsAllowed = " + mMultipleGLESContextsAllowed + " mLimitedGLESContexts = " + mLimitedGLESContexts);
				}
				mGLESDriverCheckComplete = true;
			}
		}
		private void checkGLESVersion() {
			if (!mGLESVersionCheckComplete) {
				/*
				 * mGLESVersion = SystemProperties.getInt( "ro.opengles.version", ConfigurationInfo.GL_ES_VERSION_UNDEFINED); if (mGLESVersion >= kGLES_20) { mMultipleGLESContextsAllowed = true; }
				 */
				mGLESVersion = kGLES_20;
				mMultipleGLESContextsAllowed = true;
				if (LOG_SURFACE) {
					Logger.warn("checkGLESVersion mGLESVersion =" + " " + mGLESVersion + " mMultipleGLESContextsAllowed = " + mMultipleGLESContextsAllowed);
				}
				mGLESVersionCheckComplete = true;
			}
		}
		/*
		 * Releases the EGL context. Requires that we are already in the sGLThreadManager monitor when this is called.
		 */
		public void releaseEglContextLocked(GLThread thread) {
			if (mEglOwner == thread) {
				mEglOwner = null;
			}
			notifyAll();
		}
		public synchronized boolean shouldReleaseEGLContextWhenPausing() {
			// Release the EGL context when pausing even if
			// the hardware supports multiple EGL contexts.
			// Otherwise the device could run out of EGL contexts.
			return mLimitedGLESContexts;
		}
		public synchronized boolean shouldTerminateEGLWhenPausing() {
			checkGLESVersion();
			return !mMultipleGLESContextsAllowed;
		}
		public synchronized void threadExiting(GLThread thread) {
			if (LOG_THREADS) {
				Logger.info("GLThread exiting tid=" + thread.getId());
			}
			thread.mExited = true;
			if (mEglOwner == thread) {
				mEglOwner = null;
			}
			notifyAll();
		}
		/*
		 * Tries once to acquire the right to use an EGL context. Does not block. Requires that we are already in the sGLThreadManager monitor when this is called.
		 * 
		 * @return true if the right to use an EGL context was acquired.
		 */
		public boolean tryAcquireEglContextLocked(GLThread thread) {
			if (mEglOwner == thread || mEglOwner == null) {
				mEglOwner = thread;
				notifyAll();
				return true;
			}
			checkGLESVersion();
			if (mMultipleGLESContextsAllowed) {
				return true;
			}
			// Notify the owning thread that it should release the context.
			// TODO: implement a fairness policy. Currently
			// if the owning thread is drawing continuously it will just
			// reacquire the EGL context.
			if (mEglOwner != null) {
				mEglOwner.requestReleaseEglContextLocked();
			}
			return false;
		}
	}

	/**
	 * An interface used to wrap a GL interface.
	 * <p>
	 * Typically used for implementing debugging and tracing on top of the default GL interface. You would typically use this by creating your own class that implemented all the GL methods by delegating to another GL instance. Then you
	 * could add your own behavior before or after calling the delegate. All the GLWrapper would do was instantiate and return the wrapper GL instance:
	 * 
	 * <pre class="prettyprint">
	 * class MyGLWrapper implements GLWrapper {
	 *     GL wrap(GL gl) {
	 *         return new MyGLImplementation(gl);
	 *     }
	 *     static class MyGLImplementation implements GL,GL10,GL11,... {
	 *         ...
	 *     }
	 * }
	 * </pre>
	 * 
	 * @see #setGLWrapper(GLWrapper)
	 */
	public interface GLWrapper {
		/**
		 * Wraps a gl interface in another gl interface.
		 * 
		 * @param gl
		 *            a GL interface that is to be wrapped.
		 * @return either the input argument or another GL object that wraps the input argument.
		 */
		GL wrap(GL gl);
	}

	static class LogWriter extends Writer {

		private StringBuilder mBuilder = new StringBuilder();

		@Override
		public void close() {
			flushBuilder();
		}

		@Override
		public void flush() {
			flushBuilder();
		}

		private void flushBuilder() {
			if (mBuilder.length() > 0) {
				Logger.verbose("Main Thread,GLSurfaceView " + mBuilder.toString());
				mBuilder.delete(0, mBuilder.length());
			}
		}

		@Override
		public void write(char[] buf, int offset, int count) {
			for (int i = 0; i < count; i++) {
				char c = buf[offset + i];
				if (c == '\n') {
					flushBuilder();
				} else {
					mBuilder.append(c);
				}
			}
		}
	}


	/**
	 * Check glError() after every GL call and throw an exception if glError indicates that an error has occurred. This can be used to help track down which OpenGL ES call is causing an error.
	 *
	 * @see #getDebugFlags
	 * @see #setDebugFlags
	 */
	public final static int DEBUG_CHECK_GL_ERROR = 1;

	/**
	 * Log GL calls to the system log at "verbose" level with tag "GLSurfaceView".
	 *
	 * @see #getDebugFlags
	 * @see #setDebugFlags
	 */
	public final static int DEBUG_LOG_GL_CALLS = 2;

	private final static boolean LOG_ATTACH_DETACH = true;

	private final static boolean LOG_EGL = true;

	private final static boolean LOG_PAUSE_RESUME = true;

	private final static boolean LOG_RENDERER = true;

	private final static boolean LOG_RENDERER_DRAW_FRAME = false;

	private final static boolean LOG_SURFACE = true;

	private final static boolean LOG_THREADS = true;

	/**
	 * The renderer is called continuously to re-render the scene.
	 *
	 * @see #getRenderMode()
	 * @see #setRenderMode(int)
	 */
	public final static int RENDERMODE_CONTINUOUSLY = 1;

	/**
	 * The renderer only renders when the surface is created, or when {@link #requestRender} is called.
	 *
	 * @see #getRenderMode()
	 * @see #setRenderMode(int)
	 * @see #requestRender()
	 */
	public final static int RENDERMODE_WHEN_DIRTY = 0;

	private static final GLThreadManager sGLThreadManager = new GLThreadManager();
	// private final GLThreadManager sGLThreadManager = new GLThreadManager();

	AtomicInteger contextCounter=new AtomicInteger();

	/**
	 * xcesco: per un uso normale, all'uscita del thread che si occupa del contesto GL dovremmo 
	 * distruggere il display. Mettendo a false questa variabile il display viene preservato e di 
	 * conseguenza il display viene mantenuto. Questo evita sul MIPAD (tegra K1) di avere un BAD_DISPLAY
	 * la seconda volta (ma solo la seconda) che uso la view, ad esempio ruotando il display mentre
	 * si è in modalità preview.
	 */
	protected boolean destroyDisplayOnExit=true;

	private int mDebugFlags;

	private boolean mDetached;

	private ArgonConfigChooser16 mEGLConfigChooser;

	private int mEGLContextClientVersion;

	private EGLContextFactory mEGLContextFactory;

	private EGLWindowSurfaceFactory mEGLWindowSurfaceFactory;

	private GLThread mGLThread;

	private GLWrapper mGLWrapper;	
	

	private boolean mPreserveEGLContextOnPause;

	private final WeakReference<ArgonGLSurfaceView16> mThisWeakRef = new WeakReference<ArgonGLSurfaceView16>(this);

	// ----------------------------------------------------------------------

	AtomicInteger surfaceCounter=new AtomicInteger();

	/**
	 * Standard View constructor. In order to render something, you must call {@link #setRenderer} to register a renderer.
	 */
	public ArgonGLSurfaceView16(Context context) {
		super(context);
		init();
	}

	/**
	 * Standard View constructor. In order to render something, you must call {@link #setRenderer} to register a renderer.
	 */
	public ArgonGLSurfaceView16(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void checkRenderThreadState() {
		if (mGLThread != null) {
			throw new IllegalStateException("setRenderer has already been called for this instance.");
		}
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			if (mGLThread != null) {
				// GLThread may still be running if this view was never
				// attached to a window.
				mGLThread.requestExitAndWait();
			}
		} finally {
			super.finalize();
		}
	}

	/**
	 * Get the current value of the debug flags.
	 * 
	 * @return the current value of the debug flags.
	 */
	public int getDebugFlags() {
		return mDebugFlags;
	}

	/**
	 * @return true if the EGL context will be preserved when paused
	 */
	public boolean getPreserveEGLContextOnPause() {
		return mPreserveEGLContextOnPause;
	}

	/**
	 * Get the current rendering mode. May be called from any thread. Must not be called before a renderer has been set.
	 * 
	 * @return the current rendering mode.
	 * @see #RENDERMODE_CONTINUOUSLY
	 * @see #RENDERMODE_WHEN_DIRTY
	 */
	public int getRenderMode() {
		return mGLThread.getRenderMode();
	}

	private void init() {
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		// setFormat is done by SurfaceView in SDK 2.3 and newer. Uncomment
		// this statement if back-porting to 2.2 or older:
		//holder.setFormat(PixelFormat.RGB_565);
		//
		// setType is not needed for SDK 2.0 or newer. Uncomment this
		// statement if back-porting this code to older SDKs.
		//holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
	}

	/**
	 * This method is used as part of the View class and is not normally called or subclassed by clients of GLSurfaceView.
	 */
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (LOG_ATTACH_DETACH) {
			Logger.debug("onAttachedToWindow reattach =" + mDetached);
		}
		if (mDetached && (mRenderer != null)) {
			int renderMode = RENDERMODE_CONTINUOUSLY;
			if (mGLThread != null) {
				renderMode = mGLThread.getRenderMode();
			}
			mGLThread = new GLThread(mThisWeakRef);
			if (renderMode != RENDERMODE_CONTINUOUSLY) {
				mGLThread.setRenderMode(renderMode);
			}
			mGLThread.start();
		}
		mDetached = false;
	}

	@Override
	protected void onDetachedFromWindow() {
		if (LOG_ATTACH_DETACH) {
			Logger.debug("onDetachedFromWindow");
		}
		if (mGLThread != null) {
			mGLThread.requestExitAndWait();
		}
		mDetached = true;
		super.onDetachedFromWindow();
	}

	/**
	 * Inform the view that the activity is paused. The owner of this view must call this method when the activity is paused. Calling this method will pause the rendering thread. Must not be called before a renderer has been set.
	 */
	public void onPause() {
		mGLThread.onPause();
		mRenderer.onPause();
	}

	/**
	 * Inform the view that the activity is resumed. The owner of this view must call this method when the activity is resumed. Calling this method will recreate the OpenGL display and resume the rendering thread. Must not be called before
	 * a renderer has been set.
	 */
	public void onResume() {
		mGLThread.onResume();		
		mRenderer.onResume();
	}

	/**
	 * Queue a runnable to be run on the GL rendering thread. This can be used to communicate with the Renderer on the rendering thread. Must not be called before a renderer has been set.
	 * 
	 * @param r
	 *            the runnable to be run on the GL rendering thread.
	 */
	public void queueEvent(Runnable r) {
		mGLThread.queueEvent(r);
	}

	/**
	 * Request that the renderer render a frame. This method is typically used when the render mode has been set to {@link #RENDERMODE_WHEN_DIRTY}, so that frames are only rendered on demand. May be called from any thread. Must not be
	 * called before a renderer has been set.
	 */
	public void requestRender() {
		mGLThread.requestRender();
	}

	/**
	 * Set the debug flags to a new value. The value is constructed by OR-together zero or more of the DEBUG_CHECK_* constants. The debug flags take effect whenever a surface is created. The default value is zero.
	 * 
	 * @param debugFlags
	 *            the new debug flags
	 * @see #DEBUG_CHECK_GL_ERROR
	 * @see #DEBUG_LOG_GL_CALLS
	 */
	public void setDebugFlags(int debugFlags) {
		mDebugFlags = debugFlags;
	}

	/**
	 * Install a custom EGLConfigChooser.
	 * <p>
	 * If this method is called, it must be called before {@link #setRenderer(Renderer)} is called.
	 * <p>
	 * If no setEGLConfigChooser method is called, then by default the view will choose an EGLConfig that is compatible with the current android.view.Surface, with a depth buffer depth of at least 16 bits.
	 * 
	 * @param configChooser
	 */
	public void setEGLConfigChooser(ArgonConfigChooser16 configChooser) {
		checkRenderThreadState();
		mEGLConfigChooser = configChooser;
	}

	/**
	 * Inform the default EGLContextFactory and default EGLConfigChooser which EGLContext client version to pick.
	 * <p>
	 * Use this method to create an OpenGL ES 2.0-compatible context. Example:
	 * 
	 * <pre class="prettyprint">
	 * public MyView(Context context) {
	 * 	super(context);
	 * 	setEGLContextClientVersion(2); // Pick an OpenGL ES 2.0 context.
	 * 	setRenderer(new MyRenderer());
	 * }
	 * </pre>
	 * <p>
	 * Note: Activities which require OpenGL ES 2.0 should indicate this by setting @lt;uses-feature android:glEsVersion="0x00020000" /> in the activity's AndroidManifest.xml file.
	 * <p>
	 * If this method is called, it must be called before {@link #setRenderer(Renderer)} is called.
	 * <p>
	 * This method only affects the behavior of the default EGLContexFactory and the default EGLConfigChooser. If {@link #setEGLContextFactory(EGLContextFactory)} has been called, then the supplied EGLContextFactory is responsible for
	 * creating an OpenGL ES 2.0-compatible context. If {@link #setEGLConfigChooser(ArgonConfigChooser16)} has been called, then the supplied EGLConfigChooser is responsible for choosing an OpenGL ES 2.0-compatible config.
	 * 
	 * @param version
	 *            The EGLContext client version to choose. Use 2 for OpenGL ES 2.0
	 */
	public void setEGLContextClientVersion(int version) {
		checkRenderThreadState();
		mEGLContextClientVersion = version;
	}
	/**
	 * Install a custom EGLContextFactory.
	 * <p>
	 * If this method is called, it must be called before {@link #setRenderer(Renderer)} is called.
	 * <p>
	 * If this method is not called, then by default a context will be created with no shared context and with a null attribute list.
	 */
	public void setEGLContextFactory(EGLContextFactory factory) {
		checkRenderThreadState();
		mEGLContextFactory = factory;
	}
	/**
	 * Install a custom EGLWindowSurfaceFactory.
	 * <p>
	 * If this method is called, it must be called before {@link #setRenderer(Renderer)} is called.
	 * <p>
	 * If this method is not called, then by default a window surface will be created with a null attribute list.
	 */
	public void setEGLWindowSurfaceFactory(EGLWindowSurfaceFactory factory) {
		checkRenderThreadState();
		mEGLWindowSurfaceFactory = factory;
	}
	/**
	 * Set the glWrapper. If the glWrapper is not null, its {@link GLWrapper#wrap(GL)} method is called whenever a surface is created. A GLWrapper can be used to wrap the GL object that's passed to the renderer. Wrapping a GL object enables
	 * examining and modifying the behavior of the GL calls made by the renderer.
	 * <p>
	 * Wrapping is typically used for debugging purposes.
	 * <p>
	 * The default value is null.
	 * 
	 * @param glWrapper
	 *            the new GLWrapper
	 */
	public void setGLWrapper(GLWrapper glWrapper) {
		mGLWrapper = glWrapper;
	}
	/**
	 * Control whether the EGL context is preserved when the GLSurfaceView is paused and resumed.
	 * <p>
	 * If set to true, then the EGL context may be preserved when the GLSurfaceView is paused. Whether the EGL context is actually preserved or not depends upon whether the Android device that the program is running on can support an
	 * arbitrary number of EGL contexts or not. Devices that can only support a limited number of EGL contexts must release the EGL context in order to allow multiple applications to share the GPU.
	 * <p>
	 * If set to false, the EGL context will be released when the GLSurfaceView is paused, and recreated when the GLSurfaceView is resumed.
	 * <p>
	 *
	 * The default is false.
	 *
	 * @param preserveOnPause
	 *            preserve the EGL context when paused
	 */
	public void setPreserveEGLContextOnPause(boolean preserveOnPause) {
		mPreserveEGLContextOnPause = preserveOnPause;
	}
	/**
	 * Set the renderer associated with this view. Also starts the thread that will call the renderer, which in turn causes the rendering to start.
	 * <p>
	 * This method should be called once and only once in the life-cycle of a GLSurfaceView.
	 * <p>
	 * The following GLSurfaceView methods can only be called <em>before</em> setRenderer is called:
	 * <ul>
	 * <li>{@link #setEGLConfigChooser(boolean)}
	 * <li>{@link #setEGLConfigChooser(ArgonConfigChooser16)}
	 * <li>{@link #setEGLConfigChooser(int, int, int, int, int, int)}
	 * </ul>
	 * <p>
	 * The following GLSurfaceView methods can only be called <em>after</em> setRenderer is called:
	 * <ul>
	 * <li>{@link #getRenderMode()}
	 * <li>{@link #onPause()}
	 * <li>{@link #onResume()}
	 * <li>{@link #queueEvent(Runnable)}
	 * <li>{@link #requestRender()}
	 * <li>{@link #setRenderMode(int)}
	 * </ul>
	 *
	 * @param renderer
	 *            the renderer to use to perform OpenGL drawing.
	 */
	public void setRenderer(ArgonGLRenderer renderer) {
		checkRenderThreadState();
		if (mEGLConfigChooser == null) {
			mEGLConfigChooser=ArgonGLConfigChooser.build();
//			mEGLConfigChooser = new SimpleEGLConfigChooser(false);
		}
		if (mEGLContextFactory == null) {
			mEGLContextFactory = new DefaultContextFactory();
		}
		if (mEGLWindowSurfaceFactory == null) {
			mEGLWindowSurfaceFactory = new DefaultWindowSurfaceFactory();
		}
		mRenderer = renderer;
		mGLThread = new GLThread(mThisWeakRef);
		mGLThread.start();
	}
	/**
	 * Set the rendering mode. When renderMode is RENDERMODE_CONTINUOUSLY, the renderer is called repeatedly to re-render the scene. When renderMode is RENDERMODE_WHEN_DIRTY, the renderer only rendered when the surface is created, or when
	 * {@link #requestRender} is called. Defaults to RENDERMODE_CONTINUOUSLY.
	 * <p>
	 * Using RENDERMODE_WHEN_DIRTY can improve battery life and overall system performance by allowing the GPU and CPU to idle when the view does not need to be updated.
	 * <p>
	 * This method can only be called after {@link #setRenderer(Renderer)}
	 *
	 * @param renderMode
	 *            one of the RENDERMODE_X constants
	 * @see #RENDERMODE_CONTINUOUSLY
	 * @see #RENDERMODE_WHEN_DIRTY
	 */
	public void setRenderMode(int renderMode) {
		mGLThread.setRenderMode(renderMode);
	}
	/**
	 * This method is part of the SurfaceHolder.Callback interface, and is not normally called or subclassed by clients of GLSurfaceView.
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		mGLThread.onWindowResize(w, h);
	}
	
	/**
	 * This method is part of the SurfaceHolder.Callback interface, and is not normally called or subclassed by clients of GLSurfaceView.
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		mGLThread.surfaceCreated();
	}
	
	/**
	 * This method is part of the SurfaceHolder.Callback interface, and is not normally called or subclassed by clients of GLSurfaceView.
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return
		mGLThread.surfaceDestroyed();
	}

}