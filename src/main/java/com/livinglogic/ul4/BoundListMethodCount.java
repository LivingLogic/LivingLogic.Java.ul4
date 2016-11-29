/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.List;

public class BoundListMethodCount extends BoundMethod<List>
{
	public BoundListMethodCount(List object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "list.find";
	}

	private static final Signature signature = new Signature("sub", Signature.required, "start", null, "end", null);

	public Signature getSignature()
	{
		return signature;
	}

	public static int call(List object, Object sub)
	{
		return call(object, sub, 0, object.size());
	}

	public static int call(List object, Object sub, int start)
	{
		return call(object, sub, start, object.size());
	}

	public static int call(List object, Object sub, int start, int end)
	{
		start = Utils.getSliceStartPos(object.size(), start);
		end = Utils.getSliceEndPos(object.size(), end);

		int count = 0;
		for (int i = start; i < end; ++i)
		{
			if (EQAST.call(object.get(i), sub))
				++count;
		}
		return count;
	}

	public Object evaluate(BoundArguments args)
	{
		int startIndex = args.get(1) != null ? Utils.toInt(args.get(1)) : 0;
		int endIndex = args.get(2) != null ? Utils.toInt(args.get(2)) : object.size();

		return call(object, args.get(0), startIndex, endIndex);
	}
}