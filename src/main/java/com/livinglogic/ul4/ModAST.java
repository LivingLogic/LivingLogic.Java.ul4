/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ModAST extends BinaryAST
{
	protected static class Type extends BinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "ModAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.mod";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a binary modulo expression (e.g. ``x % y``).";
		}

		@Override
		public ModAST create(String id)
		{
			return new ModAST(null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ModAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public ModAST(Template template, Slice pos, CodeAST obj1, CodeAST obj2)
	{
		super(template, pos, obj1, obj2);
	}

	public String getType()
	{
		return "mod";
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static int call(EvaluationContext context, int arg1, int arg2)
	{
		int div = arg1 / arg2;
		int mod = arg1 - div * arg2;

		if (mod != 0 && ((arg2 < 0 && mod > 0) || (arg2 > 0 && mod < 0)))
		{
			mod += arg2;
			--div;
		}
		return arg1 - div * arg2;
	}

	public static long call(EvaluationContext context, long arg1, long arg2)
	{
		long div = arg1 / arg2;
		long mod = arg1 - div * arg2;

		if (mod != 0 && ((arg2 < 0 && mod > 0) || (arg2 > 0 && mod < 0)))
		{
			mod += arg2;
			--div;
		}
		return arg1 - div * arg2;
	}

	// No version public static float call(EvaluationContext context, float arg1, float arg2)

	public static double call(EvaluationContext context, double arg1, double arg2)
	{
		double div = Math.floor(arg1 / arg2);
		double mod = arg1 - div * arg2;

		if (mod != 0 && ((arg2 < 0 && mod > 0) || (arg2 > 0 && mod < 0)))
		{
			mod += arg2;
			--div;
		}
		return arg1 - div * arg2;
	}

	public static BigInteger call(EvaluationContext context, BigInteger arg1, BigInteger arg2)
	{
		return arg1.mod(arg2); // FIXME: negative numbers?
	}

	public static BigDecimal call(EvaluationContext context, BigDecimal arg1, BigDecimal arg2)
	{
		return arg1.remainder(arg2); // FIXME: negative numbers?
	}

	public static Object call(EvaluationContext context, Object arg1, Object arg2)
	{
		if (arg1 instanceof Integer || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(context, Utils.toInt(arg1), Utils.toInt(arg2));
			else if (arg2 instanceof Long)
				return call(context, Utils.toLong(arg1), Utils.toLong(arg2));
			else if (arg2 instanceof Float)
				return call(context, Utils.toDouble(arg1), Utils.toDouble(arg2));
			else if (arg2 instanceof Double)
				return call(context, Utils.toDouble(arg1), Utils.toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return call(context, Utils.toBigInteger(Utils.toInt(arg1)), (BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return call(context, new BigDecimal(Utils.toDouble(arg1)), (BigDecimal)arg2);
		}
		else if (arg1 instanceof Long)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(context, Utils.toLong(arg1), Utils.toLong(arg2));
			else if (arg2 instanceof Float)
				return call(context, Utils.toDouble(arg1), Utils.toDouble(arg2));
			else if (arg2 instanceof Double)
				return call(context, Utils.toDouble(arg1), Utils.toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return call(context, Utils.toBigInteger(Utils.toLong(arg1)), (BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return call(context, new BigDecimal(Utils.toDouble(arg1)), (BigDecimal)arg2);
		}
		else if (arg1 instanceof Float)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float)
				return call(context, Utils.toDouble(arg1), Utils.toDouble(arg2));
			else if (arg2 instanceof Double)
				return call(context, Utils.toDouble(arg1), (((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return call(context, new BigDecimal(Utils.toDouble(arg1)), new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return call(context, new BigDecimal(Utils.toDouble(arg1)), (BigDecimal)arg2);
		}
		else if (arg1 instanceof Double)
		{
			double value1 = (((Double)arg1).doubleValue());
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return call(context, value1, Utils.toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return call(context, new BigDecimal(value1), new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return call(context, new BigDecimal(value1), ((BigDecimal)arg2));
		}
		else if (arg1 instanceof BigInteger)
		{
			BigInteger value1 = (BigInteger)arg1;
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(context, value1, Utils.toBigInteger(Utils.toInt(arg2)));
			else if (arg2 instanceof Long)
				return call(context, value1, Utils.toBigInteger(Utils.toLong(arg2)));
			else if (arg2 instanceof Float)
				return call(context, new BigDecimal(value1), new BigDecimal(((Float)arg2).doubleValue()));
			else if (arg2 instanceof Double)
				return call(context, new BigDecimal(value1), new BigDecimal(((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return call(context, value1, (BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return call(context, new BigDecimal(value1), (BigDecimal)arg2);
		}
		else if (arg1 instanceof BigDecimal)
		{
			BigDecimal value1 = (BigDecimal)arg1;
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call(context, value1, Utils.toBigDecimal(Utils.toInt(arg2)));
			else if (arg2 instanceof Long)
				return call(context, value1, Utils.toBigDecimal(Utils.toLong(arg2)));
			else if (arg2 instanceof Float)
				return call(context, value1, new BigDecimal(((Float)arg2).doubleValue()));
			else if (arg2 instanceof Double)
				return call(context, value1, new BigDecimal(((Double)arg2).doubleValue()));
			else if (arg2 instanceof BigInteger)
				return call(context, value1, new BigDecimal((BigInteger)arg2));
			else if (arg2 instanceof BigDecimal)
				return call(context, value1, (BigDecimal)arg2);
		}
		else if (arg1 instanceof Color && arg2 instanceof Color)
			return ((Color)arg1).blend((Color)arg2);
		throw new ArgumentTypeMismatchException("{!t} % {!t} not supported", arg1, arg2);
	}
}
