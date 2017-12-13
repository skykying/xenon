package com.abubusoft.xenon.core.sensor.compass;

import java.util.HashSet;

import com.abubusoft.xenon.core.ElioRuntimeException;
import com.abubusoft.xenon.core.sensor.OrientationInputConfig;
import com.abubusoft.xenon.core.sensor.OrientationInputConfig.EventType;
import com.abubusoft.xenon.core.sensor.OrientationInputListener;
import com.abubusoft.xenon.core.sensor.internal.AttachedSensors;
import com.abubusoft.xenon.core.sensor.internal.InputSensorProvider;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.view.Display;

@AttachedSensors({ Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_MAGNETIC_FIELD })
public class CompassProvider implements InputSensorProvider {
	
	static CompassProvider instance;

	/**
	 * elenco degli id dei sensori associati
	 * 
	 */
	protected HashSet<Integer> attachedTypeSensors = new HashSet<Integer>();

	public static CompassProvider instance() {
		if (instance == null) {
			instance = new CompassProvider();
		}

		return instance;
	}

	private float[] mGravity = new float[3];
	private float[] mGeomagnetic = new float[3];
	private float azimuth = 0f;

	private float pitch;

	private float roll;

	private Display display;

	public CompassProvider() {
		AttachedSensors annotations = this.getClass().getAnnotation(AttachedSensors.class);

		if (annotations == null || annotations.value().length == 0) {
			throw (new ElioRuntimeException("No sensors is defined for detector " + getClass()));
		}
		int[] sensors = annotations.value();
		for (int i = 0; i < sensors.length; i++) {
			attachedTypeSensors.add(sensors[i]);
		}
		
	}
		
	private static final float ALPHA = 0.2f;
	
	protected void lowPass( float[] input, float[] output) {	        
	    for ( int i=0; i<input.length; i++ ) {	        
	        output[i] = output[i] + ALPHA * (input[i]-output[i]);
	    }	    
	}
	
	/**
	 * Recupera la rotazione del display
	 * @return
	 */
	int getDisplayRotation() {
		int result = 1;
		try {				
				result = display.getRotation();				
		} catch (Exception ex) {
			//ex.printStackTrace();
		}
		return result;
	}
	
	AngleLowpassFilter averageHeading=new AngleLowpassFilter();
	
	AngleLowpassFilter averagePitch=new AngleLowpassFilter();
	
	AngleLowpassFilter averageRoll=new AngleLowpassFilter();
	
	float orientation[] = new float[3];
	boolean found1;
	boolean found2;
	float R[] = new float[9];
	float I[] = new float[9];
	
	long currentTime;
	long lastTime;

	private OrientationInputConfig config;

	private OrientationInputListener listener;

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			found1=true;
			lowPass(event.values.clone(), mGravity);			
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			found2=true;
			lowPass(event.values.clone(), mGeomagnetic);
			break;
		}
		
		if (found1!=found2) return;
		
		boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
		
		if (success ) {
			found1=false;
			found2=false;
			//ElioLogger.info("Eseguo per %s",event.sensor.getType());
			SensorManager.getOrientation(R, orientation);
			// Log.d(TAG, "azimuth (rad): " + azimuth);
			averageHeading.add(orientation[0]);
			averagePitch.add(orientation[1]);
			averageRoll.add(orientation[2]);
			
			// se non è trascorso il tempo minimo (il delta) andiamo avanti.
			currentTime=System.currentTimeMillis();
			if (currentTime-lastTime<config.deltaTime) return;	
			lastTime=currentTime;
			
			azimuth = (float) Math.toDegrees(averageHeading.average()); // orientation
			azimuth = (azimuth + 360) % 360;					
			pitch = (float) Math.toDegrees(averagePitch.average()); // orientation		
			roll = (float) Math.toDegrees(averageRoll.average()); // orientation
			
			update(azimuth, pitch, roll);
		}

	}
	

	
    /**
	 * aggiunta listener per i sensori di orientamento
	 * 
	 * @param configValue
	 * @param listenerValue
	 */
	public void setListener(OrientationInputConfig configValue, OrientationInputListener listenerValue) {
		config=configValue;
		listener=listenerValue;		
	}
    
	public void clearListener() {
		listener=null;
	} 
	
    /**
	 * aggiorna i listener
	 * 
     * @param currentAzimuth 
     * @param currentPitch 
     * @param currentRoll 
	 */
	public void update(float currentAzimuth, float currentPitch, float currentRoll) {
		if (listener==null) return;				
	

			config.currentAzimuth = currentAzimuth;
			config.currentPitch = currentPitch;
			config.currentRoll = currentRoll;
			// ElioLogger.info("onSensorChanged update %s",i);

			boolean somethingIsChanged;

			somethingIsChanged = (Math.abs(config.currentAzimuth - config.oldAzimuth) > config.noiseNearZero) || (Math.abs(config.currentRoll - config.oldRoll) > config.noiseNearZero)
					|| (Math.abs(config.currentPitch - config.oldPitch) > config.noiseNearZero);

			// notifichiamo evento se dobbiamo farlo sempre o se la notifica è
			// abilitata per i cambiamenti e qualcosa
			// effettivamente è cambiato
			if (config.event == EventType.NOTIFY_ALWAYS || (somethingIsChanged && config.event == EventType.NOTIFY_CHANGES)) {
				listener.update(config.currentAzimuth, config.currentPitch, config.currentRoll, config.currentAzimuth - config.oldAzimuth, config.currentRoll - config.oldRoll, config.currentPitch - config.oldPitch, somethingIsChanged);
			
				config.oldAzimuth = config.currentAzimuth;
				config.oldRoll = config.currentRoll;
				config.oldPitch = config.currentPitch;
			}
			
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public HashSet<Integer> getAttachedTypeofSensors() {
		return attachedTypeSensors;
	}
}