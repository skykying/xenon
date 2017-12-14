package com.abubusoft.xenon.game.ui;

import com.abubusoft.xenon.Camera;
import com.abubusoft.xenon.math.ArgonMath;
import com.abubusoft.xenon.math.Matrix4x4;
import com.abubusoft.xenon.math.Point3;
import com.abubusoft.xenon.texture.Texture;

import android.util.SparseArray;

/**
 * <p>
 * Un layer che rimane agganciato alla camera
 * 
 * @author Francesco Benincasa
 * 
 */
public class UILayer extends UIViewContainer implements UIViewLayer {

	public boolean visible;
	
	private Matrix4x4 matrixModelview;

	private Matrix4x4 matrixModelviewLocal;

	private float distanceFromViewer;
	
	private UIDrawerBatcher uiDrawerBatcher;
	

	/**
	 * <p>
	 * </p>
	 * 
	 * @param windowDistanceFromViewerValue
	 */
	public UILayer(int widthValue, int heightValue, float windowDistanceFromViewerValue) {
		this.witdh=widthValue;
		this.height=heightValue;
		
		views = new SparseArray<UIViewComponent>();		
		matrixModelview = new Matrix4x4();
		matrixModelviewLocal = new Matrix4x4();
		distanceFromViewer = windowDistanceFromViewerValue;

		visible = true;

		uiDrawerBatcher = new UIDrawerBatcher();		
	}

	/**
	 * Se true indica che qualocsa è stato toccato.
	 * 
	 * @param camera
	 * @param screenX
	 * @param screenY
	 * @return
	 */
	public boolean checkForClick(float screenX, float screenY) {
		Point3 outputPoint = new Point3();
		float[] inputPointsInPlane = new float[16];
		ArgonMath.convertViewToWorldST(outputPoint, inputPointsInPlane, screenX, screenY, distanceFromViewer);

		//ElioLogger.info(">>>> %s %s", outputPoint.x, outputPoint.y);

		int n = views.size();
		UIViewComponent view;
		boolean result = false;
		for (int i = 0; i < n; i++) {
			view = views.valueAt(i);
			//ElioLogger.info(">>>> Boundingbox %s, %s - %s, %s", view.boundingBox.left, view.boundingBox.top, view.boundingBox.right, view.boundingBox.bottom);
			if (view.contains(outputPoint.x, outputPoint.y)) {
				result = true;
				if (view.fireClickEvent()) {
					return true;
				}
			}
		}

		return result;
	}

	/**
	 * <p>
	 * Aggiorna la billboard.
	 * </p>
	 * 
	 * @param enlapsedTime
	 * @param speedAdapter
	 */
	public void update(long enlapsedTime, float speedAdapter) {
		UIViewComponent view;

		int n = views.size();
		for (int i = 0; i < n; i++) {
			view = views.valueAt(i);

			view.update(enlapsedTime, speedAdapter);
		}
	}

	/**
	 * <p>
	 * Resetta la billboard.
	 * </p>
	 */
	public void resetUdate() {
		UIViewComponent view;

		int n = views.size();
		for (int i = 0; i < n; i++) {
			view = views.valueAt(i);

			view.resetUdate();
		}
	}

	@Override
	public void draw(Camera camera) {
		if (!visible)
			return;

		matrixModelview.multiply(camera.info.projectionMatrix, camera.info.cameraMatrix);
		matrixModelview.translate(0, 0, -distanceFromViewer);

		uiDrawerBatcher.begin();

		UIViewComponent view;

		int n = views.size();
		for (int i = 0; i < n; i++) {
			view = views.valueAt(i);
			if (!view.visible)
				continue;
			matrixModelviewLocal.build(matrixModelview);
			matrixModelviewLocal.translate(view.position.x, view.position.y, view.position.z);

			switch (view.type) {
			case TEXT:
				uiDrawerBatcher.drawText(this, (UITextView) view, matrixModelviewLocal);
				break;
			case IMAGE:
				uiDrawerBatcher.drawImage(this, (UIImageView) view, matrixModelviewLocal);
				break;
			case IMAGE_ANIMATION:
				uiDrawerBatcher.drawImageWithAnimation(this, (UIImageView) view, matrixModelviewLocal);
				break;
			case PANEL:
				uiDrawerBatcher.drawPanel(this, (UIPanelView) view, matrixModelviewLocal);
				break;
			}
		}

		uiDrawerBatcher.end();
	}

	

	/**
	 * <p>
	 * </p>
	 * 
	 * @param introText
	 * @return
	 */
	public UITextView createTextView(String text) {
		UITextView view = new UITextView(text);

		
		addChild(view);
		
		return view;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param uid
	 * @return
	 */
	public UITextView getTextView(int uid) {
		return (UITextView) views.get(uid);
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	public UITextView createTextView() {
		return createTextView("");
		
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	public UIImageView createImageView() {
		UIImageView view = new UIImageView();
		
		addChild(view);
		return view;
	}
	
	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	public UIPanelView createPanelView(int w, int h, Texture texture) {
		UIPanelView view = new UIPanelView(w, h, texture);

		addChild(view);
		
		//view.setImage(texture);
		
		return view;
	}

	@Override
	protected void prepareToDraw(float windowWitdh, float windowHeight) {
		// TODO Auto-generated method stub
		
	}


}
