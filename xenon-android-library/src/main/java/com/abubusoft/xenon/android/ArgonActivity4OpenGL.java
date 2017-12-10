/**
 *
 */
package com.abubusoft.xenon.android;

import com.abubusoft.kripton.android.Logger;
import com.abubusoft.xenon.Argon4OpenGL;
import com.abubusoft.xenon.ArgonBeanContext;
import com.abubusoft.xenon.ArgonBeanType;
import com.abubusoft.xenon.R;
import com.abubusoft.xenon.android.listener.ArgonGestureDetector;
import com.abubusoft.xenon.android.surfaceview16.ArgonGLView;
import com.abubusoft.xenon.opengl.ArgonGLDefaultRenderer;
import com.abubusoft.xenon.opengl.ArgonGLRenderer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

/**
 * <p>
 * Activity di base per la gestione dei game e dei wallpaper. Se non viene specifiato altro, questa classe viene visualizzata dopo lo splash screen in caso di game.
 * </p>
 * <p>
 * <p>
 * Può essere usata direttamente anche senza splash screen.
 * </p>
 * <p>
 * <p>
 * Il nome del file è:
 * </p>
 * <p>
 * <pre>
 * res / raw / argonSettings.xml
 * </pre>
 * <p>
 * </p>
 *
 * @author Francesco Benincasa
 */
public class ArgonActivity4OpenGL extends Activity {

    public ArgonGestureDetector gestureDetector;

    /**
     * Consente di effettuare il build di un renderer
     *
     * @return istanza di render da usare
     */
    public ArgonGLRenderer createRenderer() {
        return new ArgonGLDefaultRenderer();
    }

    /**
     * context argon
     */
    public Argon4OpenGL argon;

    /**
     * view utilizzata per il rendering
     */
    protected ArgonGLView glView;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // recuperiamo argon dal contesto
            argon = (Argon4OpenGL) ArgonBeanContext.getBean(ArgonBeanType.ARGON);
            argon.onActivityCreated(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * Imposta il glSurfaceView nel quale viene renderizzato il context opengl
     * </p>
     *
     * @param view
     */
    public void setArgonGLSurfaceView(ArgonGLView view) {
        // nostro riferimento
        glView = view;

        setContentView(view);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        argon.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.argon_activity, menu);

        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /*if (item.getItemId() == R.id.menu_settings) {
            startActivity(new Intent(this, ArgonSettingsActivity.class));
            return true;
        }*/

        return (super.onOptionsItemSelected(item));
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();


        Logger.info("ArgonActivity4OpenGL - onResume");

        glView.onResume();

        argon.onResume(this);

        // ARGON-39, impostiamo il flag surfaceCreated
        // la superficie non è ancora pronta
        //argon.surfaceReady = false;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();

        glView.onPause();

        Logger.debug("ArgonActivity4OpenGL - onPause");


        argon.onPause(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // come da
        // http://stackoverflow.com/questions/15309743/use-scalegesturedetector-with-gesturedetector
        if (argon.isSceneReady()) {
            return gestureDetector.onTouchEvent(event);
        } else {
            Logger.debug("onTouchEvent but surface is not ready");
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

}
