package com.abubusoft.xenon.core.sensor.internal;

import java.util.ArrayList;

import com.abubusoft.xenon.core.sensor.ShakeInputConfig;
import com.abubusoft.xenon.core.sensor.ShakeInputListener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Pair;

@AttachedSensors(Sensor.TYPE_ACCELEROMETER)
public class ShakeInputDetector extends AbstractSensorInputDetector {

	/**
	 * Arrays to store gravity
	 */
	private float[] gravity = { 0.0f, 0.0f, 0.0f };
	/**
	 * linear acceleration values
	 */
	private float[] linearAcceleration = { 0.0f, 0.0f, 0.0f };

	// Indexes for x, y, and z values
	private static final int X = 0;
	private static final int Y = 1;
	private static final int Z = 2;

	int i;
	int n;
	ShakeInputConfig config;
	ShakeInputListener listener;

	@Override
	public void onSensorChanged(SensorEvent event) {
		// This method will be called event the accelerometer detects a change.

		// Call a helper method that wraps code from the Android developer site
		setCurrentAcceleration(event);

		// Get the max linear acceleration in any direction
		float maxLinearAcceleration = getMaxCurrentLinearAcceleration();

		for (i = 0; i < n; i++) {
			config = listeners.get(i).first;
			listener = listeners.get(i).second;
			// Check if the acceleration is greater than our minimum threshold
			if (maxLinearAcceleration > config.minShakeAcceleration) {
				long now = System.currentTimeMillis();

				// Set the startTime if it was reset to zero
				if (config.startTime == 0) {
					config.startTime = now;
				}

				long elapsedTime = now - config.startTime;

				// Check if we're still in the shake window we defined
				if (elapsedTime > config.maxShakeDuration) {
					// Too much time has passed. Start over!
					resetShakeDetection(config);
				} else {
					// Keep track of all the movements
					config.moveCount++;

					// Check if enough movements have been made to qualify as a
					// shake
					if (config.moveCount > config.minMovements) {
						// It's a shake! Notify the listener.
						listener.onShake();

						// Reset for the next one!
						resetShakeDetection(config);
					}
				}
			}
		}

	}

	private void setCurrentAcceleration(SensorEvent event) {
		/*
		 * BEGIN SECTION from Android developer site. This code accounts for
		 * gravity using a high-pass filter
		 */

		// alpha is calculated as t / (t + dT)
		// with t, the low-pass filter's time-constant
		// and dT, the event delivery rate

		final float alpha = 0.8f;

		// Gravity components of x, y, and z acceleration
		gravity[X] = alpha * gravity[X] + (1 - alpha) * event.values[X];
		gravity[Y] = alpha * gravity[Y] + (1 - alpha) * event.values[Y];
		gravity[Z] = alpha * gravity[Z] + (1 - alpha) * event.values[Z];

		// Linear acceleration along the x, y, and z axes (gravity effects
		// removed)
		linearAcceleration[X] = event.values[X] - gravity[X];
		linearAcceleration[Y] = event.values[Y] - gravity[Y];
		linearAcceleration[Z] = event.values[Z] - gravity[Z];

		/*
		 * END SECTION from Android developer site
		 */
	}

	private float getMaxCurrentLinearAcceleration() {
		// Start by setting the value to the x value
		float maxLinearAcceleration = linearAcceleration[X];

		// Check if the y value is greater
		if (linearAcceleration[Y] > maxLinearAcceleration) {
			maxLinearAcceleration = linearAcceleration[Y];
		}

		// Check if the z value is greater
		if (linearAcceleration[Z] > maxLinearAcceleration) {
			maxLinearAcceleration = linearAcceleration[Z];
		}

		// Return the greatest value
		return maxLinearAcceleration;
	}

	private void resetShakeDetection(ShakeInputConfig config) {
		config.startTime = 0;
		config.moveCount = 0;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public static ShakeInputDetector instance() {
		return instance;
	}

	private ArrayList<Pair<ShakeInputConfig, ShakeInputListener>> listeners = new ArrayList<Pair<ShakeInputConfig, ShakeInputListener>>();

	private static final ShakeInputDetector instance = new ShakeInputDetector();
	
	public void clearListeners()
	{
		listeners.clear();
	}

	public void addListener(ShakeInputConfig configValue, ShakeInputListener listenerValue) {
		listeners.add(new Pair<ShakeInputConfig, ShakeInputListener>(configValue, listenerValue));
		n = listeners.size();

		resetShakeDetection(configValue);
	}
}