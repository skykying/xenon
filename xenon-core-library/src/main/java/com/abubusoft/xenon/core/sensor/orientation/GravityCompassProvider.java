package com.abubusoft.xenon.core.sensor.orientation;

import com.abubusoft.xenon.core.sensor.internal.AttachedSensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * The orientation provider that delivers the current orientation from the {@link Sensor#TYPE_GRAVITY Gravity} and {@link Sensor#TYPE_MAGNETIC_FIELD Compass}.
 * 
 * @author Alexander Pacha
 * 
 */
@AttachedSensors({ Sensor.TYPE_GRAVITY, Sensor.TYPE_MAGNETIC_FIELD })
public class GravityCompassProvider extends OrientationProvider {
	
    static GravityCompassProvider instance;

    public static GravityCompassProvider instance()
    {
        if (instance==null)
        {
            instance=new GravityCompassProvider();
        }

        return instance;
    }

	/**
	 * Compass values
	 */
	private float[] magnitudeValues = new float[3];

	/**
	 * Gravity values
	 */
	private float[] gravityValues = new float[3];

	@Override
	public void onSensorChanged(SensorEvent event) {

		// we received a sensor event. it is a good practice to check
		// that we received the proper event
		switch (event.sensor.getType()) {
		case Sensor.TYPE_MAGNETIC_FIELD:
			magnitudeValues = event.values.clone();
			break;
		case Sensor.TYPE_GRAVITY:
			gravityValues = event.values.clone();
			break;
		}

		if (magnitudeValues != null && gravityValues != null) {
			float[] i = new float[16];

			// Fuse gravity-sensor (virtual sensor) with compass
			SensorManager.getRotationMatrix(currentOrientationRotationMatrix.matrix, i, gravityValues, magnitudeValues);
			// Transform rotation matrix to quaternion
			currentOrientationQuaternion.setRowMajor(currentOrientationRotationMatrix.matrix);
		}

		update();
	}
}
