/**
 * 
 */
package com.abubusoft.xenon.android;

import com.abubusoft.xenon.core.android.ElioPreferenceActivity;

import android.os.Bundle;

/**
 * Activity base delle preference, sia quella principale, sia quelle degli
 * screen secondary
 * 
 * @author Francesco Benincasa
 * 
 */
public class ArgonPreferenceBaseActivity extends ElioPreferenceActivity {

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public void onResume() {
		super.onResume();

	}

	/**
	 * Aggiunge al titolo il numero di versione e registra questa classe come
	 * listener sui cambi di preferenza.
	 * 
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
}
