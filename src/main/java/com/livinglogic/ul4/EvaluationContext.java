/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * An {@code EvaluationContext} object is passed around calls to the node method
 * {@link AST#evaluate} and stores an output stream and a map containing the
 * currently defined variables.
 */
public class EvaluationContext
{
	/**
	 * The {@code Writer} object where output can be written via {@link #write}
	 */
	protected Writer writer;

	/**
	 * A map containing the currently defined variables
	 */
	protected Map<String, Object> variables;

	/**
	 * Create a new {@code EvaluationContext} object. (No variables) will
	 * be available to the template code.
	 * @param writer The output stream where the template output will be written
	 */
	public EvaluationContext(Writer writer)
	{
		this(writer, null);
	}

	/**
	 * Create a new {@code EvaluationContext} object
	 * @param writer The output stream where the template output will be written
	 * @param variables The template variables that will be available to the
	 *                  template code (or {@code null} for no variables)
	 */
	public EvaluationContext(Writer writer, Map<String, Object> variables)
	{
		this.writer = writer;
		if (variables == null)
			variables = new HashMap<String, Object>();
		this.variables = variables;
	}

	/**
	 * Return the map containing the template variables.
	 */
	public Map<String, Object> getVariables()
	{
		return variables;
	}

	/**
	 * Return the {@code Writer} object where template output is written to.
	 */
	public Writer getWriter()
	{
		return writer;
	}

	/**
	 * Write output
	 */
	public void write(String string) throws IOException
	{
		writer.write(string);
	}

	/**
	 * Store a template variable in the variable map
	 * @param key The name of the variable
	 * @param value The value of the variable
	 */
	public void put(String key, Object value)
	{
		variables.put(key, value);
	}

	/**
	 * Return a template variable
	 * @param key The name of the variable
	 * @throws KeyException if the variable isn't defined
	 */
	public Object get(String key)
	{
		Object result = variables.get(key);

		if ((result == null) && !variables.containsKey(key))
			return new UndefinedVariable(key);
		return result;
	}

	/**
	 * Return a template variable or a default value if the variable isn't defined
	 * @param key The name of the variable
	 * @param defaultValue Will be returned if the variable named {@code key} is
	 *                     not defined
	 */
	public Object get(String key, Object defaultValue)
	{
		Object result = variables.get(key);

		if ((result == null) && !variables.containsKey(key))
			return defaultValue;
		return result;
	}

	/**
	 * Delete a template variable
	 * @param key The name of the variable
	 */
	public void remove(String key)
	{
		variables.remove(key);
	}
}
