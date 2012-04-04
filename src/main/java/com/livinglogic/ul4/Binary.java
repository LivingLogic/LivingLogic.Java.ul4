/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

abstract class Binary extends AST
{
	protected AST obj1;
	protected AST obj2;

	public Binary(AST obj1, AST obj2)
	{
		this.obj1 = obj1;
		this.obj2 = obj2;
	}

	abstract public int getType();

	public int compile(InterpretedTemplate template, Registers registers, Location location)
	{
		int r1 = obj1.compile(template, registers, location);
		int r2 = obj2.compile(template, registers, location);
		template.opcode(getType(), r1, r1, r2, location);
		registers.free(r2);
		return r1;
	}
}
