/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


public class MulAST extends BinaryAST
{
	protected static class Type extends BinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "MulAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.mul";
		}

		@Override
		public String getDoc()
		{
			return "AST node for the binary multiplication expression (e.g. ``x * y``).";
		}

		@Override
		public MulAST create(String id)
		{
			return new MulAST(null, -1, -1, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof MulAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public MulAST(Template template, int posStart, int posStop, CodeAST obj1, CodeAST obj2)
	{
		super(template, posStart, posStop, obj1, obj2);
	}

	public String getType()
	{
		return "mul";
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static String call(EvaluationContext context, int arg1, String arg2)
	{
		return StringUtils.repeat(arg2, arg1);
	}

	public static String call(EvaluationContext context, long arg1, String arg2)
	{
		if (((int)arg1) != arg1)
			throw new ArgumentTypeMismatchException("{!t} * {!t} not supported", arg1, arg2);
		return StringUtils.repeat(arg2, (int)arg1);
	}

	public static List call(EvaluationContext context, int arg1, List arg2)
	{
		ArrayList result = new ArrayList();

		for (;arg1>0;--arg1)
			result.addAll(arg2);
		return result;
	}

	public static List call(EvaluationContext context, long arg1, List arg2)
	{
		ArrayList result = new ArrayList();

		for (;arg1>0;--arg1)
			result.addAll(arg2);
		return result;
	}

	public static Object call(EvaluationContext context, int arg1, int arg2)
	{
		if (arg1 == 0 || arg2 == 0)
			return 0;
		int result = arg1 * arg2;
		if (result/arg1 == arg2) // result doesn't seem to have overflowed
			return result;
		else // we had an overflow => promote to BigInteger
			return Utils.toBigInteger(arg1).multiply(Utils.toBigInteger(arg2));
	}

	public static Object call(EvaluationContext context, long arg1, long arg2)
	{
		if (arg1 == 0 || arg2 == 0)
			return 0;
		long result = arg1 * arg2;
		if (result/arg1 == arg2) // result doesn't seem to have overflowed
			return result;
		else // we had an overflow => promote to BigInteger
			return Utils.toBigInteger(arg1).multiply(Utils.toBigInteger(arg2));
	}

	public static Object call(EvaluationContext context, float arg1, float arg2)
	{
		// FIXME: Overflow check
		return arg1 * arg2;
	}

	public static Object call(EvaluationContext context, double arg1, double arg2)
	{
		// FIXME: Overflow check
		return arg1 * arg2;
	}

	public static Object call(EvaluationContext context, int arg1, TimeDelta arg2)
	{
		return arg2.mul(arg1);
	}

	public static Object call(EvaluationContext context, long arg1, TimeDelta arg2)
	{
		return arg2.mul(arg1);
	}

	public static Object call(EvaluationContext context, float arg1, TimeDelta arg2)
	{
		return arg2.mul(arg1);
	}

	public static Object call(EvaluationContext context, double arg1, TimeDelta arg2)
	{
		return arg2.mul(arg1);
	}

	public static Object call(EvaluationContext context, TimeDelta arg1, int arg2)
	{
		return arg1.mul(arg2);
	}

	public static Object call(EvaluationContext context, TimeDelta arg1, long arg2)
	{
		return arg1.mul(arg2);
	}

	public static Object call(EvaluationContext context, TimeDelta arg1, float arg2)
	{
		return arg1.mul(arg2);
	}

	public static Object call(EvaluationContext context, TimeDelta arg1, double arg2)
	{
		return arg1.mul(arg2);
	}

	public static Object call(EvaluationContext context, int arg1, MonthDelta arg2)
	{
		return arg2.mul(arg1);
	}

	public static Object call(EvaluationContext context, long arg1, MonthDelta arg2)
	{
		return arg2.mul(arg1);
	}

	public static Object call(EvaluationContext context, MonthDelta arg1, int arg2)
	{
		return arg1.mul(arg2);
	}

	public static Object call(EvaluationContext context, MonthDelta arg1, long arg2)
	{
		return arg1.mul(arg2);
	}

	public static Object call(EvaluationContext context, Object arg1, Object arg2)
	{
		if (arg1 instanceof Integer || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(context, Utils.toInt(arg1), Utils.toInt(arg2));
			else if (arg2 instanceof Long)
				return call(context, Utils.toLong(arg1), ((Long)arg2).longValue());
			else if (arg2 instanceof Float)
				return call(context, Utils.toFloat(arg1), ((Float)arg2).floatValue());
			else if (arg2 instanceof Double)
				return call(context, Utils.toDouble(arg1), ((Double)arg2).doubleValue());
			else if (arg2 instanceof String)
				return call(context, Utils.toInt(arg1), (String)arg2);
			else if (arg2 instanceof BigInteger)
				return ((BigInteger)arg2).multiply(Utils.toBigInteger(Utils.toInt(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).multiply(new BigDecimal(Utils.toDouble(arg1)));
			else if (arg2 instanceof List)
				return call(context, Utils.toInt(arg1), (List)arg2);
			else if (arg2 instanceof TimeDelta)
				return call(context, Utils.toInt(arg1), (TimeDelta)arg2);
			else if (arg2 instanceof MonthDelta)
				return call(context, Utils.toInt(arg1), (MonthDelta)arg2);
		}
		else if (arg1 instanceof Long)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(context, Utils.toLong(arg1), Utils.toLong(arg2));
			else if (arg2 instanceof Float)
				return call(context, Utils.toFloat(arg1), ((Float)arg2).floatValue());
			else if (arg2 instanceof Double)
				return call(context, Utils.toDouble(arg1), ((Double)arg2).doubleValue());
			else if (arg2 instanceof String)
				return call(context, Utils.toInt(arg1), (String)arg2);
			else if (arg2 instanceof BigInteger)
				return ((BigInteger)arg2).multiply(Utils.toBigInteger(Utils.toLong(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).multiply(new BigDecimal(Utils.toDouble(arg1)));
			else if (arg2 instanceof List)
				return call(context, Utils.toLong(arg1), (List)arg2);
			else if (arg2 instanceof TimeDelta)
				return call(context, Utils.toLong(arg1), (TimeDelta)arg2);
			else if (arg2 instanceof MonthDelta)
				return call(context, Utils.toLong(arg1), (MonthDelta)arg2);
		}
		else if (arg1 instanceof Float)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float)
				return call(context, Utils.toFloat(arg1), Utils.toFloat(arg2));
			else if (arg2 instanceof Double)
				return call(context, Utils.toDouble(arg1), (((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal((BigInteger)arg2).multiply(new BigDecimal(Utils.toDouble(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).multiply(new BigDecimal(Utils.toDouble(arg1)));
			else if (arg2 instanceof TimeDelta)
				return call(context, Utils.toFloat(arg1), (TimeDelta)arg2);
		}
		else if (arg1 instanceof Double)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return call(context, Utils.toDouble(arg1), Utils.toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return new BigDecimal((BigInteger)arg2).multiply(new BigDecimal(Utils.toDouble(arg1)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).multiply(new BigDecimal(Utils.toDouble(arg1)));
			else if (arg2 instanceof TimeDelta)
				return call(context, Utils.toDouble(arg1), (TimeDelta)arg2);
		}
		else if (arg1 instanceof BigInteger)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return ((BigInteger)arg1).multiply(Utils.toBigInteger(Utils.toInt(arg2)));
			else if (arg2 instanceof Long)
				return ((BigInteger)arg1).multiply(Utils.toBigInteger(Utils.toLong(arg2)));
			else if (arg2 instanceof Float)
				return new BigDecimal((BigInteger)arg1).multiply(new BigDecimal(((Float)arg2).doubleValue()));
			else if (arg2 instanceof Double)
				return new BigDecimal((BigInteger)arg1).multiply(new BigDecimal(((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return ((BigInteger)arg1).multiply(((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg2).multiply(new BigDecimal((BigInteger)arg1));
		}
		else if (arg1 instanceof BigDecimal)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return ((BigDecimal)arg1).multiply(new BigDecimal(Utils.toDouble(arg2)));
			else if (arg2 instanceof BigInteger)
				return ((BigDecimal)arg1).multiply(new BigDecimal(((BigInteger)arg2)));
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg1).multiply(((BigDecimal)arg2));
		}
		else if (arg1 instanceof String)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(context, Utils.toInt(arg2), (String)arg1);
			else if (arg2 instanceof Long)
				return call(context, Utils.toLong(arg2), (String)arg1);
		}
		else if (arg1 instanceof List)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(context, Utils.toInt(arg2), (List)arg1);
			else if (arg2 instanceof Long)
				return call(context, Utils.toLong(arg2), (List)arg1);
		}
		else if (arg1 instanceof TimeDelta)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(context, (TimeDelta)arg1, Utils.toInt(arg2));
			else if (arg2 instanceof Long)
				return call(context, (TimeDelta)arg1, Utils.toLong(arg2));
			else if (arg2 instanceof Float || arg2 instanceof Double)
				return call(context, (TimeDelta)arg1, Utils.toDouble(arg2));
		}
		else if (arg1 instanceof MonthDelta)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(context, (MonthDelta)arg1, Utils.toInt(arg2));
			else if (arg2 instanceof Long)
				return call(context, (MonthDelta)arg1, Utils.toLong(arg2));
		}
		throw new ArgumentTypeMismatchException("{!t} * {!t} not supported", arg1, arg2);
	}
}
