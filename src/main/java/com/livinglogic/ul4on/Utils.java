/*
** Copyright 2012-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4on;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.livinglogic.ul4.FunctionRepr;
import com.livinglogic.ul4.SyntaxException;
import com.livinglogic.ul4.UL4Type;


/**
Utility class for reading and writing the UL4ON object serialization format.

The UL4ON object serialization format is a simple (text-based) extensible
object serialization format the supports all objects supported by UL4, i.e.
it supports the same type of objects as JSON does (plus colors, dates and
templates).

Furthermore it is extensible by implementing the {@link UL4ONSerializable}
interface and registering your class via {@link #register}.

@author W. Dörwald, A. Gaßner
**/
public class Utils
{
	/**
	Date format for serializing/deserializing {@code Date} objects.
	**/
	public final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	/**
	Registry where all {@link ObjectFactory} objects registered via
	{@link #register} are stored.
	**/
	public static Map<String, ObjectFactory> registry = new HashMap<String, ObjectFactory>();

	/**
	Register a class for the UL4ON serialization machinery.

	@param name the name of the class as returned by its
	            {@link UL4ONSerializable#getUL4ONName}.
	@param factory An {@link ObjectFactory} object that will be used to create
	               an "empty" instance of the class.
	**/
	public static void register(String name, ObjectFactory factory)
	{
		registry.put(name, factory);
	}

	/**
	Register a class for the UL4ON serialization machinery via an
	{@link UL4Type} object. The UL4ON type name will be the name of the type
	object, and the type object itself will be the object factory.

	@param type a {@link UL4Type} for the target class.
	**/
	public static void register(UL4Type type)
	{
		registry.put(type.getUL4ONName(), type);
	}

	/**
	Return the serialized UL4ON output of the object {@code data}.
	@param data the object to be dumped.
	@return the serialized object
	**/
	public static String dumps(Object data)
	{
		return dumps(data, null);
	}

	/**
	Return the serialized UL4ON output of the object {@code data}.
	@param data the object to be dumped.
	@param indent how to indent the output for pretty printing ({@code null} disables pretty printing).
	@return the serialized object
	**/
	public static String dumps(Object data, String indent)
	{
		Encoder encoder = new Encoder(indent);
		return encoder.dumps(data);
	}

	/**
	Load an object by reading in the UL4ON object serialization format from {@code reader}.
	@param reader The Reader from which to read the object
	@param registry custom type registry
	@return the deserialized object
	@throws IOException if reading from the underlying stream fails
	**/
	public static Object load(Reader reader, Map<String, ObjectFactory> registry) throws IOException
	{
		Decoder decoder = new Decoder(registry);
		return decoder.load(reader);
	}

	/**
	Load an object by reading in the UL4ON object serialization format from the string {@code s}.
	@param s The object in serialized form
	@param registry custom type registry
	@return the deserialized object
	**/
	public static Object loads(String s, Map<String, ObjectFactory> registry)
	{
		Decoder decoder = new Decoder(registry);
		return decoder.loads(s);
	}

	/**
	Load an object by reading in the UL4ON object serialization format from the CLOB {@code clob}.
	@param clob The CLOB that contains the object in serialized form
	@param registry custom type registry
	@return the deserialized object
	@throws IOException if reading from any underlying stream fails
	@throws SQLException if some database interaction fails
	**/
	public static Object loads(Clob clob, Map<String, ObjectFactory> registry) throws IOException, SQLException
	{
		return load(clob.getCharacterStream(), registry);
	}

	private static String readChars(Reader reader, int count) throws IOException
	{
		StringBuilder result = new StringBuilder();

		while (count-- != 0)
		{
			int c = reader.read();
			if (c == -1)
				throw new RuntimeException("broken stream: unexpected eof");
			result.append((char)c);
		}
		return result.toString();
	}

	public static String parseUL4StringFromReader(Reader reader) throws IOException
	{
		String eofMessage = "broken stream: unexpected eof";

		StringBuilder result = new StringBuilder();

		int delimiter = reader.read();
		if (delimiter == -1)
			throw new RuntimeException(eofMessage);

		for (;;)
		{
			int c = reader.read();

			if (c == -1)
				throw new RuntimeException(eofMessage);
			if (c == delimiter)
				return result.toString();
			else if (c == '\\')
			{
				int c2 = reader.read();

				switch (c2)
				{
					case -1:
						throw new RuntimeException(eofMessage);
					case '\\':
						result.append('\\');
						break;
					case 'n':
						result.append('\n');
						break;
					case 'r':
						result.append('\r');
						break;
					case 't':
						result.append('\t');
						break;
					case 'f':
						result.append('\f');
						break;
					case 'b':
						result.append('\b');
						break;
					case 'a':
						result.append('\u0007');
						break;
					case '"':
						result.append('"');
						break;
					case '\'':
						result.append('\'');
						break;
					case 'x':
						int cx;
						String xChars = readChars(reader, 2);
						try
						{
							cx = Integer.parseInt(xChars, 16);
						}
						catch (NumberFormatException ex)
						{
							throw new SyntaxException("illegal \\x escape: " + FunctionRepr.call(xChars), ex);
						}
						result.append((char)cx);
						break;
					case 'u':
						int cu;
						String uChars = readChars(reader, 4);
						try
						{
							cu = Integer.parseInt(uChars, 16);
						}
						catch (NumberFormatException ex)
						{
							throw new SyntaxException("illegal \\u escape: " + FunctionRepr.call(uChars), ex);
						}
						result.append((char)cu);
						break;
					case 'U':
						throw new RuntimeException("\\U escapes are not supported");
					default:
						result.append(c);
						result.append(c2);
				}
			}
			else
			{
				result.append((char)c);
			}
		}
	}
}
