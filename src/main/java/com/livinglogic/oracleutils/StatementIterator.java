/*
** Copyright 2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.oracleutils;

import java.util.Iterator;
import java.util.Map;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.livinglogic.utils.CloseableRegistry;


public class StatementIterator implements Iterator<Map<String, Object>>, AutoCloseable
{
	private ResultSet resultSet;
	Iterator<Map<String, Object>> resultSetIterator;

	StatementIterator(CloseableRegistry closeableRegistry, PreparedStatement statement)
	{
		try
		{
			this.resultSet = statement.executeQuery();
		}
		catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		}
		if (closeableRegistry != null)
			closeableRegistry.registerCloseable(this.resultSet);
		this.resultSetIterator = new ResultSetMapIterator(this.resultSet);
	}

	public boolean hasNext()
	{
		return resultSetIterator.hasNext();
	}

	public Map<String, Object> next()
	{
		try
		{
			Map<String, Object> nextResult = resultSetIterator.next();
			if (!hasNext())
				close();
			return nextResult;
		}
		catch (Exception ex)
		{
			close();
			throw ex;
		}
	}

	public void remove()
	{
		throw new UnsupportedOperationException();
	}

	public void close()
	{
		if (resultSet != null)
		{
			try
			{
				resultSet.close();
			}
			catch (SQLException ex)
			{
			}
			resultSet = null;
		}
	}
}
