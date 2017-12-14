package com.abubusoft.xenon.vbo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.abubusoft.kripton.android.Logger;

import android.opengl.GLES20;

import com.abubusoft.kripton.annotation.BindXml;

/**
 * <p>
 * Attributi dei vertici di uno shape
 * </p>
 * <dl>
 * <dt>AttributeBuffer</dt>
 * <dd>contiene le coordinate di un vertice in uno spazio tridimensinoale</dd>
 * <dt>Dimensione di un elemento</dt>
 * <dd>parametro</dd>
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
public class AttributeBuffer extends AbstractBuffer {

	private static final long serialVersionUID = 305300211006352642L;

	public enum AttributeDimensionType {
			DIM_1(1),
			DIM_2(2),
			DIM_3(3),
			DIM_4(4);

		public int value;

		AttributeDimensionType(int val) {
			value = val;
		}
	}

	AttributeBuffer(int vertexCountValue, AttributeDimensionType dimension, BufferAllocationType allocation) {
		super(vertexCountValue, dimension.value, allocation);

		coords = new float[capacity];

		build();
	}

	AttributeBuffer() {

	}

	public static final int OFFSET_X = 0;

	public static final int OFFSET_Y = 1;

	public static final int OFFSET_Z = 2;

	/**
	 * <p>
	 * Coordinate che possono essere modificate direttamente.
	 * </p>
	 * 
	 * <p>
	 * Se il tipo di allocazione è di tipo {@link BufferAllocationType#STATIC} allora questo array punta, dopo il metodo {@link #update()}
	 * </p>
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

				// possiamo annullare l'array. Non possiamo cancellare il buffer per questione di reload
				if (allocation == BufferAllocationType.STATIC) {
					coords = null;
					// facciamolo puntare alla zona di memoria del buffer
					coords = buffer.array();
				}

			} else {
				if (allocation == BufferAllocationType.STATIC) {
					String msg = "Try to allocation VertexBuffer STATIC";
					Logger.fatal(msg);
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
