/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;


public abstract class BoundMethod<T> extends Function
{
	protected T object = null;

	public BoundMethod(T object)
	{
		this.object = object;
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<method ");
		formatter.append(nameUL4());
		formatter.append(" of ");
		formatter.append(Utils.objectType(object));
		formatter.append(" object>");
	}
}
