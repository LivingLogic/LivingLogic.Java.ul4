/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

/**
An IndentAST is a literal text in the template source that represents the
indentation at the beginning of a line.
**/
public class IndentAST extends TextAST
{
	protected static class Type extends TextAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "IndentAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.indent";
		}

		@Override
		public String getDoc()
		{
			return "AST node for literal text that is an indentation at the start of the line.";
		}

		@Override
		public IndentAST create(String id)
		{
			return new IndentAST(null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof IndentAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected String text;

	public IndentAST(Template template, Slice pos, String text)
	{
		super(template, pos);
		this.text = text;
	}

	@Override
	public String getText()
	{
		return text != null ? text : super.getText();
	}

	public void setText(String text)
	{
		this.text = text;
	}

	@Override
	public Object evaluate(EvaluationContext context)
	{
		for (String indent : context.indents)
			context.write(indent);
		context.write(getText());
		return null;
	}

	public void toString(Formatter formatter)
	{
		formatter.write("indent ");
		formatter.write(FunctionRepr.call(getText()));
	}

	public String getType()
	{
		return "indent";
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(text);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		text = (String)decoder.load();
	}
}
