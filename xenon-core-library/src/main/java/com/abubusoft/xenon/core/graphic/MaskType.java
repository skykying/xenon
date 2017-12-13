package com.abubusoft.xenon.core.graphic;

/**
 * <p>Tipo di mask da applicare.</p>
 * @author Francesco Benincasa
 *
 */
public enum MaskType {

	MASK_ALPHA(0),
	MASK_RED(8);
	
	public int value;
	
	private MaskType(int v)
	{
		value=v;
	}
}
