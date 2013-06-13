/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class UndefinedVariable extends Undefined
{
	private String varname;

	public UndefinedVariable(String varname)
	{
		this.varname = varname;
	}

	public String toString()
	{
		return "UndefinedVariable(" + FunctionRepr.call(varname) + ")";
	}
}
