/*
** Copyright 2019-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
Thrown by {@link FunctionAsJSON} if the object isn't convertable to JSON.
**/
public class NotJSONableException extends UnsupportedOperationException
{
	public NotJSONableException(Object obj)
	{
		super(Utils.objectType(obj) + " can't be converted to JSON!");
	}
}
