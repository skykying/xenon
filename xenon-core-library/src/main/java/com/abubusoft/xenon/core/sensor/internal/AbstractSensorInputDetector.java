/**
 * 
 */
package com.abubusoft.xenon.core.sensor.internal;

import java.util.HashSet;

import com.abubusoft.xenon.core.XenonRuntimeException;

import android.hardware.SensorEventListener;

/**
 * <p>Classe astratta per la gestione dei sensori. Questa classe deve gestire sia i sensori hardware
 * lato android, sia i listener lato Elio, in modo da ottenere dei listener sulle informazioni che
 * in effetti servono.</p>
 * 
 * @author Francesco Benincasa
 *
 */
public abstract class AbstractSensorInputDetector implements SensorEventListener, InputSensorProvider {
	
	protected AbstractSensorInputDetector()
	{
		AttachedSensors annotations=this.getClass().getAnnotation(AttachedSensors.class);
		
		if (annotations==null || annotations.value().length==0)
		{
			throw(new XenonRuntimeException("No sensors is defined for detector "+getClass()));
		}
		int[] sensors=annotations.value();
		for (int i=0; i<sensors.length;i++ )
		{
			attachedTypeSensors.add(sensors[i]);
		}		
	}
	
	/**
	 * elenco degli id dei sensori associati
	 * 
	 */
	protected HashSet<Integer> attachedTypeSensors=new HashSet<Integer>(); 
	
	/* (non-Javadoc)
	 * @see com.abubusoft.xenon.core.sensor.internal.InputSensorDetector#getAttachedTypeofSensors()
	 */
	public HashSet<Integer> getAttachedTypeofSensors()
	{
		return attachedTypeSensors;
	}
	
	/**
	 * Se true indica che il detector è interessato al tipo di sensore.
	 * 
	 * @param sensorType
	 * 
	 * @return
	 * 		true se l'evento è interessante per questo detector
	 */
	public boolean isInterestIn(int sensorType)
	{
		return attachedTypeSensors.contains(sensorType);
		
	}

}
