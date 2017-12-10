package com.abubusoft.xenon.vbo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import org.abubu.elio.logger.ElioLogger;

import android.opengl.GLES20;

import com.abubusoft.kripton.annotation.BindDisabled;
import com.abubusoft.kripton.annotation.BindType;
import com.abubusoft.kripton.annotation.BindXml;

/**
 * <p>
 * Indici per i triangoli che compongono gli shape.
 * </p>
 * <dl>
 * <dt>IndexBuffer</dt>
 * <dd>contiene gli indici dei triangoli degli shape</dd>
 * <dt>Dimensione di un elemento</dt>
 * <dd>1</dd>
 * <dt>Tipo</dt>
 * <dd>SHORT</dd>
 * </dl>
 * http://www.learnopengles.com/android-lesson-seven-an-introduction-to-vertex-buffer-objects-vbos/
 * 
 * @author Francesco Benincasa
 * 
 */
@BindType
public class IndexBuffer extends AbstractBuffer {

	private static final long serialVersionUID = -5924109187631222447L;

	IndexBuffer(int vertexCountValue, BufferAllocationType allocation) {
		super(vertexCountValue, 1, allocation);

		values = new short[capacity];

		build();
	}

	IndexBuffer() {

	}

	/**
	 * indici che possono essere modificati direttamente
	 */
	@BindXml(elementTag = "i")
	public short[] values;

	/**
	 * coordinate che vengono usati nel caso in cui il buffer sia di tipo senza VertexBuffer
	 */
	@BindDisabled
	public ShortBuffer buffer;

	/**
	 * <p>
	 * Byte che compongono uno short
	 * </p>
	 */
	private static final int BYTES_PER_SHORT = 2;

	/**
	 * <p>
	 * </p>
	 */
	public static final int INDEX_IN_QUAD_TILE = 6;

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
		buffer.put(values, 0, size).position(0);

		if (allocation != BufferAllocationType.CLIENT) {
			if (firstUpdate) {
				setupIVBO();
				firstUpdate = false;

				// in caso di BufferAllocationType.STATIC non dobbiamo più fare niente, dato
				// che il buffer e l'array locale puntano alla stessa zona di memoria
			} else {
				if (allocation == BufferAllocationType.STATIC) {
					String msg = "Try to modify IndexBuffer STATIC";
					ElioLogger.fatal(msg);
					throw new RuntimeException(msg);
				}

				// Bind to the buffer. Future commands will affect this buffer specifically.
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bindingId);

				// Transfer data from client memory to the buffer.
				// We can release the client memory after this call.
				GLES20.glBufferSubData(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0, buffer.capacity() * BYTES_PER_SHORT, buffer);

				// IMPORTANT: Unbind from the buffer when we're done with it.
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, BINDING_ID_INVALID);
			}
		}
	}

	private void setupIVBO() {
		// Bind to the buffer. Future commands will affect this buffer specifically.
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bindingId);

		// Transfer data from client memory to the buffer.
		// We can release the client memory after this call.
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffer.capacity() * BYTES_PER_SHORT, buffer, allocation.value);

		// IMPORTANT: Unbind from the buffer when we're done with it.
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	/**
	 * <p>
	 * Se il tipo di allocazione è di tipo {@link BufferAllocationType#STATIC} allora questo array punta, dopo il metodo {@link #update()}
	 * </p>
	 **/
	public void update() {
		update(capacity);
	}

	/**
	 * Se è un vbo, ricarichiamo i valori
	 */
	public void reload() {
		if (allocation != BufferAllocationType.CLIENT) {
			setupIVBO();
		}
	}

	@Override
	public void unbind() {
		values = null;
		buffer = null;
		// destroyDirectByteBuffer(buffer);

	}

	@Override
	void build() {
		// se l'allocazione è di tipo static, effettuiamo il wrap del buffer, in modo da avere solo un'allocazione
		if (allocation == BufferAllocationType.STATIC) {
			buffer = ShortBuffer.wrap(values);
		} else {
			buffer = ByteBuffer.allocateDirect(capacity * BYTES_PER_SHORT).order(ByteOrder.nativeOrder()).asShortBuffer();
		}

	}
}
