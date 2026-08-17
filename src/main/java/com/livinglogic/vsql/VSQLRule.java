/*
** Copyright 2019-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.livinglogic.ul4.UL4Type;


/**
A vSQL rule.

@author W. Doerwald
**/
public class VSQLRule
{
	protected VSQLDataType resultType;
	protected List<Object> signature; // strings (function/method/attribute name) or datatype (type of argument)
	protected List<Object> oracleSource; // strings (literal source) or integer (embed child source)
	protected List<Object> postgresSource; // strings (literal source) or integer (embed child source)

	public VSQLRule(VSQLDataType resultType, List<Object> signature, List<Object> oracleSource, List<Object> postgresSource)
	{
		this.resultType = resultType;
		this.signature = signature;
		this.oracleSource = oracleSource;
		this.postgresSource = postgresSource;
	}

	public List<Object> getOracleSource()
	{
		return oracleSource;
	}

	public List<Object> getPostgresSource()
	{
		return postgresSource;
	}

	public void makeSQLSource(StringBuilder buffer, VSQLQuery query, List<VSQLAST> children)
	{
		for (Object s : query.getRuleSource(this))
		{
			if (s instanceof String string)
			{
				buffer.append(string);
			}
			else if (s instanceof Integer integer)
			{
				children.get(integer-1).makeSQLSource(buffer, query);
			}
		}
	}

	public VSQLDataType getResultType()
	{
		return resultType;
	}
}
