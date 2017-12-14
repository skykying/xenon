package com.abubusoft.xenon.game.hub;

import java.util.ArrayList;

import org.abubu.argon.Argon4OpenGL;
import org.abubu.argon.ArgonBeanContext;
import org.abubu.argon.ArgonBeanType;
import org.abubu.argon.game.GameActivity;
import org.abubu.argon.game.GameConfig;
import org.abubu.argon.game.R;
import org.abubu.argon.game.hub.GameHelper.GameHelperListener;
import org.abubu.elio.logger.ElioLogger;
import org.abubu.elio.util.ResourceUtility;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import android.app.Activity;
import android.content.Intent;

public class GameHub {

	private static final GameHub instance = new GameHub();

	private GameHub() {

	}

	public static GameHub instance() {
		return instance;
	}

	GameHelper helper;

	GameConfig config;

	/**
	 * Call this method from your Activity's onStart().
	 * 
	 * @param activity
	 */
	public void onActivityStart(Activity activity) {
		config = ArgonBeanContext.getBean(ArgonBeanType.CONFIG);

		helper.activity = activity;
		helper.context = activity.getApplicationContext();

		helper.debugLog("onStart");
		helper.assertConfigured("onStart");

		// if (helper.mConnectOnStart) {
		if (config.googleAutoSignin) {
			if (helper.mGoogleApiClient.isConnected()) {
				ElioLogger.warn("GameHelper: client was already connected on onStart()");
			} else {
				helper.debugLog("Connecting client.");
				helper.mConnecting = true;
				helper.mGoogleApiClient.connect();
			}
		} else {
			helper.debugLog("Not attempting to connect becase mConnectOnStart=false");
			helper.debugLog("Instead, reporting a sign-in failure.");
			helper.mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					helper.notifyListener(false);
				}
			}, 1000);
		}
	}

	/**
	 * Call this method from your Activity's onStop().
	 * 
	 */
	public void onActivityStop() {
		helper.debugLog("onStop");
		helper.assertConfigured("onStop");
		if (helper.mGoogleApiClient.isConnected()) {
			helper.debugLog("Disconnecting client due to onStop");
			helper.mGoogleApiClient.disconnect();
		} else {
			helper.debugLog("Client already disconnected when we got onStop.");
		}
		helper.mConnecting = false;
		helper.mExpectingResolution = false;

		// let go of the Activity reference
		helper.activity = null;
	}

	/**
	 * <p>
	 * Effettua il setup e registra il listener. Il tipo di client è quello {@link GameClientType#CLIENT_GAMES}.
	 * </p>
	 * 
	 * @param activity
	 * @param listener
	 *            listener
	 */
	public void onActivityCreate(Activity activity, GameHelperListener listener) {
		onActivityCreate(activity, GameClientType.CLIENT_GAMES, listener);
	}

	/**
	 * <p>
	 * Effettua il setup e registra il listener.
	 * 
	 * @param activity
	 * @param client
	 * @param listener
	 */
	public void onActivityCreate(Activity activity, GameClientType client, GameHelperListener listener) {
		if (helper == null) {
			// requestedClients
			// The requested clients (a combination of CLIENT_GAMES, CLIENT_PLUS and CLIENT_APPSTATE).
			helper = new GameHelper(activity, client.value);
			helper.setup(listener);
		}

	}

	public String getInvitationId() {
		return helper.getInvitationId();
	}

	public void reconnectClient() {

		helper.reconnectClient();
	}

	public boolean hasSignInError() {
		return helper.hasSignInError();
	}

	public GameHelper.SignInFailureReason getSignInError() {
		return helper.getSignInError();
	}

	public GoogleApiClient getApiClient() {
		return helper.getApiClient();
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	public boolean isSignedIn() {
		return helper.isSignedIn();
	}

	/**
	 * <p>
	 * Inizia il processo di login. Da usare alla pressione del tasto di login.
	 * </p>
	 */
	public void signIn() {
		helper.beginUserInitiatedSignIn();
	}

	public void signOut() {
		helper.signOut();
	}

	public void showAlert(String message) {
		if (helper == null) {
			ElioLogger.error("showAlert, no helper found in gamehub");
			return;
		}
		helper.makeSimpleDialog(message).show();
	}

	public void showAlert(String title, String message) {
		if (helper == null) {
			ElioLogger.error("showAlert, no helper found in gamehub");
			return;
		}
		helper.makeSimpleDialog(title, message).show();
	}

	public void onActivityResult(int request, int response, Intent data) {
		helper.onActivityResult(request, response, data);

	}

	/**
	 * <p>
	 * Visualizza la leaderboard.
	 * </p>
	 * 
	 * @param activity
	 */
	public void showLeaderboard(GameActivity activity) {
		GameActivity act = activity;

		if (helper.getApiClient().isConnected()) {
			act.startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(helper.getApiClient()), GameHelper.RC_UNUSED);
		} else {
			showAlert(activity.getString(R.string.game_leaderboards_not_available));
		}
	}

	/**
	 * <p>
	 * Visualizza la leaderboard selezionata mediante il suo uid
	 * </p>
	 * 
	 * @param activity
	 * @param leaderBoardResourceId
	 */
	public void showLeaderboard(GameActivity activity, int leaderBoardResourceId) {
		GameActivity act = activity;

		if (helper.getApiClient().isConnected()) {

			String leaderBoardUid = activity.getString(leaderBoardResourceId);
			act.startActivityForResult(Games.Leaderboards.getLeaderboardIntent(helper.getApiClient(), leaderBoardUid), GameHelper.RC_UNUSED);
		} else {
			showAlert(activity.getString(R.string.game_leaderboards_not_available));
		}
	}

	String getString(int resourceId) {
		return helper.activity.getString(resourceId);
	}

	/**
	 * <p>
	 * </p>
	 */
	public void updateAccomplishments() {

		GameAchievementsManager achievementsManager = GameAchievementsManager.instance();
		ArrayList<String> store = new ArrayList<>();

		for (int i = 0; i < achievementsManager.list.size(); i++) {
			final GameAchievement item = achievementsManager.list.get(i);

			if (item.checker == null) {
				ElioLogger.warn("Achievement %s can not be unlocked", getString(item.resourceName));
				continue;
			}

			try {
				boolean alreadyDone = isAchievementAlreadyDone(getString(item.uidResourceId));
				if (item.checker.check(item, config) || alreadyDone) {
					// elemento raggiunto
					if (isSignedIn()) {						
						Games.Achievements.unlock(getApiClient(), getString(item.uidResourceId));
					} else if (!alreadyDone) {
						// Se non siamo collegati non visualizziamo niente.
						// solo se non lo abbiamo già fatto prima.

						// Only show toast if not signed in. If signed in, the standard Google Play
						// toasts will appear, so we don't need to show our own.
						/*helper.activity.runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(helper.activity, getString(item.resourceName) + " " + getString(R.string.game_achievements_unlocked), Toast.LENGTH_LONG).show();
							}
						});*/
						ElioLogger.warn("Achievement %s can not be uploaded");
					}

					// a prescindere lo reimmettiamo
					store.add(getString(item.uidResourceId));
				}
			} catch (Exception e) {
				ElioLogger.warn("Achievement %s (%s) check has an error %s", getString(item.uidResourceId), getString(item.resourceName), e.getMessage());
				e.printStackTrace();
			}
		}

		config.achievements = store.toArray(new String[0]);

	}

	/**
	 * <p>
	 * Indica se l'obiettivo è stato già raggiunto
	 * </p>
	 * 
	 * @param uid
	 * @return
	 */
	private boolean isAchievementAlreadyDone(String uid) {
		if (config.achievements == null)
			return false;
		for (int i = 0; i < config.achievements.length; i++) {
			if (uid.equals(config.achievements[i])) {
				return true;
			}
		}

		return false;

	}

	/**
	 * Check for achievements and unlock the appropriate ones.
	 * 
	 * @param requestedScore
	 *            the score the user requested.
	 * @param finalScore
	 *            the score the user got.
	 * 
	 *            void checkForAchievements(int requestedScore, int finalScore) { // Check if each condition is met; if so, unlock the corresponding // achievement. if
	 *            (isPrime(finalScore)) { mOutbox.mPrimeAchievement = true; achievementToast(getString(R.string.achievement_prime_toast_text)); } if (requestedScore == 9999) {
	 *            mOutbox.mArrogantAchievement = true; achievementToast(getString(R.string.achievement_arrogant_toast_text)); } if (requestedScore == 0) {
	 *            mOutbox.mHumbleAchievement = true; achievementToast(getString(R.string.achievement_humble_toast_text)); } if (finalScore == 1337) { mOutbox.mLeetAchievement =
	 *            true; achievementToast(getString(R.string.achievement_leet_toast_text)); } mOutbox.mBoredSteps++; }
	 */

	/*
	 * void achievementToast(String achievement) { // Only show toast if not signed in. If signed in, the standard Google Play // toasts will appear, so we don't need to show our
	 * own. if (!isSignedIn()) { Toast.makeText(this, getString(R.string.achievement) + ": " + achievement, Toast.LENGTH_LONG).show(); } }
	 */

	/**
	 * <p>
	 * </p>
	 * 
	 * @param finalScore
	 */
	public void updateScore(int leaderBoardId, int finalScore) {
		config.score = Math.max(config.score, finalScore);

		if (helper.isSignedIn()) {
			// prendiamo l'applicationContext
			Argon4OpenGL argon = ArgonBeanContext.getBean(ArgonBeanType.ARGON);
			Games.Leaderboards.submitScore(helper.getApiClient(), ResourceUtility.resolveString(argon.getContext(), leaderBoardId), config.score);
		} else {
			ElioLogger.warn("Can not save on cloud");
		}

	}

	/**
	 * <p>
	 * Imposta il login allo startup e lo memorizza.
	 * </p>
	 */
	public void setAutoSignIn() {
		config.googleAutoSignin = true;
		config.save();
	}

	/**
	 * <p>
	 * Visualizza i risultati.
	 * </p>
	 * 
	 * @param activity
	 */
	public void showAchiviements(GameActivity activity) {
		if (helper.isSignedIn()) {
			activity.startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), GameHelper.RC_UNUSED);
		} else {
			showAlert(activity.getString(R.string.game_achievements_not_available));
		}

	}
}
