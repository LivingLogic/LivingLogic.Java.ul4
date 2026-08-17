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
import static com.livinglogic.utils.StringUtils.formatMessage;


/**
The unary vSQL operator {@code not}.

@author W. Doerwald
**/
public class VSQLNotAST extends VSQLUnaryAST
{
	/**
	UL4 type for the {@link VSQLNotAST} class.
	**/
	protected static class Type extends VSQLUnaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLNotAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlnotast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a unary \"not\" expression (e.g. `not x`).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLNotAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public VSQLNotAST(String sourcePrefix, VSQLAST obj, String sourceSuffix)
	{
		super(sourcePrefix, obj, sourceSuffix);
		validate();
	}

	public VSQLNotAST(VSQLAST obj, String sourceSuffix)
	{
		this(null, obj, sourceSuffix);
	}

	public VSQLNotAST(String sourcePrefix, VSQLAST obj)
	{
		this(sourcePrefix, obj, null);
	}

	public static VSQLNotAST make(VSQLAST obj)
	{
		int prec = obj.getPrecedence();
		if (prec > PRECEDENCE)
			return new VSQLNotAST("not ", obj, "");
		else
			return new VSQLNotAST("not (", obj, ")");
	}

	@Override
	public String getDescription()
	{
		return "Logical \"not\" expression";
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.UNOP_NOT;
	}

	@Override
	public String getTypeSignature()
	{
		return formatMessage("not {}", obj.getDataTypeString());
	}

	private final static int PRECEDENCE = 5;

	@Override
	public int getPrecedence()
	{
		return PRECEDENCE;
	}

	@Override
	protected Map<VSQLDataType, VSQLRule> getRules()
	{
		return rules;
	}

	private static Map<VSQLDataType, VSQLRule> rules = new HashMap<>();

	//BEGIN RULES (don't remove this comment)
	private static void addRulesPart1()
	{
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL), List.of("1"), List.of("true"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL), List.of("(case ", 1, " when 1 then 0 else 1 end)"), List.of("(not coalesce(", 1, ", false))"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT), List.of("(case nvl(", 1, ", 0) when 0 then 1 else 0 end)"), List.of("(coalesce(", 1, ", '0') = '0')"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER), List.of("(case nvl(", 1, ", 0) when 0 then 1 else 0 end)"), List.of("(coalesce(", 1, ", '0') = '0')"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA), List.of("(case nvl(", 1, ", 0) when 0 then 1 else 0 end)"), List.of("(coalesce(", 1, ", '0') = '0')"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA), List.of("(case nvl(", 1, ", 0) when 0 then 1 else 0 end)"), List.of("(coalesce(", 1, ", '0') = '0')"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA), List.of("(case nvl(", 1, ", 0) when 0 then 1 else 0 end)"), List.of("(coalesce(", 1, ", '0') = '0')"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE), List.of("(case when ", 1, " is null then 1 else 0 end)"), List.of("(not vsqlimpl.bool_date(", 1, "))"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME), List.of("(case when ", 1, " is null then 1 else 0 end)"), List.of("(not vsqlimpl.bool_datetime(", 1, "))"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR), List.of("(case when ", 1, " is null then 1 else 0 end)"), List.of("(not vsqlimpl.bool_str(", 1, "))"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR), List.of("(case when ", 1, " is null then 1 else 0 end)"), List.of("(not vsqlimpl.bool_color(", 1, "))"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO), List.of("(case when ", 1, " is null then 1 else 0 end)"), List.of("(not vsqlimpl.bool_geo(", 1, "))"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST), List.of("(1 - vsqlimpl_pkg.bool_nulllist(", 1, "))"), List.of("(not vsqlimpl.bool_nulllist(", 1, "))"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET), List.of("(1 - vsqlimpl_pkg.bool_nullset(", 1, "))"), List.of("(not vsqlimpl.bool_nullset(", 1, "))"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB), List.of("(1 - vsqlimpl_pkg.bool_clob(", 1, "))"), List.of("(not vsqlimpl.bool_clob(", 1, "))"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST), List.of("(1 - vsqlimpl_pkg.bool_intlist(", 1, "))"), List.of("(not vsqlimpl.bool_intlist(", 1, "))"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST), List.of("(1 - vsqlimpl_pkg.bool_numberlist(", 1, "))"), List.of("(not vsqlimpl.bool_numberlist(", 1, "))"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST), List.of("(1 - vsqlimpl_pkg.bool_strlist(", 1, "))"), List.of("(not vsqlimpl.bool_strlist(", 1, "))"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST), List.of("(1 - vsqlimpl_pkg.bool_cloblist(", 1, "))"), List.of("(not vsqlimpl.bool_cloblist(", 1, "))"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST), List.of("(1 - vsqlimpl_pkg.bool_datetimelist(", 1, "))"), List.of("(not vsqlimpl.bool_datelist(", 1, "))"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST), List.of("(1 - vsqlimpl_pkg.bool_datetimelist(", 1, "))"), List.of("(not vsqlimpl.bool_datetimelist(", 1, "))"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET), List.of("(1 - vsqlimpl_pkg.bool_intlist(", 1, "))"), List.of("(not vsqlimpl.bool_intset(", 1, "))"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET), List.of("(1 - vsqlimpl_pkg.bool_numberlist(", 1, "))"), List.of("(not vsqlimpl.bool_numberset(", 1, "))"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET), List.of("(1 - vsqlimpl_pkg.bool_strlist(", 1, "))"), List.of("(not vsqlimpl.bool_strset(", 1, "))"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET), List.of("(1 - vsqlimpl_pkg.bool_datetimelist(", 1, "))"), List.of("(not vsqlimpl.bool_dateset(", 1, "))"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET), List.of("(1 - vsqlimpl_pkg.bool_datetimelist(", 1, "))"), List.of("(not vsqlimpl.bool_datetimeset(", 1, "))"));
	}

	static
	{
		addRulesPart1();
	}
	//END RULES (don't remove this comment)
}
