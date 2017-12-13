package com.abubusoft.xenon.core.graphic;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import static com.abubusoft.xenon.core.graphic.BitmapManager.wrapBitmap;

/**
 * Carica le bitmap in un formato più comodo e di ridotte dimensioni, giusto per
 * evitare problemi con la memoria.
 * 
 * Wrapped
 * 
 * <pre>
 * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html}
 * </pre>
 * 
 * @author Francesco Benincasa
 * 
 * 
 */
public abstract class SampledBitmapFactory {

	private static Bitmap scaleWidth(Bitmap source, int newWidth, int newHeight, ScalePositionType scalePosition) {
		int sourceWidth = source.getWidth();
		int sourceHeight = source.getHeight();

		// Compute the scaling factors to fit the new height and width,
		// respectively.
		// To cover the final image, the final scaling will be the bigger
		// of these two.

		float xScale = (float) newWidth / sourceWidth;

		// Now get the size of the source bitmap event scaled
		float scaledWidth = xScale * sourceWidth;
		float scaledHeight = xScale * sourceHeight;

		// Let's find out the upper left coordinates if the scaled bitmap
		// should be centered in the new size give by the parameters
		float left = 0f;
		float top = 0f;

		switch (scalePosition) {
		case FIT_WIDTH_TOP:
			top = 0;
			break;
		case FIT_WIDTH_CENTER:
			top = (newHeight - scaledHeight) / 2;
			break;
		case FIT_WIDTH_BOTTOM:
			top = (newHeight - scaledHeight);
			break;
		}

		// The target rectangle for the new, scaled version of the source bitmap
		// will now be
		RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

		Bitmap reducedImage = wrapBitmap(Bitmap.createScaledBitmap(source, (int) scaledWidth, (int) (scaledHeight), false));
		Bitmap dest = wrapBitmap(Bitmap.createBitmap(newWidth, newHeight, source.getConfig()));
		Canvas canvas = new Canvas(dest);
		canvas.drawBitmap(reducedImage, null, targetRect, null);

		reducedImage.recycle();

		return dest;
	}

	/**
	 * Effettua il crop dell'immagine, partendo dalle dimensioni desiderate.
	 * 
	 * wrapped
	 * 
	 * Vedi
	 * http://stackoverflow.com/questions/8112715/how-to-crop-bitmap-center-
	 * like-imageview
	 * 
	 * @param source
	 * @param newHeight
	 * @param newWidth
	 * @return
	 */
	private static Bitmap cropCenter(Bitmap source, int newWidth, int newHeight) {
		int sourceWidth = source.getWidth();
		int sourceHeight = source.getHeight();

		// Compute the scaling factors to fit the new height and width,
		// respectively.
		// To cover the final image, the final scaling will be the bigger
		// of these two.
		float xScale = (float) newWidth / sourceWidth;
		float yScale = (float) newHeight / sourceHeight;
		float scale = Math.max(xScale, yScale);

		// Now get the size of the source bitmap event scaled
		float scaledWidth = scale * sourceWidth;
		float scaledHeight = scale * sourceHeight;

		// Let's find out the upper left coordinates if the scaled bitmap
		// should be centered in the new size give by the parameters
		float left = (newWidth - scaledWidth) / 2;
		float top = (newHeight - scaledHeight) / 2;

		// The target rectangle for the new, scaled version of the source bitmap
		// will now
		// be
		RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

		// Finally, we create a new bitmap of the specified size and draw our
		// new,
		// scaled bitmap onto it.
		Bitmap dest = wrapBitmap(Bitmap.createBitmap(newWidth, newHeight, source.getConfig()));
		Canvas canvas = new Canvas(dest);
		canvas.drawBitmap(source, null, targetRect, null);

		return dest;
	}

	/**
	 * Data una risorsa, provvede a creare una bitmap delle dimensioni
	 * desiderate.
	 * 
	 * @param res
	 * @param resId
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap createBitmap(Resources res, int resId, int reqWidth, int reqHeight, ScalePositionType type) {
		Bitmap image1 = decodeBitmap(res, resId, reqWidth, reqHeight, null);
		Bitmap image2 = null;

		switch (type) {
		case CROP_CENTER:
			image2 = wrapBitmap(cropCenter(image1, reqWidth, reqHeight));
			break;
		case FIT_WIDTH_TOP:
			image2 = wrapBitmap(scaleWidth(image1, reqWidth, reqHeight, type));
			break;
		case FIT_WIDTH_CENTER:
			image2 = wrapBitmap(scaleWidth(image1, reqWidth, reqHeight, type));
			break;
		case FIT_WIDTH_BOTTOM:
			image2 = wrapBitmap(scaleWidth(image1, reqWidth, reqHeight, type));
			break;
		}

		// se possibile lo ricicliamo
		if (image1 != image2) {
			image1.recycle();
		}

		return image2;
	}

	/**
	 * Effettua il resize della bitmap
	 * 
	 * @param sourceBitmap
	 *            bitmap da modificare
	 * @param reqWidth
	 *            width richiesta
	 * @param reqHeight
	 *            height richiesta
	 * @param type
	 *            tipo di ridimensionamento: CROP_CENTER, FIT_WIDTH_TOP,
	 *            FIT_WIDTH_CENTER, FIT_WIDTH_BOTTOM:
	 * @return immagine modificata
	 */
	public static Bitmap resizeBitmap(Bitmap sourceBitmap, int reqWidth, int reqHeight, ScalePositionType type) {
		Bitmap image1 = Bitmap.createScaledBitmap(sourceBitmap, reqWidth, reqHeight, true);
		Bitmap image2 = null;

		switch (type) {
		case CROP_CENTER:
			image2 = wrapBitmap(cropCenter(image1, reqWidth, reqHeight));
			break;
		case FIT_WIDTH_TOP:
			image2 = wrapBitmap(scaleWidth(image1, reqWidth, reqHeight, type));
			break;
		case FIT_WIDTH_CENTER:
			image2 = wrapBitmap(scaleWidth(image1, reqWidth, reqHeight, type));
			break;
		case FIT_WIDTH_BOTTOM:
			image2 = wrapBitmap(scaleWidth(image1, reqWidth, reqHeight, type));
			break;
		}

		// se possibile lo ricicliamo
		if (image1 != image2) {
			image1.recycle();
		}

		return image2;
	}

	/**
	 * Dato il nome di file, provvede a creare una bitmap delle dimensioni
	 * desiderate.
	 * 
	 * wrapped
	 * 
	 * @param imageFileName
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap createBitmap(String imageFileName, int reqWidth, int reqHeight, ScalePositionType type) {
		// questa viene wrappata
		Bitmap image1 = decodeBitmap(imageFileName, reqWidth, reqHeight, null);
		Bitmap image2 = null;

		switch (type) {
		case CROP_CENTER:
			image2 = wrapBitmap(cropCenter(image1, reqWidth, reqHeight));
			break;
		case FIT_WIDTH_TOP:
			image2 = wrapBitmap(scaleWidth(image1, reqWidth, reqHeight, type));
			break;
		case FIT_WIDTH_CENTER:
			image2 = wrapBitmap(scaleWidth(image1, reqWidth, reqHeight, type));
			break;
		case FIT_WIDTH_BOTTOM:
			image2 = wrapBitmap(scaleWidth(image1, reqWidth, reqHeight, type));
			break;
		}

		// wrapBitmap(cropCenter(image1, reqWidth, reqHeight));
		// se possibile lo ricicliamo
		if (image1 != image2) {
			image1.recycle();
		}

		return image2;
	}

	/**
	 * Calcola il fattore di zoom per caricare l'immagine da disco nel miglior
	 * modo possibile (dal punto di vista del consumo della memoria).
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	/**
	 * Decodifica un'immagine da un contesto riducendone le dimensioni. Non
	 * effettua uno scaling, semplicemente tira fuori una bitmap di dimensioni
	 * ridotte.
	 * 
	 * Wrapped
	 * 
	 * 
	 * @param res
	 * @param resId
	 * @param reqWidth
	 * @param reqHeight
	 * @param effectiveSize
	 *            <b>OPZIONALE</b> serve a memorizzare in output le dimensioni
	 *            reali dell'immagine
	 * @return
	 */
	public static Bitmap decodeBitmap(Resources res, int resId, int reqWidth, int reqHeight, Rect effectiveSize) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap temp = BitmapFactory.decodeResource(res, resId, options);

		if (effectiveSize != null) {
			effectiveSize.left = 0;
			effectiveSize.top = 0;
			effectiveSize.bottom = options.outHeight;
			effectiveSize.right = options.outWidth;
		}

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		// ricicliamo bitmap
		if (temp != null && !temp.isRecycled())
			temp.recycle();

		return wrapBitmap(BitmapFactory.decodeResource(res, resId, options));
	}

	/**
	 * Decodifica un'immagine da un contesto riducendone le dimensioni.
	 * 
	 * Wrapped
	 * 
	 * @param res
	 * @param resId
	 * @param reqWidth
	 * @param reqHeight
	 * @param effectiveSize
	 *            <b>OPZIONALE</b> serve a memorizzare in output le dimensioni
	 *            reali dell'immagine
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Bitmap decodeBitmap(String imageFileName, int reqWidth, int reqHeight, Rect effectiveSize) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap temp = BitmapFactory.decodeFile(imageFileName, options);

		if (effectiveSize != null) {
			effectiveSize.left = 0;
			effectiveSize.top = 0;
			effectiveSize.bottom = options.outHeight;
			effectiveSize.right = options.outWidth;
		}

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		
		// ricicliamo bitmap
		if (temp != null && !temp.isRecycled())
			temp.recycle();

		BufferedInputStream bis = null;
		Bitmap bp = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(imageFileName));

			bp = wrapBitmap(BitmapFactory.decodeStream(bis, null, options));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bp;

	}
}
