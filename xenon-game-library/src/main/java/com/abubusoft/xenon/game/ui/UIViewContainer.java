package com.abubusoft.xenon.game.ui;

import android.util.SparseArray;

public abstract class UIViewContainer extends UIViewComponent {

	/**
	 * elenco dei textview
	 */
	protected SparseArray<UIViewComponent> views = new SparseArray<>();


	/**
	 * <p>
	 * Aggiunge un elemento al container.
	 * </p>
	 * 
	 * @param view
	 */
	void addChild(UIViewComponent view) {
		view.uid = generateUid();
		views.put(globaleCounter, view);
	}

}
