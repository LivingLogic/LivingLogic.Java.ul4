/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

public class MethodStrip implements Method
{
	public String getName()
	{
		return "strip";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 0:
				return call(obj);
			case 1:
				return call(obj, args[0]);
			default:
				throw new ArgumentCountMismatchException("method", "strip", args.length, 0, 1);
		}
	}

	public static String call(String obj)
	{
		return StringUtils.strip(obj);
	}

	public static String call(Object obj)
	{
		if (obj instanceof String)
			return call((String)obj);
		throw new ArgumentTypeMismatchException("{}.strip()", obj);
	}

	public static String call(String obj, String stripChars)
	{
		return StringUtils.strip(obj, stripChars);
	}

	public static String call(Object obj, Object stripChars)
	{
		if (obj instanceof String && stripChars instanceof String)
			return call((String)obj, (String)stripChars);
		throw new ArgumentTypeMismatchException("{}.strip({})", obj, stripChars);
	}
}
