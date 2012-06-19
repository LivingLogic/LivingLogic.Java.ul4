/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigInteger;

public class FunctionHex implements Function
{
	public String getName()
	{
		return "hex";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "hex", args.length, 1);
	}

	public static Object call(Object obj)
	{
		if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short)
		{
			int value = ((Number)obj).intValue();
			if (value < 0)
				return "-0x" + Integer.toHexString(-value);
			else
				return "0x" + Integer.toHexString(value);
		}
		else if (obj instanceof Boolean)
		{
			return ((Boolean)obj).booleanValue() ? "0x1" : "0x0";
		}
		else if (obj instanceof Long)
		{
			long value = ((Long)obj).longValue();
			if (value < 0)
				return "-0x" + Long.toHexString(-value);
			else
				return "0x" + Long.toHexString(value);
		}
		else if (obj instanceof BigInteger)
		{
			BigInteger bi = (BigInteger)obj;
			if (bi.signum() < 0)
			{
				return "-0x" + bi.toString(16).substring(1);
			}
			else
				return "0x" + bi.toString(16);
		}
		throw new UnsupportedOperationException("hex(" + Utils.objectType(obj) + ") not supported!");
	}
}
