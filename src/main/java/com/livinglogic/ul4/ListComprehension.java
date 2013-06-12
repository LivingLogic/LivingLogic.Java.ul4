/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class ListComprehension extends AST
{
	protected AST item;
	protected Object varname;
	protected AST container;
	protected AST condition;

	public ListComprehension(Location location, int start, int end, AST item, Object varname, AST container, AST condition)
	{
		super(location, start, end);
		this.item = item;
		this.varname = varname;
		this.container = container;
		this.condition = condition;
	}

	public String getType()
	{
		return "listcomp";
	}

	public Object evaluate(EvaluationContext context)
	{
		List result = new ArrayList();

		Object container = this.container.decoratedEvaluate(context);

		Iterator iter = Utils.iterator(container);

		// Store the loop variables into a local map, so they don't leak into the surrounding scope.
		Map<String, Object> oldVariables = context.pushVariables(null);

		try
		{
			while (iter.hasNext())
			{
				context.unpackVariable(varname, iter.next());

				if (condition == null || FunctionBool.call(condition.decoratedEvaluate(context)))
				{
					Object item = this.item.decoratedEvaluate(context);
					result.add(item);
				}
			}
		}
		finally
		{
			context.setVariables(oldVariables);
		}
		return result;
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(item);
		encoder.dump(varname);
		encoder.dump(container);
		encoder.dump(condition);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		item = (AST)decoder.load();
		varname = decoder.load();
		container = (AST)decoder.load();
		condition = (AST)decoder.load();
	}
}
