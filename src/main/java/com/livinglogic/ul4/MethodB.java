/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodB extends NormalMethod
{
	public String getName()
	{
		return "b";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args) throws IOException
	{
		return call(obj);
	}

	public static int call(Color obj)
	{
		return obj.getB();
	}

	public static int call(Object obj)
	{
		if (obj instanceof Color)
			return call((Color)obj);
		throw new ArgumentTypeMismatchException("{}.b()", obj);
	}
}
