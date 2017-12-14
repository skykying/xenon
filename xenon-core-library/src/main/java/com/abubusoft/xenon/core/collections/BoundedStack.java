/**
 * 
 */
package com.abubusoft.xenon.core.collections;

import java.io.Serializable;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Rapresents a bounded stack. The difference between this
 * implementation and the standard data structure in the jdk is that
 * the bounded stack has a maximum number of elements that can be put
 * in. If you try to insert another elements in the stack, the older
 * elements will be discard.
 * 
 * @author Francesco Benincasa (908099)
 * @version 1.0.0
 * @param <E> 
 *
 */
public class BoundedStack<E> implements Serializable , Iterable<E> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7364318993666004978L;
	
	
	/**
	 * capacity of the stack.
	 */
	protected int	capacity;
	
	
	/**
	 * array of elements
	 */
	protected LinkedList<E> elements;
	
	/**
	 * BinderDefault costructor. By default it creates an bounded stack
	 * with a maximum capacity of 10 elements.
	 */
	public BoundedStack()
	{
		this(10);
	}
	
	/**
	 * reset the stack
	 */
	public void clear() {
		elements.clear();
	}
	
	/**
	 * return the size of the stack
	 * @return
	 * 		the number of the elements inserted into the stack
	 */
	public int size() 
	{
		return elements.size();
	}
	
	/**
	 * return the max elements the stack can contains.
	 * 
	 * @return
	 * 		max number of elements stack can contains.
	 */
	public int getCapacity()
	{
		return capacity;
	}
	
	/**
	 * With this constructor, it is possibile to define the
	 * maximum capacity of the stack. 
	 * 
	 * @param maxcapacity
	 * 			max capacity of the stack.
	 */
	public BoundedStack(int maxcapacity) 
	{
		if (maxcapacity<=0) maxcapacity=10;
		capacity=maxcapacity;
		
		elements=new LinkedList<E>();
	}
	
	/**
	 * Pushes an item onto the top of this stack. 
	 * 
	 * @param item
	 * 			item to add
	 * @return
	 * 			item added
	 */
	public E push(E item)
	{		
		if (elements.size()<capacity)
		{
			elements.addFirst(item);
		} else {
			elements.addFirst(item);
			elements.removeLast();			
		}
		
		return item;
	}
	
	/**
	 * Removes the object at the top of this stack and returns that object as the value of this function.
	 * 
	 * @return
	 * 		The object at the top of this stack (the last item of the Vector object).
	 * @throws EmptyStackException 
	 */
	public E pop() throws EmptyStackException
	{
		if (elements.size()==0) throw(new EmptyStackException());
		
		E item=elements.removeFirst();				
					
		return item;
	}	
	
	
	/**
	 * Looks at the object at the top of this stack without removing it from the stack.
	 * @return the object at the top of this stack (the last item of the Vector object).
	 * @throws EmptyStackException 
	 */
	public E peek() throws EmptyStackException
	{
		if (elements.size()==0) throw(new EmptyStackException());
		
		E item=elements.getFirst();
		
		return item;	
	}
	
	/**
	 * Tests if this stack is empty.
	 * 
	 * @return
	 * 	 <code>true</code> if and only if this stack contains no items; <code>false</code> otherwise.
	 */
	public boolean empty() {
		if (elements.size()>0) return false;
		
		return true;
	}
	
	/**
	 * Returns the 1-based position where an object is on this stack. 
	 * If the object o occurs as an item in this stack, this method returns 
	 * the distance from the top of the stack of the occurrence nearest the top of the stack;
	 * the topmost item on the stack is considered to be at distance 1. The equals method 
	 * is used to compare o to the items in this stack.
	 * 
	 * @param entity
	 * @return
	 * 	the 1-based position from the top of the stack where the object is located; the return value -1  indicates that the object is not on the stack.
	 */
	public int search(E entity) {		
		int level=elements.size();
		for(E a: elements)
		{
			if (a.equals(entity))
			{
				return level;
			}
			level--;
		}		
		return -1;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<E> iterator() {		
		return elements.iterator();		
	}
	
}
