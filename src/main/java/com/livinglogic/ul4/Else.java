/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class Else extends ConditionalBlock
{
	public Else(Location location)
	{
		super(location);
	}

	public String getType()
	{
		return "else";
	}

	public boolean hasToBeExecuted(EvaluationContext context) throws IOException
	{
		return true;
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("else\n");
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("{\n");
		for (AST item : content)
			buffer.append(item.toString(indent+1));
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("}\n");
		return buffer.toString();
	}
}