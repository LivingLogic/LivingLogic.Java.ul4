/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.LinkedList;
import java.io.IOException;
import java.util.Map;
import java.util.Date;
import java.math.BigDecimal;
import java.math.BigInteger;

public class CallFunc extends AST
{
	protected String name;
	protected LinkedList<AST> args;

	public CallFunc(String name)
	{
		this.name = name;
		this.args = new LinkedList<AST>();
	}

	public void append(AST arg)
	{
		args.add(arg);
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append("callfunc(");
		buffer.append(Utils.repr(name));
		for (AST arg : args)
		{
			buffer.append(", ");
			buffer.append(arg);
		}
		buffer.append(")");
		return buffer.toString();
	}

	public String getType()
	{
		return "callfunc";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		int argcount = args.size();

		if (name.equals("now"))
		{
			if (argcount == 0)
				return new Date();
			throw new ArgumentCountMismatchException("function", "now", argcount, 0);
		}
		else if (name.equals("utcnow"))
		{
			if (argcount == 0)
				return Utils.utcnow();
			throw new ArgumentCountMismatchException("function", "utcnow", argcount, 0);
		}
		else if (name.equals("vars"))
		{
			if (argcount == 0)
				return context.getVariables();
			throw new ArgumentCountMismatchException("function", "vars", argcount, 0);
		}
		else if (name.equals("random"))
		{
			if (argcount == 0)
				return Utils.random();
			throw new ArgumentCountMismatchException("function", "random", argcount, 0);
		}
		else if (name.equals("xmlescape"))
		{
			if (argcount == 1)
				return Utils.xmlescape(args.get(0).evaluate(context));
			throw new ArgumentCountMismatchException("function", "xmlescape", argcount, 1);
		}
		else if (name.equals("csv"))
		{
			if (argcount == 1)
				return Utils.csv(args.get(0).evaluate(context));
			throw new ArgumentCountMismatchException("function", "csv", argcount, 1);
		}
		else if (name.equals("str"))
		{
			if (argcount == 1)
				return Utils.str(args.get(0).evaluate(context));
			throw new ArgumentCountMismatchException("function", "str", argcount, 1);
		}
		else if (name.equals("repr"))
		{
			if (argcount == 1)
				return Utils.repr(args.get(0).evaluate(context));
			throw new ArgumentCountMismatchException("function", "repr", argcount, 1);
		}
		else if (name.equals("int"))
		{
			if (argcount == 1)
				return Utils.toInteger(args.get(0).evaluate(context));
			else if (argcount == 2)
				return Utils.toInteger(args.get(0).evaluate(context), args.get(1).evaluate(context));
			throw new ArgumentCountMismatchException("function", "int", argcount, 1, 2);
		}
		else if (name.equals("float"))
		{
			if (argcount == 1)
				return Utils.toFloat(args.get(0).evaluate(context));
			throw new ArgumentCountMismatchException("function", "float", argcount, 1);
		}
		else if (name.equals("bool"))
		{
			if (argcount == 1)
				return Utils.getBool(args.get(0).evaluate(context)) ? Boolean.TRUE : Boolean.FALSE;
			throw new ArgumentCountMismatchException("function", "bool", argcount, 1);
		}
		else if (name.equals("len"))
		{
			if (argcount == 1)
				return Utils.length(args.get(0).evaluate(context));
			throw new ArgumentCountMismatchException("function", "len", argcount, 1);
		}
		else if (name.equals("enumerate"))
		{
			if (argcount == 1)
				return Utils.enumerate(args.get(0).evaluate(context));
			throw new ArgumentCountMismatchException("function", "enumerate", argcount, 1);
		}
		else if (name.equals("enumfl"))
		{
			if (argcount == 1)
				return Utils.enumfl(args.get(0).evaluate(context));
			throw new ArgumentCountMismatchException("function", "enumfl", argcount, 1);
		}
		else if (name.equals("isfirstlast"))
		{
			if (argcount == 1)
				return Utils.isfirstlast(args.get(0).evaluate(context));
			throw new ArgumentCountMismatchException("function", "isfirstlast", argcount, 1);
		}
		else if (name.equals("isfirst"))
		{
			if (argcount == 1)
				return Utils.isfirst(args.get(0).evaluate(context));
			throw new ArgumentCountMismatchException("function", "isfirst", argcount, 1);
		}
		else if (name.equals("islast"))
		{
			if (argcount == 1)
				return Utils.islast(args.get(0).evaluate(context));
			throw new ArgumentCountMismatchException("function", "islast", argcount, 1);
		}
		else if (name.equals("isnone"))
		{
			if (argcount == 1)
				return null == args.get(0).evaluate(context);
			throw new ArgumentCountMismatchException("function", "isnone", argcount, 1);
		}
		else if (name.equals("isstr"))
		{
			if (argcount == 1)
			{
				Object arg0 = args.get(0).evaluate(context);
				return (null != arg0) && arg0 instanceof String;
			}
			throw new ArgumentCountMismatchException("function", "isstr", argcount, 1);
		}
		else if (name.equals("isint"))
		{
			if (argcount == 1)
			{
				Object arg0 = args.get(0).evaluate(context);
				return (null != arg0) && (arg0 instanceof BigInteger || arg0 instanceof Byte || arg0 instanceof Integer || arg0 instanceof Long || arg0 instanceof Short);
			}
			throw new ArgumentCountMismatchException("function", "isint", argcount, 1);
		}
		else if (name.equals("isfloat"))
		{
			if (argcount == 1)
			{
				Object arg0 = args.get(0).evaluate(context);
				return (null != arg0) && (arg0 instanceof BigDecimal || arg0 instanceof Float || arg0 instanceof Double);
			}
			throw new ArgumentCountMismatchException("function", "isfloat", argcount, 1);
		}
		else if (name.equals("isbool"))
		{
			if (argcount == 1)
			{
				Object arg0 = args.get(0).evaluate(context);
				return (null != arg0) && (arg0 instanceof Boolean);
			}
			throw new ArgumentCountMismatchException("function", "isbool", argcount, 1);
		}
		else if (name.equals("isdate"))
		{
			if (argcount == 1)
			{
				Object arg0 = args.get(0).evaluate(context);
				return (null != arg0) && (arg0 instanceof Date);
			}
			throw new ArgumentCountMismatchException("function", "isdate", argcount, 1);
		}
		else if (name.equals("islist"))
		{
			if (argcount == 1)
			{
				Object arg0 = args.get(0).evaluate(context);
				return (null != arg0) && (arg0 instanceof List) && !(arg0 instanceof Color);
			}
			throw new ArgumentCountMismatchException("function", "islist", argcount, 1);
		}
		else if (name.equals("isdict"))
		{
			if (argcount == 1)
			{
				Object arg0 = args.get(0).evaluate(context);
				return (null != arg0) && (arg0 instanceof Map) && !(arg0 instanceof Template);
			}
			throw new ArgumentCountMismatchException("function", "isdict", argcount, 1);
		}
		else if (name.equals("istemplate"))
		{
			if (argcount == 1)
			{
				Object arg0 = args.get(0).evaluate(context);
				return (null != arg0) && (arg0 instanceof Template);
			}
			throw new ArgumentCountMismatchException("function", "istemplate", argcount, 1);
		}
		else if (name.equals("iscolor"))
		{
			if (argcount == 1)
			{
				Object arg0 = args.get(0).evaluate(context);
				return (null != arg0) && (arg0 instanceof Color);
			}
			throw new ArgumentCountMismatchException("function", "iscolor", argcount, 1);
		}
		else if (name.equals("chr"))
		{
			if (argcount == 1)
			{
				return Utils.chr(args.get(0).evaluate(context));
			}
			throw new ArgumentCountMismatchException("function", "chr", argcount, 1);
		}
		else if (name.equals("ord"))
		{
			if (argcount == 1)
			{
				return Utils.ord(args.get(0).evaluate(context));
			}
			throw new ArgumentCountMismatchException("function", "ord", argcount, 1);
		}
		else if (name.equals("hex"))
		{
			if (argcount == 1)
			{
				return Utils.hex(args.get(0).evaluate(context));
			}
			throw new ArgumentCountMismatchException("function", "hex", argcount, 1);
		}
		else if (name.equals("oct"))
		{
			if (argcount == 1)
			{
				return Utils.oct(args.get(0).evaluate(context));
			}
			throw new ArgumentCountMismatchException("function", "oct", argcount, 1);
		}
		else if (name.equals("bin"))
		{
			if (argcount == 1)
			{
				return Utils.bin(args.get(0).evaluate(context));
			}
			throw new ArgumentCountMismatchException("function", "bin", argcount, 1);
		}
		else if (name.equals("abs"))
		{
			if (argcount == 1)
			{
				return Utils.abs(args.get(0).evaluate(context));
			}
			throw new ArgumentCountMismatchException("function", "abc", argcount, 1);
		}
		else if (name.equals("range"))
		{
			if (argcount == 1)
			{
				return Utils.range(args.get(0).evaluate(context));
			}
			else if (argcount == 2)
			{
				return Utils.range(args.get(0).evaluate(context), args.get(1).evaluate(context));
			}
			else if (argcount == 3)
			{
				return Utils.range(args.get(0).evaluate(context), args.get(1).evaluate(context), args.get(3).evaluate(context));
			}
			throw new ArgumentCountMismatchException("function", "range", argcount, 1, 3);
		}
		else if (name.equals("sorted"))
		{
			if (argcount == 1)
			{
				return Utils.sorted(args.get(0).evaluate(context));
			}
			throw new ArgumentCountMismatchException("function", "sorted", argcount, 1);
		}
		else if (name.equals("type"))
		{
			if (argcount == 1)
			{
				return Utils.type(args.get(0).evaluate(context));
			}
			throw new ArgumentCountMismatchException("function", "type", argcount, 1);
		}
		else if (name.equals("get"))
		{
			if (argcount == 1)
			{
				return context.getVariables().get(args.get(0).evaluate(context));
			}
			else if (argcount == 2)
			{
				Object arg0 = args.get(0).evaluate(context);
				return context.getVariables().containsKey(arg0) ? arg0 : args.get(1).evaluate(context);
			}
			throw new ArgumentCountMismatchException("function", "get", argcount, 1, 2);
		}
		else if (name.equals("json"))
		{
			if (argcount == 1)
			{
				return Utils.json(args.get(0).evaluate(context));
			}
			throw new ArgumentCountMismatchException("function", "json", argcount, 1);
		}
		else if (name.equals("reversed"))
		{
			if (argcount == 1)
			{
				return Utils.reversed(args.get(0).evaluate(context));
			}
			throw new ArgumentCountMismatchException("function", "reversed", argcount, 1);
		}
		else if (name.equals("randrange"))
		{
			if (argcount == 1)
			{
				return Utils.randrange(args.get(0).evaluate(context));
			}
			else if (argcount == 2)
			{
				return Utils.randrange(args.get(0).evaluate(context), args.get(1).evaluate(context));
			}
			else if (argcount == 3)
			{
				return Utils.randrange(args.get(0).evaluate(context), args.get(1).evaluate(context), args.get(2).evaluate(context));
			}
			throw new ArgumentCountMismatchException("function", "randrange", argcount, 1, 3);
		}
		else if (name.equals("randchoice"))
		{
			if (argcount == 1)
			{
				return Utils.randchoice(args.get(0).evaluate(context));
			}
			throw new ArgumentCountMismatchException("function", "randchoice", argcount, 1);
		}
		else if (name.equals("format"))
		{
			if (argcount == 2)
			{
				return Utils.format(args.get(0).evaluate(context), args.get(1).evaluate(context), context.getLocale());
			}
			throw new ArgumentCountMismatchException("function", "format", argcount, 2);
		}
		else if (name.equals("zip"))
		{
			if (argcount == 2)
			{
				return Utils.zip(args.get(0).evaluate(context), args.get(1).evaluate(context));
			}
			else if (argcount == 3)
			{
				return Utils.zip(args.get(0).evaluate(context), args.get(1).evaluate(context), args.get(2).evaluate(context));
			}
			throw new ArgumentCountMismatchException("function", "zip", argcount, 2, 3);
		}
		else if (name.equals("rgb"))
		{
			if (argcount == 3)
			{
				return Utils.rgb(args.get(0).evaluate(context), args.get(1).evaluate(context), args.get(2).evaluate(context));
			}
			else if (argcount == 4)
			{
				return Utils.rgb(args.get(0).evaluate(context), args.get(1).evaluate(context), args.get(2).evaluate(context), args.get(3).evaluate(context));
			}
			throw new ArgumentCountMismatchException("function", "rgb", argcount, 3, 4);
		}
		else if (name.equals("hls"))
		{
			if (argcount == 3)
			{
				return Utils.hls(args.get(0).evaluate(context), args.get(1).evaluate(context), args.get(2).evaluate(context));
			}
			else if (argcount == 4)
			{
				return Utils.hls(args.get(0).evaluate(context), args.get(1).evaluate(context), args.get(2).evaluate(context), args.get(3).evaluate(context));
			}
			throw new ArgumentCountMismatchException("function", "hls", argcount, 3, 4);
		}
		else if (name.equals("hsv"))
		{
			if (argcount == 3)
			{
				return Utils.hsv(args.get(0).evaluate(context), args.get(1).evaluate(context), args.get(2).evaluate(context));
			}
			else if (argcount == 4)
			{
				return Utils.hsv(args.get(0).evaluate(context), args.get(1).evaluate(context), args.get(2).evaluate(context), args.get(3).evaluate(context));
			}
			throw new ArgumentCountMismatchException("function", "hsv", argcount, 3, 4);
		}
		else
		{
			throw new UnknownFunctionException(name);
		}
	}
}
