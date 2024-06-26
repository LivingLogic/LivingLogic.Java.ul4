/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class ModVarAST extends ChangeVarAST
{
	protected static class Type extends ChangeVarAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "ModVarAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.modvar";
		}

		@Override
		public String getDoc()
		{
			return "AST node for an augmented assignment expression that assigns the result\nof a modulo expression to its left operand. (e.g. ``x %= y``).";
		}

		@Override
		public ModVarAST create(String id)
		{
			return new ModVarAST(null, -1, -1, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ModVarAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public ModVarAST(Template template, int posStart, int posStop, LValue lvalue, AST value)
	{
		super(template, posStart, posStop, lvalue, value);
	}

	public String getType()
	{
		return "modvar";
	}

	public Object evaluate(EvaluationContext context)
	{
		lvalue.evaluateMod(context, value.decoratedEvaluate(context));
		return null;
	}
}
