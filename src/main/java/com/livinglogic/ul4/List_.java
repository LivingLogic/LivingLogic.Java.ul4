/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import static java.util.Arrays.asList;

import static com.livinglogic.utils.SetUtils.makeSet;


public class List_ extends AbstractType
{
	@Override
	public String getNameUL4()
	{
		return "list";
	}

	@Override
	public String getDoc()
	{
		return "A ordered collection of objects.";
	}

	@Override
	public boolean instanceCheck(Object object)
	{
		return object instanceof List || object instanceof Object[];
	}

	private static final Signature signature = new Signature().addPositionalOnly("iterable", Collections.EMPTY_LIST);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object create(EvaluationContext context, BoundArguments args)
	{
		return call(context, args.get(0));
	}

	public static ArrayList call(EvaluationContext context, String obj)
	{
		ArrayList result;
		int length = obj.length();
		result = new ArrayList(obj.length());
		for (int i = 0; i < length; i++)
		{
			result.add(String.valueOf(obj.charAt(i)));
		}
		return result;
	}

	public static ArrayList call(EvaluationContext context, Collection obj)
	{
		return new ArrayList(obj);
	}

	public static ArrayList call(EvaluationContext context, Object[] obj)
	{
		return new ArrayList(asList(obj));
	}

	public static ArrayList call(EvaluationContext context, Map obj)
	{
		return new ArrayList(obj.keySet());
	}

	public static ArrayList call(EvaluationContext context, Iterable obj)
	{
		return call(context, obj.iterator());
	}

	public static ArrayList call(EvaluationContext context, Iterator obj)
	{
		ArrayList retVal = new ArrayList();
		while (obj.hasNext())
			retVal.add(obj.next());
		return retVal;
	}

	public static ArrayList call(EvaluationContext context, Object obj)
	{
		if (obj instanceof String)
			return call(context, (String)obj);
		else if (obj instanceof Collection)
			return call(context, (Collection)obj);
		else if (obj instanceof Object[])
			return call(context, (Object[])obj);
		else if (obj instanceof Map)
			return call(context, (Map)obj);
		else if (obj instanceof Iterable)
			return call(context, (Iterable)obj);
		else if (obj instanceof Iterator)
			return call(context, (Iterator)obj);
		throw new ArgumentTypeMismatchException("list({!t}) not supported", obj);
	}

	@Override
	public boolean boolInstance(EvaluationContext context, Object instance)
	{
		if (instance instanceof List)
			return !((List)instance).isEmpty();
		else
			return ((Object[])instance).length != 0;
	}

	@Override
	public int lenInstance(EvaluationContext context, Object instance)
	{
		if (instance instanceof List)
			return ((List)instance).size();
		else
			return ((Object[])instance).length;
	}

	protected static Set<String> attributes = makeSet("append", "insert", "pop", "count", "find", "rfind");

	@Override
	public Set<String> dirInstance(EvaluationContext context, Object instance)
	{
		return attributes;
	}

	@Override
	public Object getAttr(EvaluationContext context, Object object, String key)
	{
		if (object instanceof List)
			return getAttr(context, (List)object, key);
		else
			return getAttr(context, (Object[])object, key);
	}

	public Object getAttr(EvaluationContext context, List object, String key)
	{
		switch (key)
		{
			case "append":
				return new BoundListMethodAppend(object);
			case "insert":
				return new BoundListMethodInsert(object);
			case "pop":
				return new BoundListMethodPop(object);
			case "count":
				return new BoundListMethodCount(object);
			case "find":
				return new BoundListMethodFind(object);
			case "rfind":
				return new BoundListMethodRFind(object);
			default:
				return super.getAttr(context, object, key);
		}
	}

	public Object getAttr(EvaluationContext context, Object[] object, String key)
	{
		switch (key)
		{
			case "append":
				return new BoundArrayMethodAppend(object);
			case "insert":
				return new BoundArrayMethodInsert(object);
			case "pop":
				return new BoundArrayMethodPop(object);
			case "count":
				return new BoundArrayMethodCount(object);
			case "find":
				return new BoundArrayMethodFind(object);
			case "rfind":
				return new BoundArrayMethodRFind(object);
			default:
				return super.getAttr(context, object, key);
		}
	}

	public static final UL4Type type = new List_();
}
