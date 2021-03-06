/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class BoundLocalDateMethodMIMEFormat extends BoundMethod<LocalDate>
{
	public BoundLocalDateMethodMIMEFormat(LocalDate object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "mimeformat";
	}

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.US);

	public static String call(EvaluationContext context, LocalDate obj)
	{
		return obj.format(formatter);
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, object);
	}
}
