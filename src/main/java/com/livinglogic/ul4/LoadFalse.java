/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class LoadFalse extends LoadConst
{
	public LoadFalse()
	{
	}

	public int getType()
	{
		return Opcode.OC_LOADFALSE;
	}

	public String getTokenType()
	{
		return "false";
	}

	public Object getValue()
	{
		return Boolean.FALSE;
	}

	public String toString()
	{
		return "constant False";
	}
}
