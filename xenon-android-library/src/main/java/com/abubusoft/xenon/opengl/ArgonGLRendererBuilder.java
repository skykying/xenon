/**
 * 
 */
package com.abubusoft.xenon.opengl;

import com.abubusoft.xenon.opengl.ArgonGLRenderer;

/**
 * @author xcesco
 *
 */
public interface ArgonGLRendererBuilder {

	/**
	 * permette di creare un renderer
	 * 
	 * @return
	 *         instanza di renderer
	 */
	public ArgonGLRenderer createRenderer();
}
