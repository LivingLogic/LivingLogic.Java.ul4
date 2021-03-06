/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Calendar;
import java.time.LocalDate;
import java.util.GregorianCalendar;
import java.util.Map;

public class BoundLocalDateMethodMonth extends BoundMethod<LocalDate>
{
	public BoundLocalDateMethodMonth(LocalDate object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "month";
	}

	public static int call(EvaluationContext context, LocalDate obj)
	{
		return obj.getMonthValue();
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, object);
	}
}
