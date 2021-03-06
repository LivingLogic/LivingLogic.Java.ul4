/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundListMethodPop extends BoundMethod<List>
{
	public BoundListMethodPop(List object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "pop";
	}

	private static final Signature signature = new Signature().addPositionalOnly("pos", -1);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static Object call(EvaluationContext context, List obj, int pos)
	{
		if (pos < 0)
			pos += obj.size();
		return obj.remove(pos);
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, object, Utils.toInt(args.get(0)));
	}
}
