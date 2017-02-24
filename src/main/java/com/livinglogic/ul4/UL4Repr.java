/*
** Copyright 2012-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;
import java.util.Map;
import java.util.Collection;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Stack;

import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * <p>Interface for implementing support for the UL4 function {@code repr}
 * in Java classes. (see {@link FunctionRepr})</p>
 *
 * <p>An implementation might look like this:</p>
 *
 * <pre>
 * import com.livinglogic.ul4.UL4Repr;
 * 
 * public class Person implements UL4Repr
 * {
 *    private String firstname;
 *    private String lastname;
 * 
 *    public void reprUL4(UL4Repr.Formatter formatter)
 *    {
 *       formatter.append("<");
 *       formatter.append(getClass().getName());
 *       formatter.append(" firstname=");
 *       formatter.visit(firstname);
 *       formatter.append(" lastname=");
 *       formatter.visit(lastname);
 *       formatter.append(">");
 *    }
 * }
 * </pre>
 *
 * @author W. Doerwald
 */
public interface UL4Repr
{
	/**
	 * <p>Output this object in "repr" format.</p>
	 *
	 * <p>To do this, methods on the {@code Formatter} object should be called,
	 * i.e. {@link Formatter#append} to output literal text and
	 * {@link Formatter#visit} to output the "repr" version of another object.</p>
	 *
	 *
	 * @param formatter The {@code Formatter} object that handles output.
	 */
	void reprUL4(Formatter formatter);

	/**
	 * The {@code Formatter} class provides helper methods that classes can
	 * use when implementing the {@link UL4Repr} interface;
	 */
	public static class Formatter
	{
		private Stack<Object> visited;
		private StringBuilder buffer;
		private boolean ascii; // Limit the repr output of strings to ASCII?

		private static SimpleDateFormat isoReprDateFormatter = new SimpleDateFormat("@'('yyyy-MM-dd')'");
		private static SimpleDateFormat isoReprDateTimeFormatter = new SimpleDateFormat("@'('yyyy-MM-dd'T'HH:mm:ss')'");
		private static SimpleDateFormat isoReprTimestampMicroFormatter = new SimpleDateFormat("@'('yyyy-MM-dd'T'HH:mm:ss.SSS'000)'");

		/**
		 * Creates a {@code Formatter} object.
		 *
		 * @param ascii specifies whether the repr output of strings should be
		 *              forced to contain only ASCII characters or not.
		 */
		public Formatter(boolean ascii)
		{
			visited = new Stack<Object>();
			buffer = new StringBuilder();
			this.ascii = ascii;
		}

		/**
		 * Append a literal string to the output.
		 *
		 * @param string The string to append.
		 */
		public void append(String string)
		{
			buffer.append(string);
		}

		/**
		 * Append a literal character to the output.
		 *
		 * @param c The character to append.
		 */
		public void append(char c)
		{
			buffer.append(c);
		}

		private void appendHex(int value, int length)
		{
			String s = Integer.toHexString(value);
			for (int i = s.length(); i < length; ++i)
				append("0");
			append(s);
		}

		/**
		 * <p>Append the repr output of an object to the current output.</p>
		 *
		 * <p>Cycles in hierarchical data structures will be detected ({@code ...}
		 * will be output in this case).</p>
		 * 
		 * <p>If an exception occurs during formatting of the object {@code ???}
		 * will be output instead.</p>
		 *
		 * @param object The object that should be "repr formatted".
		 */
		public Formatter visit(Object object)
		{
			if (object == null)
				append("None");
			else if (object instanceof Boolean)
				append(((Boolean)object).booleanValue() ? "True" : "False");
			else if (object instanceof Integer || object instanceof Byte || object instanceof Short || object instanceof Long || object instanceof BigInteger || object instanceof Double || object instanceof Float)
				append(object.toString());
			else if (object instanceof BigDecimal)
			{
				String result = object.toString();
				if (result.indexOf('.') < 0 && result.indexOf('E') < 0 && result.indexOf('e') < 0)
					result += ".0";
				append(result);
			}
			else if (object instanceof String)
			{
				String string = (String)object;
				//buffer.ensureCapacity(buffer.capacity() + string.length() + 2);

				boolean haveSQuote = false;
				boolean haveDQuote = false;
				for (int i = 0; i < string.length(); ++i)
				{
					char c = string.charAt(i);
					if (c == '\'')
					{
						haveSQuote = true;
						if (haveDQuote)
							break;
					}
					else if (c == '"')
					{
						haveDQuote = true;
						if (haveSQuote)
							break;
					}
				}
				// Prefer single quotes: Only use double quotes if the string contains single quotes, but no double quotes
				char quote = haveSQuote && !haveDQuote ? '"' : '\'';

				append(quote);
				for (int i = 0; i < string.length(); ++i)
				{
					char c = string.charAt(i);
					switch (c)
					{
						// Escape quotes if necesssary
						case '"':
							if (c == quote)
								append("\\\"");
							else
								append('"');
							break;
						case '\'':
							if (c == quote)
								append("\\'");
							else
								append('\'');
							break;
						// Escape backslash
						case '\\':
							append("\\\\");
							break;
						// Escape ASCII whitespace
						case '\t':
							append("\\t");
							break;
						case '\n':
							append("\\n");
							break;
						case '\r':
							append("\\r");
							break;
						default:
							int cp = (int)c;
							// Escape C0 control characters
							if (cp < 0x20)
							{
								append("\\x");
								appendHex(cp, 2);
							}
							// Output the rest of ASCII (except for U+007F) literally
							else if (cp < 0x7f)
								append(c);
							// If the output should be ASCII, escape everything else
							else if (ascii)
							{
								if (cp <= 0xff)
								{
									append("\\x");
									appendHex(cp, 2);
								}
								else
								{
									append("\\u");
									appendHex(cp, 4);
								}
							}
							// else escape everything in the Unicode categories: Cc, Cf, Cs, Co, Cn, Zl, Zp, Zs
							else
							{
								int charType = Character.getType(c);
								if (charType == Character.CONTROL ||
								    charType == Character.FORMAT ||
								    charType == Character.SURROGATE ||
								    charType == Character.PRIVATE_USE ||
								    charType == Character.UNASSIGNED ||
								    charType == Character.LINE_SEPARATOR ||
								    charType == Character.PARAGRAPH_SEPARATOR ||
								    charType == Character.SPACE_SEPARATOR)
								{
									if (cp <= 0xff)
									{
										append("\\x");
										appendHex(cp, 2);
									}
									else
									{
										append("\\u");
										appendHex(cp, 4);
									}
								}
								else
									append(c);
							}
							break;
					}
				}
				append(quote);
			}
			else if (object instanceof Date)
				visitDate((Date)object);
			else
			{
				if (seen(object))
				{
					append("...");
				}
				else
				{
					visited.push(object);
					try
					{
						if (object instanceof UL4Repr)
							((UL4Repr)object).reprUL4(this);
						else if (object instanceof Set)
							visitSet((Set)object);
						else if (object instanceof Collection)
							visitCollection((Collection)object);
						else if (object instanceof Object[])
							visitArray((Object[])object);
						else if (object instanceof Map)
							visitMap((Map)object);
						else if (object instanceof Class)
							visitClass((Class)object);
						else
							append("<" + object.getClass().getName() + ">");
					}
					catch (Throwable t)
					{
						append("???");
					}
					visited.pop();
				}
			}
			return this;
		}

		private void visitClass(Class object)
		{
			append("<");
			append(object.getClass().getName());
			append(" class=");
			visit(object.getName());
			append(">");
		}

		private void visitDate(Date object)
		{
			if (BoundDateMethodMicrosecond.call(object) != 0)
				append(isoReprTimestampMicroFormatter.format(object));
			else
			{
				if (BoundDateMethodHour.call(object) != 0 || BoundDateMethodMinute.call(object) != 0 || BoundDateMethodSecond.call(object) != 0)
					append(isoReprDateTimeFormatter.format(object));
				else
					append(isoReprDateFormatter.format(object));
			}
		}

		private void visitSet(Set object)
		{
			append("{");
			boolean first = true;
			for (Object item : object)
			{
				if (first)
					first = false;
				else
					append(", ");
				visit(item);
			}
			if (first) // set is empty
				append("/");
			append("}");
		}

		private void visitCollection(Collection object)
		{
			append("[");
			boolean first = true;
			for (Object item : object)
			{
				if (first)
					first = false;
				else
					append(", ");
				visit(item);
			}
			append("]");
		}

		private void visitArray(Object[] object)
		{
			append("[");
			boolean first = true;
			for (Object item : object)
			{
				if (first)
					first = false;
				else
					append(", ");
				visit(item);
			}
			append("]");
		}

		private void visitMap(Map object)
		{
			append("{");
			boolean first = true;

			Set<Map.Entry> entrySet = ((Map)object).entrySet();
			for (Map.Entry entry : entrySet)
			{
				if (first)
					first = false;
				else
					append(", ");
				visit(entry.getKey());
				append(": ");
				visit(entry.getValue());
			}
			append("}");
		}

		/**
		 * Returns the output collected so far as a {@link java.lang.String}.
		 */
		public String toString()
		{
			return buffer.toString();
		}

		private boolean seen(Object obj)
		{
			for (Object o : visited)
			{
				if (obj == o)
					return true;
			}
			return false;
		}
	}
}
