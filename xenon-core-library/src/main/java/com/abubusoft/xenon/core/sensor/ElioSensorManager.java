package com.abubusoft.xenon.core.sensor;

import java.util.ArrayList;
import java.util.HashSet;

import com.abubusoft.xenon.core.sensor.compass.CompassProvider;
import com.abubusoft.xenon.core.sensor.internal.InputSensorProvider;
import com.abubusoft.xenon.core.sensor.internal.ShakeInputDetector;
import com.abubusoft.xenon.core.sensor.orientation.AccelerometerCompassProvider;
import com.abubusoft.xenon.core.sensor.orientation.CalibratedGyroscopeProvider;
import com.abubusoft.xenon.core.sensor.orientation.GravityCompassProvider;
import com.abubusoft.xenon.core.sensor.orientation.Fusion1OrientationProvider;
import com.abubusoft.xenon.core.sensor.orientation.Fusion2OrientationProvider;
import com.abubusoft.xenon.core.sensor.orientation.OrientationProvider;
import com.abubusoft.xenon.core.sensor.orientation.RotationVectorProvider;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.SparseArray;

/**
 * <p>
 * Gestore dei vari tipi di sensori che il framework supporta.
 * </p>
 * <h2>Bussola</h2>
 * <p>
 * Consente di capire come è orientato il dispositivo. Necessita di autorizzazioni particolari all'interno del manifest.
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
public class ElioSensorManager implements SensorEventListener {

	/**
	 * Stati del manager
	 * 
	 * @author xcesco
	 *
	 */
	public enum StatusType {
		STARTED, STOPPED;
	}

	/**
	 * Stati dei sensori gestiti
	 * 
	 * @author xcesco
	 *
	 */
	public enum SensorStatusType {
		REGISTERED, UNREGISTERED;
	};

	/**
	 * indica se il manager gestisce l'orientamento del device
	 */
	private boolean deviceOrientation;

	/**
	 * indica se il manager gestisce lo shake del device
	 */
	private boolean deviceShake;

	/**
	 * stato dell'input manager
	 * 
	 */
	private StatusType status;

	/**
	 * getter dello stato del maanger
	 * 
	 * @return stato del manager
	 */
	public StatusType getStatus() {
		return status;
	}

	/**
	 * Se non sono stati già registrati, provvede a registrare i listener.
	 */
	private void registryListener() {
		Sensor sensor;
		// definiamo sensori e detector associati
		for (int i = 0; i < sensorsToProviders.size(); i++) {
			sensor = sensorManager.getDefaultSensor(sensorsToProviders.keyAt(i));
			sensorManager.registerListener(this, sensor, config.delay.value);
		}
	}

	/**
	 * Se non sono stati già unregistrati, provvede a unregistrare i listener
	 */
	private void unregistryListener() {

		// assert: ora abbiamo creato la lista di sensori a cui ci vogliamo
		// agganciare
		Sensor sensor;
		for (int i = 0; i < sensorsToProviders.size(); i++) {
			sensor = sensorManager.getDefaultSensor(sensorsToProviders.keyAt(i));
			sensorManager.unregisterListener(this, sensor);
		}
	}

	/**
	 * a prescindere dallo stato in cui si trova, lo mettiamo in stato di stop, cancelliamo tutti gli stati.
	 */
	public void reset() {
		// intanto blocchiamo eventualmente tutto
		stopMeasure();

		providers.clear();
		deviceOrientation = false;
		deviceShake = false;
		deviceCompass=false;
	}

	/**
	 * istanza
	 */
	private static final ElioSensorManager instance = new ElioSensorManager();

	/**
	 * costruttore
	 */
	private ElioSensorManager() {
		sensorsToProviders = new SparseArray<ArrayList<InputSensorProvider>>();
		providers = new HashSet<InputSensorProvider>();
		status = StatusType.STOPPED;

		deviceOrientation = false;
		deviceShake = false;
	}

	/**
	 * insieme degli eventi
	 */
	private SparseArray<ArrayList<InputSensorProvider>> sensorsToProviders;

	private boolean initDone;

	/**
	 * detector di input relativi ai sensori agganciati
	 */
	private HashSet<InputSensorProvider> providers;

	/**
	 * configurazione del manager
	 */
	public ElioSensorConfig config;

	/**
	 * gestore dei sensori
	 */
	public SensorManager sensorManager;

	private boolean deviceCompass;

	/**
	 * instanza singleton
	 * 
	 * @return instanza singleton
	 */
	public static ElioSensorManager instance() {
		return instance;
	}

	/**
	 * Inizializzazione del sistema. Cancella tutti i listener e provider eventualmente definiti in precedenza.
	 * 
	 * @param context
	 * @param configValue
	 *            configurazione di base del manager
	 * @return true se l'inizializzazione è stata effettuata, false se era stata già effettuata in precedenza.
	 */
	public boolean init(Context context, ElioSensorConfig configValue) {
		if (initDone) {
			stopMeasure();
		}

		config = configValue;
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		initDone = true;

		deviceOrientation = false;
		deviceShake = false;
		deviceCompass=false;

		return true;
	}

	/**
	 * <p>
	 * Abilita la rilevazione dei sensori relativi all'orientamento del dispositivo.
	 * </p>
	 * 
	 * <p>
	 * Di seguito riportiamo i movimenti ed i relativi assi di rotazione
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
	 * @param config
	 * @param listener
	 */
	public void enableOrientation(OrientationInputConfig config, OrientationInputListener listener) {
		if (deviceOrientation)
			return;

		OrientationProvider provider = null;
		switch (config.provider) {
		case ACCELEROMETER_COMPASS:
			provider = AccelerometerCompassProvider.instance();
			break;
		case CALIBRATE_COMPASS:
			provider = CalibratedGyroscopeProvider.instance();
			break;
		case GRAVITY_COMPASS:
			provider = GravityCompassProvider.instance();
			break;
		case IMPROVED_ORIENTATION_SENSOR2:
			provider = Fusion2OrientationProvider.instance();
			break;
		case ROTATION_VECTOR:
			provider = RotationVectorProvider.instance();
			break;
		case IMPROVED_ORIENTATION_SENSOR1:
			provider = Fusion1OrientationProvider.instance();
			break;
		}
		if (provider != null && !providers.contains(provider)) {
			providers.add(provider);
			deviceOrientation = true;

			if (listener != null) {
				provider.addListener(config, listener);
			}
		}

	}
	
	public void enableCompass(OrientationInputConfig config, OrientationInputListener listener) {
		CompassProvider detector = CompassProvider.instance();
		detector.setListener(config, listener);
		// aggiungiamo il detector associato
		if (!deviceCompass)
			providers.add(detector);

		deviceCompass = true;
	}

	public void enableShake(ShakeInputConfig config, ShakeInputListener listener) {
		ShakeInputDetector detector = ShakeInputDetector.instance();
		detector.addListener(config, listener);
		// aggiungiamo il detector associato
		if (!deviceShake)
			providers.add(detector);

		deviceShake = true;
	}

	/**
	 * Da invocare quando vogliamo bloccare tutti
	 */
	public void onPause() {
		if (status == StatusType.STARTED) {
			// Logger.info("onPause sensor listeners");
			unregistryListener();
		}
	}

	/**
	 * Blocca la misurazione
	 */
	public void stopMeasure() {
		status = StatusType.STOPPED;

		unregistryListener();

		sensorsToProviders.clear();
		providers.clear();
	}

	/**
	 * Inizia la misurazione
	 */
	public void startMeasure() {
		if (status == StatusType.STARTED)
			return;

		if (providers.size() == 0) {
			// Logger.info("No sensor to activate!");
		} else {
			// puliamo sensori
			sensorsToProviders.clear();
			HashSet<Integer> sensorTypes;
			ArrayList<InputSensorProvider> currentList;
			// prendiamo tutti i detector di sensori
			for (InputSensorProvider item : providers) {
				sensorTypes = item.getAttachedTypeofSensors();
				// registriamoli con i vari sensori sensorsToDetectors
				for (Integer sensorType : sensorTypes) {
					if (sensorsToProviders.indexOfKey(sensorType) < 0) {
						sensorsToProviders.append(sensorType, new ArrayList<InputSensorProvider>());
					}
					currentList = sensorsToProviders.get(sensorType);
					currentList.add(item);
				}
			}

			status = StatusType.STARTED;
			registryListener();
		}

	}

	public void onResume() {
		if (status == StatusType.STARTED) {
			// Logger.info("onResume sensor listeners");
			registryListener();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		sensorIndex = sensorsToProviders.indexOfKey(sensor.getType());

		if (sensorIndex >= 0) {
			currentDetectors = sensorsToProviders.valueAt(sensorIndex);

			for (int i = 0; i < currentDetectors.size(); i++) {
				currentDetectors.get(i).onAccuracyChanged(sensor, accuracy);
			}
		}
	}

	int sensorIndex;

	ArrayList<InputSensorProvider> currentDetectors;

	@Override
	public void onSensorChanged(SensorEvent event) {
		sensorIndex = sensorsToProviders.indexOfKey(event.sensor.getType());

		if (sensorIndex >= 0) {
			currentDetectors = sensorsToProviders.valueAt(sensorIndex);

			for (int i = 0; i < currentDetectors.size(); i++) {
				currentDetectors.get(i).onSensorChanged(event);
			}
		}
	}
}
