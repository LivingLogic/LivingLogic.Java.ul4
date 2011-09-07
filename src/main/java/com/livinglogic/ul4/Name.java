/*
** Copyright 2009-2011 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class Name extends AST
{
	protected String value;

	public Name(int start, int end, String value)
	{
		super(start, end);
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

	public String getTokenType()
	{
		return "name";
	}

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r = registers.alloc();
		template.opcode(Opcode.OC_LOADVAR, r, value, location);
		return r;
	}

	public String toString()
	{
		return "name \"" + value.replaceAll("\"", "\\\\\"") + "\"";
	}
}
