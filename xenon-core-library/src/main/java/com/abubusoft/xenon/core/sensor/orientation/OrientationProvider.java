/**
 * 
 */
package com.abubusoft.xenon.core.sensor.orientation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.abubusoft.xenon.core.ElioRuntimeException;
import com.abubusoft.xenon.core.sensor.ElioSensorManager;
import com.abubusoft.xenon.core.sensor.OrientationInputConfig;
import com.abubusoft.xenon.core.sensor.OrientationInputConfig.EventType;
import com.abubusoft.xenon.core.sensor.OrientationInputListener;
import com.abubusoft.xenon.core.sensor.internal.AttachedSensors;
import com.abubusoft.xenon.core.sensor.internal.InputSensorProvider;
import com.abubusoft.xenon.core.sensor.orientation.math.EulerAngles;
import com.abubusoft.xenon.core.sensor.orientation.math.Matrixf4x4;
import com.abubusoft.xenon.core.sensor.orientation.math.Quaternion;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Pair;

/**
 * Classes implementing this interface provide an orientation of the device
 * either by directly accessing hardware, using Android sensor fusion or fusing
 * sensors itself.
 * 
 * The orientation can be provided as rotation matrix or quaternion.
 * 
 * @author Alexander Pacha
 * 
 */
public abstract class OrientationProvider implements InputSensorProvider {
	
	/**
	 * elenco degli id dei sensori associati
	 * 
	 */
	protected HashSet<Integer> attachedTypeSensors=new HashSet<Integer>();
	
	/**
     * The quaternion that holds the current rotation
     */
    protected final Quaternion currentOrientationQuaternion;

	/**
     * The matrix that holds the current rotation
     */
    protected final Matrixf4x4 currentOrientationRotationMatrix;
	
    protected ArrayList<Pair<OrientationInputConfig, OrientationInputListener>> listeners = new ArrayList<Pair<OrientationInputConfig, OrientationInputListener>>();

    /**
     * The list of sensors used by this provider
     */
    protected List<Sensor> sensorList = new ArrayList<Sensor>();

    /**
     * Sync-token for syncing read/write to sensor-data from sensor manager and
     * fusion algorithm
     */
    protected final Object syncToken = new Object();

    /**
     * Initialises a new OrientationProvider
     * 
     */
    public OrientationProvider() {
        // Initialise with identity
        currentOrientationRotationMatrix = new Matrixf4x4();

        // Initialise with identity
        currentOrientationQuaternion = new Quaternion();
        
        AttachedSensors annotations=this.getClass().getAnnotation(AttachedSensors.class);
		
		if (annotations==null || annotations.value().length==0)
		{
			throw(new ElioRuntimeException("No sensors is defined for detector "+getClass()));
		}
		int[] sensors=annotations.value();
		for (int i=0; i<sensors.length;i++ )
		{
			attachedTypeSensors.add(sensors[i]);
		}		
    }

    /**
	 * aggiunta listener per i sensori di orientamento
	 * 
	 * @param configValue
	 * @param listenerValue
	 */
	public void addListener(OrientationInputConfig configValue, OrientationInputListener listenerValue) {
		listeners.add(new Pair<OrientationInputConfig, OrientationInputListener>(configValue, listenerValue));
		n=listeners.size();
	}
    
	public void clearListeners() {
		listeners.clear();
		n=0;
	} 
	
	/* (non-Javadoc)
	 * @see com.abubusoft.xenon.core.sensor.internal.InputSensorDetector#getAttachedTypeofSensors()
	 */
	public HashSet<Integer> getAttachedTypeofSensors()
	{
		return attachedTypeSensors;
	}

    
    /**
     * @return Returns the current rotation of the device in the Euler-Angles
     */
    public EulerAngles getEulerAngles() {
        synchronized (syncToken) {

            float[] angles = new float[3];
            SensorManager.getOrientation(currentOrientationRotationMatrix.matrix, angles);
            return new EulerAngles(angles[0], angles[1], angles[2]);
        }
    }
    
    /**
     * @return Returns the current rotation of the device in the quaternion
     *         format (vector4f)
     */
    public Quaternion getQuaternion() {
        synchronized (syncToken) {
            return currentOrientationQuaternion.clone();
        }
    }

    /**
     * @return Returns the current rotation of the device in the rotation matrix
     *         format (4x4 matrix)
     */
    public Matrixf4x4 getRotationMatrix() {
        synchronized (syncToken) {
            return currentOrientationRotationMatrix;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not doing anything
    }

    /**
     * Starts the sensor fusion (e.g. when resuming the activity)
     */
    public void start() {
    	SensorManager sensorManager=ElioSensorManager.instance().sensorManager;
    	
        // enable our sensor when the activity is resumed, ask for
        // 10 ms updates.
        for (Sensor sensor : sensorList) {
            // enable our sensors when the activity is resumed, ask for
            // 20 ms updates (Sensor_delay_game)
            sensorManager.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_GAME);
        }
    }
    
	protected double currentAzimuth;

	protected double currentPitch;

	protected double currentRoll;
	
	int i;
	
	int n;
	
	double tempResult[]=new double[3];

	private Pair<OrientationInputConfig, OrientationInputListener> item;

	private OrientationInputConfig currentConfig;

	private OrientationInputListener currentListener;

    
    /**
	 * aggiorna i listener
	 */
	public void update() {
		
		if (n==0) return;
		
		currentOrientationQuaternion.toEulerAngles(tempResult);		
	
		currentAzimuth = Math.toDegrees(tempResult[0]);
		currentPitch = Math.toDegrees(tempResult[1]);
		currentRoll = Math.toDegrees(tempResult[2]);

		// ElioLogger.info("onSensorChanged update VALUES %.3f %.3f %.3f",initialAzimuth, initialPitch, initialRoll);
		for (i = 0; i < n; i++) {
			item = listeners.get(i);
			currentConfig = item.first;
			currentListener = item.second;

			currentConfig.currentAzimuth = currentAzimuth;
			currentConfig.currentPitch = currentPitch;
			currentConfig.currentRoll = currentRoll;
			// ElioLogger.info("onSensorChanged update %s",i);

			boolean somethingIsChanged;

			somethingIsChanged = (Math.abs(currentConfig.currentAzimuth - currentConfig.oldAzimuth) > currentConfig.noiseNearZero) || (Math.abs(currentConfig.currentRoll - currentConfig.oldRoll) > currentConfig.noiseNearZero)
					|| (Math.abs(currentConfig.currentPitch - currentConfig.oldPitch) > currentConfig.noiseNearZero);

			// notifichiamo evento se dobbiamo farlo sempre o se la notifica è
			// abilitata per i cambiamenti e qualcosa
			// effettivamente è cambiato
			if (currentConfig.event == EventType.NOTIFY_ALWAYS || (somethingIsChanged && currentConfig.event == EventType.NOTIFY_CHANGES)) {
				currentListener.update(currentConfig.currentAzimuth, currentConfig.currentPitch, currentConfig.currentRoll, currentConfig.currentAzimuth - currentConfig.oldAzimuth, currentConfig.currentRoll - currentConfig.oldRoll, currentConfig.currentPitch - currentConfig.oldPitch, somethingIsChanged);

				// se i nuovi valori sono stati registrati allora registriamo anche gli old value
				currentConfig.oldAzimuth = currentConfig.currentAzimuth;
				currentConfig.oldRoll = currentConfig.currentRoll;
				currentConfig.oldPitch = currentConfig.currentPitch;
			}
		}

	}

    /**
     * Stops the sensor fusion (e.g. when pausing/suspending the activity)
     */
    public void stop() {
    	SensorManager sensorManager=ElioSensorManager.instance().sensorManager;
    	
        // make sure to turn our sensors off when the activity is paused
        for (Sensor sensor : sensorList) {
            sensorManager.unregisterListener(this, sensor);
        }
    }
}
