/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.List;

public class BoundListMethodFind extends BoundMethod<List>
{
	public BoundListMethodFind(List object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "find";
	}

	private static final Signature signature = new Signature().addPositionalOnly("sub").addPositionalOnly("start", null).addPositionalOnly("end", null);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static int call(EvaluationContext context, List object, Object sub)
	{
		return object.indexOf(sub);
	}

	public static int call(EvaluationContext context, List object, Object sub, int start)
	{
		start = Utils.getSliceStartPos(object.size(), start);
		if (start != 0)
			object = object.subList(start, object.size());
		int pos = object.indexOf(sub);
		if (pos != -1)
			pos += start;
		return pos;
	}

	public static int call(EvaluationContext context, List object, Object sub, int start, int end)
	{
		start = Utils.getSliceStartPos(object.size(), start);
		end = Utils.getSliceEndPos(object.size(), end);
		if (start != 0 || end != object.size())
			object = object.subList(start, end);
		int pos = object.indexOf(sub);
		if (pos != -1)
			pos += start;
		return pos;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		int startIndex = args.get(1) != null ? Utils.toInt(args.get(1)) : 0;
		int endIndex = args.get(2) != null ? Utils.toInt(args.get(2)) : object.size();

		return call(context, object, args.get(0), startIndex, endIndex);
	}
}
