/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Collection;
import java.util.Map;
import static java.util.Arrays.asList;

public class ContainsAST extends BinaryAST
{
	public ContainsAST(Tag tag, Slice pos, CodeAST obj1, CodeAST obj2)
	{
		super(tag, pos, obj1, obj2);
	}

	public String getType()
	{
		return "contains";
	}

	public static CodeAST make(Tag tag, Slice pos, CodeAST obj1, CodeAST obj2)
	{
		if (obj1 instanceof ConstAST && obj2 instanceof ConstAST)
		{
			Object result = call(((ConstAST)obj1).value, ((ConstAST)obj2).value);
			if (!(result instanceof Undefined))
				return new ConstAST(tag, pos, result);
		}
		return new ContainsAST(tag, pos, obj1, obj2);
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static boolean call(String obj, String container)
	{
		return container.indexOf(obj) >= 0;
	}

	public static boolean call(Object obj, Collection container)
	{
		return container.contains(obj);
	}

	public static boolean call(Object obj, Object[] container)
	{
		return asList(container).contains(obj);
	}

	public static boolean call(Object obj, Map container)
	{
		return container.containsKey(obj);
	}

	public static boolean call(Object obj, UL4Attributes container)
	{
		return container.getAttributeNamesUL4().contains(obj);
	}

	public static boolean call(Object obj, Object container)
	{
		if (container instanceof String)
		{
			if (obj instanceof String)
				return call((String)obj, (String)container);
		}
		else if (container instanceof Collection)
			return call(obj, (Collection)container);
		else if (container instanceof Object[])
			return call(obj, (Object[])container);
		else if (container instanceof Map)
			return call(obj, (Map)container);
		else if (container instanceof UL4Attributes)
		{
			if (obj instanceof String)
				return call((String)obj, (UL4Attributes)container);
		}
		throw new ArgumentTypeMismatchException("{!t} in {!t} not supported", obj, container);
	}
}
