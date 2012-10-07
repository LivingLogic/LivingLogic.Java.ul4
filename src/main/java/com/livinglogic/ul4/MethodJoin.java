/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

public class MethodJoin implements Method
{
	public String getName()
	{
		return "join";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 1:
				return call(obj, args[0]);
			default:
				throw new ArgumentCountMismatchException("method", "join", args.length, 1);
		}
	}

	public static String call(String obj, Iterator iterator)
	{
		StringBuilder buffer = new StringBuilder();

		boolean first = true;
		while (iterator.hasNext())
		{
			if (!first)
				buffer.append(obj);
			buffer.append((String)iterator.next());
			first = false;
		}
		return buffer.toString();
	}

	public static String call(String obj, Object iterable)
	{
		return call(obj, Utils.iterator(iterable));
	}

	public static String call(Object obj, Object iterable)
	{
		if (obj instanceof String)
		{
			if (iterable instanceof Iterator)
				return call((String)obj, (Iterator)iterable);
			return call((String)obj, iterable);
		}
		else
			throw new ArgumentTypeMismatchException("{}.join({})", obj, iterable);
	}
}
