/**
 * 
 */
package com.abubusoft.xenon.core.util;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import java.util.Set;


/**
 * @author Francesco Benincasa
 *
 */
public abstract class ElioCollection {

	/**
	 * Dato un insieme parametrico di elementi, li converte in un set
	 * @param items
	 * @return
	 */
	public static <E> Set<E> createSet(E... items)
	{
		Set<E> ret=new HashSet<E>();
		
		for (E item: items)
		{
			ret.add(item);
		}
		
		return ret;
	}
	
	/**
	 * Ordinamento liste con l'algoritmo Insertion Sort
	 * http://www.cs.washington.edu/education/courses/cse373/01wi/slides/Measurement/sld010.htm
	 * http://en.wikipedia.org/wiki/Sorting_algorithm
	 * 
	 * @param list
	 * @param comp
	 */
	public static <E> void sort(List<E> list, Comparator<E> comp)
	{
		int n=list.size();
		int j;
		E item;
		for (int i=1; i<n; i++)
		{
			item=list.get(i);
			
			j=i-1;
			
			while((j>=0) && (comp.compare(item, list.get(j))<0))
			{
				list.set(j+1, list.get(j));
				j--;
			}
			
			list.set(j+1,item);
		}
	}
		
}
