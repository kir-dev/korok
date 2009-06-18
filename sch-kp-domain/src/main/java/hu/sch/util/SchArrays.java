package hu.sch.util;

import java.lang.reflect.Array;

/**
 * @author NB4L1
 */
public final class SchArrays
{
	private SchArrays()
	{
	}
	
	public static int countNull(Object[] array)
	{
		if (array == null)
			return 0;
		
		int nullCount = 0;
		
		for (Object obj : array)
			if (obj == null)
				nullCount++;
		
		return nullCount;
	}
	
	public static int countNotNull(Object[] array)
	{
		return array == null ? 0 : array.length - countNull(array);
	}
	
	/**
	 * @param <T>
	 * @param array to remove null elements from
	 * @return an array without null elements - can be the same, if the original contains no null elements
	 * @throws NullPointerException if array is null
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] compact(T[] array)
	{
		final int newSize = countNotNull(array);
		
		if (array.length == newSize)
			return array;
		
		final T[] result = (T[])Array.newInstance(array.getClass().getComponentType(), newSize);
		
		int index = 0;
		
		for (T t : array)
			if (t != null)
				result[index++] = t;
		
		return result;
	}
}
