/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionRGB extends Function
{
	@Override
	public String getNameUL4()
	{
		return "rgb";
	}

	private static final Signature signature = new Signature().addBoth("r").addBoth("g").addBoth("b").addBoth("a", 1.0);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(args.getDouble(0), args.getDouble(1), args.getDouble(2), args.getDouble(3));
	}

	public static Color call(double arg1, double arg2, double arg3)
	{
		return Color.fromrgb(arg1, arg2, arg3);
	}

	public static Color call(double arg1, double arg2, double arg3, double arg4)
	{
		return Color.fromrgb(arg1, arg2, arg3, arg4);
	}

	public static final Function function = new FunctionRGB();
}
