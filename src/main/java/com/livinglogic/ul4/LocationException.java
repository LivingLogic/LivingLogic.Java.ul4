/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;

import static com.livinglogic.ul4.Utils.getInnerException;

public class LocationException extends RuntimeException implements UL4Dir, UL4GetAttr
{
	protected AST location;

	public LocationException(AST location)
	{
		super(makeMessage(location));
		this.location = location;
	}

	public AST getLocation()
	{
		return location;
	}

	private static String makeMessage(AST location)
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append(location.getTemplateDescriptionText());
		buffer.append(": ");
		buffer.append(location.getLocationDescriptionText());
		buffer.append("\n");
		buffer.append(location.getSourceSnippetText());
		return buffer.toString();
	}

	public String getDescription()
	{
		return location.getTemplateDescriptionText() + "; " + location.getLocationDescriptionText();
	}

	protected static Set<String> attributes = Set.of("context", "location");

	@Override
	public Set<String> dirUL4(EvaluationContext context)
	{
		return attributes;
	}

	@Override
	public Object getAttrUL4(EvaluationContext context, String key)
	{
		switch (key)
		{
			case "context":
				return getInnerException(this);
			case "location":
				return location;
			default:
				return UL4GetAttr.super.getAttrUL4(context, key);
		}
	}
}
