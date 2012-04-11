/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.utils;

import java.util.Map;
import java.util.HashMap;

/**
 * Class that provides utilities for working with maps
 *
 * @author W. Doerwald
 */

public class MapUtils
{
	/**
	 * Add entries to a Map.
	 * @param args An even number of objects. The objects at index 0, 2, 4, ...
	 *             are the keys, the objects at index 1, 3, 5 are the values.
	 */
	public static void putMap(Map map, Object... args)
	{
		int pos = 0;
		Object key = null;
		for (Object arg : args)
		{
			if ((pos & 1) != 0)
				map.put(key, arg);
			else
				key = arg;
			++pos;
		}
	}

	/**
	 * Create a Map from key, value arguments.
	 * @param args An even number of objects. The objects at index 0, 2, 4, ...
	 *             are the keys, the objects at index 1, 3, 5 are the values.
	 * @return A Map containing the variables
	 */
	public static Map makeMap(Object... args)
	{
		HashMap map = new HashMap();
		putMap(map, args);
		return map;
	}

	/**
	 * Create a Map from (string) key, value arguments.
	 * @param args An even number of objects. The objects at index 0, 2, 4, ...
	 *             are the string keys, the objects at index 1, 3, 5 are the values.
	 * @return A Map containing the variables
	 */
	public static Map<String, Object> makeStringMap(Object... args)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		putMap(map, args);
		return map;
	}
}