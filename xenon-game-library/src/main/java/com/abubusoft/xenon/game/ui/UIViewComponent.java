package com.abubusoft.xenon.game.ui;

import com.abubusoft.xenon.android.XenonLogger;
import com.abubusoft.xenon.math.Point3;

import android.graphics.RectF;

/**
 * <p>
 * Un oggetto della user interface. Ogni oggetto definisce il suo width e height in vari modi.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public abstract class UIViewComponent {

	static int globaleCounter = 0;
	
	static int generateUid() {
		return globaleCounter++;
	}

	
	/**
	 * <p>
	 * Se true indica che la posizione che abbiamo ora è come quella di prima, quindi va già bene.
	 * </p>
	 */
	protected boolean valid;

	/**
	 * <p>
	 * Larghezza del boundingbox che definisce il component. Se non valorizzato è -1.
	 * </p>
	 */
	protected int witdh = -1;

	/**
	 * Axed Aligned Bounding Box che contiene il componente
	 */
	public final RectF boundingBox = new RectF();

	/**
	 * <p>
	 * Altezza del boundingbox che definisce il component. Se non valorizzato è -1.
	 * </p>
	 */
	protected int height = -1;

	public String name;

	/**
	 * <p>
	 * Larghezza del boundingbox che circoscrive il componente
	 * </p>
	 * 
	 * @return
	 */
	public int getWidth() {
		return witdh;
	}

	/**
	 * <p>
	 * Altezza del boundingbox che circoscrive il componente
	 * </p>
	 * 
	 * @return
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * <p>
	 * Imposta il listener sull'onClick
	 * </p>
	 * 
	 * @param value
	 */
	public void setListener(UIViewOnClickListener value) {
		listener = value;
	}

	/**
	 * <p>
	 * Scatena evento onClick. Se restituisce true vuol dire l'evento di click è stato consumato.
	 * </p>
	 *
	 * @return
	 */
	public boolean fireClickEvent() {
		XenonLogger.debug("Click event on component %s %s of type %s", uid, name, type);
		if (listener != null) {
			return listener.onClick(this);
		}

		return false;
	}

	/**
	 * <p>
	 * Listener sull'onClick.
	 * </p>
	 */
	protected UIViewOnClickListener listener;

	/**
	 * <p>
	 * Id all'interno del layer
	 * </p>
	 */
	public int uid;

	/**
	 * vecchia posizione
	 */
	// protected final Point3D oldPosition;

	/**
	 * Tipo di componente. Serve ad evitare l'uso dei cast per capire quale tipo di componete stiamo utilizzando.</p>
	 */
	public UIViewComponentType type;

	/**
	 * <p>
	 * Aggiorna la view
	 * </p>
	 * 
	 * @param enlapsedTime
	 *            tempo trascorso
	 * @param speedAdapter
	 *            moltiplicatore per le velocità espresse al secondo
	 */
	public abstract void update(long enlapsedTime, float speedAdapter);

	public abstract void resetUdate();

	/**
	 * <p>
	 * </p>
	 */
	public UIViewComponent() {
		valid = false;

		positionInScreen = DockLocation.build(DockHorizontal.LEFT, DockVertical.TOP);
		position = new Point3();
		visible = true;
		anchor = new DockLocation();
		// oldPosition = new Point3D();

		anchor.set(DockHorizontal.CENTER, DockVertical.CENTER);
		positionInScreen.set(DockHorizontal.CENTER, DockVertical.CENTER);

		type = UIViewComponentType.UNKNOWN;
	}

	public final DockLocation anchor;

	/**
	 * padding da applicare al punto di applicazione dell'ancora
	 */
	protected float padding;

	public void setPadding(float value) {
		padding = value;
		valid = false;
	}

	/**
	 * <p>
	 * Prepara la posizione del componente.
	 * </p>
	 * 
	 * @param windowWitdh
	 * @param windowHeight
	 * @param componentHeight
	 */
	protected void preparePosition(float windowWitdh, float windowHeight, float componentHeight) {

		// già fatto, quindi possiamo saltare
		if (valid)
			return;

		switch (positionInScreen.dockX) {
		case LEFT:
			position.x = -windowWitdh * 0.5f;
			break;
		case CENTER:
			position.x = 0;
			break;
		case RIGHT:
			position.x = windowWitdh * 0.5f;
			break;
		case ABSOLUTE:
			position.x = positionInScreen.valueX;
			break;
		}

		switch (positionInScreen.dockY) {
		case BOTTOM:
			position.y = -windowHeight * 0.5f;
			break;
		case CENTER:
			position.y = 0;
			break;
		case TOP:
			position.y = windowHeight * 0.5f;
			break;
		case ABSOLUTE:
			position.y = positionInScreen.valueY;
			break;
		}

	}

	protected abstract void prepareToDraw(float windowWitdh, float windowHeight);

	/**
	 * <p>
	 * Imposta contemporaneamente la posizione nello schermo e l'ancora a livello di testo.
	 * </p>
	 * 
	 * @param horizontalValue
	 * @param verticalValue
	 */
	public void setPosition(DockHorizontal horizontalValue, DockVertical verticalValue) {
		anchor.set(horizontalValue, verticalValue);
		positionInScreen.set(horizontalValue, verticalValue);

		valid = false;
	}

	/**
	 * aggiorna il boundingbox
	 */
	protected void updateBoundingBox() {

	}

	/**
	 * <p>
	 * </p>
	 */
	protected final DockLocation positionInScreen;

	/**
	 * <p>
	 * Posizione del testo
	 * </p>
	 */
	protected final Point3 position;

	/**
	 * <p>
	 * Indica se il componente è visibile o meno.
	 * </p>
	 */
	public boolean visible;

	public boolean contains(float x, float y) {
		return boundingBox.contains(x - position.x, y - position.y);
	}

}
