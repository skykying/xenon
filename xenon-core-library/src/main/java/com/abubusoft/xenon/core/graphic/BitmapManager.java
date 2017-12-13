package com.abubusoft.xenon.core.graphic;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import com.abubusoft.xenon.core.logger.ElioLogger;

import android.graphics.Bitmap;

/**
 * Gestore delle bitmap il cui unico scopo è poter cancellare tutte le bitmap
 * che per un motivo o per l'altro non sono state ancora pulice
 * 
 * @author Francesco Benincasa
 * 
 */
public class BitmapManager {

	/**
	 * indica se il bitmap manager è abilitato
	 */
	private boolean enabled;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	private final static BitmapManager instance = new BitmapManager();

	private List<SoftReference<Bitmap>> bitmapList;

	private List<StackTraceElement[]> stackList;

	private BitmapManager() {
		enabled=false;		
		bitmapList = new ArrayList<SoftReference<Bitmap>>();
		stackList = new ArrayList<StackTraceElement[]>();
	}

	public static BitmapManager instance() {
		return instance;
	}
	
	/**
	 * @param source
	 * @return
	 */
	public static Bitmap wrapBitmap(Bitmap source) {
		return instance.wrap(source);
	}


	/**
	 * Effettua il wrap delle bitmap. Deve essere messo nello stesso metodo in
	 * cui viene creata la bitmap.
	 * 
	 * @param source
	 * @return
	 */
	public Bitmap wrap(Bitmap source) {
		if (instance.enabled) {
			bitmapList.add(new SoftReference<Bitmap>(source));
			stackList.add(Thread.currentThread().getStackTrace());
		}
		return source;
	}

	/**
	 * rilasci le bitmap associate ad un determinato bitmapmanager
	 * 
	 * @param bm
	 */
	public static void releaseBitmaps(BitmapManager bm) {
		if (bm != null)
			bm.release();
	}

	/**
	 * pulisce tutte le bitmap ancora in piedi
	 */
	public void release() {
		Bitmap bitmap;
		StackTraceElement[] stack;

		int counter = 0;
		int n = bitmapList.size();

		for (int i = 0; i < n; i++) {
			bitmap = bitmapList.get(i).get();
			stack = stackList.get(i);

			if (bitmap != null && !bitmap.isRecycled()) {
				ElioLogger.debug("Bitmap cancello bitmap %s ", stack.toString());
				bitmap.recycle();
				counter++;
			}
		}

		bitmapList.clear();

		if (counter > 0) {
			ElioLogger.debug("Bitmap cancellate %s ", counter);
		}
	}
}
