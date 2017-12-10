package com.abubusoft.xenon.vbo;

import java.io.Serializable;

import com.abubusoft.xenon.engine.SharedData;
import com.abubusoft.xenon.mesh.QuadMesh;

import com.abubusoft.kripton.annotation.Bind;
import com.abubusoft.kripton.annotation.BindDisabled;

public abstract class AbstractBuffer implements SharedData, Serializable {

	private static final long serialVersionUID = 8424163604930751532L;

	AbstractBuffer(int vertexCountValue, int vertexDimensionValue, BufferAllocationType allocationValue) {
		allocation = allocationValue;

		vertexCount = vertexCountValue;
		vertexDimension = vertexDimensionValue;
		capacity = vertexCount * vertexDimension;

		firstUpdate = true;
	}
	
	AbstractBuffer()
	{
		firstUpdate = true;
	}

	/**
	 * dimensione di un float espressa in byte
	 */
	public static final int BYTES_PER_FLOAT = 4;

	/**
	 * <p>
	 * Per comodità lo mettiamo anche qua. Numero di vertici in un quad (4)
	 * </p>
	 */
	public static final int VERTEX_IN_QUAD_TILE = QuadMesh.VERTEX_IN_INDEXED_QUAD;

	/**
	 * <p>
	 * Indica se è stato già fatto il primo update.
	 * </p>
	 * 
	 * <p>
	 * Se <code>true</code> indica se deve essere fatto il prima update.
	 * </p>
	 */
	@BindDisabled
	protected transient boolean firstUpdate;

	/**
	 * numero di vertici
	 */
	@Bind
	public int vertexCount;

	/**
	 * dimensioni di un vertice
	 */
	@Bind
	public int vertexDimension;
	
	/**
	 * dimensioni di un vertice
	 */
	public int vertexDimension() { return vertexDimension; }

	/**
	 * capacità del buffer, in termini di vertici * dimensioni
	 */
	@Bind
	public int capacity;

	/**
	 * 
	 */
	@Bind
	public int cursor;

	/**
	 * <p>
	 * Imposta al primo tile
	 */
	public void cursorReset() {
		cursor = 0;
	}

	/**
	 * Indica se il buffer è aggiornabile.
	 * 
	 * @return
	 * 		true se il buffer è aggiornabile
	 */
	public boolean isUpdatable() {
		if (allocation == BufferAllocationType.STATIC) {
			return firstUpdate;
		}

		return true;
	}

	/**
	 * <p>
	 * Sposta il cursore di n posizioni. Il nextVertex viene moltiplicato per le dimensioni dei vertici.
	 * </p>
	 */
	public void cursorMove(int nextVertex) {
		cursor += nextVertex * vertexDimension;
	}

	/**
	 * <p>
	 * Legge il cursore attuale. Si può usare direttamente l'attributo cursor.
	 * </p>
	 * 
	 * @return valore cursori
	 */
	public int cursorRead() {
		return cursor;
	}

	/**
	 * binding id del vbo. Non può essere final in quanto in caso di load può cambiare.
	 */
	public int bindingId = BINDING_ID_INVALID;

	/**
	 * <p>
	 * Binding hardware non valido
	 * </p>
	 */
	public static final int BINDING_ID_INVALID = 0;

	/**
	 * tipo di buffer. Messo qua da options per comodità
	 */
	@Bind	
	public BufferAllocationType allocation;
	
	/**
	 * costruisce in base ai parametri già definiti nel buffer il buffer interno.
	 */
	abstract void build();

	public abstract void reload();

	public abstract void unbind();

	/**
	 * DirectByteBuffers are garbage collected by using a phantom reference and a reference queue. Every once a while, the JVM checks the reference queue and cleans the DirectByteBuffers. However, as this doesn't happen immediately after
	 * discarding all references to a DirectByteBuffer, it's easy to OutOfMemoryError yourself using DirectByteBuffers. This function explicitly calls the Cleaner method of a DirectByteBuffer.
	 * 
	 * @param <E>
	 * 
	 * @param toBeDestroyed
	 *            The DirectByteBuffer that will be "cleaned". Utilizes reflection.
	 * 
	 */
	/*protected static <E extends Buffer> void destroyDirectByteBuffer(E toBeDestroyed) {

		Preconditions.checkArgument(toBeDestroyed.isDirect(), "toBeDestroyed isn't direct!");

		Method cleanerMethod;
		try {
			cleanerMethod = toBeDestroyed.getClass().getMethod("cleaner");
			cleanerMethod.setAccessible(true);
			Object cleaner = cleanerMethod.invoke(toBeDestroyed);
			Method cleanMethod = cleaner.getClass().getMethod("clean");
			cleanMethod.setAccessible(true);
			cleanMethod.invoke(cleaner);
			ElioLogger.info("destroyDirectByteBuffer success!");
		} catch (Exception e) {
			ElioLogger.info("destroyDirectByteBuffer error "+e.getMessage());
			e.printStackTrace();
		}

	}*/

}
