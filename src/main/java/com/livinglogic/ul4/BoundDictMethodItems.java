/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class BoundDictMethodItems extends BoundMethod<Map>
{
	public BoundDictMethodItems(Map object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "items";
	}

	public static Object call(EvaluationContext context, Map object)
	{
		return new MapItemIterator(object);
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, object);
	}

	private static class MapItemIterator implements Iterator<Vector>
	{
		Iterator iterator;

		public MapItemIterator(Map map)
		{
			iterator = map.entrySet().iterator();
		}

		@Override
		public boolean hasNext()
		{
			return iterator.hasNext();
		}

		@Override
		public Vector next()
		{
			Vector retVal = new Vector(2);
			Map.Entry entry = (Map.Entry)iterator.next();
			retVal.add(entry.getKey());
			retVal.add(entry.getValue());
			return retVal;
		}

		@Override
		public void remove()
		{
			iterator.remove();
		}
	}
}
