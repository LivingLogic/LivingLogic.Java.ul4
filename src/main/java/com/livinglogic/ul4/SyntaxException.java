/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class SyntaxException extends Exception
{
	public SyntaxException(UL4Token token)
	{
		super("Lexical error near " + token);
	}

	public SyntaxException(Object object)
	{
		super("Lexical error near " + object);
	}
}