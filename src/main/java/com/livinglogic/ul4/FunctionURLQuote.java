/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FunctionURLQuote extends Function
{
	@Override
	public String getNameUL4()
	{
		return "urlquote";
	}

	private static final Signature signature = new Signature("string", Signature.required);

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

	public static Object call(String obj)
	{
		try
		{
			return URLEncoder.encode(obj, "utf-8");
		}
		catch (UnsupportedEncodingException ex)
		{
			// Can't happen
			throw new RuntimeException(ex);
		}
	}

	public static Object call(Object obj)
	{
		if (obj instanceof String)
			return call((String)obj);
		throw new ArgumentTypeMismatchException("urlquote({!t}) not supported", obj);
	}

	public static Function function = new FunctionURLQuote();
}
