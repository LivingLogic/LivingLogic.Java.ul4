/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FloorDivVarAST extends ChangeVarAST
{
	public FloorDivVarAST(Tag tag, int start, int end, LValue lvalue, AST value)
	{
		super(tag, start, end, lvalue, value);
	}

	public String getType()
	{
		return "floordivvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateFloorDiv(context, value.decoratedEvaluate(context));
		return null;
	}
}
