package com.abubusoft.xenon.core.sensor;

/**
 * <p>
 * Configurazione dell'orientamento. Gli angoli sono espressi in gradi
 * </p>
 * 
 * <pre>
 *           ^ Roll (z)
 *           |
 *      +---------+
 *      |         |
 *      | Heading |
 *      |   (x)   |
 *      |    x    | ---> Pitch (y)
 *      |         |  
 *      +---------+
 * 
 * </pre>
 * 
 * @author Francesco Benincasa
 * 
 */
public class OrientationInputConfig {
	
	/**
	 * Tipo di notifiche
	 * 
	 * @author Francesco Benincasa
	 * 
	 */
	public enum EventType {
		/**
		 * indica di notificare sempre e comunque tutti gli eventi
		 */
		NOTIFY_ALWAYS,

		/**
		 * indica di notificare solo le modifiche ai valori
		 */
		NOTIFY_CHANGES;
	}
	
	public enum ProviderType
	{
		ACCELEROMETER_COMPASS,
		CALIBRATE_COMPASS,
		GRAVITY_COMPASS,
		IMPROVED_ORIENTATION_SENSOR1,
		IMPROVED_ORIENTATION_SENSOR2,
		ROTATION_VECTOR
	}

	/**
	 * limite di default (minimo)
	 */
	public static final double DEFAULT_LIMIT = 20.0;

	/**
	 * valore di default in gradi per il quale consideriamo la misurazione 0
	 */
	public static final double DEFAULT_NOISE_NEAR_ZERO = 1.0;

	public static final double NO_LIMIT = 360.0;

	/**
	 * <p>
	 * Configurazione di default.
	 * </p>
	 * <p>
	 * <dl>
	 * <dt>delta</dt>
	 * <dd>delta minimo in gradi misurabile = 1.0</dd>
	 * <dt>refreshCounter</dt>
	 * <dd>ogni quante rilevazioni prendere quella buona= 1</dd>
	 * <dt>pitchLimit</dt>
	 * <dd>limite in abs in gradi del pitch = 20.0</dd>
	 * <dt>rollLimit</dt>
	 * <dd>limite in abs in gradi del roll = 20.0</dd>
	 * <dt>azimouthLimit</dt>
	 * <dd>limite in abs dell'azimouth = 360.0</dd>
	 * <dt>noiseNearZero</dt>
	 * <dd>delta in gradi in abs dentro il quale posso considerare la misurazione come 0= 3.0</dd>
	 * <dt>intValues</dt>
	 * <dd>se true indica che i valori devono essere a scalini = true</dd>
	 * <dt>event</dt>
	 * <dd>tipo di evento da gestire = {@link EventType#NOTIFY_CHANGES} (segnala solo i cambiamenti).</dd>
	 * </dl>
	 * </p>
	 * 
	 */
	public static OrientationInputConfig build() {
		return new OrientationInputConfig();
	}

	/**
	 * limite in abs dell'azimouth
	 */
	public double azimouthLimit;

	public double currentAzimuth;

	public double currentPitch;

	public double currentRoll;
	
	public long deltaTime;

	/**
	 * indica quando notificare l'evento.
	 * 
	 */
	public EventType event;

	/**
	 * delta in gradi in abs dentro il quale posso considerare la misurazione come 0
	 */
	public double noiseNearZero;
	
	/**
	 * ultimo azimuth rilevato per questo config
	 */
	public double oldAzimuth;
	
	/**
	 * ultimo pitch rilevato per questo config
	 */
	public double oldPitch;
	
	/**
	 * ultimo roll rilevato per questo config
	 */
	public double oldRoll;

	/**
	 * limite in abs in gradi del pitch
	 */
	public double pitchLimit;

	public ProviderType provider;

	/**
	 * limite in abs in gradi del roll
	 */
	public double rollLimit;

	/**
	 * costruttore
	 */
	private OrientationInputConfig() {
		restoreDefaultValue();
	}

	/**
	 * <pre>
	 *           ^ Roll (z)
	 *           |
	 *      +---------+
	 *      |         |
	 *      | Heading |
	 *      |   (x)   |
	 *      |    x    | ---> Pitch (y)
	 *      |         |  
	 *      +---------+
	 * 
	 * </pre>
	 * 
	 * @param value
	 * @return	this
	 */
	public OrientationInputConfig azimouthLimit(double value) {
		this.azimouthLimit = value;
		return this;
	}

	/**
	 * indica quando notificare l'evento.
	 * 
	 * @param value
	 * 		quando notificare l'evento
	 * @return
	 * 		this
	 */
	public OrientationInputConfig event(EventType value) {
		event = value;

		return this;
	}

	/**
	 * <pre>
	 *           ^ Roll (z)
	 *           |
	 *      +---------+
	 *      |         |
	 *      | Heading |
	 *      |   (x)   |
	 *      |    x    | ---> Pitch (y)
	 *      |         |  
	 *      +---------+
	 * 
	 * </pre>
	 * 
	 * @param value
	 * @return this
	 */
	public OrientationInputConfig pitchLimit(double value) {
		this.pitchLimit = value;
		return this;
	}

	/**
	 * @param value
	 * @return this
	 */
	public OrientationInputConfig provider(ProviderType value) {
		this.provider = value;
		return this;
	}

	/**
	 * imposta i valori di default
	 */
	private void restoreDefaultValue() {
		noiseNearZero = DEFAULT_NOISE_NEAR_ZERO;

		// vuol dire che non lo rileviamo
		azimouthLimit = NO_LIMIT;

		// limite 20°
		rollLimit = DEFAULT_LIMIT;

		// limite 20°
		pitchLimit = DEFAULT_LIMIT;

		// notifica solo i cambiamenti
		event = EventType.NOTIFY_CHANGES;
		
		// ogni 250 ms
		deltaTime=150;
		
		// tipo di provider
		provider=ProviderType.IMPROVED_ORIENTATION_SENSOR1;
	}
	

	/**
	 * <pre>
	 *           ^ Roll (z)
	 *           |
	 *      +---------+
	 *      |         |
	 *      | Heading |
	 *      |   (x)   |
	 *      |    x    | ---> Pitch (y)
	 *      |         |  
	 *      +---------+
	 * 
	 * </pre>
	 * 
	 * @param value
	 * @return this
	 */
	public OrientationInputConfig rollLimit(double value) {
		this.rollLimit = value;
		return this;
	}
	
	/**
	 * Differenza sotto la quale due misurazioni sono considerate uguali. Di default
	 * il suo valore è {@link #DEFAULT_NOISE_NEAR_ZERO}
	 * 
	 * @param value
	 * @return
	 * 		builder
	 */
	public OrientationInputConfig noiseNearZero(double value) {
		this.noiseNearZero = value;
		return this;
	}
	
	/**
	 * tempo minimo tra una misurazione ed un'altra
	 * 
	 * @param value
	 * @return
	 * 		builder
	 */
	public OrientationInputConfig deltaTime(long value) {
		this.deltaTime = value;
		return this;
	}
	
	

}
