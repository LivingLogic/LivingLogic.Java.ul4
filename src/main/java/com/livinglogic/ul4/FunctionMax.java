/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Iterator;

public class FunctionMax implements Function
{
	public String getName()
	{
		return "max";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		return args.length == 0 ? call() : call(args);
	}

	public static Object call()
	{
		throw new ArgumentCountMismatchException("function", "max", 0, 1, -1);
	}

	public static Object call(Object... objs)
	{
		Iterator iter = Utils.iterator(objs.length == 1 ? objs[0] : objs);

		Object maxValue = null;
		boolean first = true;

		for (;iter.hasNext();)
		{
			Object testValue = iter.next();
			if (first || GT.call(testValue, maxValue))
				maxValue = testValue;
			first = false;
		}
		if (first)
			throw new UnsupportedOperationException("max() arg is an empty sequence!");
		return maxValue;
	}
}
