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
A {@link VSQLQuery} for PostgreSQL databases.

@author W. Doerwald
**/
public class PostgresVSQLQuery extends VSQLQuery
{
	public PostgresVSQLQuery(String comment, Map<String, VSQLField> vars)
	{
		super(comment, vars);
	}

	public PostgresVSQLQuery(String comment)
	{
		super(comment);
	}

	public PostgresVSQLQuery(Map<String, VSQLField> vars)
	{
		super(vars);
	}

	public PostgresVSQLQuery()
	{
		super();
	}

	@Override
	protected List<Object> getRuleSource(VSQLRule rule)
	{
		return rule.getPostgresSource();
	}

	@Override
	public String getBoolSQLSource(boolean value)
	{
		return value ? "true" : "false";
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
		// In Postgres a vSQL `BOOL` value already is a real condition
		return sql;
	}

	@Override
	protected String getSumOperandSQLSource(String sql, VSQLDataType dataType)
	{
		// Postgres has no `sum()` for its native `boolean` type, so a vSQL
		// `BOOL` value has to be converted to a number first
		return dataType == VSQLDataType.BOOL ? sql + "::int::bigint" : sql;
	}

	@Override
	protected String getSQLSourceNoFrom(int indentLevel)
	{
		// Postgres doesn't have (and doesn't need) `dual`, so a query without
		// any tables gets no "from" clause at all
		return null;
	}

	@Override
	protected String getSQLSourceOffsetLimit(int indentLevel)
	{
		if (offset < 0 && limit < 0)
			return null;
		String indent = "\t".repeat(indentLevel);
		StringBuilder buffer = new StringBuilder();
		if (limit >= 0)
			buffer.append(indent).append("limit ").append(limit).append("\n");
		if (offset >= 0)
			buffer.append(indent).append("offset ").append(offset).append("\n");
		return buffer.toString();
	}

	private static DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("'date '''yyyy-MM-dd''", Locale.US);
	private static DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("'timestamp '''yyyy-MM-dd HH:mm:ss''", Locale.US);

	private static final Map<VSQLDataType, SeqSQL> seqSQL = Map.ofEntries(
		Map.entry(VSQLDataType.INTLIST, new SeqSQL("array[", "]::bigint[]")),
		Map.entry(VSQLDataType.NUMBERLIST, new SeqSQL("array[", "]::numeric[]")),
		Map.entry(VSQLDataType.STRLIST, new SeqSQL("array[", "]::text[]")),
		Map.entry(VSQLDataType.CLOBLIST, new SeqSQL("array[", "]::text[]")),
		Map.entry(VSQLDataType.DATELIST, new SeqSQL("array[", "]::date[]")),
		Map.entry(VSQLDataType.DATETIMELIST, new SeqSQL("array[", "]::timestamp[]")),
		Map.entry(VSQLDataType.INTSET, new SeqSQL("vsqlimpl.set_intlist(array[", "]::bigint[])")),
		Map.entry(VSQLDataType.NUMBERSET, new SeqSQL("vsqlimpl.set_numberlist(array[", "]::numeric[])")),
		Map.entry(VSQLDataType.STRSET, new SeqSQL("vsqlimpl.set_strlist(array[", "]::text[])")),
		Map.entry(VSQLDataType.DATESET, new SeqSQL("vsqlimpl.set_datelist(array[", "]::date[])")),
		Map.entry(VSQLDataType.DATETIMESET, new SeqSQL("vsqlimpl.set_datetimelist(array[", "]::timestamp[])"))
	);
}
