/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionAsUL4ON implements Function
{
	public String getName()
	{
		return "asul4on";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "asul4on", args.length, 1);
	}

	public static String call(Object obj)
	{
		return com.livinglogic.ul4on.Utils.dumps(obj);
	}
}