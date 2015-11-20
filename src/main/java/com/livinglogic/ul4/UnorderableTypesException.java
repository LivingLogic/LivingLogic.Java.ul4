/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * Thrown by comparisons when the types can't be compared.
 */
public class UnorderableTypesException extends ArgumentTypeMismatchException
{
	public UnorderableTypesException(String operator, Object arg1, Object arg2)
	{
		super("{} " + operator + " {}", arg1, arg2);
	}
}
