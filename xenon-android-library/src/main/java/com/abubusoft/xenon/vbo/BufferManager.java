package com.abubusoft.xenon.vbo;

import java.util.ArrayList;

import com.abubusoft.xenon.opengl.ArgonGL;
import com.abubusoft.xenon.vbo.AttributeBuffer.AttributeDimensionType;
import com.abubusoft.xenon.core.logger.ElioLogger;

import android.opengl.GLES20;

public class BufferManager {
	private final static BufferManager instance = new BufferManager();

	public static final BufferManager instance() {
		return instance;
	}

	/**
	 * numero di vbo allocati in video memory
	 */
	int vboToUnbind;

	ArrayList<AbstractBuffer> vbos;

	private BufferManager() {
		vbos = new ArrayList<>();
		vboToUnbind = 0;
	}

	/**
	 * <p>
	 * Crea un vbo per i colori. Effettua il build ed il bind al contesto openGL del buffer.
	 * </p>
	 * 
	 */
	public ColorBuffer createColorBuffer(int vertexCountValue, BufferAllocationType allocationValue) {
		ColorBuffer item = new ColorBuffer(vertexCountValue, allocationValue);
		BufferHelper.bindBuffer(item);

		return item;
	}

	/**
	 * <p>
	 * Crea un vbo. Effettua il build ed il bind al contesto openGL del buffer.
	 * </p>
	 * 
	 * @param vertexCountValue
	 * @param allocationValue
	 * @return vertexBuffer
	 */
	public VertexBuffer createVertexBuffer(int vertexCountValue, BufferAllocationType allocationValue) {
		VertexBuffer item = new VertexBuffer(vertexCountValue, allocationValue);
		BufferHelper.bindBuffer(item);

		return item;
	}

	/**
	 * <p>
	 * Crea un attribute vbo di tipo float e di dimensioni parametriche. Effettua il build ed il bind al contesto openGL del buffer.
	 * </p>
	 * 
	 */
	public AttributeBuffer createAttributeBuffer(int vertexCountValue, AttributeDimensionType vertexDimensions, BufferAllocationType allocationValue) {
		AttributeBuffer item = new AttributeBuffer(vertexCountValue, vertexDimensions, allocationValue);
		BufferHelper.bindBuffer(item);

		return item;
	}

	/**
	 * <p>
	 * Crea un texture vbo. Effettua il build ed il bind al contesto openGL del buffer.
	 * </p>
	 * 
	 */
	public TextureBuffer createTextureBuffer(int vertexCountValue, BufferAllocationType allocationValue) {
		TextureBuffer item = new TextureBuffer(vertexCountValue, allocationValue);
		BufferHelper.bindBuffer(item);

		return item;
	}

	/**
	 * <p>
	 * Crea un vbo. Effettua il build ed il bind al contesto openGL del buffer.
	 * </p>
	 * 
	 */
	public IndexBuffer createIndexBuffer(int vertexCountValue, BufferAllocationType allocationValue) {
		IndexBuffer item = new IndexBuffer(vertexCountValue, allocationValue);
		BufferHelper.bindBuffer(item);

		return item;
	}

	public void clearBuffers() {

		if (vbos.size() > 0) {
			int c = 0;
			int n = vbos.size();
			int[] vbosIds = new int[vboToUnbind];
			AbstractBuffer current;

			// ricaviamo tutti i bindingId
			for (int i = 0; i < n; i++) {
				current = vbos.get(i);
				if (current.allocation != BufferAllocationType.CLIENT) {
					vbosIds[c] = current.bindingId;
					ElioLogger.debug("Mark as buffer to remove from GPU memory VBO-id " + current.bindingId);
					c++;
				}
			}

			ArgonGL.clearGlError();
			GLES20.glDeleteBuffers(vbosIds.length, vbosIds, 0);
			// GLES20.glFlush();

			ArgonGL.checkGlError("glDeleteBuffers");

			for (int i = 0; i < n; i++) {
				current = vbos.get(i);
				current.unbind();
			}
			vbos.clear();
			vboToUnbind = 0;

			ElioLogger.debug("Clear  " + n + " VBO (" + vbosIds.length + " have gpu memory) ");

		} else {
			ElioLogger.debug("Clear 0 VBO");
		}

		vbos.clear();

	}

	public void reloadVertexBuffers() {
		// puliamo i binder
		if (vbos.size() > 0) {
			int c = 0;
			int n = vbos.size();
			int[] vbosIds = new int[vboToUnbind];
			AbstractBuffer current;

			// ricaviamo tutti i bindingId
			for (int i = 0; i < n; i++) {
				current = vbos.get(i);
				if (current.allocation != BufferAllocationType.CLIENT) {
					vbosIds[c] = current.bindingId;
					ElioLogger.debug("Mark as buffer to remove from GPU memory VBO-id " + current.bindingId);
					c++;
				}
			}

			GLES20.glDeleteBuffers(vbosIds.length, vbosIds, 0);
			GLES20.glFlush();

			// cancelliamo le vecchie texture
			ElioLogger.debug("Unbinded " + n + " old vbos, without deleting them ");

			for (int i = 0; i < n; i++) {
				current = vbos.get(i);
				current.bindingId = BufferHelper.newBindingId();

				current.reload();

				ElioLogger.debug("Rebind vbo %s", i);
			}
		}

	}

	/*
	 * public void unbindVertexBuffers() { if (vbos.size() > 0) { int c = 0; int n = vbos.size(); int[] vbosIds = new int[vboToUnbind]; AbstractBuffer current;
	 * 
	 * // ricaviamo tutti i bindingId for (int i = 0; i < n; i++) { current = vbos.get(i); if (current.allocation != BufferAllocationType.CLIENT) { vbosIds[c] = current.bindingId;
	 * ElioLogger.debug("Mark as buffer to remove from GPU memory VBO-id "+current.bindingId); c++; } }
	 * 
	 * GLES20.glDeleteBuffers(vbosIds.length, vbosIds, 0); ArgonGL.checkGlError("glDeleteBuffers"); GLES20.glFlush();
	 * 
	 * for (int i=0;i<n;i++) { current = vbos.get(i); current.unbind(); } vbos.clear();
	 * 
	 * // cancelliamo le vecchie texture ElioLogger.debug("Unbinded " + n + " old vbos"); }
	 * 
	 * }
	 */
}
