/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class BoundDateMethodWeek extends BoundMethod<Date>
{
	public BoundDateMethodWeek(Date object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "date.week";
	}

	private static final Signature signature = new Signature("firstweekday", 0, "mindaysinfirstweek", 4);

	public Signature getSignature()
	{
		return signature;
	}

	public static int call(Date object, int firstWeekday, int minDaysInFirstWeek)
	{
		// Normalize parameters
		firstWeekday %= 7;
		if (minDaysInFirstWeek < 1)
			minDaysInFirstWeek = 1;
		else if (minDaysInFirstWeek > 7)
			minDaysInFirstWeek = 7;

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(object);
		calendar.setFirstDayOfWeek(DateProto.ul4Weekday2JavaWeekday(firstWeekday));
		calendar.setMinimalDaysInFirstWeek(minDaysInFirstWeek);

		int week = calendar.get(Calendar.WEEK_OF_YEAR);

		return week;
	}

	public Object evaluate(BoundArguments args)
	{
		int firstWeekday = Utils.toInt(args.get(0));
		int minDaysInFirstWeek = Utils.toInt(args.get(1));
		return call(object, firstWeekday, minDaysInFirstWeek);
	}
}
