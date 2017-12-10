package com.abubusoft.xenon.shader;

import java.nio.FloatBuffer;

import com.abubusoft.xenon.ArgonBeanContext;
import com.abubusoft.xenon.ArgonBeanType;
import com.abubusoft.xenon.R;
import com.abubusoft.xenon.opengl.ArgonGL;

import android.content.Context;
import android.opengl.GLES20;

/**
 * Forma che rappresenta una tiledMap
 * 
 * @author Francesco Benincasa
 * 
 */
public class ShaderTiledMap extends Shader {

	/**
	 * Costruisce una tile
	 * 
	 * @param context
	 * @param oneTextureForLayer
	 * @param options
	 */
	public ShaderTiledMap(boolean oneTextureForLayer, ArgonShaderOptions options) {
		Context context = ArgonBeanContext.getBean(ArgonBeanType.CONTEXT);
		// imposta gli shader program ed il numero di texture
		setupFromFiles(context, R.raw.shader_tiledmap_vertex, R.raw.shader_tiledmap_fragment, options.define("MORE_TEXTURES", !(options.numberOfTextures == 1 || oneTextureForLayer)));
	}

	/*
	 * public ShaderTiledMap(Context context, int vertexProgramId, int fragmentProgramId, ArgonShaderOptions options) { super(context, vertexProgramId, fragmentProgramId, options);
	 * }
	 */

	protected int textureSelectorPtr;

	protected int opacityPtr;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.abubu.argon.shader.Shader#assignPtrs()
	 */
	protected void assignPtrs() {
		super.assignPtrs();

		textureSelectorPtr = GLES20.glGetAttribLocation(programId, "a_textureIndex");
		opacityPtr = GLES20.glGetUniformLocation(programId, "u_opacity");
	}

	/**
	 * <p>
	 * Imposta l'array del texture selector.
	 * </p>
	 * 
	 * @param i
	 * @param textureCoords
	 */
	public void setTextureSelectorArray(FloatBuffer textureSelector) {
		GLES20.glVertexAttribPointer(textureSelectorPtr, 1, GLES20.GL_FLOAT, false, 0, textureSelector);
		GLES20.glEnableVertexAttribArray(textureSelectorPtr);
		ArgonGL.checkGlError("Shader (id="+programId+") setTextureSelectorArray");
	}

	/**
	 * <p>
	 * Imposta un opacity value, da 0f a 1f.
	 * </p>
	 * 
	 * @param initialValue
	 */
	public void setOpacity(float value) {
		GLES20.glUniform1f(opacityPtr, value);
		ArgonGL.checkGlError("Shader (id="+programId+") setOpacity");
	}

}
