/*
** Copyright 2009-2022 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Set;
import static java.util.Arrays.asList;
import java.io.IOException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class RenderXAST extends RenderAST
{
	protected static class Type extends RenderAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "RenderXAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.renderx";
		}

		@Override
		public String getDoc()
		{
			return "AST node for rendering a template and XML-escaping the output\n(e.g. ``<?renderx t(x)?>``.";
		}

		@Override
		public RenderXAST create(String id)
		{
			return new RenderXAST(null, -1, -1, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof RenderXAST;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public RenderXAST(Template template, int posStart, int posStop, AST obj)
	{
		super(template, posStart, posStop, obj);
	}

	public RenderXAST(CallAST call)
	{
		super(call);
	}

	public String getType()
	{
		return "renderx";
	}

	public void call(EvaluationContext context, UL4Render obj, List<Object> args, Map<String, Object> kwargs)
	{
		context.pushEscape(XMLStringEscape.function);
		try
		{
			super.call(context, obj, args, kwargs);
		}
		finally
		{
			context.popEscape();
		}
	}
}
