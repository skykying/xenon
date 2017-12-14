/*
 * Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.abubusoft.xenon.game;

import com.abubusoft.xenon.android.ArgonActivity4OpenGL;
import com.abubusoft.xenon.game.hub.GameHelper;
import com.abubusoft.xenon.game.hub.GameHub;

import android.content.Intent;
import android.os.Bundle;

/**
 * Example base class for games. This implementation takes care of setting up the API client object and managing its lifecycle. Subclasses only need to override the
 * @link{#onSignInSucceeded} and @link{#onSignInFailed} abstract methods. To initiate the sign-in flow when the user clicks the sign-in button, subclasses should call
 * @link{#beginUserInitiatedSignIn}. By default, this class only instantiates the GoogleApiClient object. If the PlusClient or AppStateClient objects are also wanted, call the
 * BaseGameActivity(int) constructor and specify the requested clients. For example, to request PlusClient and GamesClient, use BaseGameActivity(CLIENT_GAMES | CLIENT_PLUS). To
 * request all available clients, use BaseGameActivity(CLIENT_ALL). Alternatively, you can also specify the requested clients via
 * 
 * @link{#setRequestedClients , but you must do so before @link{#onCreate} gets called, otherwise the call will have no effect. ff
 * @author Bruno Oliveira (Google)
 */
public abstract class BaseGameActivity extends ArgonActivity4OpenGL implements GameHelper.GameHelperListener {
	
	// request codes we use when invoking an external activity
    //public static final int RC_RESOLVE = 5000, RC_UNUSED = 5001;

	// The game helper object. This class is mainly a wrapper around this object.
	//public GameHelper gameHelper;
	
	// Requested clients. By default, that's just the games client.
	//protected int mRequestedClients = CLIENT_GAMES;

	//protected boolean mDebugLog = false;

	/** Constructs a BaseGameActivity with default client (GamesClient). */
	protected BaseGameActivity() {
		super();
	}

	/**
	 * Sets the requested clients. The preferred way to set the requested clients is via the constructor, but this method is available if for some reason your code cannot do this
	 * in the constructor. This must be called before onCreate or getGameHelper() in order to have any effect. If called after onCreate()/getGameHelper(), this method is a no-op.
	 * 
	 * @param requestedClients
	 *            A combination of the flags CLIENT_GAMES, CLIENT_PLUS and CLIENT_APPSTATE, or CLIENT_ALL to request all available clients.
	 */
	/*protected void setRequestedClients(int requestedClients) {
		mRequestedClients = requestedClients;
	}*/


	/* (non-Javadoc)
	 * @see com.abubusoft.xenon.android.ArgonActivity4OpenGL#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		
		GameHub.instance().onActivityCreate(this, this);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		GameHub.instance().onActivityStart(this);
	}

	/* (non-Javadoc)
	 * @see com.abubusoft.xenon.android.ArgonActivity4OpenGL#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		GameHub.instance().onActivityStop();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int request, int response, Intent data) {
		super.onActivityResult(request, response, data);
		GameHub.instance().onActivityResult(request, response, data);
	}

}
