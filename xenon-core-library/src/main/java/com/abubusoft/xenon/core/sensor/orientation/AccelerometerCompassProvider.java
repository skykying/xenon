package com.abubusoft.xenon.core.sensor.orientation;

import com.abubusoft.xenon.core.sensor.internal.AttachedSensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * The orientation provider that delivers the current orientation from the {@link Sensor#TYPE_ACCELEROMETER
 * Accelerometer} and {@link Sensor#TYPE_MAGNETIC_FIELD Compass}.
 * 
 * @author Alexander Pacha
 * 
 */
@AttachedSensors({Sensor.TYPE_ACCELEROMETER,Sensor.TYPE_MAGNETIC_FIELD})
public class AccelerometerCompassProvider extends OrientationProvider {

    /**
     * Compass values
     */
    private float[] magnitudeValues = new float[3];

    /**
     * Accelerometer values
     */
    private float[] accelerometerValues = new float[3];
    
    static AccelerometerCompassProvider instance;

    public static AccelerometerCompassProvider instance()
    {
        if (instance==null)
        {
            instance=new AccelerometerCompassProvider();
        }

        return instance;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // we received a sensor event. it is a good practice to check
        // that we received the proper event
    	switch(event.sensor.getType())
    	{
    	case Sensor.TYPE_MAGNETIC_FIELD:
    		 magnitudeValues = event.values.clone();
    		break;
    	case Sensor.TYPE_ACCELEROMETER:
    		accelerometerValues = event.values.clone();
    		break;
    	}
        
        if (magnitudeValues != null && accelerometerValues != null) {
            float[] i = new float[16];

            // Fuse accelerometer with compass
            SensorManager.getRotationMatrix(currentOrientationRotationMatrix.matrix, i, accelerometerValues,
                    magnitudeValues);
            // Transform rotation matrix to quaternion
            currentOrientationQuaternion.setRowMajor(currentOrientationRotationMatrix.matrix);
        }
        
        update();
    }
}
