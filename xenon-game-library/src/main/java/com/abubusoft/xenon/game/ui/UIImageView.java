package com.abubusoft.xenon.game.ui;

import org.abubu.argon.animations.TextureAnimationHandler;
import org.abubu.argon.mesh.MeshFactory;
import org.abubu.argon.mesh.MeshOptions;
import org.abubu.argon.mesh.MeshSprite;
import org.abubu.argon.mesh.modifiers.VertexModifier;
import org.abubu.argon.texture.Texture;
import org.abubu.argon.vbo.BufferAllocationType;
import org.abubu.argon.vbo.BufferAllocationOptions;

/**
 * <p>
 * .
 * </p>
 * 
 * <p>
 * Definisce le dimensioni in modo esplicito
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class UIImageView extends UIViewComponent {

	/**
	 * <p>
	 * Catalogo delle immagini, definite per stato.
	 * </p>
	 */
	public UIImageViewStatus status;

	/**
	 * <p>
	 * .
	 * </p>
	 */
	public TextureAnimationHandler animationHandler;

	/**
	 * <p>
	 * Numero di copie dell'immagine.
	 * </p>
	 */
	int multiple;

	public void setMultiple(int value) {
		multiple = value;

		valid = false;
	}

	public void addMultiple(int value) {
		multiple += value;

		valid = false;
	}

	/**
	 * <p>
	 * .
	 * </p>
	 */
	public MeshSprite shape;

	/**
	 * <p>
	 * </p>
	 */
	public UIImageView() {
		super();
		BufferAllocationOptions bufferOptions = BufferAllocationOptions.build().indexAllocation(BufferAllocationType.STATIC).vertexAllocation(BufferAllocationType.DYNAMIC).textureAllocation(BufferAllocationType.STREAM);
		shape = MeshFactory.createSprite(1, 1, MeshOptions.build().bufferAllocationOptions(bufferOptions));

		type = UIViewComponentType.IMAGE;
		multiple = 1;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param witdhValue
	 * @param heightValue
	 */
	public void setDimension(int witdhValue, int heightValue) {
		witdh = witdhValue;
		height = heightValue;

		VertexModifier.setDimension(shape, witdhValue, heightValue, true);

		valid = false;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param imageValue
	 * @param witdhValue
	 * @param heightValue
	 */
	public void setImage(Texture imageValue, int witdhValue, int heightValue) {
		status = UIImageViewStatus.build().add(0, imageValue).activeStatus(0);
		animationHandler = null;
		type = UIViewComponentType.IMAGE;
		setDimension(witdhValue, heightValue);
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param imageValue
	 * @param witdhValue
	 * @param heightValue
	 */
	public void setImages(UIImageViewStatus statusValue, int witdhValue, int heightValue) {
		status = statusValue;
		animationHandler = null;
		type = UIViewComponentType.IMAGE;
		setDimension(witdhValue, heightValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.abubu.argon.ui.ArgonViewComponent#prepareToDraw(float, float)
	 */
	@Override
	protected void prepareToDraw(float windowWitdh, float windowHeight) {
		preparePosition(windowWitdh, windowHeight, height);

		// se è già valido non facciamo niente
		if (valid)
			return;

		// i vertici hanno l'origine nel loro centro, i bounding box
		// la correzione del position e del bounding deve partire da questa considerazione

		// definiamo l'ancora del testo
		float ix = 0, iy = 0;

		// definisce l'ancora del testo
		switch (anchor.dockX) {
		case LEFT:
			ix = multiple * witdh * 0.5f + padding;

			break;
		case RIGHT:
			ix = -multiple * witdh * 0.5f - padding;

			break;
		case CENTER:
			ix = 0;

			if (multiple > 1) {
				ix = -multiple * witdh * 0.5f;
			}

			break;
		case ABSOLUTE:
			ix = padding;

			break;
		}

		switch (anchor.dockY) {
		case BOTTOM:
			iy = height * 0.5f + padding;

			break;
		case CENTER:
			iy = 0;

			break;
		case TOP:
			iy = -height * 0.5f - padding;

			break;
		case ABSOLUTE:
			iy = -padding;

			break;
		}

		position.x += ix;
		position.y += iy;

		boundingBox.left = -multiple * witdh * 0.5f;
		boundingBox.right = multiple * witdh * 0.5f;

		// il bounding box ha le Y invertite. Dobbiamo quindi invertire la loro definizione
		// boundingBox.top = height*0.5f;
		// boundingBox.bottom = -height*0.5f;
		boundingBox.top = -height * 0.5f;
		boundingBox.bottom = height * 0.5f;

		// ora è valido
		valid = true;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param animatorValue
	 * @param witdhValue
	 * @param heightValue
	 */
	public void setImage(TextureAnimationHandler value, int witdhValue, int heightValue) {
		status = null;
		animationHandler = value;
		type = UIViewComponentType.IMAGE_ANIMATION;

		setDimension(witdhValue, heightValue);
	}

	@Override
	public void update(long enlapsedTime, float speedAdapter) {
		if (animationHandler != null) {
			animationHandler.update(enlapsedTime);
		}

	}

	@Override
	public void resetUdate() {
		if (animationHandler != null) {
			animationHandler.play();
		}

	}

}
