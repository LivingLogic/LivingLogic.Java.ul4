/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

abstract class BlockAST extends CodeAST implements BlockLike
{
	protected Tag endtag = null;
	protected List<AST> content = new LinkedList<AST>();

	public BlockAST(Tag tag, Slice pos)
	{
		super(tag, pos);
	}

	@Override
	public IndentAST popTrailingIndent()
	{
		if (content.size() > 0)
		{
			AST lastItem = content.get(content.size()-1);
			if (lastItem instanceof IndentAST)
			{
				content.remove(content.size()-1);
				return (IndentAST)lastItem;
			}
		}
		return null;
	}

	@Override
	public void append(AST item)
	{
		content.add(item);
	}

	@Override
	public void finish(Tag endtag)
	{
		this.endtag = endtag;
	}

	public Tag getEndTag()
	{
		return endtag;
	}

	public List<AST> getContent()
	{
		return content;
	}

	/**
	 * Return whether this block can handle a {@code break} oder {@code continue} tag ({@code true})
	 * or whether the decision should be delegated to the parent block ({@code false}).
	 * Returns {@code true} for {@code for} and {@code while} blocks and
	 * {@code false} for {@code if}/{@code elif}/{@code else}.
	 * For {@code InterpretedTemplate} an exception is thrown.
	 */
	abstract public boolean handleLoopControl(String name);

	public Object decoratedEvaluate(EvaluationContext context)
	{
		try
		{
			return evaluate(context);
		}
		catch (BreakException|ContinueException|ReturnException|LocationException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw new LocationException(ex, this);
		}
	}

	public Object evaluate(EvaluationContext context)
	{
		for (AST item : content)
			item.decoratedEvaluate(context);
		return null;
	}

	public static void blockToString(Formatter formatter, List<AST> content)
	{
		if (content.size() != 0)
		{
			for (AST item : content)
			{
				item.toString(formatter);
				formatter.lf();
			}
		}
		else
		{
			formatter.write("pass");
			formatter.lf();
		}
	}

	public void toString(Formatter formatter)
	{
		blockToString(formatter, content);
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(endtag);
		encoder.dump(content);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		endtag = (Tag)decoder.load();
		content = (List<AST>)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "endtag", "content");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "endtag":
				return endtag;
			case "content":
				return content;
			default:
				return super.getAttrUL4(key);
		}
	}
}
