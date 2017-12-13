package com.abubusoft.xenon.core.sensor;

public class ElioSensorConfig {

	/**
	 * delay con il quale rilevare il cambiamento dei sensori.
	 */
	public ElioSensorDelayType delay;

	/**
	 * Configurazione di default dell'input:
	 * 
	 * <ul>
	 * <li>delay = DELAY_GAME</li>
	 * </ul>
	 * 
	 * @return
	 * 		istanza di default della configurazione
	 */
	public static ElioSensorConfig build() {
		return (new ElioSensorConfig()).delay(ElioSensorDelayType.DELAY_GAME);
	}

	/**
	 * delay con il quale rilevare il cambiamento dei sensori.
	 * 
	 * @param value
	 * 		valore di delay
	 * @return
	 * 		this
	 */
	public ElioSensorConfig delay(ElioSensorDelayType value) {
		delay = value;

		return this;
	}


}
