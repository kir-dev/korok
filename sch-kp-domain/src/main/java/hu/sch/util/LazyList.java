package hu.sch.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * @author NB4L1
 */
public final class LazyList<E> extends LazyCollection<E> implements List<E>
{
	private List<E> _list;
	
	@Override
	protected List<E> get(boolean init)
	{
		if (_list == null)
		{
			if (init)
				_list = new ArrayList<E>();
			else
				return SchCollections.emptyList();
		}
		
		return _list;
	}
	
	public void add(int index, E element)
	{
		get(true).add(index, element);
	}
	
	public boolean addAll(int index, Collection<? extends E> c)
	{
		return get(true).addAll(index, c);
	}
	
	public E get(int index)
	{
		return get(false).get(index);
	}
	
	public int indexOf(Object o)
	{
		return get(false).indexOf(o);
	}
	
	public int lastIndexOf(Object o)
	{
		return get(false).lastIndexOf(o);
	}
	
	public ListIterator<E> listIterator()
	{
		return get(false).listIterator();
	}
	
	public ListIterator<E> listIterator(int index)
	{
		return get(false).listIterator(index);
	}
	
	public E remove(int index)
	{
		return get(false).remove(index);
	}
	
	public E set(int index, E element)
	{
		return get(false).set(index, element);
	}
	
	public List<E> subList(int fromIndex, int toIndex)
	{
		return get(false).subList(fromIndex, toIndex);
	}
}
