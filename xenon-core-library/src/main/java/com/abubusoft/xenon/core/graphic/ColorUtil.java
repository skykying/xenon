/**
 * 
 */
package com.abubusoft.xenon.core.graphic;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * @author Francesco Benincasa
 * 
 */
public abstract class ColorUtil {

	/**
	 * <p>
	 * Suddivide un colore nei suoi componenti.
	 * </p>
	 * 
	 * <ul>
	 * <li>0 - Alpha</li>
	 * <li>1 - Red</li>
	 * <li>2 - Green</li>
	 * <li>3 - Blue</li>
	 * </ul>
	 * 
	 * @param color
	 * 		colore in input
	 * @param argb
	 * 		array nel quale mettere le componenti argb del colore
	 */
	public static void splitInComponent(int color, int[] argb) {
		argb[0] = (color >> 24) & 0xFF;
		argb[1] = (color >> 16) & 0xFF;
		argb[2] = (color >> 8) & 0xFF;
		argb[3] = color & 0xFF;
	}

	public static int transformColor(float fraction, int startColor, int endColor) {
		int startInt = (Integer) startColor;
		int startA = (startInt >> 24) & 0xff;
		int startR = (startInt >> 16) & 0xff;
		int startG = (startInt >> 8) & 0xff;
		int startB = startInt & 0xff;

		int endInt = (Integer) endColor;
		int endA = (endInt >> 24) & 0xff;
		int endR = (endInt >> 16) & 0xff;
		int endG = (endInt >> 8) & 0xff;
		int endB = endInt & 0xff;

		return (int) ((startA + (int) (fraction * (endA - startA))) << 24) | (int) ((startR + (int) (fraction * (endR - startR))) << 16) | (int) ((startG + (int) (fraction * (endG - startG))) << 8)
				| (int) ((startB + (int) (fraction * (endB - startB))));
	}

	/**
	 * <p>
	 * Modifica l'alpha channel in percentuale rispetto a quello del colore
	 * iniziale.
	 * </p>
	 * 
	 * @param color
	 *            colore iniziale
	 * @param fraction
	 *            da 0f a 1f
	 * @return
	 */
	public static int transformAlpha(int color, float fraction) {
		int colorA = (color >> 24) & 0xff;
		int colorR = (color >> 16) & 0xff;
		int colorG = (color >> 8) & 0xff;
		int colorB = color & 0xff;

		return (int) (((int) (fraction * colorA) << 24) | (int) (colorR << 16) | (int) (colorG << 8) | (int) (colorB));
	}

	/**
	 * <p>
	 * Dato un colore, modifica in percentuale la luminosità.
	 * </p>
	 * 
	 * @param color
	 * @param factor
	 * @return
	 */
	public static int luminosity(int color, float factor) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= factor; // value component
		color = Color.HSVToColor(hsv);

		return color;
	}

	public static int luminosity(int color, float factor, float[] hsv) {
		Color.colorToHSV(color, hsv);
		hsv[2] *= factor; // value component
		color = Color.HSVToColor(hsv);

		return color;
	}

	/**
	 * <p>
	 * Data una bitmap, calcola il colore medio
	 * </p>
	 * 
	 * @param pic
	 * @return
	 */
	public static int averageARGB(Bitmap pic) {
		int A, R, G, B;
		A = R = G = B = 0;
		int width = pic.getWidth();
		int height = pic.getHeight();
		int size = width * height;

		int[] buffer = new int[size];

		pic.getPixels(buffer, 0, width, 0, 0, width, height);
		// int pixelColor;

		for (int c : buffer) {
			A += Color.alpha(c);
			R += Color.red(c);
			G += Color.green(c);
			B += Color.blue(c);
		}

		/*
		 * for (int x = 0; x < width; ++x) { for (int y = 0; y < height; ++y) {
		 * pixelColor = buffer; pic.getPixel(x, y); A +=
		 * Color.alpha(pixelColor); R += Color.red(pixelColor); G +=
		 * Color.green(pixelColor); B += Color.blue(pixelColor); } }
		 */

		A /= size;
		R /= size;
		G /= size;
		B /= size;

		buffer = null;

		return Color.argb(A, R, G, B);
	}
}
