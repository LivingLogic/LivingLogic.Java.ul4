/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

class IfBlockAST extends ConditionalBlockWithCondition
{
	public IfBlockAST(Tag tag, int start, int end, AST condition)
	{
		super(tag, start, end, condition);
	}

	public String getType()
	{
		return "ifblock";
	}
}
