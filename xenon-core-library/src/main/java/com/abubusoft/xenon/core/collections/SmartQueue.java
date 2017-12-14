/**
 * 
 */
package com.abubusoft.xenon.core.collections;

import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * Rappresenta una coda "infinita" di elementi. Gli elementi vengono inseriti normalmente in coda e vengono estratti in testa. (LIFO)
 * </p>
 * 
 * <p>
 * In fase di creazione dell'istanza viene definito una dimensione di elementi. Questo numero definisce il numero di elementi della lista che vengono riutilizzati. Questo non vuol
 * dire che c'è un limite massimo di elementi. La struttura può crescere indefinitivamente, fatto sta che quando la struttura decresce, verranno salvati un numero di elementi pari
 * a quello definito all'inizio.
 * </p>
 * <p>
 * Questo risulta molto utile nella gestione dinamica di elementi.
 * </p>
 * <p>
 * <b>Non è thread-safe</b>
 * </p>
 * 
 * @param <E>
 *            tipo
 * 
 */
public class SmartQueue<E> {

	public final static int DEFAULT_CAPACITY = 16;

	/**
	 * cursore, serve a iterare sulla struttura.
	 */
	protected Item cursor;

	/**
	 * cancelliamo tutti gli elementi.
	 */
	public void clear() {
		// cancelliamo
		if (size == 0)
			return;

		Item t;

		while (size > 0) {
			if (size == 1) {
				t = head;

				head = null;
				last = null;

				pool.freeObject(t);
			} else if (size > 1) {
				t = head;
				// prima di rilasciare spostiamo head
				head = head.next;
				pool.freeObject(t);
			}
			size--;
		}
	}

	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * ripristina il cursore all'inizio
	 */
	public void cursorReset() {
		cursor = head;
	}

	/**
	 * si sposta sul prossimo. Se true indica che in effetti c'è ancora qualche elemento da analizzare. Se false indica che la lista è finita.
	 * 
	 * Se si esegue dopo aver raggiunto la fine, da eccezione.
	 * 
	 * @return
	 */
	public boolean cursorNext() {
		if (cursor == null)
			new NoSuchElementException();
		cursor = cursor.next;

		return cursor != null;
	}

	public boolean cursorHasNext() {
		if (cursor == null)
			new NoSuchElementException();

		return cursor != null;
	}

	/**
	 * Recupera il valore sotto il cursore
	 * 
	 * @return
	 */
	public E cursorValue() {
		if (cursor == null)
			new NoSuchElementException();

		return cursor.value;
	}

	public SmartQueue(int capacity) {
		pool = new ItemPool(capacity);
		head = null;
		last = null;
		size = 0;
	}

	public SmartQueue() {
		this(DEFAULT_CAPACITY);
	}

	/**
	 * riferimento al primo elemento della lista
	 */
	protected Item head;

	/**
	 * riferimento all'ultimo elemento della lista
	 */
	protected Item last;

	/**
	 * numero di elementi della lista
	 */
	protected int size;

	/**
	 * @return the size
	 */
	public int size() {
		return size;
	}

	/**
	 * Aggiunge un elemento in coda alla queue
	 * 
	 * @param item
	 */
	public void add(E item) {
		Item e = pool.newObject();
		e.value = item;

		if (last == null) {
			last = e;
		} else {
			last.next = e;
			// e.previous = last;

			last = e;
		}

		// se non abbiamo niente allora lo mettiamo anche come head
		if (head == null) {
			head = e;
		}

		size++;
	}

	/**
	 * Recupera ma non rimuove il primo elemento.
	 */
	public E peek() {
		if (head == null)
			return null;

		E value = head.value;

		return value;
	}

	/**
	 * Recupera il primo elemento, rimuovendolo dalla coda.
	 * 
	 * @return il primo elemento recuperato dalla coda
	 * @throws NoSuchElementException
	 *             se la coda è vuota
	 */
	public E pop() {
		E value = null;
		if (size == 0) {
			throw (new NoSuchElementException());
		}

		value = head.value;
		if (size == 1) {
			Item t = head;

			head = null;
			last = null;

			pool.freeObject(t);
		} else if (size > 1) {
			Item t = head;
			// prima di rilasciare spostiamo head
			head = head.next;
			pool.freeObject(t);
		}
		size--;

		return value;
	}

	/**
	 * @author Francesco Benincasa
	 * 
	 */
	class Item implements PooledObject {
		/**
		 * valore salvato.
		 */
		E value;

		/**
		 * riferimento al prossimo elemento della lista.
		 */
		Item next;

		@Override
		public void initializePoolObject() {
			next = null;
		}

		@Override
		public void finalizePoolObject() {
			next = null;
		}
	}

	/**
	 * Pool degli item che compongono la queue. Serve ad evitare di dover ogni volta creare e poi recuperare con il garbage collector.
	 * 
	 * @author Francesco Benincasa
	 * 
	 */
	class ItemPool extends ObjectPool<Item> {
		public ItemPool(int maxSize) {
			super(maxSize);
		}

		@Override
		protected Item createPooledObject() {
			return new Item();
		}

	}

	protected ItemPool pool;
}
