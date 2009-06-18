package hu.sch.util;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NB4L1
 */
public final class LazySet<E> extends LazyCollection<E> implements Set<E>
{
	private Set<E> _set;
	
	@Override
	protected Set<E> get(boolean init)
	{
		if (_set == null)
		{
			if (init)
				_set = new HashSet<E>();
			else
				return SchCollections.emptySet();
		}
		
		return _set;
	}
}
