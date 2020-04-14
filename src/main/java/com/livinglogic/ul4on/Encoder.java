/*
** Copyright 2012-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4on;

import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import com.livinglogic.ul4.UL4Repr;
import com.livinglogic.ul4.UL4GetAttr;
import com.livinglogic.ul4.UL4Dir;
import com.livinglogic.ul4.UL4Type;
import com.livinglogic.ul4.Color;
import com.livinglogic.ul4.MonthDelta;
import com.livinglogic.ul4.TimeDelta;
import com.livinglogic.ul4.Slice;
import com.livinglogic.ul4.FunctionRepr;
import com.livinglogic.ul4.BoundDateMethodYear;
import com.livinglogic.ul4.BoundDateMethodMonth;
import com.livinglogic.ul4.BoundDateMethodDay;
import com.livinglogic.ul4.BoundDateMethodHour;
import com.livinglogic.ul4.BoundDateMethodMinute;
import com.livinglogic.ul4.BoundDateMethodSecond;
import com.livinglogic.ul4.BoundDateMethodMicrosecond;
import com.livinglogic.ul4.AttributeException;
import com.livinglogic.ul4.BoundMethod;
import com.livinglogic.ul4.Signature;
import com.livinglogic.ul4.BoundArguments;

import static com.livinglogic.utils.SetUtils.makeSet;


/**
 * An {@code Encoder} object is used for writing any object to a
 * {@code Writer} object using the UL4ON serialization format (or returning
 * such an object dump as a string).
 */
public class Encoder implements UL4Repr, UL4GetAttr, UL4Dir, UL4Type
{
	/**
	 * The {@code Writer} instance where the final output currently will be written.
	 * Set temporarily during calls to {@code dump}, so that the argument doesn't
	 * have to be passed around.
	 */
	private Writer writer = null;

	/**
	 * {@code indent} specifies which string should be used for indentation
	 * when pretty printing (<code>null</code> means no pretty printing).
	 */
	private String indent = null;

	/**
	 * {@code level} specifies the indentation level when pretty printing.
	 */
	private int level = 0;

	/**
	 * {@code first} specifies wether any output has been written or not.
	 */
	private boolean first = true;


	/**
	 * A {@code Map} that maps certain objects that have been output before to an
	 * index that specifies at which position in the list of unique objects that
	 * have been output before this object is.
	 */
	private Map<Object, Integer> object2id = new IdentityHashMap<Object, Integer>();

	/**
	 * A {@code Map} that maps string to strings of the same value. This is used
	 * to make sure that strings always use the same string object.
	 */
	private Map<String, String> strings = new HashMap<String, String>();

	/**
	 * Create an {@code Encoder} object for writing serialized UL4ON output
	 * to the {@code Writer} {@code writer}
	 */
	public Encoder(String indent)
	{
		this.indent = indent;
		this.level = 0;
		this.first = true;
	}

	/**
	 * Create an {@code Encoder} object for writing serialized UL4ON output
	 * to the {@code Writer} {@code writer}
	 */
	public Encoder()
	{
		this(null);
	}

	private void reset(Writer writer)
	{
		this.writer = writer;
		level = 0;
		first = true;
	}

	/**
	 * Record that the object {@code obj} has been output and should be available
	 * to output backreferences to this object later.
	 */
	private void record(Object obj)
	{
		object2id.put(obj, object2id.size());
	}

	private void line(String line, Object... additionalObjs) throws IOException
	{
		// Write indentation/separator
		if (indent != null)
		{
			for (int i = 0; i < level; ++i)
				writer.write(indent);
		}
		else
		{
			if (!first)
				writer.write(" ");
		}
		first = false;

		writer.write(line);

		String oldindent = indent;
		try
		{
			indent = null;

			for (Object obj : additionalObjs)
				dump(obj);
		}
		finally
		{
			indent = oldindent;
		}

		if (indent != null)
			writer.write("\n");
	}

	private Object internString(Object obj)
	{
		if (obj instanceof String)
		{
			String str = (String)obj;
			String oldStr = strings.get(str);
			if (oldStr != null)
				obj = oldStr;
			else
				strings.put(str, str);
		}
		return obj;
	}

	/**
	 * Create an UL4ON dump of an object and write it to an output writer.
	 * @param writer the output stream to which the dump should be written.
	 * @param obj the object to be dumped.
	 */
	public void dump(Writer writer, Object obj) throws IOException
	{
		reset(writer);
		dump(obj);
		this.writer = null;
	}

	/**
	 * Create an UL4ON dump of an object and return the dump as a string.
	 * @param obj the object to be dumped.
	 * @return the UL4ON dump of the object
	 */
	public String dumps(Object obj)
	{
		try (StringWriter writer = new StringWriter())
		{
			reset(writer);
			dump(obj);
			String result = writer.toString();
			this.writer = null;
			return result;
		}
		catch (IOException exc)
		{
			// can't happen anyway
			throw new RuntimeException(exc);
		}
	}

	/**
	 * Writes the object {@code obj} to the writer in the UL4ON object serialization format.
	 * This is called by implementations of {@see UL4ONSerializable}, but should
	 * not be called from outside, as {@code writer} may not be set in this case.
	 * @param obj the object to be dumped.
	 * @throws IOException if writing to the stream fails
	 */
	public void dump(Object obj) throws IOException
	{
		// Have we serialized this object before?
		obj = internString(obj);
		Integer index = object2id.get(obj);
		if (index != null)
		{
			// Yes -> output a backreference
			String indexStr = index.toString();
			line("^" + indexStr);
		}
		else
		{
			// No -> write the real object
			if (obj == null)
				line("n");
			else if (obj instanceof Boolean)
				line(((Boolean)obj).booleanValue() ? "bT" : "bF");
			else if (obj instanceof Integer || obj instanceof Long || obj instanceof Byte || obj instanceof Short || obj instanceof BigInteger)
				line("i" + obj.toString());
			else if (obj instanceof Float || obj instanceof Double || obj instanceof BigDecimal)
				line("f" + obj.toString());
			else if (obj instanceof String)
			{
				record(obj);
				String dump = FunctionRepr.call((String)obj);
				dump = dump.replace("<", "\\x3c");
				line("S" + dump);
			}
			else if (obj instanceof Date)
			{
				record(obj);
				Date date = (Date)obj;
				line("Z", BoundDateMethodYear.call(date), BoundDateMethodMonth.call(date), BoundDateMethodDay.call(date), BoundDateMethodHour.call(date), BoundDateMethodMinute.call(date), BoundDateMethodSecond.call(date), BoundDateMethodMicrosecond.call(date));
			}
			else if (obj instanceof LocalDate)
			{
				record(obj);
				LocalDate date = (LocalDate)obj;
				line("X", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
			}
			else if (obj instanceof LocalDateTime)
			{
				record(obj);
				LocalDateTime datetime = (LocalDateTime)obj;
				line("Z", datetime.getYear(), datetime.getMonthValue(), datetime.getDayOfMonth(), datetime.getHour(), datetime.getMinute(), datetime.getSecond(), datetime.getNano()/1000);
			}
			else if (obj instanceof TimeDelta)
			{
				record(obj);
				TimeDelta td = (TimeDelta)obj;
				line("T", td.getDays(), td.getSeconds(), td.getMicroseconds());
			}
			else if (obj instanceof MonthDelta)
			{
				record(obj);
				line("M", ((MonthDelta)obj).getMonths());
			}
			else if (obj instanceof Color)
			{
				record(obj);
				Color color = (Color)obj;
				line("C", color.getR(), color.getG(), color.getB(), color.getA());
			}
			else if (obj instanceof Slice)
			{
				line("r", ((Slice)obj).getStart(), ((Slice)obj).getStop());
			}
			else if (obj instanceof UL4ONSerializable) // check this before Collection and Map
			{
				record(obj);
				line("O", internString(((UL4ONSerializable)obj).getUL4ONName()));
				++level;
				((UL4ONSerializable)obj).dumpUL4ON(this);
				--level;
				line(")");
			}
			else if (obj instanceof Set)
			{
				record(obj);
				line("Y");
				++level;
				for (Object item: (Set<Object>)obj)
				{
					dump(item);
				}
				--level;
				line("}");
			}
			else if (obj instanceof Collection)
			{
				record(obj);
				line("L");
				++level;
				for (Object o: (Collection)obj)
					dump(o);
				--level;
				line("]");
			}
			else if (obj instanceof Map)
			{
				record(obj);
				line(obj instanceof LinkedHashMap ? "E" : "D");
				++level;
				for (Map.Entry entry: ((Map<Object, Object>)obj).entrySet())
				{
					dump(entry.getKey());
					dump(entry.getValue());
				}
				--level;
				line("}");
			}
			else
			{
				throw new RuntimeException("unknown type " + obj.getClass());
			}
		}
	}

	@Override
	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter
			.append("< ")
			.append(getClass().getName())
			.append(" indent=")
			.visit(indent)
			.append(">")
		;
	}

	protected static Set<String> attributes = makeSet("dumps");

	@Override
	public Set<String> dirUL4()
	{
		return attributes;
	}

	@Override
	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "dumps":
				return new BoundMethodDumpS(this);
			default:
				throw new AttributeException(this, key);
		}
	}

	private static class BoundMethodDumpS extends BoundMethod<Encoder>
	{
		public BoundMethodDumpS(Encoder object)
		{
			super(object);
		}

		@Override
		public String nameUL4()
		{
			return "dumps";
		}

		private static final Signature signature = new Signature("obj", Signature.required);

		@Override
		public Signature getSignature()
		{
			return signature;
		}

		@Override
		public Object evaluate(BoundArguments arguments)
		{
			return object.dumps(arguments.get(0));
		}
	}

	@Override
	public String typeUL4()
	{
		return "ul4on.Encoder";
	}
}
