/**
 * 
 */
package com.abubusoft.xenon.core;

/**
 * @author Francesco Benincasa
 *
 */
public class XenonRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7885443692058739678L;
	
	public XenonRuntimeException(String msg)
	{
		super(msg);
	}
	
	public XenonRuntimeException(Throwable origin)
	{
		super(origin);
	}
	
	public XenonRuntimeException(String msg, Throwable origin)
	{
		super(msg, origin);
	}

}
