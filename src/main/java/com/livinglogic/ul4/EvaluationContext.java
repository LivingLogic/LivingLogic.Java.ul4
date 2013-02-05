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
import java.util.Iterator;

import static com.livinglogic.utils.MapUtils.makeMap;
import com.livinglogic.utils.MapChain;
import com.livinglogic.utils.MapUtils;

/**
 * An {@code EvaluationContext} object is passed around calls to the node method
 * {@link AST#evaluate} and stores an output stream and a map containing the
 * currently defined variables as well as other globally available information.
 */
public class EvaluationContext
{
	/**
	 * The {@code Writer} object where output can be written via {@link #write}.
	 * May by {@code null}, in which case output will be ignored.
	 */
	protected Writer writer;

	/**
	 * A map containing the currently defined variables
	 */
	protected Map<String, Object> variables;

	/**
	 * The currently executing code object
	 */
	InterpretedCode code;

	/**
	 * A {@link com.livinglogic.utils.MapChain} object chaining all variables:
	 * The user defined ones from {@link #variables} and the map containing the
	 * global functions.
	 */
	protected MapChain<String, Object> allVariables;

	/**
	 * Create a new {@code EvaluationContext} object. No variables will
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
		this.code = null;
		this.allVariables = new MapChain<String, Object>(variables, functions);
	}

	/**
	 * Set the writer in {@link #writer} and return the previously defined one.
	 */
	public Writer setWriter(Writer writer)
	{
		Writer oldWriter = this.writer;
		this.writer = writer;
		return oldWriter;
	}

	/**
	 * Set the active code object and return the previously active ones.
	 */
	public InterpretedCode setCode(InterpretedCode code)
	{
		InterpretedCode result = this.code;
		this.code = code;
		return result;
	}

	/**
	 * Return the currently active code object.
	 */
	public InterpretedCode getCode()
	{
		return code;
	}

	/**
	 * Return the map containing the variables local to the template/function.
	 */
	public Map<String, Object> getVariables()
	{
		return variables;
	}

	/**
	 * Return the map containing the all variables.
	 */
	public Map<String, Object> getAllVariables()
	{
		return allVariables;
	}

	/**
	 * Set a new map containing the template variables and return the previous one.
	 */
	public Map<String, Object> setVariables(Map<String, Object> variables)
	{
		if (variables == null)
			variables = new HashMap<String, Object>();
		Map<String, Object> result = this.variables;
		this.variables = variables;
		allVariables.setFirst(variables);
		return result;
	}

	/**
	 * Replace the map containing the template variables with a new map that
	 * deferres non-existant keys to the previous one and return the previous one.
	 */
	public Map<String, Object> pushVariables(Map<String, Object> variables)
	{
		if (variables == null)
			variables = new HashMap<String, Object>();
		return setVariables(new MapChain<String, Object>(variables, getVariables()));
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
		if (writer != null)
			writer.write(string);
	}

	/**
	 * Store a template variable in the variable map
	 * @param key The name of the variable
	 * @param value The value of the variable
	 */
	public void put(String key, Object value)
	{
		if ("self".equals(key))
			throw new RuntimeException("can't assign to self");
		variables.put(key, value);
	}

	/**
	 * Return a template variable
	 * @param key The name of the variable
	 * @throws KeyException if the variable isn't defined
	 */
	public Object get(String key)
	{
		Object result = allVariables.get(key);

		if ((result == null) && !allVariables.containsKey(key))
			return new UndefinedVariable(key);
		return result;
	}

	/**
	 * Delete a variable
	 * @param key The name of the variable
	 */
	public void remove(String key)
	{
		variables.remove(key);
	}

	public static void unpackVariable(Map<String, Object> variables, Object varname, Object item)
	{
		if (varname instanceof String)
		{
			if ("self".equals(varname))
				throw new RuntimeException("can't assign to self");
			variables.put((String)varname, item);
		}
		else
		{
			Iterator<Object> itemIter = Utils.iterator(item);
			java.util.List varnames = (java.util.List)varname;
			int varnameCount = varnames.size();

			for (int i = 0;;++i)
			{
				if (itemIter.hasNext())
				{
					if (i < varnameCount)
					{
						unpackVariable(variables, varnames.get(i), itemIter.next());
					}
					else
					{
						throw new UnpackingException("mismatched for loop unpacking: " + varnameCount + " varnames, >" + i + " items");
					}
				}
				else
				{
					if (i < varnameCount)
					{
						throw new UnpackingException("mismatched for loop unpacking: " + varnameCount + "+ varnames, " + i + " items");
					}
					else
					{
						break;
					}
				}
			}
		}
	}

	public void unpackVariable(Object varname, Object item)
	{
		unpackVariable(getVariables(), varname, item);
	}

	private static Map<String, Object> functions = new HashMap<String, Object>();

	static
	{
		MapUtils.putMap(
			functions,
			"now", new FunctionNow(),
			"utcnow", new FunctionUTCNow(),
			"date", new FunctionDate(),
			"timedelta", new FunctionTimeDelta(),
			"monthdelta", new FunctionMonthDelta(),
			"random", new FunctionRandom(),
			"xmlescape", new FunctionXMLEscape(),
			"csv", new FunctionCSV(),
			"str", new FunctionStr(),
			"repr", new FunctionRepr(),
			"int", new FunctionInt(),
			"float", new FunctionFloat(),
			"bool", new FunctionBool(),
			"len", new FunctionLen(),
			"any", new FunctionAny(),
			"all", new FunctionAll(),
			"enumerate", new FunctionEnumerate(),
			"enumfl", new FunctionEnumFL(),
			"isfirstlast", new FunctionIsFirstLast(),
			"isfirst", new FunctionIsFirst(),
			"islast", new FunctionIsLast(),
			"isundefined", new FunctionIsUndefined(),
			"isdefined", new FunctionIsDefined(),
			"isnone", new FunctionIsNone(),
			"isstr", new FunctionIsStr(),
			"isint", new FunctionIsInt(),
			"isfloat", new FunctionIsFloat(),
			"isbool", new FunctionIsBool(),
			"isdate", new FunctionIsDate(),
			"islist", new FunctionIsList(),
			"isdict", new FunctionIsDict(),
			"istemplate", new FunctionIsTemplate(),
			"isfunction", new FunctionIsFunction(),
			"iscolor", new FunctionIsColor(),
			"istimedelta", new FunctionIsTimeDelta(),
			"ismonthdelta", new FunctionIsMonthDelta(),
			"chr", new FunctionChr(),
			"ord", new FunctionOrd(),
			"hex", new FunctionHex(),
			"oct", new FunctionOct(),
			"bin", new FunctionBin(),
			"abs", new FunctionAbs(),
			"range", new FunctionRange(),
			"min", new FunctionMin(),
			"max", new FunctionMax(),
			"sorted", new FunctionSorted(),
			"type", new FunctionType(),
			"asjson", new FunctionAsJSON(),
			"fromjson", new FunctionFromJSON(),
			"asul4on", new FunctionAsUL4ON(),
			"fromul4on", new FunctionFromUL4ON(),
			"reversed", new FunctionReversed(),
			"randrange", new FunctionRandRange(),
			"randchoice", new FunctionRandChoice(),
			"format", new FunctionFormat(),
			"urlquote", new FunctionURLQuote(),
			"urlunquote", new FunctionURLUnquote(),
			"zip", new FunctionZip(),
			"rgb", new FunctionRGB(),
			"hls", new FunctionHLS(),
			"hsv", new FunctionHSV()
		);
	}
}
