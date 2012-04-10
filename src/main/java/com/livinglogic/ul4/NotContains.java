/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class NotContains extends Binary
{
	public NotContains(AST obj1, AST obj2)
	{
		super(obj1, obj2);
	}

	public int getType()
	{
		return Opcode.OC_NOTCONTAINS;
	}

	public Object evaluate(EvaluationContext context)
	{
		return Utils.notcontains(obj1.evaluate(context), obj2.evaluate(context));
	}
}
