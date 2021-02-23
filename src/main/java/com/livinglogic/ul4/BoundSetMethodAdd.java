/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Set;

public class BoundSetMethodAdd extends BoundMethod<Set>
{
	public BoundSetMethodAdd(Set object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "add";
	}

	private static final Signature signature = new Signature("object", Signature.remainingParameters);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static void call(Set set, List<Object> objects)
	{
		set.addAll(objects);
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		call(object, (List<Object>)args.get(0));
		return null;
	}
}
