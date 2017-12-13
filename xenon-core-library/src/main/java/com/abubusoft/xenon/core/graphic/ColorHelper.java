package com.abubusoft.xenon.core.graphic;

import android.graphics.Color;

public abstract class ColorHelper {
	
	private static float[] temp=new float[3];

	private static String fix(String input) {
		if (input.length() == 1) {
			return "0" + input;
		}

		return input;
	}

	public static String toARGBString(int color) {
		String ret;

		ret = "#";
		ret += fix(Integer.toHexString(Color.alpha(color)));
		ret += fix(Integer.toHexString(Color.red(color)));
		ret += fix(Integer.toHexString(Color.green(color)));
		ret += fix(Integer.toHexString(Color.blue(color)));

		return ret.toUpperCase();
	}
	
	/**
	 * @param hue
	 * @param saturation
	 * @param value
	 * @return
	 */
	public static int hsv(int hue, int saturation, int value)
	{
		return hsvNormalized(hue, saturation/255f, value/255f);
	}
		
	/**
	 * I parametri 
	 * @param hue
	 * 			[0 .. 360)
	 * @param saturation
	 * 			[0 .. 1]
	 * @param value
	 * 			[0 .. 1]
	 * @return
	 */
	public static int hsvNormalized(float hue, float saturation, float value)
	{
		int color;
		
		hue=Math.max(0, Math.min(360, hue));
		saturation=Math.max(0, Math.min(1, saturation));
		value=Math.max(0, Math.min(1, value));
		
		temp[0]=hue;
		temp[1]=saturation;
		temp[2]=value;
		
		color=Color.HSVToColor(temp);
		
		return color;
	}
	
	/**
	 * I parametri vanno da [0 .. 1]
	 * @param red
	 * @param green
	 * @param blue
	 * @return
	 */
	public static int rgbNormalized(float red, float green, float blue)
	{
		return rgb((int)(red*255), (int)(green*255), (int)(blue*255));
	}
	
	/**
	 * I parametri vanno da [0 .. 255]
	 * 
	 * @param red
	 * @param green
	 * @param blue
	 * @return
	 */
	public static int rgb(int red, int green, int blue)
	{
		int color;
		
		red=Math.max(0, Math.min(255, red));
		green=Math.max(0, Math.min(255, green));
		blue=Math.max(0, Math.min(255, blue));
		
		temp[0]=red;
		temp[1]=green;
		temp[2]=blue;
		
		color=Color.rgb(red, green, blue);
		
		return color;
	}
	
	public static float[] toHSV(int color) {		
		float[] ret=new float[3];
		toHSV(color, ret);
		
		return ret;
	}

	public static void toHSV(int color, float[] ret) {		
		float r, g, b;

		r = Color.red(color) / 255f;
		g = Color.green(color) / 255f;
		b = Color.blue(color) / 255f;

		float minRGB = Math.min(r, Math.min(g, b));
		float maxRGB = Math.max(r, Math.max(g, b));

		float computedH;
		float computedS;
		float computedV;

		// Black-gray-white
		if (minRGB == maxRGB) {
			computedV = minRGB;

			ret[0] = 0;
			ret[1] = 0;
			ret[2] = computedV;
			return;
		}

		// Colors other than black-gray-white:
		float d = (r == minRGB) ? g - b : ((b == minRGB) ? r - g : b - r);
		float h = (r == minRGB) ? 3 : ((b == minRGB) ? 1 : 5);
		computedH = 60 * (h - d / (maxRGB - minRGB));
		computedS = (maxRGB - minRGB) / maxRGB;
		computedV = maxRGB;

		ret[0] = computedH;
		ret[1] = computedS;
		ret[2] = computedV;

	}

	/**
	 * Given H,S,L in range of 0-360, 0-1, 0-1 Returns a Color.
	 * 
	 * @param hue
	 * @param saturation
	 * @param value
	 * @return
	 */
	public static int fromHSV(float hue, float sat, float lum) {
		int color=0;
		float[] a={hue, sat, lum};
		color=Color.HSVToColor(a);
		
		return color;
		
		/*float v;
		float red, green, blue;
		float m;
		float sv;
		int sextant;
		float fract, vsf, mid1, mid2;

		red = lum; // default to gray
		green = lum;
		blue = lum;
		v = (lum <= 0.5f) ? (lum * (1.0f + sat)) : (lum + sat - lum * sat);
		m = lum + lum - v;
		sv = (v - m) / v;
		hue /= 60.0; // get into range 0..6
		sextant = (int) Math.floor(hue); // int32 rounds up or down.
		fract = hue - sextant;
		vsf = v * sv * fract;
		mid1 = m + vsf;
		mid2 = v - vsf;

		if (v > 0) {
			switch (sextant) {
			case 0:
				red = v;
				green = mid1;
				blue = m;
				break;
			case 1:
				red = mid2;
				green = v;
				blue = m;
				break;
			case 2:
				red = m;
				green = v;
				blue = mid1;
				break;
			case 3:
				red = m;
				green = mid2;
				blue = v;
				break;
			case 4:
				red = mid1;
				green = m;
				blue = v;
				break;
			case 5:
				red = v;
				green = m;
				blue = mid2;
				break;
			}
		}
		// return color(red * 255, green * 255, blue * 255);
		return 0xff000000 | (Math.round(red * 255) << 16) | (Math.round(green * 255) << 8) | (Math.round(blue * 255) << 0);*/
	}

	/**
	 * HSV to RGB color conversion
	 * 
	 * H runs from 0 to 360 degrees S and V run from 0 to 100
	 * 
	 * Ported from the excellent java algorithm by Eugene Vishnevsky at:
	 * http://www.cs.rit.edu/~ncs/color/t_convert.html
	 */
	/*
	 * public static int fromHSV(float h, float s, float v) { float r, g, b; int
	 * i; float f, p, q, t;
	 * 
	 * // Make sure our arguments stay in-range h = Math.max(0, Math.min(360,
	 * h)); s = Math.max(0, Math.min(1, s)); v = Math.max(0, Math.min(1, v));
	 * 
	 * // We accept saturation and value arguments from 0 to 100 because that's
	 * // how Photoshop represents those values. Internally, however, the //
	 * saturation and value are calculated from a range of 0 to 1. We make //
	 * That conversion here. // s /= 100; // v /= 100;
	 * 
	 * if (s == 0) { // Achromatic (grey) r = g = b = v; // return [Math.round(r
	 * * 255), Math.round(g * 255), Math.round(b * // 255)]; return 0xff000000 |
	 * (Math.round(r * 255) << 16) | (Math.round(g * 255) << 8) | (Math.round(b
	 * * 255) << 0); }
	 * 
	 * h /= 60f; // sector 0 to 5 i = (int) Math.floor(h); f = h - i; //
	 * factorial part of h p = v * (1 - s); q = v * (1 - s * f); t = v * (1 - s
	 * * (1 - f));
	 * 
	 * switch (i) { case 0: r = v; g = t; b = p; break;
	 * 
	 * case 1: r = q; g = v; b = p; break;
	 * 
	 * case 2: r = p; g = v; b = t; break;
	 * 
	 * case 3: r = p; g = q; b = v; break;
	 * 
	 * case 4: r = t; g = p; b = v; break;
	 * 
	 * default: // case 5: r = v; g = p; b = q; break; }
	 * 
	 * return 0xff000000 | (Math.round(r * 255) << 16) | (Math.round(g * 255) <<
	 * 8) | (Math.round(b * 255) << 0); }
	 */

	/**
	 * Converts the components of a color, as specified by the HSB model, to an
	 * equivalent set of values for the default RGB model. The saturation and
	 * brightness components should be floating-point values between zero and
	 * one (numbers in the range 0.0-1.0). The hue component can be any
	 * floating-point number. The floor of this number is subtracted from it to
	 * create a fraction between 0 and 1. This fractional number is then
	 * multiplied by 360 to produce the hue angle in the HSB color model. The
	 * integer that is returned by HSBtoRGB encodes the value of a color in bits
	 * 0-23 of an integer value that is the same format used by the method
	 * getRGB. This integer can be supplied as an argument to the Color
	 * constructor that takes a single integer argument.
	 * 
	 * Parameters:
	 * 
	 * @param hue
	 * @param saturation
	 * @param value
	 *            hue the hue component of the color saturation the saturation
	 *            of the color brightness the brightness of the color Returns:
	 *            the RGB value of the color with the indicated hue, saturation,
	 *            and brightness.
	 **/
	/*
	 * public static int fromHSV(float hue, float saturation, float brightness)
	 * { // float[] a = { hue, saturation, value }; // return
	 * Color.HSVToColor(a);
	 * 
	 * int r = 0, g = 0, b = 0; if (saturation == 0) { r = g = b = (int)
	 * (brightness * 255.0f + 0.5f); } else { float h = (hue - (float)
	 * Math.floor(hue)) * 6.0f; float f = h - (float) java.lang.Math.floor(h);
	 * float p = brightness * (1.0f - saturation); float q = brightness * (1.0f
	 * - saturation * f); float t = brightness * (1.0f - (saturation * (1.0f -
	 * f))); switch ((int) h) { case 0: r = (int) (brightness * 255.0f + 0.5f);
	 * g = (int) (t * 255.0f + 0.5f); b = (int) (p * 255.0f + 0.5f); break; case
	 * 1: r = (int) (q * 255.0f + 0.5f); g = (int) (brightness * 255.0f + 0.5f);
	 * b = (int) (p * 255.0f + 0.5f); break; case 2: r = (int) (p * 255.0f +
	 * 0.5f); g = (int) (brightness * 255.0f + 0.5f); b = (int) (t * 255.0f +
	 * 0.5f); break; case 3: r = (int) (p * 255.0f + 0.5f); g = (int) (q *
	 * 255.0f + 0.5f); b = (int) (brightness * 255.0f + 0.5f); break; case 4: r
	 * = (int) (t * 255.0f + 0.5f); g = (int) (p * 255.0f + 0.5f); b = (int)
	 * (brightness * 255.0f + 0.5f); break; case 5: r = (int) (brightness *
	 * 255.0f + 0.5f); g = (int) (p * 255.0f + 0.5f); b = (int) (q * 255.0f +
	 * 0.5f); break; } } return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
	 * 
	 * }
	 */

	public static String toRGBString(int color) {
		String ret;

		ret = "#";
		ret += fix(Integer.toHexString(Color.red(color)));
		ret += fix(Integer.toHexString(Color.green(color)));
		ret += fix(Integer.toHexString(Color.blue(color)));

		return ret.toUpperCase();
	}

	public static String toHSVString(int color) {
		float[] ret = toHSV(color);

		return "{hue=" + ret[0] + ", sat=" + ret[1] + ",val=" + ret[2] + "}";
	}

	public static String toAHSVString(int color) {
		float[] ret = toHSV(color);

		return "{alpha=" + Color.alpha(color) + ", hue=" + ret[0] + ", sat=" + ret[1] + ",val=" + ret[2] + "}";
	}
}
