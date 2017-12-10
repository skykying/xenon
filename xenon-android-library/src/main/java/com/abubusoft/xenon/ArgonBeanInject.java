package com.abubusoft.xenon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * <p>Effettua l'inject del campo con l'entità argon specificata.</p> 
 * @author Francesco Benincasa
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.FIELD)
public @interface ArgonBeanInject {

	/**
	 * <p>
	 * Indica il tipo di injection da inserire.
	 * </p>
	 * 
	 * @return
	 */
	ArgonBeanType value();
	
}
