/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class TrueDivAST extends BinaryAST
{
	protected static class Type extends BinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "TrueDivAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.truediv";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a binary true division expression (e.g. ``x / y``).";
		}

		@Override
		public TrueDivAST create(String id)
		{
			return new TrueDivAST(null, -1, -1, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof TrueDivAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public TrueDivAST(Template template, int posStart, int posStop, CodeAST obj1, CodeAST obj2)
	{
		super(template, posStart, posStop, obj1, obj2);
	}

	public String getType()
	{
		return "truediv";
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(context, obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static Object call(EvaluationContext context, Object arg1, Object arg2)
	{
		if (arg1 instanceof Integer || arg1 instanceof Long || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean || arg1 instanceof Float || arg1 instanceof Double)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short)
			{
				Utils.checkZeroDivisorInteger((Number)arg2);
				return Utils.toDouble(arg1) / Utils.toDouble(arg2);
			}
			if (arg2 instanceof Boolean)
			{
				Utils.checkZeroDivisorBoolean((Boolean)arg2);
				return Utils.toDouble(arg1) / Utils.toDouble(arg2);
			}
			if (arg2 instanceof Float || arg2 instanceof Double)
			{
				Utils.checkZeroDivisorFloat((Number)arg2);
				return Utils.toDouble(arg1) / Utils.toDouble(arg2);
			}
			else if (arg2 instanceof BigInteger)
			{
				Utils.checkZeroDivisorBigInteger((BigInteger)arg2);
				return new BigDecimal(Utils.toDouble(arg1)).divide(new BigDecimal((BigInteger)arg2), MathContext.DECIMAL128);
			}
			else if (arg2 instanceof BigDecimal)
			{
				Utils.checkZeroDivisorBigDecimal((BigDecimal)arg2);
				return new BigDecimal(Utils.toDouble(arg1)).divide((BigDecimal)arg2, MathContext.DECIMAL128);
			}
		}
		else if (arg1 instanceof BigInteger)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return new BigDecimal((BigInteger)arg1).divide(new BigDecimal(Utils.toDouble(arg2)), MathContext.DECIMAL128);
			else if (arg2 instanceof BigInteger)
				return new BigDecimal((BigInteger)arg1).divide(new BigDecimal((BigInteger)arg2), MathContext.DECIMAL128);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal((BigInteger)arg1).divide((BigDecimal)arg2, MathContext.DECIMAL128);
		}
		else if (arg1 instanceof BigDecimal)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return ((BigDecimal)arg1).divide(new BigDecimal(Utils.toDouble(arg2)), MathContext.DECIMAL128);
			else if (arg2 instanceof BigInteger)
				return ((BigDecimal)arg1).divide(new BigDecimal((BigInteger)arg2), MathContext.DECIMAL128);
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg1).divide((BigDecimal)arg2, MathContext.DECIMAL128);
		}
		else if (arg1 instanceof TimeDelta)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return ((TimeDelta)arg1).truediv(Utils.toInt(arg2));
			else if (arg2 instanceof Long)
				return ((TimeDelta)arg1).truediv(Utils.toLong(arg2));
			else if (arg2 instanceof Float)
				return ((TimeDelta)arg1).truediv(Utils.toFloat(arg2));
			else if (arg2 instanceof Double)
				return ((TimeDelta)arg1).truediv(Utils.toDouble(arg2));
			else if (arg2 instanceof BigInteger)
				return ((TimeDelta)arg1).truediv((BigInteger)arg2);
			else if (arg2 instanceof BigDecimal)
				return ((TimeDelta)arg1).truediv((BigDecimal)arg2);
			else if (arg2 instanceof TimeDelta)
				return ((TimeDelta)arg1).truediv((TimeDelta)arg2);
		}
		else if (arg1 instanceof MonthDelta)
		{
			if (arg2 instanceof MonthDelta)
				return ((MonthDelta)arg1).truediv((MonthDelta)arg2);
		}
		throw new ArgumentTypeMismatchException("{!t} / {!t} not supported", arg1, arg2);
	}

}
