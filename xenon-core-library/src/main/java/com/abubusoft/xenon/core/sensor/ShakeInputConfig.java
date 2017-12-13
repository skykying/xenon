package com.abubusoft.xenon.core.sensor;

/**
 * <p>Configurazione relativa allo shake</p>
 * 
 * @author Francesco Benincasa
 *
 */
public class ShakeInputConfig {
    /**
     * Minimum acceleration needed to count as a shake movement
     */
    public int minShakeAcceleration= 5;

    /**
     * Minimum number of movements to register a shake
     */
    public int minMovements = 2;

    /**
     * Maximum time (in milliseconds) for the whole shake to occur
     */
    public long maxShakeDuration = 500;

	public long startTime;

	public int moveCount;
    
    public static ShakeInputConfig build()
    {
    	return (new ShakeInputConfig());
    }
    
    /**
     * @param value
     * @return
     * 		this
     */
    public ShakeInputConfig minShakeAcceleration(int value)
    {
    	minShakeAcceleration=value;
    	return this;
    }
    
    /**
     * @param value
     * @return
     * 		this
     */
    public ShakeInputConfig minMovements(int value)
    {
    	minMovements=value;
    	return this;
    }
    
    /**
     * @param value
     * @return
     * 		this
     */
    public ShakeInputConfig maxDuration(long value)
    {
    	maxShakeDuration=value;
    	return this;
    }
    
    
}
