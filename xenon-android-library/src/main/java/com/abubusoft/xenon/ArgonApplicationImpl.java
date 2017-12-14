package com.abubusoft.xenon;

import com.abubusoft.kripton.android.Logger;

import android.app.Activity;
import android.content.Context;

/**
 * @author Francesco Benincasa
 */
public abstract class ArgonApplicationImpl<E extends Argon> implements ArgonApplication<E> {

    /**
     * Argon manager for wallpaper
     */
    public E argon;

    @Override
    public void setArgon(E argonValue) {
        argon = argonValue;
    }

    /* (non-Javadoc)
     * @see com.abubusoft.xenon.ArgonApplication4App#getContext()
     */
    @Override
    public Context getContext() {
        return argon.getContext();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.abubusoft.xenon.ArgonApplication4App#onAfterStartupFirstTime()
     */
    @Override
    public void onAfterStartupFirstTime() {
        Logger.info("onAfterStartupFirstTime");

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.abubusoft.xenon.ArgonApplication4App#onAfterStartupFirstTimeForThisVersion()
     */
    @Override
    public void onAfterStartupFirstTimeForThisVersion() {
        Logger.info("onAfterStartupFirstTimeForThisVersion");
    }

    public void onDestroy(Activity activity) {
        // non faccio niente
    }

}
