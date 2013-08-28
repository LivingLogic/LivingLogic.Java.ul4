/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class BoundStringMethodRSplit extends BoundMethod<String>
{
	private static Signature signature = new Signature("rsplit", "sep", null, "count", null);

	public BoundStringMethodRSplit(String object)
	{
		super(object);
	}

	public Signature getSignature()
	{
		return signature;
	}

	public static List<String> call(String object)
	{
		return Arrays.asList(StringUtils.split(object));
	}

	public static List<String> call(String object, int maxsplit)
	{
		ArrayList<String> result = new ArrayList<String>();
		int start, end;
		start = end = object.length() - 1;
		while (maxsplit-- > 0)
		{
			while (start >= 0 && Character.isWhitespace(object.charAt(start)))
				--start;
			if (start < 0)
				break;
			end = start--;
			while (start >= 0 && !Character.isWhitespace(object.charAt(start)))
				--start;
			if (start != end)
				result.add(0, object.substring(start+1, end+1));
		}
		if (start >= 0)
		{
			while (start >= 0 && Character.isWhitespace(object.charAt(start)))
				--start;
			if (start >= 0)
				result.add(0, object.substring(0, start+1));
		}
		return result;
	}

	public static List<String> call(String object, Object separator)
	{
		if (separator == null)
			return call(object);
		else if (separator instanceof String)
			return call(object, (String)separator, 0x7fffffff);
		throw new ArgumentTypeMismatchException("{}.rsplit({})", object, separator);
	}

	public static List<String> call(String object, String separator, int maxsplit)
	{
		if (separator.length() == 0)
			throw new UnsupportedOperationException("empty separator not supported");

		ArrayList<String> result = new ArrayList<String>();
		int start = object.length(), end = start, seplen = separator.length();
		while (maxsplit-- > 0)
		{
			start = object.lastIndexOf(separator, end-seplen);
			if (start < 0)
				break;
			result.add(0, object.substring(start+seplen, end));
			end = start;
		}
		result.add(0, object.substring(0, end));
		return result;
	}

	public Object callUL4(Object[] args, Map<String, Object> kwargs)
	{
		args = signature.makeArgumentArray(args, kwargs);

		Object separator = args[0];
		Object maxsplit = args[1];

		if (maxsplit == null)
		{
			if (separator == null)
				return call(object);
			else if (separator instanceof String)
				return call(object, (String)separator);
		}
		else
		{
			if (separator == null)
				return call(object, Utils.toInt(maxsplit));
			else if (separator instanceof String)
				return call(object, (String)separator, Utils.toInt(maxsplit));
		}
		throw new ArgumentTypeMismatchException("{}.rsplit({}, {})", object, separator, maxsplit);
	}
}
