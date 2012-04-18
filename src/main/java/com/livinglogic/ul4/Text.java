/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

class Text extends AST
{
	protected String text;

	public Text(String text)
	{
		this.text = text;
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("text(");
		buffer.append(Utils.repr(text));
		buffer.append(")\n");
		return buffer.toString();
	}

	public String getType()
	{
		return "text";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.write(text);
		return null;
	}
}
