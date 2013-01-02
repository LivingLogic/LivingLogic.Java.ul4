/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public abstract class ChangeVar extends Tag
{
	protected String varname;
	protected AST value;

	public ChangeVar(Location location, String varname, AST value)
	{
		super(location);
		this.varname = varname;
		this.value = value;
	}

	public String toString(int indent)
	{
		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append(getType() + "(" + FunctionRepr.call(varname) + ", " + value + ")\n");
		return buffer.toString();
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(varname);
		encoder.dump(value);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		varname = (String)decoder.load();
		value = (AST)decoder.load();
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("varname", new ValueMaker(){public Object getValue(Object object){return ((ChangeVar)object).varname;}});
			v.put("value", new ValueMaker(){public Object getValue(Object object){return ((ChangeVar)object).value;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
