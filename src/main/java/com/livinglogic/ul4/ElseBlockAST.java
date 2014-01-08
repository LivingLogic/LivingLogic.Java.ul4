/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class ElseBlockAST extends ConditionalBlock
{
	public ElseBlockAST(Location location, int start, int end)
	{
		super(location, start, end);
	}

	public String getType()
	{
		return "elseblock";
	}

	public boolean hasToBeExecuted(EvaluationContext context)
	{
		return true;
	}

	public void toString(Formatter formatter)
	{
		formatter.write("else:");
		formatter.lf();
		formatter.indent();
		super.toString(formatter);
		formatter.dedent();
	}
}