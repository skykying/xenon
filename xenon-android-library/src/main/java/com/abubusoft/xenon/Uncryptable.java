/**
 * 
 */
package com.abubusoft.xenon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Interfaccia che serve a marcare come non cryptabili (con proguard)
 * @author Francesco Benincasa
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value= ElementType.TYPE)
public @interface Uncryptable {

}
