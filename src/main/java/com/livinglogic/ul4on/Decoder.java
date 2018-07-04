/*
** Copyright 2012-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4on;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Stack;

import com.livinglogic.ul4.Color;
import com.livinglogic.ul4.MonthDelta;
import com.livinglogic.ul4.TimeDelta;
import com.livinglogic.ul4.Slice;
import com.livinglogic.ul4.FunctionDate;
import com.livinglogic.ul4.FunctionRepr;

/**
 * A {@code Decoder} object wraps a {@code Reader} object and can read any object
 * in the UL4ON serialization format from this {@code Reader}.
 */
public class Decoder implements Iterable<Object>
{
	/**
	 * The {@code Reader} instance from where serialized objects will be read.
	 */
	private Reader reader = null;

	/**
	 * The current position in the UL4ON stream
	 */
	private int position = 0;

	/**
	 * The next character to be read by {@code nextChar}
	 */
	private int bufferedChar = -1;

	/**
	 * The list of objects that have been read so far from {@code reader} and
	 * that must be available for backreferences.
	 */
	private List<Object> objects = new ArrayList<Object>();

	/**
	 * A {@code Map} that maps string to strings of the same value. This is used
	 * to make sure that string keys in a map always use the same string objects.
	 */
	private Map<Object, Object> keys = new HashMap<Object, Object>();

	/**
	 * Custom type registry. Any type name not found in this registry will be
	 * looked up in the globals registry {@link Utils#registry}
	 */
	private Map<String, ObjectFactory> registry = null;

	/**
	 * Stack of types (used for error reporting).
	 */
	private Stack<String> stack = new Stack<String>();

	/**
	 * Create an {@code Decoder} object for reading serialized UL4ON dump
	 * from the {@code Reader} {@code reader}.
	 *
	 * @param reader the {@code Reader} from which the UL4ON dump will be read.
	 * @param registry custom type registry.
	 */
	public Decoder(Reader reader, Map<String, ObjectFactory> registry)
	{
		this.reader = reader;
		this.registry = registry;
	}

	/**
	 * Reads a object in the UL4ON dump from the reader and returns it.
	 * @return the object read from the stream
	 * @throws IOException if reading from the stream fails
	 */
	public Object load() throws IOException
	{
		char typecode = nextChar();

		if (typecode == '^')
		{
			int position = (Integer)readInt();
			return objects.get(position);
		}
		else if (typecode == 'n' || typecode == 'N')
		{
			if (typecode == 'N')
				loading(null);
			return null;
		}
		else if (typecode == 'b' || typecode == 'B')
		{
			int data = readChar();
			Boolean result;
			if (data == 'T')
				result = true;
			else if (data == 'F')
				result = false;
			else
				throw new DecoderException(position, path(), "expected 'T' or 'F', got " + charRepr(data));
			if (typecode == 'B')
				loading(result);
			++position;
			return result;
		}
		else if (typecode == 'i' || typecode == 'I')
		{
			StringBuilder buffer = new StringBuilder();
			Object result = null;
			while (true)
			{
				int c = readChar();
				if (c == '-' || Character.isDigit(c))
					buffer.append((char)c);
				else
				{
					String string = buffer.toString();
					try
					{
						result = Integer.parseInt(string);
					}
					catch (NumberFormatException ex1)
					{
						try
						{
							result = Long.parseLong(string);
						}
						catch (NumberFormatException ex2)
						{
							result = new BigInteger(string);
						}
					}
					break;
				}
			}
			if (typecode == 'I')
				loading(result);
			return result;
		}
		else if (typecode == 'f' || typecode == 'F')
		{
			double result = readFloat();
			if (typecode == 'F')
				loading(result);
			return result;
		}
		else if (typecode == 's' || typecode == 'S')
		{
			String result = Utils.parseUL4StringFromReader(reader);
			if (typecode == 'S')
				loading(result);
			return result;
		}
		else if (typecode == 'c' || typecode == 'C')
		{
			int oldpos = -1;
			if (typecode == 'C')
				oldpos = beginFakeLoading();

			pushType("color");
			try
			{
				int r = (Integer)load();
				int g = (Integer)load();
				int b = (Integer)load();
				int a = (Integer)load();
				Color result = new Color(r, g, b, a);

				if (typecode == 'C')
					endFakeLoading(oldpos, result);
				return result;
			}
			finally
			{
				popType();
			}
		}
		else if (typecode == 'z' || typecode == 'Z')
		{
			int oldpos = -1;
			if (typecode == 'Z')
				oldpos = beginFakeLoading();

			pushType("datetime");
			try
			{
				int year = (Integer)load();
				int month = (Integer)load();
				int day = (Integer)load();
				int hour = (Integer)load();
				int minute = (Integer)load();
				int second = (Integer)load();
				int microsecond = (Integer)load();
				LocalDateTime result = LocalDateTime.of(year, month, day, hour, minute, second, 1000*microsecond);

				if (typecode == 'Z')
					endFakeLoading(oldpos, result);

				return result;
			}
			finally
			{
				popType();
			}
		}
		else if (typecode == 'x' || typecode == 'X')
		{
			int oldpos = -1;
			if (typecode == 'X')
				oldpos = beginFakeLoading();

			pushType("date");
			try
			{
				int year = (Integer)load();
				int month = (Integer)load();
				int day = (Integer)load();
				LocalDate result = LocalDate.of(year, month, day);

				if (typecode == 'X')
					endFakeLoading(oldpos, result);

				return result;
			}
			finally
			{
				popType();
			}
		}
		else if (typecode == 't' || typecode == 'T')
		{
			int oldpos = -1;
			if (typecode == 'T')
				oldpos = beginFakeLoading();

			pushType("timedelta");
			try
			{
				int days = (Integer)load();
				int seconds = (Integer)load();
				int microseconds = (Integer)load();
				TimeDelta result = new TimeDelta(days, seconds, microseconds);

				if (typecode == 'T')
					endFakeLoading(oldpos, result);
				return result;
			}
			finally
			{
				popType();
			}
		}
		else if (typecode == 'm' || typecode == 'M')
		{
			int oldpos = -1;
			if (typecode == 'M')
				oldpos = beginFakeLoading();

			pushType("monthdelta");
			try
			{
				int months = (Integer)load();
				MonthDelta result = new MonthDelta(months);

				if (typecode == 'M')
					endFakeLoading(oldpos, result);
				return result;
			}
			finally
			{
				popType();
			}
		}
		else if (typecode == 'l' || typecode == 'L')
		{
			List result = new ArrayList();

			if (typecode == 'L')
				loading(result);

			pushType("list");
			try
			{
				while (true)
				{
					typecode = nextChar();
					if (typecode == ']')
						return result;
					else
					{
						pushbackChar(typecode);
						result.add(load());
					}
				}
			}
			finally
			{
				popType();
			}
		}
		else if (typecode == 'd' || typecode == 'D' || typecode == 'e' || typecode == 'E')
		{
			Map result = (typecode == 'e' || typecode == 'E') ? new LinkedHashMap() : new HashMap();

			if (typecode == 'D' || typecode == 'E')
				loading(result);

			pushType("list");
			try
			{
				while (true)
				{
					typecode = nextChar();
					if (typecode == '}')
						return result;
					else
					{
						pushbackChar(typecode);
						Object key = load();
						Object value = load();
						if (key instanceof String)
						{
							Object oldKey = keys.get(key);

							if (oldKey == null)
								keys.put(key, key);
							else
								key = oldKey;
						}

						result.put(key, value);
					}
				}
			}
			finally
			{
				popType();
			}
		}
		else if (typecode == 'y' || typecode == 'Y')
		{
			Set result = new HashSet();

			if (typecode == 'Y')
				loading(result);

			pushType("set");
			try
			{
				while (true)
				{
					typecode = nextChar();
					if (typecode == '}')
						return result;
					else
					{
						pushbackChar(typecode);
						Object item = load();
						result.add(item);
					}
				}
			}
			finally
			{
				popType();
			}
		}
		else if (typecode == 'r' || typecode == 'R')
		{
			int oldpos = -1;
			if (typecode == 'R')
				oldpos = beginFakeLoading();

			pushType("set");
			Object start;
			Object stop;
			try
			{
				start = load();
				stop = load();
			}
			finally
			{
				popType();
			}
			boolean hasStart = (start != null);
			boolean hasStop = (stop != null);
			int startIndex = hasStart ? com.livinglogic.ul4.Utils.toInt(start) : -1;
			int stopIndex = hasStop ? com.livinglogic.ul4.Utils.toInt(stop) : -1;
			Slice result = new Slice(hasStart, hasStop, startIndex, stopIndex);
			if (typecode == 'R')
				endFakeLoading(oldpos, result);
			return result;
		}
		else if (typecode == 'o' || typecode == 'O')
		{
			int oldpos = -1;
			if (typecode == 'O')
				oldpos = beginFakeLoading();

			String name = (String)load();

			ObjectFactory factory = null;

			if (registry != null)
				factory = registry.get(name);

			if (factory == null)
				factory = Utils.registry.get(name);

			if (factory == null)
				throw new DecoderException(position, path(), "can't load object of type " + FunctionRepr.call(name));

			UL4ONSerializable result = factory.create();

			if (typecode == 'O')
				endFakeLoading(oldpos, result);

			pushType(name);
			try
			{
				result.loadUL4ON(this);

				int nextTypecode = nextChar();

				if (nextTypecode != ')')
					throw new DecoderException(position, path(), "object terminator ')' expected, got " + charRepr(nextTypecode));

				return result;
			}
			finally
			{
				popType();
			}
		}
		else
			throw new DecoderException(position, path(), "unknown typecode " + charRepr(typecode));
	}

	/**
	 * Return an iterator that reads the content of an object until the "object terminator" {@code )} is hit.
	 * This object terminator will not be read from the input stream.
	 * Also note, that the iterator should always be exhausted when it is read, otherwise the stream will be
	 * in an undefined state.
	 */
	public Iterator<Object> iterator()
	{
		return new ObjectContentIterator();
	}

	/**
	 * Record {@code obj} in the list of backreferences.
	 */
	private void loading(Object obj)
	{
		objects.add(obj);
	}

	/**
	 * For loading custom object or immutable objects that have attributes we have a problem:
	 * We have to record the object we're loading *now*, so that it is available for backreferences.
	 * However until we've read the UL4ON name of the class (for custom object) or the attributes
	 * of the object (for immutable objects with attributes), we can't create the object.
	 * So we push {@code null} to the backreference list for now and put the right object in this spot,
	 * once we've created it (via {@code endFakeLoading}). This shouldn't lead to problems,
	 * because during the time the backreference is wrong, only the class name is read,
	 * so our object won't be referenced. For immutable objects the attributes normally
	 * don't reference the object itself.
	*/
	private int beginFakeLoading()
	{
		int oldpos = objects.size();
		loading(null);
		return oldpos;
	}

	/**
	 * Fixes backreferences in object list
	 */
	private void endFakeLoading(int oldpos, Object value)
	{
		objects.set(oldpos, value);
	}

	private int readChar() throws IOException
	{
		int c = reader.read();
		++position;
		return c;
	}

	/**
	 * Read the next not-whitespace character
	 */
	private char nextChar() throws IOException
	{
		if (bufferedChar >= 0)
		{
			char result = (char)bufferedChar;
			bufferedChar = -1;
			return result;
		}
		while (true)
		{
			int c = readChar();
			if (c == -1)
				throw new DecoderException(position, path(), "unexpected EOF");

			if (!Character.isWhitespace(c))
				return (char)c;
		}
	}

	private void pushbackChar(char c)
	{
		bufferedChar = c;
	}

	private String charRepr(int c)
	{
		if (c < 0)
			return "EOF";
		else
			return FunctionRepr.call(Character.toString((char)c));
	}

	private int readInt() throws IOException
	{
		StringBuilder buffer = new StringBuilder();

		while (true)
		{
			int c = readChar();
			if (c == '-' || Character.isDigit(c))
				buffer.append((char)c);
			else
			{
				return Integer.parseInt(buffer.toString());
			}
		}
	}

	private double readFloat() throws IOException
	{
		StringBuilder buffer = new StringBuilder();

		while (true)
		{
			int c = readChar();
			if (c == '-' || c == '+' || Character.isDigit(c) || c == '.' || c == 'e' || c == 'E')
				buffer.append((char)c);
			else
				return Double.valueOf(buffer.toString());
		}
	}

	private void pushType(String type)
	{
		stack.push(type);
	}

	private void popType()
	{
		stack.pop();
	}

	private String path()
	{
		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < stack.size(); ++i)
		{
			if (i > 0)
				buffer.append("/");
			buffer.append(stack.get(i));
		}
		return buffer.toString();
	}

	private class ObjectContentIterator implements Iterator
	{
		private char bufferedChar;

		ObjectContentIterator()
		{
			readTypeCode();
		}

		public boolean hasNext()
		{
			return bufferedChar != ')';
		}

		public Object next()
		{
			if (bufferedChar == ')')
				return null;
			else
			{
				Object item = null;
				try
				{
					item = load();
				}
				catch (Exception ex)
				{
					throw new RuntimeException(ex);
				}
				readTypeCode();
				return item;
			}
		}

		public void remove()
		{
			throw new UnsupportedOperationException();
		}

		private void readTypeCode()
		{
			try
			{
				bufferedChar = nextChar();
			}
			catch (Exception ex)
			{
				throw new RuntimeException(ex);
			}
			pushbackChar(bufferedChar);
		}
	}
}
