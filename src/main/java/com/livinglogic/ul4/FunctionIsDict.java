/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class FunctionIsDict extends NormalFunction
{
	public String getName()
	{
		return "isdict";
	}

	protected void makeSignature(Signature signature)
	{
		signature.add("obj");
	}

	public Object evaluate(EvaluationContext context, Object[] args)
	{
		return call(args[0]);
	}

	public static boolean call(Object obj)
	{
		return (null != obj) && (obj instanceof Map) && !(obj instanceof Template);
	}
}
