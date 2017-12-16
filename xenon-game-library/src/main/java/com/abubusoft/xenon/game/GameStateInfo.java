/*
    Copyright 2008-2009 Fabiano Sarracco

    This file is part of it-fs-fsm.

    it-fs-fsm is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    it-fs-fsm is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with it-fs-fsm.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.abubusoft.xenon.game;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.abubusoft.xenon.android.listener.XenonGestureListenerImpl;

/**
 * Annotation for a state of a finite state machine.
 * 
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface GameStateInfo {

	/**
	 * <p>Chiave dello stato. Viene convertita in lowercase</p> 
	 * 
	 * @return
	 */
	String key();
	
	/**
	 * <p>se true indica che la game state machine parte proprio da questo stato.</p>
	 * @return
	 */
	boolean initialState() default false;

	/**
	 * <p>Gesture listener.</p>
	 * 
	 * @return
	 */
	Class<? extends XenonGestureListenerImpl> gestureListenerClazz();

}