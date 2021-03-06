/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class BoundDateMethodCalendar extends BoundMethod<Date>
{
	public BoundDateMethodCalendar(Date object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "calendar";
	}

	private static final Signature signature = new Signature().addBoth("firstweekday", 0).addBoth("mindaysinfirstweek", 4);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static Date_.Calendar call(Date object, int firstWeekday, int minDaysInFirstWeek)
	{
		// Normalize parameters
		firstWeekday %= 7;
		if (minDaysInFirstWeek < 1)
			minDaysInFirstWeek = 1;
		else if (minDaysInFirstWeek > 7)
			minDaysInFirstWeek = 7;

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(object);
		calendar.setFirstDayOfWeek(Date_.ul4Weekday2JavaWeekday(firstWeekday));
		calendar.setMinimalDaysInFirstWeek(minDaysInFirstWeek);

		int year = calendar.getWeekYear();
		int week = calendar.get(Calendar.WEEK_OF_YEAR);
		int weekday = calendar.get(Calendar.DAY_OF_WEEK);

		return new Date_.Calendar(year, week, Date_.javaWeekday2UL4Weekday(weekday));
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		int firstWeekday = Utils.toInt(args.get(0));
		int minDaysInFirstWeek = Utils.toInt(args.get(1));
		return call(object, firstWeekday, minDaysInFirstWeek).asList();
	}
}
