/**
 * 
 */
package com.abubusoft.xenon.vbo;

import android.opengl.GLES20;

/**
 * @author xcesco
 *
 */
public abstract class BufferHelper {
	/**
	 * <p>
	 * Genera un singolo texture id e lo restituisce.
	 * </p>
	 * 
	 * @return
	 */
	static int newBindingId() {
		int[] resourceId = new int[1];
		GLES20.glGenBuffers(resourceId.length, resourceId, 0);

		return resourceId[0];
	}
	
	/**
	 * <p>
	 * Effettua il bind (ottiene un bindingId valido per il contesto OpenGL) e registra il buffer.
	 * </p>
	 * @param <E>
	 * 
	 * @param buffer
	 */
	public static <E extends AbstractBuffer> void bindBuffer(E buffer) {
		if (buffer.allocation == BufferAllocationType.CLIENT) {
			buffer.bindingId = VertexBuffer.BINDING_ID_INVALID;
		} else {
			buffer.bindingId = BufferHelper.newBindingId();
			BufferManager.instance().vboToUnbind++;
		}

		BufferManager.instance().vbos.add(buffer);
	}
	
	/**
	 * <p>
	 * Effettua il bind (ottiene un bindingId valido per il contesto OpenGL) e registra il buffer.
	 * </p>
	 * @param <E>
	 * 
	 * @param buffer
	 */
	public static <E extends AbstractBuffer> void buildBuffer(E buffer) {
		buffer.build();
	}
	
	
}
