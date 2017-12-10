package com.abubusoft.xenon.vbo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.abubu.elio.logger.ElioLogger;

import android.opengl.GLES20;

import com.abubusoft.kripton.annotation.BindXml;

//TODO da usare.

/**
 * <p>
 * Buffer VBO per le texture cubiche.
 * </p>
 * 
 * <dl>
 * <dt>CubeTexture</dt>
 * <dd>contiene le coordinate per le texture cubiche</dd>
 * <dt>Dimensione di un elemento</dt>
 * <dd>3</dd>
 * <dt>Tipo</dt>
 * <dd>FLOAT</dd>
 * </dl>
 * 
 * @author Francesco Benincasa
 * 
 */
public class CubeTextureBuffer extends AbstractBuffer {
	private static final long serialVersionUID = -7293463267603536972L;

	public static final int CUBE_TEXTURE_DIMENSIONS = 3;

	CubeTextureBuffer(int vertexCountValue, BufferAllocationType allocationValue) {
		super(vertexCountValue, CUBE_TEXTURE_DIMENSIONS, allocationValue);

		coords = new float[capacity];

	}

	CubeTextureBuffer() {

	}

	/**
	 * Le coordinate nello spazio delle texture: s t p
	 */
	public static final int OFFSET_S = 0;

	/**
	 * Le coordinate nello spazio delle texture: s t p
	 */
	public static final int OFFSET_T = 1;

	/**
	 * Le coordinate nello spazio delle texture: s t p
	 */
	public static final int OFFSET_P = 2;

	/**
	 * coordinate che possono essere modificate direttamente
	 */
	@BindXml(elementTag = "v")
	public float[] coords;

	/**
	 * coordinate che vengono usati nel caso in cui il buffer sia di tipo senza VertexBuffer
	 */
	public FloatBuffer buffer;

	/**
	 * <p>
	 * Aggiorna il float buffer ed eventualmente il vbo in video memory.
	 * </p>
	 * 
	 * <p>
	 * Se il vbo non può essere aggiornato nuovamente, allora viene sollevata un'eccezione.
	 * </p>
	 */
	public void update(int size) {
		// passiamo da client java a client nativo
		buffer.put(coords, 0, size).position(0);
		// buffer.put(components).position(0);

		if (allocation != BufferAllocationType.CLIENT) {
			if (firstUpdate) {
				setupVBO();
				firstUpdate = false;

				// in caso di BufferAllocationType.STATIC non dobbiamo più fare niente, dato
				// che il buffer e l'array locale puntano alla stessa zona di memoria
			} else {
				if (allocation == BufferAllocationType.STATIC) {
					String msg = "Try to allocation TextureBuffer STATIC";
					ElioLogger.fatal(msg);
					throw new RuntimeException(msg);
				}

				// Bind to the buffer. Future commands will affect this buffer specifically.
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bindingId);

				// Transfer data from client memory to the buffer.
				// We can release the client memory after this call.
				GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, buffer.capacity() * BYTES_PER_FLOAT, buffer);

				// IMPORTANT: Unbind from the buffer when we're done with it.
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			}
		}
	}

	private void setupVBO() {
		// Bind to the buffer. Future commands will affect this buffer specifically.
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bindingId);

		// Transfer data from client memory to the buffer.
		// We can release the client memory after this call.
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, buffer.capacity() * BYTES_PER_FLOAT, buffer, allocation.value);

		// IMPORTANT: Unbind from the buffer when we're done with it.
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}

	/**
	 * <p>
	 * Se il tipo di allocazione è di tipo {@link BufferAllocationType#STATIC} allora questo array punta, dopo il metodo {@link #update()}
	 * </p>
	 */
	public void update() {
		update(capacity);
	}

	/**
	 * Se è un vbo, ricarichiamo i valori
	 */
	public void reload() {
		if (allocation != BufferAllocationType.CLIENT) {
			setupVBO();
		}
	}

	@Override
	public void unbind() {
		coords = null;
		buffer = null;
		// destroyDirectByteBuffer(buffer);

	}

	@Override
	void build() {
		// se l'allocazione è di tipo static, effettuiamo il wrap del buffer, in modo da avere solo un'allocazione
		if (allocation == BufferAllocationType.STATIC) {
			buffer = FloatBuffer.wrap(coords);
		} else {
			buffer = ByteBuffer.allocateDirect(capacity * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		}

	}

}
