/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class GTAST extends BinaryAST
{
	public GTAST(Tag tag, Slice pos, CodeAST obj1, CodeAST obj2)
	{
		super(tag, pos, obj1, obj2);
	}

	public String getType()
	{
		return "gt";
	}

	public static CodeAST make(Tag tag, Slice pos, CodeAST obj1, CodeAST obj2)
	{
		if (obj1 instanceof ConstAST && obj2 instanceof ConstAST)
		{
			Object result = call(((ConstAST)obj1).value, ((ConstAST)obj2).value);
			if (!(result instanceof Undefined))
				return new ConstAST(tag, pos, result);
		}
		return new GTAST(tag, pos, obj1, obj2);
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static boolean call(Object obj1, Object obj2)
	{
		return Utils.cmp(obj1, obj2, ">") > 0;
	}
}
