/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class FunctionStr extends Function
{
	public String nameUL4()
	{
		return "str";
	}

	private static final Signature signature = new Signature("obj", null);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0));
	}

	public static SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat formatterDatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat formatterTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS'000'");

	public static String call()
	{
		return "";
	}

	public static String call(Object obj)
	{
		if (obj == null)
			return "";
		else if (obj instanceof Undefined)
			return "";
		else if (obj instanceof Boolean)
			return ((Boolean)obj).booleanValue() ? "True" : "False";
		else if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short || obj instanceof Long || obj instanceof BigInteger)
			return obj.toString();
		else if (obj instanceof Double || obj instanceof Float)
			return StringUtils.replace(obj.toString(), ".0E", "E").toLowerCase();
		else if (obj instanceof BigDecimal)
		{
			String result = obj.toString();
			if (result.indexOf('.') < 0 && result.indexOf('E') < 0 && result.indexOf('e') < 0)
				result += ".0";
			return result;
		}
		else if (obj instanceof String)
			return (String)obj;
		else if (obj instanceof Date)
		{
			if (BoundDateMethodMicrosecond.call((Date)obj) != 0)
				return formatterTimestamp.format(obj);
			else
				return formatterDatetime.format(obj);
		}
		else if (obj instanceof Color)
			return obj.toString();
		else if (obj instanceof TimeDelta)
			return obj.toString();
		else if (obj instanceof MonthDelta)
			return obj.toString();
		else if (obj instanceof Signature)
			return obj.toString();
		else if (obj instanceof Throwable)
		{
			String message = ((Throwable)obj).getLocalizedMessage();
			return message != null ? message : "";
		}
		else
			return FunctionRepr.call(obj);
	}
}
