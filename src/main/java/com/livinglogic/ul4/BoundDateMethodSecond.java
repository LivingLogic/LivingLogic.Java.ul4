/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

public class BoundDateMethodSecond extends BoundMethod<Date>
{
	public BoundDateMethodSecond(Date object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "date.second";
	}

	public static int call(Date obj)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(obj);
		return calendar.get(Calendar.SECOND);
	}

	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}
