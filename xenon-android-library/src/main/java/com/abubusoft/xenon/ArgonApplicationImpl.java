package com.abubusoft.xenon;

import org.abubu.elio.logger.ElioLogger;
import org.abubu.elio.logger.ElioLoggerLevelType;

import android.app.Activity;
import android.content.Context;

/**
 * 
 * @author Francesco Benincasa
 * 
 */
public abstract class ArgonApplicationImpl<E extends Argon> implements ArgonApplication<E> {

	/**
	 * Argon manager for wallpaper
	 */
	public E argon;
	
	@Override
	public void setArgon(E argonValue) {
		argon=argonValue;
	}
	
	/* (non-Javadoc)
	 * @see org.abubu.argon.ArgonApplication4App#getContext()
	 */
	@Override
	public Context getContext()
	{
		return argon.getContext();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.abubu.argon.ArgonApplication4App#onAfterStartupFirstTime()
	 */
	@Override
	public void onAfterStartupFirstTime() {
		if (ElioLogger.isEnabledFor(ElioLoggerLevelType.INFO))
		{
			ElioLogger.info("onAfterStartupFirstTime");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.abubu.argon.ArgonApplication4App#onAfterStartupFirstTimeForThisVersion()
	 */
	@Override
	public void onAfterStartupFirstTimeForThisVersion() {
		if (ElioLogger.isEnabledFor(ElioLoggerLevelType.INFO))
		{
			ElioLogger.info("onAfterStartupFirstTimeForThisVersion");
		}

	}

	public void onDestroy(Activity activity) {
		// non faccio niente
	}

}
