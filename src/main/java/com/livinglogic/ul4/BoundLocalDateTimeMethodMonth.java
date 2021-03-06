/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Calendar;
import java.time.LocalDateTime;
import java.util.GregorianCalendar;
import java.util.Map;

public class BoundLocalDateTimeMethodMonth extends BoundMethod<LocalDateTime>
{
	public BoundLocalDateTimeMethodMonth(LocalDateTime object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "month";
	}

	public static int call(EvaluationContext context, LocalDateTime obj)
	{
		return obj.getMonthValue();
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, object);
	}
}
