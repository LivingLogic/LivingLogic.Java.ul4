/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
Thrown when an object doesn't supported the requested index. This exception
might be thrown by {@link UL4GetItem#getItemUL4}.
**/
public class IndexException extends IndexOutOfBoundsException
{
	protected Object object;
	protected Object key;

	public IndexException(Object object, Object key)
	{
		super(Utils.formatMessage("{!r} instance has no entry for index {!r}!", object, key));
		this.object = object;
		this.key = key;
	}

	public Object getObject()
	{
		return object;
	}

	public Object getKey()
	{
		return key;
	}
}
