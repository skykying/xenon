/**
 * 
 */
package com.abubusoft.xenon.game;

import com.abubusoft.xenon.ArgonBeanContext;
import com.abubusoft.xenon.ArgonBeanType;
import com.abubusoft.xenon.android.surfaceview16.ArgonGLView;
import com.abubusoft.xenon.game.hub.GameHub;
import org.abubu.elio.logger.ElioLogger;
import org.abubu.elio.util.ResourceUtility;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * <p>
 * Activity di base per la gestione dei game e dei wallpaper. Se non viene specifiato altro, questa classe viene visualizzata dopo lo splash screen in caso di game.
 * </p>
 * 
 * <p>
 * Può essere usata direttamente anche senza splash screen.
 * </p>
 * 
 * <p>
 * Il nome del file è:
 * </p>
 * 
 * <pre>
 * res / raw / argonSettings.xml
 * </pre>
 * <p>
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class GameActivity extends BaseGameActivity {

	/**
	 * <p>banner pubblicitario</p>
	 */
	private AdView adView;

	/**
	 * <p>parent view</p>
	 */
	private FrameLayout parentView;

	/**
	 * <p>
	 * Imposta il glSurfaceView nel quale viene renderizzato il context opengl
	 * </p>
	 * 
	 * @param view
	 */
	@Override
	public void setArgonGLSurfaceView(ArgonGLView view) {
		// nostro riferimento
		glView = view;

		setContentView(R.layout.argon_game_layout);
		replace(R.id.argon_game_content, view);

		parentView = (FrameLayout) findViewById(R.id.argon_game_container);
	}

	/**
	 * <p>
	 * Dato un id all'interno di un layout, lo sostituisce con una nuova view.
	 * </p>
	 * 
	 * @param oldResourceId
	 * @param newView
	 */
	private void replace(int oldResourceId, View newView) {
		View C = findViewById(oldResourceId);
		ViewGroup parent = (ViewGroup) C.getParent();

		int index = parent.indexOfChild(C);
		parent.removeView(C);

		parent.addView(newView, index);
	}

	/**
	 * <p>
	 * Visualizza il banner.
	 * </p>
	 */
	public void showAd() {

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showAdview = true;

				if (adView != null && parentView.indexOfChild(adView) > -1) {
					ElioLogger.warn("AdView is already present!");
					return;
				}

				adView = new AdView(GameActivity.this); // context) (AdView)
														// findViewById(R.id.argon_game_admob);
				adView.setAdSize(AdSize.SMART_BANNER);
				adView.setAdUnitId(ResourceUtility.resolveString(GameActivity.this, R.string.admob_editor_id));
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
				lp.gravity = Gravity.BOTTOM;
				adView.setBackgroundColor(Color.TRANSPARENT);
				adView.setLayoutParams(lp);
				adView.loadAd(new AdRequest.Builder().build());
				if (adView != null) {

					adView.setAdListener(new AdListener() {
						private boolean firstTime = true;

						@Override
						public void onAdLoaded() {
							if (firstTime) {
								firstTime = false;
								if (showAdview) {
									// lo aggiungiamo solo se siamo in uno stato
									// di display
									parentView.addView(adView);
								}
							}
						}
					});
				}

			}
		});
	}

	/**
	 * indica se visualizzare o meno l'adview
	 */
	public boolean showAdview;

	/**
	 * <p>
	 * Rimuove il banner se presente
	 * </p>
	 */
	public void hideAd() {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				showAdview = false;

				if (adView != null && parentView.indexOfChild(adView) > -1) {
					adView.pause();
					adView.removeAllViews();
					adView.setVisibility(View.GONE);
					parentView.removeView(adView);
					// adView.loadAd(null);
					adView.destroy();
					adView = null;

					int n = Thread.activeCount();
					// adView.setEnabled(false);
					Thread[] t = new Thread[n];

					Thread.enumerate(t);

					for (int i = 0; i < n; i++) {
						if ("WebViewCoreThread".equals(t[i].getName())) {
							t[i].interrupt();
							ElioLogger.warn("Stop WebViewCoreThread!");
							return;
						}
					}

				} else {

					ElioLogger.warn("Try to remove an adView is not created!");
				}
			}
		});

	}

	@Override
	public void onSignInFailed() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.abubusoft.xenon.game.hub.GameHelper.GameHelperListener#onSignInSucceeded()
	 */
	@Override
	public void onSignInSucceeded() {
		GameHub.instance().setAutoSignIn();
		
		GameApplicationImpl application=ArgonBeanContext.getBean(ArgonBeanType.APPLICATION);
		application.onSignInSucceeded();		
	}



}
