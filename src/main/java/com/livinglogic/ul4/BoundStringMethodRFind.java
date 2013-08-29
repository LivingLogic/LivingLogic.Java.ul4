/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public class BoundStringMethodRFind extends BoundMethod<String>
{
	private static final Signature signature = new Signature("rfind", "sub", Signature.required, "start", null, "end", null);

	public BoundStringMethodRFind(String object)
	{
		super(object);
	}

	public Signature getSignature()
	{
		return signature;
	}

	public static int call(String object, String sub)
	{
		return object.lastIndexOf(sub);
	}

	public static int call(String object, String sub, int start)
	{
		start = Utils.getSliceStartPos(object.length(), start);
		int result = object.lastIndexOf(sub);
		if (result < start)
			return -1;
		return result;
	}

	public static int call(String object, String sub, int start, int end)
	{
		start = Utils.getSliceStartPos(object.length(), start);
		end = Utils.getSliceStartPos(object.length(), end);
		end -= sub.length();
		if (end < 0)
			return -1;
		int result = object.lastIndexOf(sub, end);
		if (result < start)
			return -1;
		return result;
	}

	public Object callUL4(Object[] args, Map<String, Object> kwargs)
	{
		args = signature.makeArgumentArray(args, kwargs);

		if (args[0] instanceof String)
		{
			int startIndex = args[1] != null ? Utils.toInt(args[1]) : 0;
			int endIndex = args[2] != null ? Utils.toInt(args[2]) : object.length();
			return call(object, (String)args[0], startIndex, endIndex);
		}
		throw new ArgumentTypeMismatchException("{}.rfind({}, {}, {})", object, args[0], args[1], args[2]);
	}
}
