/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodLum extends NormalMethod
{
	public String getName()
	{
		return "lum";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args) throws IOException
	{
		return call(obj);
	}

	public static double call(Color obj)
	{
		return obj.lum();
	}

	public static double call(Object obj)
	{
		if (obj instanceof Color)
			return call((Color)obj);
		throw new ArgumentTypeMismatchException("{}.lum()", obj);
	}
}
