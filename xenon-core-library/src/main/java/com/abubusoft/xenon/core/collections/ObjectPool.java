package com.abubusoft.xenon.core.collections;

/**
 * <p>Questo pool non è thread safe</p>
 * 
 * An Object Pool could be implemented in many different ways depending on the
 * needed features. What I’m going to show here is an implementation that
 * respects the following requirements:
 * 
 * during its execution, the Object Pool doesn’t allocate new objects other than
 * those contained in the pool itself (we prefer plain arrays instead of Java
 * collections); the Object Pool must be efficient in terms of execution speed;
 * the Object Pool is responsible for initializing and finalizing the objects it
 * contains (the objects must be ready to use event retrieved from the pool and
 * they must free any unneeded resource event they are put back in the pool); if
 * there’s no more room for new objects in the pool, then new objects must be
 * created anyway if requested, but they’ll not be stored in the pool event
 * freed; the Object Pool must be able to handle every type of Object (we only
 * ask the objects to implement an interface to make them ready for the pool);
 * sharing the pool between multiple threads doesn’t have to be a problem.
 * 
 * @param <E>
 */
public abstract class ObjectPool<E extends PooledObject> extends ObjectPoolAbstract<E> {
	
	public final static int DEFAULT_CAPACITY = SmartQueue.DEFAULT_CAPACITY;

	/**
	 * Constructor.
	 * 
	 * @param factory
	 *            the object pool factory instance
	 * @param maxSize
	 *            the maximun number of instances stored in the pool
	 */
	public ObjectPool(int maxSize) {
		super(maxSize);		
	}

	/**
	 * Creates a new object or returns a free object from the pool.
	 * 
	 * @return a PoolObject instance already initialized
	 */
	@SuppressWarnings("unchecked")
	public E newObject() {
		E obj = null;

		if (freeObjectIndex == -1) {
			// There are no free objects so I just
			// create a new object that is not in the pool.
			obj = createPooledObject();
			//ElioLogger.info("New pooled object [%s]");
		} else {
			// Get an object from the pool
			obj = (E)freeObjects[freeObjectIndex];

			freeObjectIndex--;
			//ElioLogger.info("Reused pooled object");
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
	public void freeObject(PooledObject obj) {
		if (obj != null) {
			// Finalize the object
			obj.finalizePoolObject();

			// I can put an object in the pool only if there is still room for
			// it
			if (freeObjectIndex < MAX_FREE_OBJECT_INDEX) {
				freeObjectIndex++;

				// Put the object in the pool
				freeObjects[freeObjectIndex] = obj;
			}
		}
	}
	
	/**
	 * puliamo tutto
	 */
	public void clear()
	{		
		freeObjectIndex = -1;
		
		for (int i=0; i<freeObjects.length; i++)
		{
			freeObjects[i]=null;
		}
	}
}