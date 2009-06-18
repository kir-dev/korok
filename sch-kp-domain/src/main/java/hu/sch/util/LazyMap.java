package hu.sch.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author NB4L1
 */
public final class LazyMap<K, V> implements Map<K, V>
{
	private Map<K, V> _map;
	
	private Map<K, V> get(boolean init)
	{
		if (_map == null)
		{
			if (init)
				_map = new HashMap<K, V>();
			else
				return SchCollections.emptyMap();
		}
		
		return _map;
	}
	
	public void clear()
	{
		get(false).clear();
	}
	
	public boolean containsKey(Object key)
	{
		return get(false).containsKey(key);
	}
	
	public boolean containsValue(Object value)
	{
		return get(false).containsValue(value);
	}
	
	public Set<Entry<K, V>> entrySet()
	{
		return get(false).entrySet();
	}
	
	public V get(Object key)
	{
		return get(false).get(key);
	}
	
	public boolean isEmpty()
	{
		return get(false).isEmpty();
	}
	
	public Set<K> keySet()
	{
		return get(false).keySet();
	}
	
	public V put(K key, V value)
	{
		return get(true).put(key, value);
	}
	
	public void putAll(Map<? extends K, ? extends V> m)
	{
		get(true).putAll(m);
	}
	
	public V remove(Object key)
	{
		return get(false).remove(key);
	}
	
	public int size()
	{
		return get(false).size();
	}
	
	public Collection<V> values()
	{
		return get(false).values();
	}
}
