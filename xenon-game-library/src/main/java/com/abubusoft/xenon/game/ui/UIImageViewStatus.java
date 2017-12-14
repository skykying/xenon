/**
 * 
 */
package com.abubusoft.xenon.game.ui;

import com.abubusoft.xenon.texture.Texture;

import android.util.SparseArray;

/**
 * <p>
 * .
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class UIImageViewStatus {

	/**
	 * <p>.</p>
	 */
	Texture image;

	/**
	 * <p>
	 * .
	 * </p>
	 */
	int key;

	/**
	 * <p>
	 * .
	 * </p>
	 */
	private UIImageViewStatus() {
		array = new SparseArray<>(2);
	}

	/**
	 * <p>
	 * .
	 * </p>
	 */
	SparseArray<Texture> array;

	/**
	 * <p>
	 * .
	 * </p>
	 * 
	 * @return
	 */
	public static UIImageViewStatus build() {
		return new UIImageViewStatus();
	}

	/**
	 * <p>
	 * .
	 * </p>
	 * 
	 * @param statusKey
	 * @param texture
	 * @return
	 */
	public UIImageViewStatus add(int statusKey, Texture texture) {
		array.append(statusKey, texture);

		return this;
	}

	/**
	 * <p>
	 * .
	 * </p>
	 * 
	 * @return
	 */
	public Texture getImage() {
		return image;
	}

	/**
	 * <p>.</p>
	 * @return
	 */
	public int getKey() {
		return key;
	}

	/**
	 * <p>
	 * Imposta il nuovo stato mediante la sua key.
	 * </p>
	 * 
	 * @param value
	 */
	public void setStatus(int value) {
		image = array.get(value);
		key = value;
	}

	/**
	 * <p>
	 * Fluent interface per lo stato corrente.
	 * </p>
	 * 
	 * @param statusKey
	 * @return
	 */
	public UIImageViewStatus activeStatus(int value) {
		setStatus(value);
		return this;
	}
}
