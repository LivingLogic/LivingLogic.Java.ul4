/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionChr implements Function
{
	public static Object call(Object obj)
	{
		if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short)
		{
			int intValue = ((Number)obj).intValue();
			char charValue = (char)intValue;
			if (intValue != (int)charValue)
			{
				throw new IndexOutOfBoundsException("Code point " + intValue + " is invalid!");
			}
			return String.valueOf(charValue);
		}
		else if (obj instanceof Boolean)
		{
			return ((Boolean)obj).booleanValue() ? "\u0001" : "\u0000";
		}
		else if (obj instanceof Long)
		{
			long longValue = ((Long)obj).longValue();
			char charValue = (char)longValue;
			if (longValue != (long)charValue)
			{
				throw new IndexOutOfBoundsException("Code point " + longValue + " is invalid!");
			}
			return String.valueOf(charValue);
		}
		// FIXME: Add support for BigInteger
		throw new UnsupportedOperationException("chr(" + Utils.objectType(obj) + ") not supported!");
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "chr", args.length, 1);
	}

	public String getName()
	{
		return "chr";
	}
}
