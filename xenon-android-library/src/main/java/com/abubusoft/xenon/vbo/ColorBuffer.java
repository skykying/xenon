package com.abubusoft.xenon.vbo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.abubu.elio.logger.ElioLogger;

import android.opengl.GLES20;

import com.abubusoft.kripton.annotation.BindDisabled;
import com.abubusoft.kripton.annotation.BindType;
import com.abubusoft.kripton.annotation.BindXml;

/**
 * <dl>
 * <dt>ColorBuffer</dt>
 * <dd>contiene i colori in formato RGBA</dd>
 * <dt>Dimensione di un elemento</dt>
 * <dd>4</dd>
 * <dt>Tipo</dt>
 * <dd>FLOAT</dd>
 * </dl>
 * <p>
 * <a href="http://www.learnopengles.com/android-lesson-seven-an-introduction-to-vertex-buffer-objects-vbos/">android-lesson-seven-an-introduction-to-vertex-buffer-objects-vbos</a>
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
@BindType
public class ColorBuffer extends AbstractBuffer {
	private static final long serialVersionUID = 5085156311661451089L;

	public static final int COLOR_DIMENSIONS = 4;

	ColorBuffer(int vertexCountValue, BufferAllocationType allocationValue) {
		super(vertexCountValue, COLOR_DIMENSIONS, allocationValue);

		components = new float[capacity];

		build();
	}

	ColorBuffer() {

	}

	public static final int OFFSET_R = 0;

	public static final int OFFSET_G = 1;

	public static final int OFFSET_B = 2;

	public static final int OFFSET_A = 3;

	/**
	 * <p>
	 * coordinate che possono essere modificate direttamente.
	 * </p>
	 * 
	 * <p>
	 * Se il tipo di allocazione è di tipo {@link BufferAllocationType#STATIC} allora questo array punta, dopo il metodo {@link #update()}
	 * </p>
	 */
	@BindXml(elementTag = "v")
	public float[] components;

	/**
	 * coordinate che vengono usati nel caso in cui il buffer sia di tipo senza VertexBuffer
	 */
	@BindDisabled
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
		buffer.put(components, 0, size).position(0);
		// buffer.put(components).position(0);

		if (allocation != BufferAllocationType.CLIENT) {
			if (firstUpdate) {
				setupVBO();
				firstUpdate = false;

				// in caso di BufferAllocationType.STATIC non dobbiamo più fare niente, dato
				// che il buffer e l'array locale puntano alla stessa zona di memoria
			} else {
				if (allocation == BufferAllocationType.STATIC) {
					String msg = "Try to update STATIC buffer already updated";
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.abubu.argon.renderer.SharedData#update()
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
		components = null;
		buffer = null;
		// destroyDirectByteBuffer(buffer);

	}

	@Override
	void build() {
		// se l'allocazione è di tipo static, effettuiamo il wrap del buffer, in modo da avere solo un'allocazione
		if (allocation == BufferAllocationType.STATIC) {
			buffer = FloatBuffer.wrap(components);
		} else {
			buffer = ByteBuffer.allocateDirect(capacity * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		}
	}
}
