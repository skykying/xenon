/**
 * 
 */
package com.abubusoft.xenon.core.collections;

/**
 * @author Francesco Benincasa
 *
 */
public interface BulletIterator<E extends PooledObject> {

	void iteraction(E item, BulletStatusType status);
}
