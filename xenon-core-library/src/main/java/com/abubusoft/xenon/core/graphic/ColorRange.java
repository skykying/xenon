/**
 * 
 */
package com.abubusoft.xenon.core.graphic;

import android.graphics.Color;

/**
 * @author Francesco Benincasa
 * 
 */
public class ColorRange {

	public int startColor;

	private float[] tempStart = new float[3];
	private float[] tempEnd = new float[3];
	private float[] tempRange = new float[3];

	public int endColor;

	/**
	 * Dato un valore iniziale e finale, calcola la percentuale
	 * 
	 * @param start
	 * @param end
	 * @param percentage
	 * @return
	 */
	int ramp(int start, int end, float percentage) {
		return (int) (start + (end - start) * percentage);
	}

	/**
	 * Dato un valore iniziale e finale, calcola la percentuale
	 * 
	 * @param start
	 * @param end
	 * @param percentage
	 * @return
	 */
	float ramp(float start, float end, float percentage) {
		return (start + (end - start) * percentage);
	}

	/**
	 * @param percentage
	 *            da 0 a 1
	 * @return
	 */
	public int interpolate(float percentage, ColorRangeType type) {
		int color = 0;

		if (type.isWorkingOnHSV()) {
			Color.colorToHSV(startColor, tempStart);
			Color.colorToHSV(endColor, tempEnd);

			tempRange[0] = type.isEnable(ColorRangeMasks.MASK_HUE) ? ramp(tempStart[0], tempEnd[0], percentage) : tempStart[0];
			tempRange[1] = type.isEnable(ColorRangeMasks.MASK_SAT) ? ramp(tempStart[1], tempEnd[1], percentage) : tempStart[1];
			tempRange[2] = type.isEnable(ColorRangeMasks.MASK_VALUE) ? ramp(tempStart[2], tempEnd[2], percentage) : tempStart[2];
			
			color = Color.HSVToColor(tempRange);

		} else if (type.isWorkingOnRGB()) {
			tempStart[0] = Color.red(startColor);
			tempStart[1] = Color.green(startColor);
			tempStart[2] = Color.blue(startColor);			

			tempEnd[0] = Color.red(endColor);
			tempEnd[1] = Color.green(endColor);
			tempEnd[2] = Color.blue(endColor);			

			tempRange[0] = type.isEnable(ColorRangeMasks.MASK_RED) ? ramp(tempStart[0], tempEnd[0], percentage) : tempStart[0];
			tempRange[1] = type.isEnable(ColorRangeMasks.MASK_GREEN) ? ramp(tempStart[1], tempEnd[1], percentage) : tempStart[1];
			tempRange[2] = type.isEnable(ColorRangeMasks.MASK_BLUE) ? ramp(tempStart[2], tempEnd[2], percentage) : tempStart[2];

			color = Color.rgb((int) tempRange[0], (int) tempRange[1], (int) tempRange[2]);
		}

		if (type.isWorkingOnAlphaChannel()) {
			color = Color.argb(ramp(Color.alpha(startColor), Color.alpha(endColor), percentage), Color.red(color), Color.green(color), Color.blue(color));
		}

		return color;
	}

	/**
	 * @param alpha
	 *            from 0 to 255
	 * @param hue
	 *            from 0 to 359
	 * @param saturation
	 *            from 0 to 1
	 * @param value
	 *            from 0 to 1
	 */
	protected int setColorHSV(int alpha, float hue, int saturation, int value) {
		float[] c = { hue, saturation, value };
		int color = Color.HSVToColor(alpha, c);

		return color;
	}

	/**
	 * @param alpha
	 *            from 0 to 255
	 * @param red
	 *            from 0 to 255
	 * @param green
	 *            from 0 to 255
	 * @param blue
	 *            from 0 to 255
	 */
	protected int setColorRGB(int alpha, int red, int green, int blue) {
		int color = Color.argb(alpha, red, green, blue);

		return color;
	}

}
