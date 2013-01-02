/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionIsColor implements Function
{
	public String getName()
	{
		return "iscolor";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "iscolor", args.length, 1);
	}

	public static boolean call(Object obj)
	{
		return (null != obj) && (obj instanceof Color);
	}
}
