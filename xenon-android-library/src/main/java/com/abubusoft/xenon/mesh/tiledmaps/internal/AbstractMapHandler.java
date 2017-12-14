/**
 * 
 */
package com.abubusoft.xenon.mesh.tiledmaps.internal;

import com.abubusoft.xenon.ArgonApplication4OpenGL;
import com.abubusoft.xenon.math.Matrix4x4;
import com.abubusoft.xenon.mesh.tiledmaps.Layer;
import com.abubusoft.xenon.mesh.tiledmaps.ObjDefinition;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap;
import com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers.AbstractMapController;
import com.abubusoft.xenon.shader.ShaderTiledMap;

/**
 * Gestore base per un gestore di tiledmap
 * 
 * @author xcesco
 * @param <E>
 *
 */
public abstract class AbstractMapHandler<E extends AbstractMapController> implements MapHandler {

	protected LayerOffsetHolder layerOffsetHolder;

	protected TiledMap map;

	/**
	 * controller con il cast alla classe concreta. Serve ad evitare ad invocazione di dover effettuare il cast.
	 */
	protected E controller;

	public AbstractMapHandler(TiledMap map) {
		this.map = map;
		layerOffsetHolder = new LayerOffsetHolder();		
	}

	/**
	 * <p>
	 * Calcola la mappa e la disegna. Tra le varie cose aggiorna anche il frame marker
	 * </p>
	 * 
	 * <p>
	 * Ricordarsi di abilitare il blend prima di questo metodo (tipicamente nel {@link ArgonApplication4OpenGL#onSceneReady(android.content.SharedPreferences, boolean, boolean, boolean)})
	 * </p>
	 * 
	 * <pre>
	 * GLES20.glEnable(GLES20.GL_BLEND);
	 * GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	 * </pre>
	 * 
	 * 
	 * @param deltaTime
	 *            tempo trascorso dall'ultimo draw
	 * @param modelViewProjection
	 *            matrice MVP
	 */
	@Override
	public void draw(long deltaTime, Matrix4x4 modelViewProjection) {
		// aggiorniamo il frame marker per gli oggetti
		ObjDefinition.updateGlobalFrameMarker();

		ShaderTiledMap shader = map.shader;
		Layer item;
		// offset
		int n;

		shader.use();

		map.updateTime(deltaTime);
		// le texture vengono impostate all'inizio
		int nTexture = map.texturesCount;
		int nLayer = map.layers.size();

		if (nTexture == 1) {
			// impostiamo la texture una volta sola, ce n'è solo una
			shader.setTexture(0, map.layers.get(0).textureList.get(0));

			// versione ottimizzata
			for (int i = 0; i < nLayer; i++) {
				item = map.layers.get(i);

				// http://stackoverflow.com/questions/1295424/how-to-convert-float-to-int-with-java
				convertMap2ViewLayer(layerOffsetHolder, Math.round(item.layerOffsetX), Math.round(item.layerOffsetY));

				if (item.isDrawable()) {
					item.drawer().drawLayer(shader, deltaTime, layerOffsetHolder.tileIndexX - 1, layerOffsetHolder.tileIndexY - 1, layerOffsetHolder.offsetX, layerOffsetHolder.offsetY, modelViewProjection);
				}
			}
		} else {
			// versione normale
			for (int i = 0; i < nLayer; i++) {
				item = map.layers.get(i);

				// http://stackoverflow.com/questions/1295424/how-to-convert-float-to-int-with-java
				/*
				 * currentMapOffsetX = Math.round(item.screenOffsetX); currentMapOffsetY = Math.round(item.screenOffsetY);
				 * 
				 * offsetX = currentMapOffsetX % map.tileWidth; offsetY = currentMapOffsetY % map.tileWidth;
				 */
				convertMap2ViewLayer(layerOffsetHolder, Math.round(item.layerOffsetX), Math.round(item.layerOffsetY));

				if (item.isDrawable()) {
					n = item.textureList.size();

					// imposta le texture
					for (int j = 0; j < n; j++) {
						shader.setTexture(j, item.textureList.get(j));
					}

					item.drawer().drawLayer(shader, deltaTime, layerOffsetHolder.tileIndexX - 1, layerOffsetHolder.tileIndexY - 1, layerOffsetHolder.offsetX, layerOffsetHolder.offsetY, modelViewProjection);
				}
			}
		}

	}

	/**
	 * <p>
	 * Disegna la mappa in base al controller.
	 * </p>
	 * 
	 * <p>
	 * Ricordarsi di abilitare il blend prima di questo metodo (tipicamente nel {@link ArgonApplication4OpenGL#onSceneReady(android.content.SharedPreferences, boolean, boolean, boolean)})
	 * </p>
	 * 
	 * <pre>
	 * GLES20.glEnable(GLES20.GL_BLEND);
	 * GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	 * </pre>
	 * 
	 * @param deltaTime
	 */
	@Override
	public void draw(long deltaTime) {
		draw(deltaTime, map.controller.getMatrixModelViewProjection());
	}
}