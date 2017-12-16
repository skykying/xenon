package com.abubusoft.xenon.tiletest01;

import android.graphics.Color;
import android.opengl.GLES20;

import com.abubusoft.xenon.XenonApplication4OpenGLImpl;
import com.abubusoft.xenon.XenonBean;
import com.abubusoft.xenon.android.XenonLogger;
import com.abubusoft.xenon.animations.TextureAnimationManager;
import com.abubusoft.xenon.engine.Phase;
import com.abubusoft.xenon.engine.TouchEventListener;
import com.abubusoft.xenon.engine.TouchManager;
import com.abubusoft.xenon.engine.TouchType;
import com.abubusoft.xenon.math.Matrix4x4;
import com.abubusoft.xenon.math.Point2;
import com.abubusoft.xenon.mesh.Mesh;
import com.abubusoft.xenon.mesh.MeshFactory;
import com.abubusoft.xenon.mesh.tiledmaps.ObjDefinition;
import com.abubusoft.xenon.mesh.tiledmaps.ObjectLayer;
import com.abubusoft.xenon.mesh.tiledmaps.ObjectLayerDrawer;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMapFactory;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMapFillScreenType;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMapOptions;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMapPositionType;
import com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers.MapController;
import com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers.ObjActionType;
import com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers.ObjModelController;
import com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers.ObjModelControllerFactory;
import com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers.ObjModelControllerOptions;
import com.abubusoft.xenon.mesh.tiledmaps.orthogonal.OrthogonalHelper;
import com.abubusoft.xenon.shader.drawers.LineDrawer;
import com.abubusoft.xenon.shader.drawers.SpriteDrawerBatcher;
import com.abubusoft.xenon.shader.drawers.TiledMapShaderDrawer;
import com.abubusoft.xenon.texture.TextureFilterType;
import com.abubusoft.xenon.texture.TextureOptions;

import static com.abubusoft.xenon.context.XenonBeanContext.getContext;

/**
 * 
 * @author Francesco Benincasa
 * 
 */
@XenonBean
public class TileTest01Application extends XenonApplication4OpenGLImpl implements ObjectLayerDrawer, TouchEventListener {

	SpriteDrawerBatcher batch;
	
	@Override
	public void onStartup() {

	}

	private Mesh lines;

	// public ObjActionSequence path;

	private LineDrawer linesDrawer;

	private TiledMap map;

	// private Matrix4x4 matrixModelViewProjection;
	// private Matrix4x4 matrixModelview;
	public MapController mapController;

	Matrix4x4 matrixObject;

	ObjModelController objPlayerController;

	@Override
	public void onFrameDraw(Phase phase, long enlapsedTime, float speedAdapter) {
		TiledMapShaderDrawer.draw(map, enlapsedTime);
	}

	@Override
	public void onFramePrepare(Phase phase, long enlapsedTime, float speedAdapter) {
		objPlayerController.update(enlapsedTime);

		if (objPlayerController.isSequenceRunning()) {
			mapController.position(objPlayerController.obj.x, objPlayerController.obj.y, TiledMapPositionType.MIDDLE_CENTER);
		} else if (objPlayerController.isSequenceFinished()) {
			objPlayerController.clearActions();
		}

		// ElioLogger.info("onFramePrepare");

	}

	@Override
	public void onObjectLayerFrameDraw(TiledMap tiledMap, ObjectLayer layer, long enlapsedTime, Matrix4x4 modelview) {
		ObjDefinition obj;
		Point2 p;

		batch.begin();

		for (int i = 0; i < layer.objects.size(); i++) {
			obj = layer.objects.get(i);
			p = OrthogonalHelper.translateInScreenCoords(map, obj);

			matrixObject.build(modelview);

			// TODO come per tiledMap.positionInMap.x faccio il round o no?
			matrixObject.translate(Math.round(p.x), Math.round(p.y), 0f);
			// matrixObject.translate(p.x, p.y, 0f);

			batch.draw(objPlayerController.sprite, objPlayerController.timeline, matrixObject);
		}

		batch.end();

		linesDrawer.begin();
		matrixObject.build(modelview);
		p = OrthogonalHelper.translateInScreenCoords(map, objPlayerController.obj);
		matrixObject.translate(Math.round(p.x), Math.round(p.y), 0f);
		linesDrawer.setColor(Color.WHITE);
		linesDrawer.setLineWidth(1);
		// linesDrawer.draw(lines, matrixObject);
		linesDrawer.end();

	}

	@Override
	public void onSceneCreate(boolean firstSceneCreation, boolean preferencesIsChanged, boolean screenIsChanged) {
		TextureAnimationManager.instance().createTextureAnimationAtlasFromAssets(getContext(), "sprite_kino", TextureOptions.build());
		TextureAnimationManager.instance().createTextureAnimationAtlasFromAssets(getContext(), "player1", TextureOptions.build());

		matrixObject = new Matrix4x4();

		linesDrawer = new LineDrawer();

		map = TiledMapFactory.loadFromAsset(getContext(), "prova01.tmx", TextureFilterType.LINEAR);
		mapController = map.buildView(camera, TiledMapOptions.build().visibileTiles(12).fillScreenType(TiledMapFillScreenType.FILL_CUSTOM_HEIGHT)
				.startPosition(TiledMapPositionType.LEFT_TOP).scrollHorizontalLocked(true).scrollVerticalLocked(true));
		map.attachObjectLayerDrawer("objects", this);

		objPlayerController = ObjModelControllerFactory.create(getContext(), "player", "kino", map,
				ObjModelControllerOptions.build().status(ObjActionType.STAY_DOWN));
		objPlayerController.action(ObjActionType.STAY_DOWN);

		lines = MeshFactory.createWireframe(objPlayerController.sprite);

		batch = new SpriteDrawerBatcher();

		TouchManager.instance().setListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.abubu.argon.ArgonApplication4OpenGL#onSceneReady(android.content.
	 * SharedPreferences, boolean, boolean, boolean)
	 */
	@Override
	public void onSceneReady(boolean firstSceneCreation, boolean preferencesIsChanged, boolean screenIsChanged) {
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.abubu.argon.engine.TouchEventListener#onTouch(org.abubu.argon.engine
	 * .TouchType, float, float)
	 */
	@Override
	public void onTouch(TouchType type, float x, float y) {
		XenonLogger.info("onTouch %s - THREAD %s", type, Thread.currentThread().getName());
		switch (type) {
		case SCROLL:
			mapController.scrollFromScreen(x, y);
			break;
		case DOUBLE_TAP:
			Point2 point = mapController.touch(x, y);
			objPlayerController.clearActions();
			objPlayerController.navigateTo(point.x, point.y);

			break;
		}

	}

}
