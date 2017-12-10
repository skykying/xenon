/**
 * 
 */
package com.abubusoft.xenon.engine;

import org.abubu.elio.collections.PooledObject;

/**
 * @author Francesco Benincasa
 *
 */
public class TouchMessage implements PooledObject {

	public TouchType type;
	
	public float x;
	
	public float y;

	@Override
	public void initializePoolObject() {
		
		
	}

	@Override
	public void finalizePoolObject() {
		type=null;
		
		x=0f;
		y=0f;
		
	}
	
}
