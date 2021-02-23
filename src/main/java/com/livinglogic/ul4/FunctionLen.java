/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Collection;
import java.util.Map;

public class FunctionLen extends Function
{
	@Override
	public String getNameUL4()
	{
		return "len";
	}

	private static final Signature signature = new Signature("sequence", Signature.required);

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

	public static Object call(Object obj)
	{
		return Proto.get(obj).len(obj);
	}

	public static Function function = new FunctionLen();
}
