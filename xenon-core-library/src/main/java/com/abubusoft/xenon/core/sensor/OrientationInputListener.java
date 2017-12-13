package com.abubusoft.xenon.core.sensor;

import com.abubusoft.xenon.core.sensor.internal.InputListener;

/**
 * <p>
 * Interfaccia atta a rilevare i movimenti. Ricordiamo che:
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
public interface OrientationInputListener extends InputListener {
	/**
	 * <p>
	 * Esegue il listener del rilevatore del sensore.
	 * </p>
	 * 
	 * <pre>
	 *           ^ Roll (z)
	 *           |
	 *      +---------+
	 *      |         |
	 *      | Heading |
	 *      |   (x)   | ---> Pitch (y)
	 *      |    x    | 
	 *      |         |  
	 *      +---------+
	 * 
	 * </pre>
	 * 
	 * @param heading
	 *            valore attuale in gradi (0 - 360)
	 * @param pitch
	 *            valore attuale in gradi (0 - 360)
	 * @param roll
	 *            valore attuale in gradi (0 - 360)
	 * @param deltaHeading
	 *            delta head in gradi rispetto all'ultima misurazione (current - old)
	 * @param deltaPitch
	 *            delta pitch in gradi rispetto all'ultima misurazione (current - old)
	 * @param deltaRoll
	 *            delta roll in gradi rispetto all'ultima misurazione (current - old)
	 * @param somethingIsChanged
	 *            se true indica se qualcosa è cambiato
	 */
	void update(double heading, double pitch, double roll, double deltaHeading, double deltaPitch, double deltaRoll, boolean somethingIsChanged);
}
