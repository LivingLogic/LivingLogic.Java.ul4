/*
** Copyright 2019-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
A {@link VSQLQuery} for Oracle databases.

@author W. Doerwald
**/
public class OracleVSQLQuery extends VSQLQuery
{
	public OracleVSQLQuery(String comment, Map<String, VSQLField> vars)
	{
		super(comment, vars);
	}

	public OracleVSQLQuery(String comment)
	{
		super(comment);
	}

	public OracleVSQLQuery(Map<String, VSQLField> vars)
	{
		super(vars);
	}

	public OracleVSQLQuery()
	{
		super();
	}

	@Override
	protected List<Object> getRuleSource(VSQLRule rule)
	{
		return rule.getOracleSource();
	}

	@Override
	public String getBoolSQLSource(boolean value)
	{
		return value ? "1" : "0";
	}

	@Override
	public String getDateSQLSource(LocalDate value)
	{
		return formatterDate.format(value);
	}

	@Override
	public String getDateTimeSQLSource(LocalDateTime value)
	{
		return formatterDateTime.format(value);
	}

	@Override
	public SeqSQL getSeqSQL(VSQLDataType dataType)
	{
		return seqSQL.get(dataType);
	}

	@Override
	protected String getConditionSQLSource(String sql)
	{
		// In Oracle a vSQL `BOOL` value is a number, so it has to be
		// converted into a real condition
		return sql + " = 1";
	}

	@Override
	protected String getSumOperandSQLSource(String sql, VSQLDataType dataType)
	{
		return sql;
	}

	@Override
	protected String getSQLSourceNoFrom(int indentLevel)
	{
		// Oracle requires a "from" clause, so we select from `dual`
		String indent = "\t".repeat(indentLevel);
		return indent + "from\n" + indent + "\tdual\n";
	}

	@Override
	protected String getSQLSourceOffsetLimit(int indentLevel)
	{
		if (offset < 0 && limit < 0)
			return null;
		String indent = "\t".repeat(indentLevel);
		StringBuilder buffer = new StringBuilder();
		if (offset >= 0)
			buffer.append(indent).append("offset ").append(offset).append(" rows\n");
		if (limit >= 0)
			buffer.append(indent).append("fetch next ").append(limit).append(" rows only\n");
		return buffer.toString();
	}

	private static DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("'to_date('''yyyy-MM-dd''', ''YYYY-MM-DD'')'", Locale.US);
	private static DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("'to_date('''yyyy-MM-dd HH:mm:ss'', '''YYYY-MM-DD HH24:MI:SS'')'", Locale.US);

	private static final Map<VSQLDataType, SeqSQL> seqSQL = Map.ofEntries(
		Map.entry(VSQLDataType.INTLIST, new SeqSQL("integers(", ")")),
		Map.entry(VSQLDataType.NUMBERLIST, new SeqSQL("numbers(", ")")),
		Map.entry(VSQLDataType.STRLIST, new SeqSQL("varchars(", ")")),
		Map.entry(VSQLDataType.CLOBLIST, new SeqSQL("clobs(", ")")),
		Map.entry(VSQLDataType.DATELIST, new SeqSQL("dates(", ")")),
		Map.entry(VSQLDataType.DATETIMELIST, new SeqSQL("dates(", ")")),
		Map.entry(VSQLDataType.INTSET, new SeqSQL("vsqlimpl_pkg.set_intlist(integers(", "))")),
		Map.entry(VSQLDataType.NUMBERSET, new SeqSQL("vsqlimpl_pkg.set_numberlist(numbers(", "))")),
		Map.entry(VSQLDataType.STRSET, new SeqSQL("vsqlimpl_pkg.set_strlist(varchars(", "))")),
		Map.entry(VSQLDataType.DATESET, new SeqSQL("vsqlimpl_pkg.set_datetimelist(dates(", "))")),
		Map.entry(VSQLDataType.DATETIMESET, new SeqSQL("vsqlimpl_pkg.set_datetimelist(dates(", "))"))
	);
}
