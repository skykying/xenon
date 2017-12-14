package com.abubusoft.xenon.game.ui;

import org.abubu.argon.animations.TextureKeyFrame;
import org.abubu.argon.math.Matrix4x4;
import org.abubu.argon.mesh.MeshSprite;
import org.abubu.argon.mesh.modifiers.TextureModifier;
import org.abubu.argon.shader.Shader;
import org.abubu.argon.shader.ShaderManager;
import org.abubu.argon.texture.Texture;
import org.abubu.argon.texture.TextureRegion;
import org.abubu.argon.vbo.BufferAllocationType;
import org.abubu.argon.vbo.IndexBuffer;

import android.opengl.GLES20;

/**
 * Consente di disegnare degli sprite in modo ottimizzato. Lo shader è sempre lo stesso. Se i vari parametri non vengono cambiati, il type evita di cambiarli.
 * 
 * @author Francesco Benincasa
 * 
 */
public class UIDrawerBatcher {

	/**
	 * Usa di default uno shader del texturedAnimatorManager
	 */
	public UIDrawerBatcher() {
		defaultShader = ShaderManager.instance().createShaderTexture();
	}

	/**
	 * Usa di default uno shader del texturedAnimatorManager
	 * 
	 * @param shaderValue
	 */
	public UIDrawerBatcher(Shader shaderValue) {
		defaultShader = shaderValue;
	}

	/**
	 * Imposta lo shader corrente
	 * 
	 * @param shaderValue
	 */
	public void setShader(Shader shaderValue) {
		defaultShader = shaderValue;
	}

	private enum DrawType {
		ANIMATED_SPRITE, STATIC_SPRITE
	};

	DrawType lastDrawType;

	/**
	 * shader corrente
	 */
	Shader currentShader;

	/**
	 * shader default
	 */
	Shader defaultShader;
	/**
	 * texture atlas attualmente usata
	 */
	Texture lastTexture;

	/**
	 * ultimo frame usato
	 */
	TextureRegion lastTextureRegion;

	/**
	 * frame usato adesso
	 */
	TextureRegion currentTextureRegion;

	/**
	 * texture usata adesso
	 */
	Texture currentTexture;

	/**
	 * ultimo shape usato
	 */
	MeshSprite lastUsedShape;

	/**
	 * Inizializza batch
	 */
	public void begin() {
		currentShader = defaultShader;
		currentShader.use();

		lastTexture = null;

		lastUsedShape = null;
		lastTextureRegion = null;
	}

	/**
	 * Inizializza batch
	 */
	public void begin(Shader shader) {
		currentShader = shader;
		currentShader.use();

		lastTexture = null;

		lastUsedShape = null;
		lastTextureRegion = null;
	}

	/**
	 * @param introText
	 * @param font
	 * @param matrixModelViewProjection
	 */
	public void drawText(UIViewComponent parent, UITextView text, Matrix4x4 matrixModelViewProjection) {
		resetIfNot(DrawType.STATIC_SPRITE);

		ArgonBitmapFont font = text.font;

		text.prepareToDraw(parent.witdh, parent.height);

		// shape
		currentShader.setVertexCoordinatesArray(text.shape.vertices);
		// lo shape deve cambiare sempre, dato che il testo cambia (o almeno mi aspetto che sia così)
		lastUsedShape = null;

		// texture binder
		if (font.data.imageTextures[0] != lastTexture) {
			currentShader.setTexture(0, font.data.imageTextures[0]);
			lastTexture = null;// at.index;
		}

		// texture coordinate (default 0)
		currentShader.setTextureCoordinatesArray(0, text.shape.textures[0]);

		// matrice di proiezione
		currentShader.setModelViewProjectionMatrix(matrixModelViewProjection.asFloatBuffer());

		currentShader.setIndexBuffer(text.shape.indexes);

		if (text.shape.indexes.allocation == BufferAllocationType.CLIENT) {
			GLES20.glDrawElements(text.shape.drawMode.value, IndexBuffer.INDEX_IN_QUAD_TILE * text.value.length(), GLES20.GL_UNSIGNED_SHORT, text.shape.indexes.buffer);
		} else {
			GLES20.glDrawElements(text.shape.drawMode.value, IndexBuffer.INDEX_IN_QUAD_TILE * text.value.length(), GLES20.GL_UNSIGNED_SHORT, 0);
		}
		currentShader.unsetIndexBuffer(text.shape.indexes);
	}

	/**
	 * Disegna in modalità batch uno sprite.
	 * 
	 * @param shape
	 * @param animator
	 * @param matrixModelViewProjection
	 */
	public void drawImageWithAnimation(UIViewComponent parent, UIImageView image, Matrix4x4 matrixModelViewProjection) {
		resetIfNot(DrawType.ANIMATED_SPRITE);

		image.prepareToDraw(parent.witdh, parent.height);

		MeshSprite shape = image.shape;
		//TextureAnimationHandler status = image.timeline;
		TextureKeyFrame value=image.animationHandler.value();

		// shape
		if (shape != lastUsedShape) {
			currentShader.setVertexCoordinatesArray(shape.vertices);
			lastUsedShape = shape;
		}

		// texture coordinate (indice 0)
		currentTextureRegion = value.textureRegion;
		if (currentTextureRegion != lastTextureRegion) {
			TextureModifier.setTextureCoords(shape, currentTextureRegion);
			// shape.setTextureCoords(currentTextureRegione.frame);
			currentShader.setTextureCoordinatesArray(0, shape.textures[0]);
			lastTextureRegion = currentTextureRegion;
		}

		// texture binder
		if (value.texture != lastTexture) {
			lastTexture = value.texture;
			currentShader.setTexture(0, lastTexture);			
		}

		currentShader.setIndexBuffer(image.shape.indexes);
		for (int i = 0; i < image.multiple; i++) {
			// matrice di proiezione

			currentShader.setModelViewProjectionMatrix(matrixModelViewProjection.asFloatBuffer());

			if (image.shape.indexes.allocation == BufferAllocationType.CLIENT) {
				GLES20.glDrawElements(image.shape.drawMode.value, IndexBuffer.INDEX_IN_QUAD_TILE, GLES20.GL_UNSIGNED_SHORT, image.shape.indexes.buffer);
			} else {
				GLES20.glDrawElements(image.shape.drawMode.value, IndexBuffer.INDEX_IN_QUAD_TILE, GLES20.GL_UNSIGNED_SHORT, 0);
			}

			// lo moltiplichiamo dopo per poterlo spostare
			matrixModelViewProjection.translate(image.witdh, 0, 0);
		}

		currentShader.unsetIndexBuffer(image.shape.indexes);

	}

	/**
	 * @param introText
	 * @param font
	 * @param matrixModelViewProjection
	 */
	public void drawImage(UIViewComponent parent, UIImageView image, Matrix4x4 matrixModelViewProjection) {
		resetIfNot(DrawType.STATIC_SPRITE);

		image.prepareToDraw(parent.witdh, parent.height);

		// shape
		currentShader.setVertexCoordinatesArray(image.shape.vertices);
		// lo shape deve cambiare sempre, dato che il testo cambia (o almeno mi aspetto che sia così)
		lastUsedShape = null;

		// texture binder
		currentTexture = image.status.getImage();
		/*
		 * if (currentTexture.index != lastTextureIndex) { currentShader.setTexture(0, currentTexture); lastTextureIndex = currentTexture.index; }
		 */
		currentShader.setTexture(0, currentTexture);

		// texture coordinate (default 0)
		currentShader.setTextureCoordinatesArray(0, image.shape.textures[0]);

		currentShader.setIndexBuffer(image.shape.indexes);
		for (int i = 0; i < image.multiple; i++) {
			// matrice di proiezione
			currentShader.setModelViewProjectionMatrix(matrixModelViewProjection.asFloatBuffer());
			// GLES20.glDrawArrays(image.shape.drawMode.value, 0, image.shape.vertexCount);

			if (image.shape.indexes.allocation == BufferAllocationType.CLIENT) {
				GLES20.glDrawElements(image.shape.drawMode.value, IndexBuffer.INDEX_IN_QUAD_TILE, GLES20.GL_UNSIGNED_SHORT, image.shape.indexes.buffer);
			} else {
				GLES20.glDrawElements(image.shape.drawMode.value, IndexBuffer.INDEX_IN_QUAD_TILE, GLES20.GL_UNSIGNED_SHORT, 0);
			}
			// lo moltiplichiamo dopo
			matrixModelViewProjection.translate(image.witdh, 0, 0);
		}
		currentShader.unsetIndexBuffer(image.shape.indexes);

	}

	private void resetIfNot(DrawType currentDrawType) {
		if (currentDrawType != lastDrawType) {
			currentTextureRegion = null;
			lastTexture = null;
			lastTextureRegion = null;
			lastUsedShape = null;
		}
		lastDrawType = currentDrawType;
	}

	public void end() {
		// currentShader.close();
		currentShader = null;
	}

	public void drawPanel(UILayer uiLayer, UIPanelView view, Matrix4x4 matrixModelviewLocal) {
		// TODO Auto-generated method stub

	}
}
