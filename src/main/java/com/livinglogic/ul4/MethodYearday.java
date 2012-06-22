/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Date;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MethodYearday implements Method
{
	public String getName()
	{
		return "yearday";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException
	{
		switch (args.length)
		{
			case 0:
				return call(obj);
			default:
				throw new ArgumentCountMismatchException("method", "yearday", args.length, 0);
		}
	}

	public static int call(Date obj)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(obj);
		return calendar.get(Calendar.DAY_OF_YEAR);
	}

	public static int call(Object obj)
	{
		if (obj instanceof Date)
			return call((Date)obj);
		throw new ArgumentTypeMismatchException("{}.yearday()", obj);
	}
}
