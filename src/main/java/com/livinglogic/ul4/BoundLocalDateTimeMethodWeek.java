/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDateTime;

public class BoundLocalDateTimeMethodWeek extends BoundMethod<LocalDateTime>
{
	public BoundLocalDateTimeMethodWeek(LocalDateTime object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "week";
	}

	private static final Signature signature = new Signature().addBoth("firstweekday", 0).addBoth("mindaysinfirstweek", 4);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static int call(EvaluationContext context, LocalDateTime object, int firstWeekday, int minDaysInFirstWeek)
	{
		return BoundLocalDateTimeMethodCalendar.call(context, object, firstWeekday, minDaysInFirstWeek).week;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		int firstWeekday = Utils.toInt(args.get(0));
		int minDaysInFirstWeek = Utils.toInt(args.get(1));
		return call(context, object, firstWeekday, minDaysInFirstWeek);
	}
}
