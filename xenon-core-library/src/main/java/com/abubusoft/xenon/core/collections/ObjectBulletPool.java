/**
 * 
 */
package com.abubusoft.xenon.core.collections;

import java.util.LinkedList;

/**
 * @author Francesco Benincasa
 * 
 */
public abstract class ObjectBulletPool<E extends PooledObject> {

	protected LinkedList<BulletItem<E>> usedObjects;
	
	protected LinkedList<BulletItem<E>> freeObjects;
	
	protected int maxObjects;
	
	protected BulletIterator<E> iteraction;
	
	protected int freeObjectIndex = -1;
	
	public ObjectBulletPool(int maxObjectsValue)
	{
		maxObjects=maxObjectsValue;
		usedObjects=new LinkedList<BulletItem<E>>();
		freeObjects=new LinkedList<BulletItem<E>>();
	}
	
	/**
	 * Creates a new object for the object pool.
	 * 
	 * @return new object instance for the object pool
	 */
	public abstract E createPooledObject();
	
	/**
	 * Creates a new object or returns a free object from the pool.
	 * 
	 * @return a PoolObject instance already initialized
	 */
	public synchronized E newObject() {
		E obj = null;

		if (freeObjectIndex == -1) {
			// There are no free objects so I just
			// create a new object that is not in the pool.
			obj = createPooledObject();
		} else {
			// Get an object from the pool
			//obj = usedObjects.get(freeObjectIndex);

			freeObjectIndex--;
		}

		// Initialize the object
		obj.initializePoolObject();

		return obj;
	}
	
	/**
	 * Stores an object instance in the pool to make it available for a
	 * subsequent call to newObject() (the object is considered free).
	 * 
	 * @param obj
	 *            the object to store in the pool and that will be finalized
	 */
	public synchronized void freeObject(E obj) {
		if (obj != null) {
			// Finalize the object
			obj.finalizePoolObject();

			// I can put an object in the pool only if there is still room for
			// it
			if (freeObjectIndex < maxObjects) {
				freeObjectIndex++;

				// Put the object in the pool
				//freeObjects.set(freeObjectIndex,obj);
			}
		}
	}
	
	public void interaction()
	{
		int n=usedObjects.size();
		//BulletStatusType status;
		
		for (int i=0; i<n; i++)
		{
			//status=iteraction.iteraction(usedObjects);
			
			/*if (status==BulletStatus.REMOVE)
			{
				
			}*/
		}
	}

}
