/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.util.Iterator;

public class FunctionMin extends Function
{
	public String nameUL4()
	{
		return "min";
	}

	protected void makeSignature(Signature signature)
	{
		signature.setRemainingArguments("args");
	}

	public Object evaluate(Object[] args)
	{
		List<Object> argList = (List<Object>)args[0];
		return (argList.size() == 0) ? call() : call(argList);
	}

	public static Object call()
	{
		throw new MissingArgumentException("min", "args", 0);
	}

	public static Object call(List<Object> objs)
	{
		Iterator iter = Utils.iterator(objs.size() == 1 ? objs.get(0) : objs);

		Object minValue = null;
		boolean first = true;

		for (;iter.hasNext();)
		{
			Object testValue = iter.next();
			if (first || LT.call(testValue, minValue))
				minValue = testValue;
			first = false;
		}
		if (first)
			throw new UnsupportedOperationException("min() arg is an empty sequence!");
		return minValue;
	}
}
