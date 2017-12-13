package com.abubusoft.xenon.core.sensor.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotazione che consente di associare un particolare set di sensori ad un particolare SensorDetector.
 * 
 * @author xcesco
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface AttachedSensors {
	int[] value();
}
