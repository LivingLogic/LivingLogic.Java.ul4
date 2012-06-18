/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodStrip implements Method
{
	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 0:
				return Utils.strip(obj);
			case 1:
				return Utils.strip(obj, args[0]);
			default:
				throw new ArgumentCountMismatchException("method", "strip", args.length, 0, 1);
		}
	}

	public String getName()
	{
		return "strip";
	}
}
