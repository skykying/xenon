package com.abubusoft.xenon.android;

import com.abubusoft.xenon.R;
import org.abubu.elio.android.ElioPreferenceActivity;

import android.os.Bundle;

/**
 * <p>Rappresenta l'activity di default per la gestione delle preference.</p>
 * 
 * <p>Referenzia un file xml di configurazione delle preference con il nome.</p>
 * 
 * <pre>res/xml/preference_main</pre>
 * 
 * @author Francesco Benincasa
 *
 */
public class ArgonSettingsActivity extends ElioPreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);		
		addPreferencesFromResource(R.xml.preference_main);
	}
}
