/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Random;

public class FunctionRandom implements Function
{
	private static Random rng = new Random();

	public static double call()
	{
		return rng.nextDouble();
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 0)
			return call();
		throw new ArgumentCountMismatchException("function", "random", args.length, 0);
	}

	public String getName()
	{
		return "random";
	}
}
