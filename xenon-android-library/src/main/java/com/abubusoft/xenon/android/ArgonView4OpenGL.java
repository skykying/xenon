package com.abubusoft.xenon.android;

import com.abubusoft.xenon.Argon4OpenGL;
import com.abubusoft.xenon.ArgonBeanContext;
import com.abubusoft.xenon.ArgonBeanType;
import com.abubusoft.xenon.android.listener.ArgonGestureDetector;
import com.abubusoft.xenon.android.surfaceview16.ArgonGLSurfaceView16;
import com.abubusoft.xenon.opengl.ArgonGLDefaultRenderer;
import com.abubusoft.xenon.opengl.ArgonGLRenderer;
import com.abubusoft.xenon.core.Uncryptable;

import android.content.Context;

/**
 * View opengl
 * 
 * @author Francesco Benincasa
 * 
 */
@Uncryptable
public class ArgonView4OpenGL extends ArgonGLSurfaceView16 {
	
	protected Argon4OpenGL argon;

	public ArgonView4OpenGL(Context context) {
		super(context);
	}

	/**
	 * Avvia il contesto argon
	 */
	public void startArgonContext() {
		try {
			// crea l'applicazione
			argon = (Argon4OpenGL) ArgonBeanContext.getBean(ArgonBeanType.ARGON);
			//ApplicationManager.getInstance().attributes.get(ApplicationManagerAttributeKeys.MODE);
			argon.onViewCreated(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArgonGestureDetector gestureDetector;

	/**
	 * Consente di effettuare il build di un renderer
	 * 
	 * @return istanza di render da usare
	 */
	public ArgonGLRenderer createRenderer() {
		 return new ArgonGLDefaultRenderer(); 
	}

}
