/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.math.BigDecimal;
import java.math.BigInteger;

public class FunctionAbs extends Function
{
	@Override
	public String getNameUL4()
	{
		return "abs";
	}

	private static final Signature signature = new Signature().addPositionalOnly("number");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0));
	}

	public static Object call(Object arg)
	{
		if (arg instanceof Integer)
		{
			int value = ((Integer)arg).intValue();
			if (value >= 0)
				return arg;
			else if (value == -0x80000000) // Prevent overflow by switching to long
				return 0x80000000L;
			else
				return -value;
		}
		else if (arg instanceof Long)
		{
			long value = ((Long)arg).longValue();
			if (value >= 0)
				return arg;
			else if (value == -0x8000000000000000L) // Prevent overflow by switching to BigInteger
				return new BigInteger("8000000000000000", 16);
			else
				return -value;
		}
		else if (arg instanceof Boolean)
			return ((Boolean)arg).booleanValue() ? 1 : 0;
		else if (arg instanceof Byte || arg instanceof Short)
		{
			int value = ((Number)arg).intValue();
			if (value >= 0)
				return arg;
			else
				return -value;
		}
		else if (arg instanceof Float)
		{
			float value = ((Float)arg).floatValue();
			if (value >= 0)
				return arg;
			else
				return -value;
		}
		else if (arg instanceof Double)
		{
			double value = ((Double)arg).doubleValue();
			if (value >= 0)
				return arg;
			else
				return -value;
		}
		else if (arg instanceof BigInteger)
			return ((BigInteger)arg).abs();
		else if (arg instanceof BigDecimal)
			return ((BigDecimal)arg).abs();
		else if (arg instanceof UL4Abs)
			return ((UL4Abs)arg).absUL4();
		throw new ArgumentTypeMismatchException("abs({!t}) not supported", arg);
	}

	public static final Function function = new FunctionAbs();
}
