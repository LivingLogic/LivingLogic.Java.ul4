/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class AddVar extends ChangeVar
{
	public AddVar(int start, int end, Name name, AST value)
	{
		super(start, end, name, value);
	}

	public int getType()
	{
		return Opcode.OC_ADDVAR;
	}
}
