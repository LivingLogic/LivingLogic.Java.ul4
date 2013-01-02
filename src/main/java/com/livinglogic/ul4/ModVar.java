/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class ModVar extends ChangeVar
{
	public ModVar(Location location, String varname, AST value)
	{
		super(location, varname, value);
	}

	public String getType()
	{
		return "modvar";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.put(varname, Mod.call(context.get(varname), value.decoratedEvaluate(context)));
		return null;
	}
}
