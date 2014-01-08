/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class MulVarAST extends ChangeVarAST
{
	public MulVarAST(Location location, int start, int end, LValue lvalue, AST value)
	{
		super(location, start, end, lvalue, value);
	}

	public String getType()
	{
		return "mulvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateMul(context, value.decoratedEvaluate(context));
		return null;
	}
}