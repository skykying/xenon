package com.abubusoft.xenon.core.sensor;

import com.abubusoft.xenon.core.sensor.internal.InputListener;

public interface CompassInputListener extends InputListener {

	void update(double heading, double pitch, double roll, double deltaHeading, double deltaPitch, double deltaRoll, boolean somethingIsChanged);
}
