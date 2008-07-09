package com.livinglogic.ul4;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.StringEscapeUtils;

class Range extends AbstractList
{
	int start;

	int stop;

	int step;

	int length;

	public Range(int start, int stop, int step)
	{
		if (0 == step)
		{
			throw new IllegalArgumentException("Step argument must be different from zero!");
		}
		else if (0 < step)
		{
			this.length = rangeLength(start, stop, step);
		}
		else
		{
			this.length = rangeLength(stop, start, -step);
		}
		this.start = start;
		this.stop = stop;
		this.step = step;
	}

	public Object get(int index)
	{
		if ((index < 0) || (index >= length))
		{
			throw new IndexOutOfBoundsException("Invalid index: " + index);
		}
		return new Integer(start + index * step);
	}

	protected int rangeLength(int lowerEnd, int higherEnd, int positiveStep)
	{
		int retVal = 0;
		if (lowerEnd < higherEnd)
		{
			int diff = higherEnd - lowerEnd - 1;
			retVal = diff/positiveStep + 1;
		}
		return retVal;
	}

	public int size()
	{
		return length;
	}
}

class StringIterator implements Iterator
{
	String string;

	int stringSize;

	int index;

	public StringIterator(String string)
	{
		this.string = string;
		stringSize = string.length();
		index = 0;
	}

	public boolean hasNext()
	{
		return index < stringSize;
	}

	public Object next()
	{
		if (index >= stringSize)
		{
			throw new NoSuchElementException("No more characters available!");
		}
		return String.valueOf(string.charAt(index++));
	}

	public void remove()
	{
		throw new UnsupportedOperationException("Strings don't support character removal!");
	}
}

class MapItemIterator implements Iterator
{
	Iterator iterator;

	public MapItemIterator(Map map)
	{
		iterator = map.entrySet().iterator();
	}

	public boolean hasNext()
	{
		return iterator.hasNext();
	}

	public Object next()
	{
		Vector retVal = new Vector(2);
		Map.Entry entry = (Map.Entry)iterator.next();
		retVal.add(entry.getKey());
		retVal.add(entry.getValue());
		return retVal;
	}

	public void remove()
	{
		iterator.remove();
	}
}

class SequenceEnumerator implements Iterator
{
	Iterator sequenceIterator;

	int index = 0;

	public SequenceEnumerator(Iterator sequenceIterator)
	{
		this.sequenceIterator = sequenceIterator;
	}

	public boolean hasNext()
	{
		return sequenceIterator.hasNext();
	}

	public Object next()
	{
		Vector retVal = new Vector(2);
		retVal.add(new Integer(index++));
		retVal.add(sequenceIterator.next());
		return retVal;
	}

	public void remove()
	{
		sequenceIterator.remove();
	}
}

public class Utils
{
	protected static final Integer INTEGER_TRUE = new Integer(1);

	protected static final Integer INTEGER_FALSE = new Integer(0);

	public static Object neg(Integer arg)
	{
		return new Integer(-arg.intValue());
	}

	public static Object neg(Number arg)
	{
		return new Double(-arg.doubleValue());
	}

	public static Object neg(Object arg)
	{
		if (arg instanceof Integer)
			return neg((Integer)arg);
		else if (arg instanceof Number)
			return neg((Number)arg);
		throw new UnsupportedOperationException("Can't negate instance of " + arg.getClass() + "!");
	}

	public static Object add(Integer arg1, Integer arg2)
	{
		return new Integer(arg1.intValue() + arg2.intValue());
	}

	public static Object add(Number arg1, Number arg2)
	{
		return new Double(arg1.doubleValue() + arg2.doubleValue());
	}

	public static Object add(String arg1, String arg2)
	{
		return arg1 + arg2;
	}

	public static Object add(Object arg1, Object arg2)
	{
		if (arg1 instanceof Integer && arg2 instanceof Integer)
			return add((Integer)arg1, (Integer)arg2);
		else if (arg1 instanceof Number && arg2 instanceof Number)
			return add((Number)arg1, (Number)arg2);
		else if (arg1 instanceof String && arg2 instanceof String)
			return add((String)arg1, (String)arg2);
		throw new UnsupportedOperationException("Can't add instances of " + arg1.getClass() + " and " + arg2.getClass() + "!");
	}

	public static Object sub(Integer arg1, Integer arg2)
	{
		return new Integer(arg1.intValue() - arg2.intValue());
	}

	public static Object sub(Number arg1, Number arg2)
	{
		return new Double(arg1.doubleValue() - arg2.doubleValue());
	}

	public static Object sub(Object arg1, Object arg2)
	{
		if (arg1 instanceof Integer && arg2 instanceof Integer)
			return sub((Integer)arg1, (Integer)arg2);
		else if (arg1 instanceof Number && arg2 instanceof Number)
			return sub((Number)arg1, (Number)arg2);
		else if (arg1 instanceof String && arg2 instanceof String)
			return sub((String)arg1, (String)arg2);
		throw new UnsupportedOperationException("Can't subtract instances of " + arg1.getClass() + " and " + arg2.getClass() + "!");
	}

	public static Object mul(String arg1, Integer arg2)
	{
		return StringUtils.repeat(arg1, arg2.intValue());
	}

	public static Object mul(Integer arg1, String arg2)
	{
		return StringUtils.repeat(arg2, arg1.intValue());
	}

	public static Object mul(Integer arg1, Integer arg2)
	{
		return new Integer(arg1.intValue() * arg2.intValue());
	}

	public static Object mul(Number arg1, Number arg2)
	{
		return new Double(arg1.doubleValue() * arg2.doubleValue());
	}

	public static Object mul(Object arg1, Object arg2)
	{
		if (arg1 instanceof String && arg2 instanceof Integer)
			return mul((String)arg1, (Integer)arg2);
		if (arg1 instanceof Integer && arg2 instanceof String)
			return mul((Integer)arg1, (String)arg2);
		if (arg1 instanceof Integer && arg2 instanceof Integer)
			return mul((Integer)arg1, (Integer)arg2);
		if (arg1 instanceof Number && arg2 instanceof Number)
			return mul((Number)arg1, (Number)arg2);
		throw new UnsupportedOperationException("Can't multiply instances of " + arg1.getClass() + " and " + arg2.getClass() + "!");
	}

	public static Object truediv(Number arg1, Number arg2)
	{
		return new Double(arg1.doubleValue() / arg2.doubleValue());
	}

	public static Object truediv(Object arg1, Object arg2)
	{
		throw new UnsupportedOperationException("Can't divide instances of " + arg1.getClass() + " and " + arg2.getClass() + "!");
	}

	public static Object floordiv(Integer arg1, Integer arg2)
	{
		return new Integer(arg1.intValue() / arg2.intValue());
	}

	public static Object floordiv(Number arg1, Number arg2)
	{
		return new Double((int)(arg1.doubleValue() / arg2.doubleValue()));
	}

	public static Object floordiv(Object arg1, Object arg2)
	{
		throw new UnsupportedOperationException("Can't divide instances of " + arg1.getClass() + " and " + arg2.getClass() + "!");
	}

	public static Object mod(Integer arg1, Integer arg2)
	{
		return new Integer(arg1.intValue() % arg2.intValue());
	}

	public static Object mod(Object arg1, Object arg2)
	{
		throw new UnsupportedOperationException("Can't apply the modulo operator to instances of " + arg1.getClass() + " and " + arg2.getClass() + "!");
	}

	public static Object getItem(String arg1, Integer arg2)
	{
		int index = arg2.intValue();
		if (0 > index)
		{
			index += arg1.length();
		}
		return arg1.substring(index, index + 1);
	}

	public static Object getItem(List arg1, Integer arg2)
	{
		int index = arg2.intValue();
		if (0 > index)
		{
			index += arg1.size();
		}
		return arg1.get(index);
	}

	public static Object getItem(Map arg1, Object arg2)
	{
		return arg1.get(arg2);
	}

	public static Object getItem(Object arg1, Object arg2)
	{
		if (arg1 instanceof String && arg2 instanceof Integer)
			return getItem((String)arg1, (Integer)arg2);
		else if (arg1 instanceof List && arg2 instanceof Integer)
			return getItem((List)arg1, (Integer)arg2);
		else if (arg1 instanceof Map && arg2 instanceof Integer)
			return getItem((Map)arg1, (Integer)arg2);
		throw new UnsupportedOperationException("Instance of " + arg1.getClass() + " does not support getitem with argument of type " + arg2.getClass() + "!");
	}

	private static int getSliceStartPos(int sequenceSize, Integer virtualPos)
	{
		int retVal;
		if (null == virtualPos)
		{
			retVal = 0;
		}
		else
		{
			retVal = virtualPos.intValue();
			if (0 > retVal)
			{
				retVal += sequenceSize;
			}
			if (0 > retVal)
			{
				retVal = 0;
			}
			else if (sequenceSize < retVal)
			{
				retVal = sequenceSize;
			}
		}
		return retVal;
	}

	private static int getSliceEndPos(int sequenceSize, Integer virtualPos)
	{
		int retVal;
		if (null == virtualPos)
		{
			retVal = sequenceSize;
		}
		else
		{
			retVal = virtualPos.intValue();
			if (0 > retVal)
			{
				retVal += sequenceSize;
			}
			if (0 > retVal)
			{
				retVal = 0;
			}
			else if (sequenceSize < retVal)
			{
				retVal = sequenceSize;
			}
		}
		return retVal;
	}

	public static Object getSlice(List arg1, Integer arg2, Integer arg3)
	{
		int size = arg1.size();
		int start = getSliceStartPos(size, arg2);
		int end = getSliceEndPos(size, arg3);
		if (end < start)
			end = start;
		return arg1.subList(start, end);
	}

	public static Object getSlice(String arg1, Integer arg2, Integer arg3)
	{
		return StringUtils.substring(arg1, arg2, arg3);
	}

	public static Object getSlice(Object arg1, Object arg2, Object arg3)
	{
		if (arg1 instanceof List)
			return getSlice((List)arg1, (Integer)arg2, (Integer)arg3);
		else if (arg1 instanceof String)
			return getSlice((String)arg1, (Integer)arg2, (Integer)arg3);
		throw new UnsupportedOperationException("Instance of " + arg1.getClass() + " does not support getslice with arguments of type " + arg2.getClass() + " and " + arg3.getClass() + "!");
	}

	public static boolean getBool(Boolean obj)
	{
		return obj.booleanValue();
	}

	public static boolean getBool(String obj)
	{
		return (obj.length() > 0);
	}

	public static boolean getBool(Integer obj)
	{
		return (obj.intValue() != 0);
	}

	public static boolean getBool(Double obj)
	{
		return (obj.doubleValue() != 0.);
	}

	public static boolean getBool(Date obj)
	{
		return true;
	}

	public static boolean getBool(Collection obj)
	{
		return !obj.isEmpty();
	}

	public static boolean getBool(Map obj)
	{
		return !obj.isEmpty();
	}

	public static boolean getBool(Object obj)
	{
		if (null == obj)
			return false;
		else if (obj instanceof Boolean)
			return getBool((Boolean)obj);
		else if (obj instanceof String)
			return getBool((String)obj);
		else if (obj instanceof Integer)
			return getBool((Integer)obj);
		else if (obj instanceof Double)
			return getBool((Double)obj);
		else if (obj instanceof Date)
			return getBool((Date)obj);
		else if (obj instanceof Collection)
			return getBool((Collection)obj);
		else if (obj instanceof Map)
			return getBool((Map)obj);
		return true;
	}

	public static boolean lt(Object obj1, Object obj2)
	{
		if (null != obj1)
		{
			if (null != obj2)
				return ((Comparable)obj1).compareTo(obj2) < 0;
		}
		else if (null == obj2)
			return false;
		throw new RuntimeException("Can't compare object to null!");
	}

	public static boolean le(Object obj1, Object obj2)
	{
		if (null != obj1)
		{
			if (null != obj2)
				return ((Comparable)obj1).compareTo(obj2) <= 0;
		}
		else if (null == obj2)
			return true;
		throw new RuntimeException("Can't compare object to null!");
	}

	public static boolean contains(String obj, String container)
	{
		return container.indexOf(obj) >= 0;
	}

	public static boolean contains(Object obj, Collection container)
	{
		return container.contains(obj);
	}

	public static boolean contains(Object obj, Map container)
	{
		return container.containsKey(obj);
	}

	public static boolean contains(Object obj, Object container)
	{
		throw new RuntimeException("Can't determine presence for instance of " + obj.getClass() + " in container instance of class " + container.getClass() + "!");
	}

	public static String xmlescape(Object obj)
	{
		if (obj == null)
			return "";

		String str = obj.toString();
		int length = str.length();
		StringBuffer sb = new StringBuffer((int)(1.2 * length));
		for (int offset = 0; offset < length; offset++)
		{
			char c = str.charAt(offset);
			switch (c)
			{
				case '<':
					sb.append("&lt;");
					break;
				case '>':
					sb.append("&gt;");
					break;
				case '&':
					sb.append("&amp;");
					break;
				case '\'':
					sb.append("&#39;");
					break;
				case '"':
					sb.append("&quot;");
					break;
				case '\t':
					sb.append(c);
					break;
				case '\n':
					sb.append(c);
					break;
				case '\r':
					sb.append(c);
					break;
				case '\u0085':
					sb.append(c);
					break;
				default:
					if ((('\u0020' <= c) && (c <= '\u007e')) || ('\u00A0' <= c))
						sb.append(c);
					else
						sb.append("&#").append((int)c).append(';');
					break;
			}
		}
		return sb.toString();
	}

	public static String csvescape(Object obj)
	{
		if (obj == null)
			return "";
		if (!(obj instanceof String))
			obj = repr(obj);
		return StringEscapeUtils.escapeCsv((String)obj);
	}

	public static Object toInteger(String obj)
	{
		return Integer.valueOf(obj);
	}

	public static Object toInteger(Integer obj)
	{
		return obj;
	}

	public static Object toInteger(Number obj)
	{
		return new Integer(obj.intValue());
	}

	public static Object toInteger(Boolean obj)
	{
		return obj.booleanValue() ? INTEGER_TRUE : INTEGER_FALSE;
	}

	public static Object toInteger(Object obj)
	{
		if (obj instanceof String)
			return toInteger((String)obj);
		else if (obj instanceof Integer)
			return toInteger((Integer)obj);
		else if (obj instanceof Number)
			return toInteger((Number)obj);
		else if (obj instanceof Boolean)
			return toInteger((Boolean)obj);
		throw new UnsupportedOperationException("Can't convert instance of " + obj.getClass() + " to an integer!");
	}

	public static String repr(Object obj)
	{
		if (obj == null)
			return "None";
		else if (obj instanceof Boolean)
			return ((Boolean)obj).booleanValue() ? "True" : "False";
		else if (obj instanceof Integer)
			return String.valueOf(((Integer)obj).intValue());
		else if (obj instanceof Double)
			return String.valueOf(((Double)obj).doubleValue());
		else if (obj instanceof String)
			return new StringBuffer()
				.append("\"")
				.append(StringEscapeUtils.escapeJava(((String)obj)))
				.append("\"")
				.toString();
		else if (obj instanceof Date)
			return isoformat((Date)obj);
		else if (obj instanceof Collection)
		{
			StringBuffer sb = new StringBuffer();
			sb.append("[");
			boolean first = true;
			for (Iterator iter = ((Collection)obj).iterator(); iter.hasNext();)
			{
				if (first)
					first = false;
				else
					sb.append(", ");
				sb.append(repr(iter.next()));
			}
			sb.append("]");
			return sb.toString();
		}
		else if (obj instanceof Map)
		{
			StringBuffer sb = new StringBuffer();
			sb.append("{");
			boolean first = true;
			for (Iterator iter = ((Map)obj).entrySet().iterator(); iter.hasNext();)
			{
				if (first)
					first = false;
				else
					sb.append(", ");
				Map.Entry entry = (Map.Entry)iter.next();
				sb.append(repr(entry.getKey()));
				sb.append(": ");
				sb.append(repr(entry.getValue()));
			}
			sb.append("}");
			return sb.toString();
		}
		return null;
	}
	
	public static Object length(String obj)
	{
		return new Integer(obj.length());
	}

	public static Object length(Collection obj)
	{
		return new Integer(obj.size());
	}

	public static Object length(Map obj)
	{
		return new Integer(obj.size());
	}

	public static Object length(Object obj)
	{
		if (obj instanceof String)
			return length((String)obj);
		else if (obj instanceof Collection)
			return length((Collection)obj);
		else if (obj instanceof Map)
			return length((Map)obj);
		throw new UnsupportedOperationException("Can't determine length for instance of " + obj.getClass() + "!");
	}

	public static Iterator iterator(String obj)
	{
		return new StringIterator(obj);
	}

	public static Iterator iterator(Collection obj)
	{
		return obj.iterator();
	}

	public static Iterator iterator(Map obj)
	{
		return obj.keySet().iterator();
	}

	public static Iterator iterator(Object obj)
	{
		if (obj instanceof String)
			return iterator((String)obj);
		else if (obj instanceof Collection)
			return iterator((Collection)obj);
		else if (obj instanceof Map)
			return iterator((Map)obj);
		else if (obj instanceof Iterator)
			return (Iterator)obj;
		throw new UnsupportedOperationException("Can't iterate instance of " + obj.getClass() + "!");
	}

	public static Object enumerate(Object obj)
	{
		return new SequenceEnumerator(iterator(obj));
	}

	public static Object chr(Integer obj)
	{
		int intValue = obj.intValue();
		char charValue = (char)intValue;
		if (intValue != (int)charValue)
		{
			throw new IndexOutOfBoundsException("Code point " + intValue + " is invalid!");
		}
		return String.valueOf(charValue);
	}

	public static Object chr(Object obj)
	{
		if (obj instanceof Integer)
			return chr((Integer)obj);
		throw new UnsupportedOperationException("Instance of " + obj.getClass() + " is no valid unicode codepoint!");
	}

	public static Object ord(String obj)
	{
		if (1 != obj.length())
		{
			throw new IllegalArgumentException("String " + obj + " contains more than one unicode character!");
		}
		return new Integer((int)obj.charAt(0));
	}

	public static Object ord(Object obj)
	{
		if (obj instanceof String)
			return chr((String)obj);
		throw new UnsupportedOperationException("Can't determine unicode code point for instance of " + obj.getClass() + "!");
	}

	public static Object hex(Integer obj)
	{
		return "0x" + Integer.toHexString(obj.intValue());
	}

	public static Object hex(Object obj)
	{
		if (obj instanceof Integer)
			return chr((Integer)obj);
		throw new UnsupportedOperationException("Instance of " + obj.getClass() + " can't be represented as a hexadecimal string!");
	}

	public static Object oct(Integer obj)
	{
		return "0o" + Integer.toOctalString(obj.intValue());
	}

	public static Object oct(Object obj)
	{
		if (obj instanceof Integer)
			return chr((Integer)obj);
		throw new UnsupportedOperationException("Instance of " + obj.getClass() + " can't be represented as an octal string!");
	}

	public static Object bin(Integer obj)
	{
		return "0b" + Integer.toBinaryString(obj.intValue());
	}

	public static Object bin(Object obj)
	{
		if (obj instanceof Integer)
			return chr((Integer)obj);
		throw new UnsupportedOperationException("Instance of " + obj.getClass() + " can't be represented as a binary string!");
	}

	public static Object sorted(String obj)
	{
		Vector retVal;
		int length = obj.length();
		retVal = new Vector(obj.length());
		for (int i = 0; i < length; i++)
		{
			retVal.add(String.valueOf(obj.charAt(i)));
		}
		Collections.sort(retVal);
		return retVal;
	}

	public static Object sorted(Collection obj)
	{
		Vector retVal = new Vector(obj);
		Collections.sort(retVal);
		return retVal;
	}

	public static Object sorted(Map obj)
	{
		Vector retVal = new Vector(obj.keySet());
		Collections.sort(retVal);
		return retVal;
	}

	public static Object sorted(Object obj)
	{
		if (obj instanceof String)
			return sorted((String)obj);
		else if (obj instanceof Collection)
			return sorted((Collection)obj);
		else if (obj instanceof Map)
			return sorted((Map)obj);
		throw new RuntimeException("Can't sort instance of " + obj.getClass() + "!");
	}

	public static Object range(Integer obj)
	{
		return new Range(0, obj.intValue(), 1);
	}

	public static Object range(Object obj)
	{
		if (obj instanceof Integer)
			return range((Integer)obj);
		throw new UnsupportedOperationException("Can't build a range for parameter: instance of " + obj.getClass() + "!");
	}

	public static Object range(Integer obj1, Integer obj2)
	{
		return new Range(obj1.intValue(), obj2.intValue(), 1);
	}

	public static Object range(Object obj1, Object obj2)
	{
		if (obj1 instanceof Integer && obj2 instanceof Integer)
			return range((Integer)obj1, (Integer)obj2);
		throw new UnsupportedOperationException("Can't build a range for parameters: instances of " + obj1.getClass() + " and " + obj2.getClass() + "!");
	}

	public static Object range(Integer obj1, Integer obj2, Integer obj3)
	{
		return new Range(obj1.intValue(), obj2.intValue(), obj3.intValue());
	}

	public static Object range(Object obj1, Object obj2, Object obj3)
	{
		if (obj1 instanceof Integer && obj2 instanceof Integer && obj3 instanceof Integer)
			return range((Integer)obj1, (Integer)obj2, (Integer)obj3);
		throw new UnsupportedOperationException("Can't build a range for parameters: instances of " + obj1.getClass() + " and " + obj2.getClass() + " and " + obj3.getClass() + "!");
	}

	public static Object split(String obj1, String obj2)
	{
		LinkedList retVal = new LinkedList();
		int length = obj1.length();
		int delimLength = obj2.length();
		int pos1 = 0;
		int pos2;
		while (pos1 < length)
		{
			while ((pos1 < length) && obj1.startsWith(obj2, pos1))
			{
				if (0 == pos1)
				{
					retVal.add("");
				}
				pos1 += delimLength;
				retVal.add("");
			}
			if (pos1 < length)
			{
				pos2 = pos1 + 1;
				if (!retVal.isEmpty())
				{
					retVal.removeLast();
				}
				while ((pos2 < length) && !obj1.startsWith(obj2, pos2))
				{
					pos2++;
				}
				retVal.add(obj1.substring(pos1, pos2));
				pos1 = pos2;
			}
		}
		return retVal;
	}

	public static Object split(Object obj)
	{
		if (obj instanceof String)
			return StringUtils.split((String)obj);
		throw new UnsupportedOperationException("Can't split instance of " + obj.getClass() + "!");
	}

	public static Object split(Object obj1, Object obj2)
	{
		if ((obj1 instanceof String) && (obj2 instanceof String))
			return split((String)obj1, (String)obj2);
		throw new UnsupportedOperationException("Can't split instance of " + obj1.getClass() + " with delimiter instance of " + obj2.getClass() + "!");
	}

	public static Object strip(Object obj)
	{
		if (obj instanceof String)
			return StringUtils.strip((String)obj);
		throw new UnsupportedOperationException("Can't strip instance of " + obj.getClass() + "!");
	}

	public static Object lstrip(Object obj)
	{
		if (obj instanceof String)
			return StringUtils.stripStart((String)obj, null);
		throw new UnsupportedOperationException("Can't lstrip instance of " + obj.getClass() + "!");
	}

	public static Object rstrip(Object obj)
	{
		if (obj instanceof String)
			return StringUtils.stripEnd((String)obj, null);
		throw new UnsupportedOperationException("Can't rstrip instance of " + obj.getClass() + "!");
	}

	public static Object upper(String obj)
	{
		return obj.toUpperCase();
	}

	public static Object upper(Object obj)
	{
		if (obj instanceof String)
			return upper((String)obj);
		throw new UnsupportedOperationException("Can't convert an instance of " + obj.getClass() + " to upper case!");
	}

	public static Object lower(String obj)
	{
		return obj.toLowerCase();
	}

	public static Object lower(Object obj)
	{
		if (obj instanceof String)
			return lower((String)obj);
		throw new UnsupportedOperationException("Can't convert an instance of " + obj.getClass() + " to lower case!");
	}

	public static SimpleDateFormat isoDateFormatter = new SimpleDateFormat("yyyy.MM.dd'T'HH:mm:ss.SSS'000'");

	public static String isoformat(Date obj)
	{
		return isoDateFormatter.format(obj);
	}

	public static String isoformat(Object obj)
	{
		if (obj instanceof Date)
			return isoformat((Date)obj);
		throw new UnsupportedOperationException("Can't call isoformat on instance of " + obj.getClass() + "!");
	}

	public static Object format(Date obj, String formatString, Locale locale)
	{
		StringBuffer javaFormatString = new StringBuffer();
		int formatStringLength = formatString.length();
		boolean escapeCharacterFound = false;
		boolean inLiteral = false;
		char formatChar;
		String javaFormatSequence;
		for (int i = 0; i < formatStringLength; i++)
		{
			formatChar = formatString.charAt(i);
			if (escapeCharacterFound)
			{
				switch (formatChar)
				{
					case 'a':
						javaFormatSequence = "EE";
						break;
					case 'A':
						javaFormatSequence = "EEEE";
						break;
					case 'b':
						javaFormatSequence = "MMM";
						break;
					case 'B':
						javaFormatSequence = "MMMM";
						break;
					case 'c':
						throw new UnsupportedOperationException("Unimplemented escape sequence %c");
					case 'd':
						javaFormatSequence = "dd";
						break;
					case 'f':
						javaFormatSequence = "SSS'000";
						break;
					case 'H':
						javaFormatSequence = "HH";
						break;
					case 'I':
						javaFormatSequence = "hh";
						break;
					case 'j':
						javaFormatSequence = "DDD";
						break;
					case 'm':
						javaFormatSequence = "MM";
						break;
					case 'M':
						javaFormatSequence = "mm";
						break;
					case 'p':
						javaFormatSequence = "aa";
						break;
					case 'S':
						javaFormatSequence = "ss";
						break;
					case 'U':
						javaFormatSequence = "ww";
						break;
					case 'w':
						throw new UnsupportedOperationException("Unimplemented escape sequence %w");
					case 'W':
						javaFormatSequence = "ww";
						break;
					case 'x':
						throw new UnsupportedOperationException("Unimplemented escape sequence %x");
					case 'X':
						throw new UnsupportedOperationException("Unimplemented escape sequence %X");
					case 'y':
						javaFormatSequence = "yy";
						break;
					case 'Y':
						javaFormatSequence = "yyyy";
						break;
					default:
						javaFormatSequence = null;
						break;
				}
				if (inLiteral != (null == javaFormatSequence))
				{
					javaFormatString.append('\'');
					inLiteral = !inLiteral;
				}
				if (null != javaFormatSequence)
				{
					javaFormatString.append(javaFormatSequence);
					if ('f' == formatChar)
					{
						inLiteral = true;
					}
				}
				else
				{
					javaFormatString.append(formatChar);
				}
				escapeCharacterFound = false;
			}
			else
			{
				escapeCharacterFound = ('%' == formatChar);
				if (!escapeCharacterFound)
				{
					if (inLiteral = !inLiteral)
					{
						javaFormatString.append('\'');
					}
					javaFormatString.append(formatChar);
					if ('\'' == formatChar)
					{
						javaFormatString.append(formatChar);
					}
				}
			}
		}
		if (inLiteral)
		{
			javaFormatString.append('\'');
		}
		return new SimpleDateFormat(javaFormatString.toString(), locale).format(obj);
	}

	public static Object format(Object obj, Object formatString, Locale locale)
	{
		if (formatString instanceof String)
		{
			if (obj instanceof Date)
			{
				return format((Date)obj, (String)formatString, locale);
			}
		}
		throw new UnsupportedOperationException("Can't call format on instance of " + obj.getClass() + " with format string instance of " + formatString.getClass() + "!");
	}

	public static Object replace(Object obj, Object arg1, Object arg2)
	{
		if (obj instanceof String && arg1 instanceof String && arg2 instanceof String)
			return StringUtils.replace((String)obj, (String)arg1, (String)arg2);
		throw new UnsupportedOperationException("Can't call replace on instance of " + obj.getClass() + "!");
	}

	public static Object items(Map obj)
	{
		return new MapItemIterator(obj);
	}

	public static Object items(Object obj)
	{
		if (obj instanceof Map)
			return items((Map)obj);
		throw new UnsupportedOperationException("Instance of " + obj.getClass() + " can't be iterated as a map!");
	}

	public static void main(String[] args)
	{
		//System.out.println(split("\t\tgurk\t\t\t\t\t\thurz\t\tschwumpl\t\t\t\t", "\t\t"));
		System.out.println(split("gurk\t\t\t\t\t\thurz\t\tschwumpl", "\t\t"));
		//System.out.println(split("\t\tgurk\t\t\t\t\t\thurz\t\tschwumpl\t\t\t\t"));
		//System.out.println(split("gurk\t\t\t\t\t\thurz\t\tschwumpl"));
		//System.out.println(split("  gurk      hurz  schwumpl    "));
	}
}
