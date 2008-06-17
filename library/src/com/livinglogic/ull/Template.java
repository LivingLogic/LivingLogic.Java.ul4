package com.livinglogic.ull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.io.StringWriter;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Template
{
	class IteratorStackEntry
	{
		public int iteratorRegSpec;
		public int pc;
		public Iterator iterator;
	
		public IteratorStackEntry(int iteratorRegSpec, int pc, Iterator iterator)
		{
			this.iteratorRegSpec = iteratorRegSpec;
			this.pc = pc;
			this.iterator = iterator;
		}
	}

	public static final String HEADER = "ull";

	public static final String VERSION = "1";
	
	public String source;

	public List opcodes;

	private boolean annotated = false;

	public Template()
	{
		this.source = null;
		this.opcodes = new LinkedList();
	}

	public void opcode(int name, Location location)
	{
		opcodes.add(new Opcode(name, -1, -1, -1, -1, -1, null, location));
	}

	public void opcode(int name, String arg, Location location)
	{
		opcodes.add(new Opcode(name, -1, -1, -1, -1, -1, arg, location));
	}

	public void opcode(int name, int r1, Location location)
	{
		opcodes.add(new Opcode(name, r1, -1, -1, -1, -1, null, location));
	}

	public void opcode(int name, int r1, String arg, Location location)
	{
		opcodes.add(new Opcode(name, r1, -1, -1, -1, -1, arg, location));
	}

	public void opcode(int name, int r1, int r2, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, -1, -1, -1, null, location));
	}

	public void opcode(int name, int r1, int r2, String arg, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, -1, -1, -1, arg, location));
	}

	public void opcode(int name, int r1, int r2, int r3, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, -1, -1, null, location));
	}

	public void opcode(int name, int r1, int r2, int r3, String arg, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, -1, -1, arg, location));
	}

	public void opcode(int name, int r1, int r2, int r3, int r4, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, r4, -1, null, location));
	}

	public void opcode(int name, int r1, int r2, int r3, int r4, String arg, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, r4, -1, arg, location));
	}

	public void opcode(int name, int r1, int r2, int r3, int r4, int r5, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, r4, r5, null, location));
	}

	public void opcode(int name, int r1, int r2, int r3, int r4, int r5, String arg, Location location)
	{
		opcodes.add(new Opcode(name, r1, r2, r3, r4, r5, arg, location));
	}

	protected static int readintInternal(Reader reader, char terminator1, char terminator0) throws IOException
	{
		int retVal = 0;
		boolean terminatorFound = false;
		int readInt = reader.read();
		char charValue;
		int intValue;
		while ((-1 < readInt) && !terminatorFound)
		{
			charValue = (char)readInt;
			intValue = Character.digit(charValue, 10);
			if (-1 < intValue)
			{
				retVal = retVal * 10 + intValue;
			}
			else if (charValue == terminator1)
			{
				terminatorFound = true;
			}
			else if (charValue == terminator0)
			{
				terminatorFound = true;
				retVal = -1;
			}
			else
			{
				throw new RuntimeException("Invalid terminator, expected " + terminator1 + " or " + terminator0 + ", got " + charValue);
			}
			if (!terminatorFound)
			{
				readInt = reader.read();
			}
		}
		return retVal;
	}

	protected static int readint(Reader reader, char terminator) throws IOException
	{
		int retVal = readintInternal(reader, terminator, terminator);
		if (0 > retVal)
		{
			throw new RuntimeException("Invalid integer read!");
		}
		return retVal;
	}

	protected static String readstr(Reader reader, char terminator1, char terminator0) throws IOException
	{
		String retVal = null;
		int stringLength = readintInternal(reader, terminator1, terminator0);
		if (-1 < stringLength)
		{
			char[] retValChars = new char[stringLength];
			int readCharSize = reader.read(retValChars);
			if (readCharSize != stringLength)
			{
				throw new RuntimeException("Short read!");
			}
			retVal = new String(retValChars);
		}
		return retVal;
	}

	protected static int readspec(Reader reader) throws IOException
	{
		int retVal;
		int readInt = reader.read();
		char charValue;
		if (-1 < readInt)
		{
			charValue = (char)readInt;
			if ('-' == charValue)
			{
				retVal = -1;
			}
			else
			{
				retVal = Character.digit(charValue, 10);
				if (0 > retVal)
				{
					throw new RuntimeException("Invalid register spec " + charValue);
				}
			}
		}
		else
		{
			throw new RuntimeException("Short read!");
		}
		return retVal;
	}

	protected static void readcr(Reader reader) throws IOException
	{
		int readInt = reader.read();
		if (-1 < readInt)
		{
			char charValue = (char)readInt;
			if ('\n' != charValue)
			{
				throw new RuntimeException("Invalid linefeed " + charValue);
			}
		}
		else
		{
			throw new RuntimeException("Short read!");
		}
	}

	public static Template load(Reader reader) throws IOException
	{
		Template retVal = new Template();
		BufferedReader bufferedReader = new BufferedReader(reader);
		String header = bufferedReader.readLine();
		if (!HEADER.equals(header))
		{
			throw new RuntimeException("Invalid header, expected " + HEADER + ", got " + header);
		}
		String version = bufferedReader.readLine();
		if (!VERSION.equals(version))
		{
			throw new RuntimeException("Invalid version, expected " + VERSION + ", got " + version);
		}
		retVal.source = readstr(bufferedReader, '\'', '"');
		readcr(bufferedReader);
		int count = readint(bufferedReader, '#');
		readcr(bufferedReader);
		Location location = null;
		for (int i = 0; i < count; i++)
		{
			int r1 = readspec(bufferedReader);
			int r2 = readspec(bufferedReader);
			int r3 = readspec(bufferedReader);
			int r4 = readspec(bufferedReader);
			int r5 = readspec(bufferedReader);
			String code = readstr(bufferedReader, ':', '.');
			String arg = readstr(bufferedReader, ';', ',');
			int readInt = bufferedReader.read();
			if (-1 < readInt)
			{
				char charValue = (char)readInt;
				if ('^' == charValue)
				{
					if (null == location)
					{
						throw new RuntimeException("No previous location!");
					}
				}
				else if ('*' == charValue)
				{
					location = new Location(retVal.source, readstr(bufferedReader, '=', '-'),
						readint(bufferedReader, '<'), readint(bufferedReader, '>'),
						readint(bufferedReader, '['), readint(bufferedReader, ']'));
				}
				else
				{
					throw new RuntimeException("Invalid location spec " + charValue);
				}
			}
			else
			{
				throw new RuntimeException("Short read!");
			}
			retVal.opcodes.add(new Opcode(code, r1, r2, r3, r4, r5, arg, location));
			readcr(bufferedReader);
		}
		return retVal;
	}

	public static Template loads(String bytecode)
	{
		StringReader reader = new StringReader(bytecode);
		try
		{
			return load(reader);
		}
		catch (IOException ex) // can not happen, when reading from a StringReaders
		{
			return null;
		}
	}

	protected static void writeint(Writer writer, int value, char terminator) throws IOException
	{
		writer.write(String.valueOf(value));
		writer.write(terminator);
	}

	protected static void writestr(Writer writer, String value, char terminator1, char terminator0) throws IOException
	{
		if (value == null)
		{
			writer.write(terminator0);
		}
		else
		{
			writer.write(String.valueOf(value.length()));
			writer.write(terminator1);
			writer.write(value);
		}
	}

	protected static void writespec(Writer writer, int spec) throws IOException
	{
		if (spec == -1)
			writer.write("-");
		else
			writer.write(String.valueOf(spec));
	}

	public void dump(Writer writer) throws IOException
	{
		writer.write(HEADER);
		writer.write("\n");
		writer.write(VERSION);
		writer.write("\n");
		writestr(writer, source, '\'', '"');
		writer.write("\n");
		writeint(writer, opcodes.size(), '#');
		writer.write("\n");
		Location lastLocation = null;
		for (int i = 0; i < opcodes.size(); ++i)
		{
			Opcode opcode = (Opcode)opcodes.get(i);
			writespec(writer, opcode.r1);
			writespec(writer, opcode.r2);
			writespec(writer, opcode.r3);
			writespec(writer, opcode.r4);
			writespec(writer, opcode.r5);
			writestr(writer, Opcode.code2name(opcode.name), ':', '.');
			writestr(writer, opcode.arg, ';', ',');
			if (opcode.location != lastLocation)
			{
				writer.write("*");
				writestr(writer, opcode.location.type, '=', '-');
				writeint(writer, opcode.location.starttag, '<');
				writeint(writer, opcode.location.endtag, '>');
				writeint(writer, opcode.location.startcode, '[');
				writeint(writer, opcode.location.endcode, ']');
				lastLocation = opcode.location;
			}
			else
			{
				writer.write("^");
			}
			writer.write("\n");
		}
	}

	public String dumps()
	{
		StringWriter writer = new StringWriter();
		try
		{
			dump(writer);
		}
		catch (IOException ex) // can not happen, when dumping the a StringWriter
		{
		}
		return writer.toString();
	}

	protected void annotate()
	{
		if (!annotated)
		{
			LinkedList stack = new LinkedList();
			for (int i = 0; i < opcodes.size(); ++i)
			{
				Opcode opcode = (Opcode)opcodes.get(i);
				switch (opcode.name)
				{
					case Opcode.OC_IF:
						stack.add(new Integer(i));
						break;
					case Opcode.OC_ELSE:
						((Opcode)opcodes.get(((Integer)stack.getLast()).intValue())).jump = i;
						stack.set(stack.size()-1, new Integer(i));
						break;
					case Opcode.OC_ENDIF:
						((Opcode)opcodes.get(((Integer)stack.getLast()).intValue())).jump = i;
						stack.removeLast();
						break;
					case Opcode.OC_FOR:
						stack.add(new Integer(i));
						break;
					case Opcode.OC_ENDFOR:
						((Opcode)opcodes.get(((Integer)stack.getLast()).intValue())).jump = i;
						stack.removeLast();
						break;
				}
			}
			annotated = true;
		}
	}

	public Iterator render(Object data)
	{
		return new Renderer(data, null);
	}

	public Iterator render(Object data, Map templates)
	{
		return new Renderer(data, templates);
	}

	public String renders(Object data)
	{
		return renders(data, null);
	}

	public String renders(Object data, Map templates)
	{
		StringBuffer output = new StringBuffer();

		for (Iterator iterator = render(data, templates); iterator.hasNext();)
		{
			output.append((String)iterator.next());
		}
		return output.toString();
	}

	class Renderer implements Iterator
	{
		private int pc = 0;
		private Object[] reg = new Object[10];
		private HashMap variables = new HashMap();
		private Map templates;
		private LinkedList iterators = new LinkedList();
		private Iterator subTemplateIterator = null;

		private String nextChunk = null;

		public Renderer(Object data, Map templates)
		{
			annotate();
			variables.put("data", data);
			if (templates == null)
				templates = new HashMap();
			this.templates = templates;
			getNextChunk();
		}

		public void remove()
		{
			throw new UnsupportedOperationException("remove() not supported for " + getClass() + "!");
		}

		public boolean hasNext()
		{
			return nextChunk != null;
		}

		public Object next()
		{
			String result = nextChunk;
			getNextChunk();
			return result;
		}

		public void getNextChunk()
		{
			if (subTemplateIterator != null)
			{
				if (subTemplateIterator.hasNext())
				{
					nextChunk = (String)subTemplateIterator.next();
					return;
				}
				else
				{
					subTemplateIterator = null;
				}
			}
			while (pc < opcodes.size())
			{
				Opcode code = (Opcode)opcodes.get(pc);

				try
				{
					switch (code.name)
					{
						case Opcode.OC_TEXT:
							nextChunk = code.location.getCode();
							++pc;
							return;
						case Opcode.OC_PRINT:
							nextChunk = Utils.toString(reg[code.r1]);
							++pc;
							return;
						case Opcode.OC_LOADNONE:
							reg[code.r1] = null;
							break;
						case Opcode.OC_LOADFALSE:
							reg[code.r1] = Boolean.FALSE;
							break;
						case Opcode.OC_LOADTRUE:
							reg[code.r1] = Boolean.TRUE;
							break;
						case Opcode.OC_LOADSTR:
							reg[code.r1] = code.arg;
							break;
						case Opcode.OC_LOADINT:
							reg[code.r1] = new Integer(Integer.parseInt(code.arg));
							break;
						case Opcode.OC_LOADFLOAT:
							reg[code.r1] = new Double(Double.parseDouble(code.arg));
							break;
						case Opcode.OC_LOADVAR:
							reg[code.r1] = variables.get(code.arg);
							break;
						case Opcode.OC_STOREVAR:
							variables.put(code.arg, reg[code.r1]);
							break;
						case Opcode.OC_ADDVAR:
							variables.put(code.arg, Utils.add(variables.get(code.arg), reg[code.r1]));
							break;
						case Opcode.OC_SUBVAR:
							variables.put(code.arg, Utils.sub(variables.get(code.arg), reg[code.r1]));
							break;
						case Opcode.OC_MULVAR:
							variables.put(code.arg, Utils.mul(variables.get(code.arg), reg[code.r1]));
							break;
						case Opcode.OC_TRUEDIVVAR:
							variables.put(code.arg, Utils.truediv(variables.get(code.arg), reg[code.r1]));
							break;
						case Opcode.OC_FLOORDIVVAR:
							variables.put(code.arg, Utils.floordiv(variables.get(code.arg), reg[code.r1]));
							break;
						case Opcode.OC_MODVAR:
							variables.put(code.arg, Utils.mod(variables.get(code.arg), reg[code.r1]));
							break;
						case Opcode.OC_DELVAR:
							variables.remove(code.arg);
							break;
						case Opcode.OC_FOR:
							Iterator iterator = Utils.iterator(reg[code.r2]);
							if (iterator.hasNext())
							{
								reg[code.r1] = iterator.next();
								iterators.add(new IteratorStackEntry(code.r1, pc, iterator));
							}
							else
							{
								pc = code.jump+1;
								continue;
							}
							break;
						case Opcode.OC_ENDFOR:
							IteratorStackEntry entry = (IteratorStackEntry)iterators.getLast();
							if (entry.iterator.hasNext())
							{
								reg[entry.iteratorRegSpec] = entry.iterator.next();
								pc = entry.pc;
							}
							else
							{
								iterators.removeLast();
							}
							break;
						case Opcode.OC_IF:
							if (!Utils.getBool(reg[code.r1]))
							{
								pc = code.jump+1;
								continue;
							}
							break;
						case Opcode.OC_ELSE:
							pc = code.jump+1;
							continue;
						case Opcode.OC_ENDIF:
							//Skip to next opcode
							break;
						case Opcode.OC_GETATTR:
							reg[code.r1] = ((Map)reg[code.r2]).get(code.arg);
							break;
						case Opcode.OC_GETITEM:
							reg[code.r1] = Utils.getItem(reg[code.r2], reg[code.r3]);
							break;
						case Opcode.OC_GETSLICE12:
							reg[code.r1] = Utils.getSlice(reg[code.r2], reg[code.r3], reg[code.r4]);
							break;
						case Opcode.OC_GETSLICE1:
							reg[code.r1] = Utils.getSlice(reg[code.r2], reg[code.r3], null);
							break;
						case Opcode.OC_GETSLICE2:
							reg[code.r1] = Utils.getSlice(reg[code.r2], null, reg[code.r4]);
							break;
						case Opcode.OC_GETSLICE:
							reg[code.r1] = Utils.getSlice(reg[code.r2], null, null);
							break;
						case Opcode.OC_NOT:
							reg[code.r1] = Utils.getBool(reg[code.r2]) ? Boolean.FALSE : Boolean.TRUE;
							break;
						case Opcode.OC_NEG:
							reg[code.r1] = Utils.neg(reg[code.r2]);
							break;
						case Opcode.OC_EQUALS:
							reg[code.r1] = Utils.equals(reg[code.r2], reg[code.r3]) ? Boolean.TRUE : Boolean.FALSE;
							break;
						case Opcode.OC_NOTEQUALS:
							reg[code.r1] = Utils.equals(reg[code.r2], reg[code.r3]) ? Boolean.FALSE : Boolean.TRUE;
							break;
						case Opcode.OC_CONTAINS:
							reg[code.r1] = Utils.contains(reg[code.r2], reg[code.r3]) ? Boolean.TRUE : Boolean.FALSE;
							break;
						case Opcode.OC_NOTCONTAINS:
							reg[code.r1] = Utils.contains(reg[code.r2], reg[code.r3]) ? Boolean.FALSE : Boolean.TRUE;
							break;
						case Opcode.OC_OR:
							reg[code.r1] = (Utils.getBool(reg[code.r2]) || Utils.getBool(reg[code.r3])) ? Boolean.TRUE : Boolean.FALSE;
							break;
						case Opcode.OC_AND:
							reg[code.r1] = (Utils.getBool(reg[code.r2]) && Utils.getBool(reg[code.r3])) ? Boolean.TRUE : Boolean.FALSE;
							break;
						case Opcode.OC_ADD:
							reg[code.r1] = Utils.add(reg[code.r2], reg[code.r3]);
							break;
						case Opcode.OC_SUB:
							reg[code.r1] = Utils.sub(reg[code.r2], reg[code.r3]);
							break;
						case Opcode.OC_MUL:
							reg[code.r1] = Utils.mul(reg[code.r2], reg[code.r3]);
							break;
						case Opcode.OC_TRUEDIV:
							reg[code.r1] = Utils.truediv(reg[code.r2], reg[code.r3]);
							break;
						case Opcode.OC_FLOORDIV:
							reg[code.r1] = Utils.floordiv(reg[code.r2], reg[code.r3]);
							break;
						case Opcode.OC_MOD:
							reg[code.r1] = Utils.mod(reg[code.r2], reg[code.r3]);
							break;
						case Opcode.OC_CALLFUNC0:
							throw new UnknownFunctionException(code.arg);
						case Opcode.OC_CALLFUNC1:
							switch (code.argcode)
							{
								case Opcode.CF1_XMLESCAPE:
									reg[code.r1] = Utils.xmlescape(reg[code.r2]);
									break;
								case Opcode.CF1_STR:
									reg[code.r1] = Utils.toString(reg[code.r2]);
									break;
								case Opcode.CF1_INT:
									reg[code.r1] = Utils.toInteger(reg[code.r2]);
									break;
								case Opcode.CF1_LEN:
									reg[code.r1] = Utils.length(reg[code.r2]);
									break;
								case Opcode.CF1_ENUMERATE:
									reg[code.r1] = Utils.enumerate(reg[code.r2]);
									break;
								case Opcode.CF1_ISNONE:
									reg[code.r1] = (null == reg[code.r2]) ? Boolean.TRUE : Boolean.FALSE;
									break;
								case Opcode.CF1_ISSTR:
									reg[code.r1] = ((null != reg[code.r2]) && (reg[code.r2] instanceof String)) ? Boolean.TRUE : Boolean.FALSE;
									break;
								case Opcode.CF1_ISINT:
									reg[code.r1] = ((null != reg[code.r2]) && (reg[code.r2] instanceof Integer)) ? Boolean.TRUE : Boolean.FALSE;
									break;
								case Opcode.CF1_ISFLOAT:
									reg[code.r1] = ((null != reg[code.r2]) && (reg[code.r2] instanceof Double)) ? Boolean.TRUE : Boolean.FALSE;
									break;
								case Opcode.CF1_ISBOOL:
									reg[code.r1] = ((null != reg[code.r2]) && (reg[code.r2] instanceof Boolean)) ? Boolean.TRUE : Boolean.FALSE;
									break;
								case Opcode.CF1_ISLIST:
									reg[code.r1] = ((null != reg[code.r2]) && (reg[code.r2] instanceof List)) ? Boolean.TRUE : Boolean.FALSE;
									break;
								case Opcode.CF1_ISDICT:
									reg[code.r1] = ((null != reg[code.r2]) && (reg[code.r2] instanceof Map)) ? Boolean.TRUE : Boolean.FALSE;
									break;
								case Opcode.CF1_CHR:
									reg[code.r1] = Utils.chr(reg[code.r2]);
									break;
								case Opcode.CF1_ORD:
									reg[code.r1] = Utils.ord(reg[code.r2]);
									break;
								case Opcode.CF1_HEX:
									reg[code.r1] = Utils.hex(reg[code.r2]);
									break;
								case Opcode.CF1_OCT:
									reg[code.r1] = Utils.oct(reg[code.r2]);
									break;
								case Opcode.CF1_BIN:
									reg[code.r1] = Utils.bin(reg[code.r2]);
									break;
								case Opcode.CF1_SORTED:
									reg[code.r1] = Utils.sorted(reg[code.r2]);
									break;
								case Opcode.CF1_RANGE:
									reg[code.r1] = Utils.range(reg[code.r2]);
									break;
							}
							break;
						case Opcode.OC_CALLFUNC2:
							switch (code.argcode)
							{
								case Opcode.CF2_RANGE:
									reg[code.r1] = Utils.range(reg[code.r2], reg[code.r3]);
									break;
							}
							break;
						case Opcode.OC_CALLFUNC3:
							switch (code.argcode)
							{
								case Opcode.CF3_RANGE:
									reg[code.r1] = Utils.range(reg[code.r2], reg[code.r3], reg[code.r4]);
									break;
							}
							break;
						case Opcode.OC_CALLMETH0:
							switch (code.argcode)
							{
								case Opcode.CM0_SPLIT:
									reg[code.r1] = Utils.split(reg[code.r2]);
									break;
								case Opcode.CM0_STRIP:
									reg[code.r1] = Utils.strip(reg[code.r2]);
									break;
								case Opcode.CM0_LSTRIP:
									reg[code.r1] = Utils.lstrip(reg[code.r2]);
									break;
								case Opcode.CM0_RSTRIP:
									reg[code.r1] = Utils.rstrip(reg[code.r2]);
									break;
								case Opcode.CM0_UPPER:
									reg[code.r1] = Utils.upper(reg[code.r2]);
									break;
								case Opcode.CM0_LOWER:
									reg[code.r1] = Utils.lower(reg[code.r2]);
									break;
								case Opcode.CM0_ITEMS:
									reg[code.r1] = Utils.items(reg[code.r2]);
									break;
							}
							break;
						case Opcode.OC_CALLMETH1:
							switch (code.argcode)
							{
								case Opcode.CM1_SPLIT:
									reg[code.r1] = Utils.split(reg[code.r2], reg[code.r3]);
									break;
/*								case Opcode.CM1_RSPLIT:
									reg[code.r1] = Utils.rsplit(reg[code.r2], reg[code.r3]);
									break;
								case Opcode.CM1_STRIP:
									reg[code.r1] = Utils.strip(reg[code.r2], reg[code.r3]);
									break;
								case Opcode.CM1_LSTRIP:
									reg[code.r1] = Utils.lstrip(reg[code.r2], reg[code.r3]);
									break;
								case Opcode.CM1_RSTRIP:
									reg[code.r1] = Utils.rstrip(reg[code.r2], reg[code.r3]);
									break;
								case Opcode.CM1_STARTSWITH:
									reg[code.r1] = Utils.startswith(reg[code.r2], reg[code.r3]);
									break;
								case Opcode.CM1_ENDSWITH:
									reg[code.r1] = Utils.endswith(reg[code.r2], reg[code.r3]);
									break;
								case Opcode.CM1_FIND:
									reg[code.r1] = Utils.items(reg[code.r2], reg[code.r3]);
									break;
*/							}
							break;
						case Opcode.OC_CALLMETH2:
								throw new UnknownMethodException(code.arg);
						case Opcode.OC_CALLMETH3:
								throw new UnknownMethodException(code.arg);
						case Opcode.OC_RENDER:
							subTemplateIterator = ((Template)templates.get(code.arg)).render(reg[code.r1], templates);
							if (subTemplateIterator.hasNext())
							{
								nextChunk = (String)subTemplateIterator.next();
								++pc;
								return;
							}
							else
							{
								subTemplateIterator = null;
							}
							break;
						default:
							throw new RuntimeException("Unknown opcode '" + code.name + "'!");
					}
				}
				catch (Exception ex)
				{
					throw new LocationException(ex, code.location);
				}
				++pc;
			}
			// finished => no next chunk available
			nextChunk = null;
		}
	}
}
