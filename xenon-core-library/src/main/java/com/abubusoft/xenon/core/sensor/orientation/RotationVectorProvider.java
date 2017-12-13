package com.abubusoft.xenon.core.sensor.orientation;

import com.abubusoft.xenon.core.sensor.internal.AttachedSensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * The orientation provider that delivers the current orientation from the {@link Sensor#TYPE_ROTATION_VECTOR Android
 * Rotation Vector sensor}.
 * 
 * @author Alexander Pacha
 * 
 */
@AttachedSensors({ Sensor.TYPE_ROTATION_VECTOR })
public class RotationVectorProvider extends OrientationProvider {

    static RotationVectorProvider instance;

    public static RotationVectorProvider instance()
    {
        if (instance==null)
        {
            instance=new RotationVectorProvider();
        }

        return instance;
    }
    
    @Override
    public void onSensorChanged(SensorEvent event) {
        // we received a sensor event. it is a good practice to check
        // that we received the proper event
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // convert the rotation-vector to a 4x4 matrix. the matrix
            // is interpreted by Open GL as the inverse of the
            // rotation-vector, which is what we want.
            SensorManager.getRotationMatrixFromVector(currentOrientationRotationMatrix.matrix, event.values);

            // Get Quaternion
            float[] q = new float[4];
            // Calculate angle. Starting with API_18, Android will provide this value as event.values[3], but if not, we have to calculate it manually.
            SensorManager.getQuaternionFromVector(q, event.values);
            currentOrientationQuaternion.setXYZW(q[1], q[2], q[3], -q[0]);
        }
        
        update();
    }
}
