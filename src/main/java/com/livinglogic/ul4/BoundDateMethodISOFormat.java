/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class BoundDateMethodISOFormat extends BoundMethod<Date>
{
	public BoundDateMethodISOFormat(Date object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "isoformat";
	}

	private static SimpleDateFormat formatter0 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'000'");

	public static String call(Date object)
	{
		if (BoundDateMethodMicrosecond.call(object) != 0)
			return formatter1.format(object);
		else
			return formatter0.format(object);
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(object);
	}
}
