package com.abubusoft.xenon.vbo;

public class BufferAllocationOptions {

	/**
	 * <p>
	 * Una volta aggiornato il vbo con il metodo update, indica se rilasciare o meno la memoria client, ovvero quella che consente di aggiornare il vbo.
	 * </p>
	 */
	// public boolean releaseClientMemory;

	/**
	 * <p>
	 * tipo di index buffer
	 * </p>
	 */
	public BufferAllocationType indexAllocation;

	/**
	 * tipo di vertex buffer
	 */
	public BufferAllocationType vertexAllocation;

	/**
	 * tipo di texture buffer
	 */
	public BufferAllocationType textureAllocation;

	/**
	 * tipo di color buffer
	 */
	public BufferAllocationType colorAllocation;

	/**
	 * allocazione per le normali
	 */
	public BufferAllocationType normalAllocation;

	/**
	 * allocazione per gli attributi
	 */
	public BufferAllocationType attributeAllocation;

	/**
	 * <p>
	 * Configurazione base:
	 * </p>
	 * <ul>
	 * <li>BufferAllocationType = CLIENT</li>
	 * <li>releaseClientMemory = false</li>
	 * <li>updateAfterCreation = true</li>
	 * </ul>
	 * 
	 * @return this
	 */
	public static BufferAllocationOptions build() {
		BufferAllocationOptions value = new BufferAllocationOptions();
		value.allocation(BufferAllocationType.CLIENT);
		return value;
	}

	/**
	 * <p>
	 * Imposta in una volta sola tutti e tre tipi di buffer
	 * </p>
	 * 
	 * @param value
	 * @return this
	 */
	public BufferAllocationOptions allocation(BufferAllocationType value) {
		textureAllocation = value;
		vertexAllocation = value;
		indexAllocation = value;
		colorAllocation = value;
		normalAllocation = value;
		attributeAllocation = value;

		return this;
	}

	/*
	 * public BufferAllocationOptions releaseClientMemory(boolean value) { releaseClientMemory = value;
	 * 
	 * return this; }
	 */

	public BufferAllocationOptions indexAllocation(BufferAllocationType value) {
		indexAllocation = value;
		return this;
	}

	public BufferAllocationOptions vertexAllocation(BufferAllocationType value) {
		vertexAllocation = value;
		return this;
	}

	public BufferAllocationOptions colorAllocation(BufferAllocationType value) {
		colorAllocation = value;
		return this;
	}

	public BufferAllocationOptions textureAllocation(BufferAllocationType value) {
		textureAllocation = value;
		return this;
	}

	public BufferAllocationOptions normalAllocation(BufferAllocationType value) {
		textureAllocation = value;
		return this;
	}

	public BufferAllocationOptions attributeAllocation(BufferAllocationType value) {
		textureAllocation = value;
		return this;
	}

}
