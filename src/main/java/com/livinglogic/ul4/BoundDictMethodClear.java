/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundDictMethodClear extends BoundMethod<Map>
{
	public BoundDictMethodClear(Map object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "clear";
	}

	public static void call(Map object)
	{
		object.clear();
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		call(object);
		return null;
	}
}
