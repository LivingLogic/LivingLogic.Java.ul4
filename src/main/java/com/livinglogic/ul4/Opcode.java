/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.HashMap;
import com.livinglogic.utils.ObjectAsMap;

public class Opcode extends ObjectAsMap
{
	public static final int OC_TEXT = 0;
	public static final int OC_LOADNONE = 1;
	public static final int OC_LOADFALSE = 2;
	public static final int OC_LOADTRUE = 3;
	public static final int OC_LOADINT = 4;
	public static final int OC_LOADFLOAT = 5;
	public static final int OC_LOADSTR = 6;
	public static final int OC_LOADDATE = 7;
	public static final int OC_LOADCOLOR = 8;
	public static final int OC_BUILDLIST = 9;
	public static final int OC_BUILDDICT = 10;
	public static final int OC_ADDLIST = 11;
	public static final int OC_ADDDICT = 12;
	public static final int OC_UPDATEDICT = 13;
	public static final int OC_LOADVAR = 14;
	public static final int OC_STOREVAR = 15;
	public static final int OC_ADDVAR = 16;
	public static final int OC_SUBVAR = 17;
	public static final int OC_MULVAR = 18;
	public static final int OC_TRUEDIVVAR = 19;
	public static final int OC_FLOORDIVVAR = 20;
	public static final int OC_MODVAR = 21;
	public static final int OC_DELVAR = 22;
	public static final int OC_GETATTR = 23;
	public static final int OC_GETITEM = 24;
	public static final int OC_GETSLICE12 = 25;
	public static final int OC_GETSLICE1 = 26;
	public static final int OC_GETSLICE2 = 27;
	public static final int OC_GETSLICE = 28;
	public static final int OC_PRINT = 29;
	public static final int OC_PRINTX = 30;
	public static final int OC_NOT = 31;
	public static final int OC_NEG = 32;
	public static final int OC_CONTAINS = 33;
	public static final int OC_NOTCONTAINS = 34;
	public static final int OC_EQ = 35;
	public static final int OC_NE = 36;
	public static final int OC_LT = 37;
	public static final int OC_LE = 38;
	public static final int OC_GT = 39;
	public static final int OC_GE = 40;
	public static final int OC_ADD = 41;
	public static final int OC_SUB = 42;
	public static final int OC_MUL = 43;
	public static final int OC_FLOORDIV = 44;
	public static final int OC_TRUEDIV = 45;
	public static final int OC_AND = 46;
	public static final int OC_OR = 47;
	public static final int OC_MOD = 48;
	public static final int OC_CALLFUNC0 = 49;
	public static final int OC_CALLFUNC1 = 50;
	public static final int OC_CALLFUNC2 = 51;
	public static final int OC_CALLFUNC3 = 52;
	public static final int OC_CALLFUNC4 = 53;
	public static final int OC_CALLMETH0 = 54;
	public static final int OC_CALLMETH1 = 55;
	public static final int OC_CALLMETH2 = 56;
	public static final int OC_CALLMETH3 = 57;
	public static final int OC_CALLMETHKW = 58;
	public static final int OC_IF = 59;
	public static final int OC_ELSE = 60;
	public static final int OC_ENDIF = 61;
	public static final int OC_FOR = 62;
	public static final int OC_ENDFOR = 63;
	public static final int OC_BREAK = 64;
	public static final int OC_CONTINUE = 65;
	public static final int OC_RENDER = 66;
	public static final int OC_DEF = 67;
	public static final int OC_ENDDEF = 68;

	public static final int CF0_NOW = 0;
	public static final int CF0_UTCNOW = 1;
	public static final int CF0_VARS = 2;
	public static final int CF0_RANDOM = 3;

	public static final int CF1_XMLESCAPE = 0;
	public static final int CF1_STR = 1;
	public static final int CF1_REPR = 2;
	public static final int CF1_INT = 3;
	public static final int CF1_FLOAT = 4;
	public static final int CF1_BOOL = 5;
	public static final int CF1_LEN = 6;
	public static final int CF1_ENUM = 7;
	public static final int CF1_ENUMFL = 8;
	public static final int CF1_ISFIRSTLAST = 9;
	public static final int CF1_ISFIRST = 10;
	public static final int CF1_ISLAST = 11;
	public static final int CF1_ISNONE = 12;
	public static final int CF1_ISSTR = 13;
	public static final int CF1_ISINT = 14;
	public static final int CF1_ISFLOAT = 15;
	public static final int CF1_ISBOOL = 16;
	public static final int CF1_ISDATE = 17;
	public static final int CF1_ISLIST = 18;
	public static final int CF1_ISDICT = 19;
	public static final int CF1_ISTEMPLATE = 20;
	public static final int CF1_ISCOLOR = 21;
	public static final int CF1_CHR = 22;
	public static final int CF1_ORD = 23;
	public static final int CF1_HEX = 24;
	public static final int CF1_OCT = 25;
	public static final int CF1_BIN = 26;
	public static final int CF1_ABS = 27;
	public static final int CF1_SORTED = 28;
	public static final int CF1_RANGE = 29;
	public static final int CF1_TYPE = 30;
	public static final int CF1_CSV = 31;
	public static final int CF1_GET = 32;
	public static final int CF1_JSON = 33;
	public static final int CF1_REVERSED = 34;
	public static final int CF1_RANDRANGE = 35;
	public static final int CF1_RANDCHOICE = 36;

	public static final int CF2_FORMAT = 0;
	public static final int CF2_RANGE = 1;
	public static final int CF2_GET = 2;
	public static final int CF2_ZIP = 3;
	public static final int CF2_INT = 4;
	public static final int CF2_RANDRANGE = 5;

	public static final int CF3_RANGE = 0;
	public static final int CF3_ZIP = 1;
	public static final int CF3_RGB = 2;
	public static final int CF3_HLS = 3;
	public static final int CF3_HSV = 4;
	public static final int CF3_RANDRANGE = 5;

	public static final int CF4_RGB = 0;
	public static final int CF4_HLS = 1;
	public static final int CF4_HSV = 2;

	public static final int CM0_SPLIT = 0;
	public static final int CM0_RSPLIT = 1;
	public static final int CM0_STRIP = 2;
	public static final int CM0_LSTRIP = 3;
	public static final int CM0_RSTRIP = 4;
	public static final int CM0_LOWER = 5;
	public static final int CM0_UPPER = 6;
	public static final int CM0_CAPITALIZE = 7;
	public static final int CM0_ITEMS = 8;
	public static final int CM0_ISOFORMAT = 9;
	public static final int CM0_MIMEFORMAT = 10;
	public static final int CM0_R = 11;
	public static final int CM0_G = 12;
	public static final int CM0_B = 13;
	public static final int CM0_A = 14;
	public static final int CM0_HLS = 15;
	public static final int CM0_HLSA = 16;
	public static final int CM0_HSV = 17;
	public static final int CM0_HSVA = 18;
	public static final int CM0_LUM = 19;
	public static final int CM0_DAY = 20;
	public static final int CM0_MONTH = 21;
	public static final int CM0_YEAR = 22;
	public static final int CM0_HOUR = 23;
	public static final int CM0_MINUTE = 24;
	public static final int CM0_SECOND = 25;
	public static final int CM0_MICROSECOND = 26;
	public static final int CM0_WEEKDAY = 27;
	public static final int CM0_YEARDAY = 28;
	public static final int CM0_RENDER = 29;

	public static final int CM1_SPLIT = 0;
	public static final int CM1_RSPLIT = 1;
	public static final int CM1_STRIP = 2;
	public static final int CM1_LSTRIP = 3;
	public static final int CM1_RSTRIP = 4;
	public static final int CM1_STARTSWITH = 5;
	public static final int CM1_ENDSWITH = 6;
	public static final int CM1_FIND = 7;
	public static final int CM1_RFIND = 8;
	public static final int CM1_GET = 9;
	public static final int CM1_WITHLUM = 10;
	public static final int CM1_WITHA = 11;
	public static final int CM1_JOIN = 12;

	public static final int CM2_SPLIT = 0;
	public static final int CM2_RSPLIT = 1;
	public static final int CM2_FIND = 2;
	public static final int CM2_RFIND = 3;
	public static final int CM2_REPLACE = 4;
	public static final int CM2_GET = 5;

	public static final int CM3_FIND = 0;
	public static final int CM3_RFIND = 1;

	public static final int CMKW_RENDER = 0;

	public int name;
	public int r1;
	public int r2;
	public int r3;
	public int r4;
	public int r5;
	public String arg;
	public int argcode;
	public Location location;
	public int jump; // store the index of the "corresponding" opcode, e.g. the "endfor" opcode for a "for" opcode (set by InterpretedTemplate.annotate())
	public InterpretedTemplate template; // if the opcode if a "def" opcode, store the subtemplate here (set by InterpretedTemplate.annotate())

	private static HashMap<String, Integer> _name2code = new HashMap<String, Integer>();
	private static HashMap<Integer, String> _code2name = new HashMap<Integer, String>();

	private static void _mapName2Code(String name, int code)
	{
		_name2code.put(name, code);
		_code2name.put(code, name);
	}

	static
	{
		_mapName2Code(null, OC_TEXT);
		_mapName2Code("loadnone", OC_LOADNONE);
		_mapName2Code("loadfalse", OC_LOADFALSE);
		_mapName2Code("loadtrue", OC_LOADTRUE);
		_mapName2Code("loadint", OC_LOADINT);
		_mapName2Code("loadfloat", OC_LOADFLOAT);
		_mapName2Code("loadstr", OC_LOADSTR);
		_mapName2Code("loaddate", OC_LOADDATE);
		_mapName2Code("loadcolor", OC_LOADCOLOR);
		_mapName2Code("buildlist", OC_BUILDLIST);
		_mapName2Code("builddict", OC_BUILDDICT);
		_mapName2Code("addlist", OC_ADDLIST);
		_mapName2Code("adddict", OC_ADDDICT);
		_mapName2Code("updatedict", OC_UPDATEDICT);
		_mapName2Code("loadvar", OC_LOADVAR);
		_mapName2Code("storevar", OC_STOREVAR);
		_mapName2Code("addvar", OC_ADDVAR);
		_mapName2Code("subvar", OC_SUBVAR);
		_mapName2Code("mulvar", OC_MULVAR);
		_mapName2Code("truedivvar", OC_TRUEDIVVAR);
		_mapName2Code("floordivvar", OC_FLOORDIVVAR);
		_mapName2Code("modvar", OC_MODVAR);
		_mapName2Code("delvar", OC_DELVAR);
		_mapName2Code("getattr", OC_GETATTR);
		_mapName2Code("getitem", OC_GETITEM);
		_mapName2Code("getslice12", OC_GETSLICE12);
		_mapName2Code("getslice1", OC_GETSLICE1);
		_mapName2Code("getslice2", OC_GETSLICE2);
		_mapName2Code("getslice", OC_GETSLICE);
		_mapName2Code("print", OC_PRINT);
		_mapName2Code("printx", OC_PRINTX);
		_mapName2Code("not", OC_NOT);
		_mapName2Code("neg", OC_NEG);
		_mapName2Code("contains", OC_CONTAINS);
		_mapName2Code("notcontains", OC_NOTCONTAINS);
		_mapName2Code("eq", OC_EQ);
		_mapName2Code("ne", OC_NE);
		_mapName2Code("lt", OC_LT);
		_mapName2Code("le", OC_LE);
		_mapName2Code("gt", OC_GT);
		_mapName2Code("ge", OC_GE);
		_mapName2Code("add", OC_ADD);
		_mapName2Code("sub", OC_SUB);
		_mapName2Code("mul", OC_MUL);
		_mapName2Code("floordiv", OC_FLOORDIV);
		_mapName2Code("truediv", OC_TRUEDIV);
		_mapName2Code("and", OC_AND);
		_mapName2Code("or", OC_OR);
		_mapName2Code("mod", OC_MOD);
		_mapName2Code("callfunc0", OC_CALLFUNC0);
		_mapName2Code("callfunc1", OC_CALLFUNC1);
		_mapName2Code("callfunc2", OC_CALLFUNC2);
		_mapName2Code("callfunc3", OC_CALLFUNC3);
		_mapName2Code("callfunc4", OC_CALLFUNC4);
		_mapName2Code("callmeth0", OC_CALLMETH0);
		_mapName2Code("callmeth1", OC_CALLMETH1);
		_mapName2Code("callmeth2", OC_CALLMETH2);
		_mapName2Code("callmeth3", OC_CALLMETH3);
		_mapName2Code("callmethkw", OC_CALLMETHKW);
		_mapName2Code("if", OC_IF);
		_mapName2Code("else", OC_ELSE);
		_mapName2Code("endif", OC_ENDIF);
		_mapName2Code("for", OC_FOR);
		_mapName2Code("endfor", OC_ENDFOR);
		_mapName2Code("break", OC_BREAK);
		_mapName2Code("continue", OC_CONTINUE);
		_mapName2Code("render", OC_RENDER);
		_mapName2Code("def", OC_DEF);
		_mapName2Code("enddef", OC_ENDDEF);
	}

	public static int name2code(String name)
	{
		if (_name2code.containsKey(name))
			return _name2code.get(name);
		throw new IllegalArgumentException("Opcode name " + name + " unknown!");
	}

	public static int callfunc0name2code(String name)
	{
		if (name.equals("now"))
			return CF0_NOW;
		else if (name.equals("utcnow"))
			return CF0_UTCNOW;
		else if (name.equals("vars"))
			return CF0_VARS;
		else if (name.equals("random"))
			return CF0_RANDOM;
		else
			throw new UnknownFunctionException(name);
	}

	public static int callfunc1name2code(String name)
	{
		if (name.equals("xmlescape"))
			return CF1_XMLESCAPE;
		else if (name.equals("str"))
			return CF1_STR;
		else if (name.equals("repr"))
			return CF1_REPR;
		else if (name.equals("int"))
			return CF1_INT;
		else if (name.equals("float"))
			return CF1_FLOAT;
		else if (name.equals("bool"))
			return CF1_BOOL;
		else if (name.equals("len"))
			return CF1_LEN;
		else if (name.equals("enum"))
			return CF1_ENUM;
		else if (name.equals("enumfl"))
			return CF1_ENUMFL;
		else if (name.equals("isfirstlast"))
			return CF1_ISFIRSTLAST;
		else if (name.equals("isfirst"))
			return CF1_ISFIRST;
		else if (name.equals("islast"))
			return CF1_ISLAST;
		else if (name.equals("isnone"))
			return CF1_ISNONE;
		else if (name.equals("isstr"))
			return CF1_ISSTR;
		else if (name.equals("isint"))
			return CF1_ISINT;
		else if (name.equals("isfloat"))
			return CF1_ISFLOAT;
		else if (name.equals("isbool"))
			return CF1_ISBOOL;
		else if (name.equals("isdate"))
			return CF1_ISDATE;
		else if (name.equals("islist"))
			return CF1_ISLIST;
		else if (name.equals("isdict"))
			return CF1_ISDICT;
		else if (name.equals("istemplate"))
			return CF1_ISTEMPLATE;
		else if (name.equals("iscolor"))
			return CF1_ISCOLOR;
		else if (name.equals("chr"))
			return CF1_CHR;
		else if (name.equals("ord"))
			return CF1_ORD;
		else if (name.equals("hex"))
			return CF1_HEX;
		else if (name.equals("oct"))
			return CF1_OCT;
		else if (name.equals("bin"))
			return CF1_BIN;
		else if (name.equals("abs"))
			return CF1_ABS;
		else if (name.equals("sorted"))
			return CF1_SORTED;
		else if (name.equals("range"))
			return CF1_RANGE;
		else if (name.equals("type"))
			return CF1_TYPE;
		else if (name.equals("csv"))
			return CF1_CSV;
		else if (name.equals("get"))
			return CF1_GET;
		else if (name.equals("json"))
			return CF1_JSON;
		else if (name.equals("reversed"))
			return CF1_REVERSED;
		else if (name.equals("randrange"))
			return CF1_RANDRANGE;
		else if (name.equals("randchoice"))
			return CF1_RANDCHOICE;
		else
			throw new UnknownFunctionException(name);
	}

	public static int callfunc2name2code(String name)
	{
		if (name.equals("format"))
			return CF2_FORMAT;
		else if (name.equals("range"))
			return CF2_RANGE;
		else if (name.equals("get"))
			return CF2_GET;
		else if (name.equals("zip"))
			return CF2_ZIP;
		else if (name.equals("int"))
			return CF2_INT;
		else if (name.equals("randrange"))
			return CF2_RANDRANGE;
		else
			throw new UnknownFunctionException(name);
	}

	public static int callfunc3name2code(String name)
	{
		if (name.equals("range"))
			return CF3_RANGE;
		if (name.equals("zip"))
			return CF3_ZIP;
		else if (name.equals("rgb"))
			return CF3_RGB;
		else if (name.equals("hls"))
			return CF3_HLS;
		else if (name.equals("hsv"))
			return CF3_HSV;
		else if (name.equals("randrange"))
			return CF3_RANDRANGE;
		else
			throw new UnknownFunctionException(name);
	}

	public static int callfunc4name2code(String name)
	{
		if (name.equals("rgb"))
			return CF4_RGB;
		else if (name.equals("hls"))
			return CF4_HLS;
		else if (name.equals("hsv"))
			return CF4_HSV;
		else
			throw new UnknownFunctionException(name);
	}

	public static int callmeth0name2code(String name)
	{
		if (name.equals("split"))
			return CM0_SPLIT;
		else if (name.equals("rsplit"))
			return CM0_RSPLIT;
		else if (name.equals("strip"))
			return CM0_STRIP;
		else if (name.equals("lstrip"))
			return CM0_LSTRIP;
		else if (name.equals("rstrip"))
			return CM0_RSTRIP;
		else if (name.equals("upper"))
			return CM0_UPPER;
		else if (name.equals("lower"))
			return CM0_LOWER;
		else if (name.equals("capitalize"))
			return CM0_CAPITALIZE;
		else if (name.equals("items"))
			return CM0_ITEMS;
		else if (name.equals("isoformat"))
			return CM0_ISOFORMAT;
		else if (name.equals("mimeformat"))
			return CM0_MIMEFORMAT;
		else if (name.equals("r"))
			return CM0_R;
		else if (name.equals("g"))
			return CM0_G;
		else if (name.equals("b"))
			return CM0_B;
		else if (name.equals("a"))
			return CM0_A;
		else if (name.equals("hls"))
			return CM0_HLS;
		else if (name.equals("hlsa"))
			return CM0_HLSA;
		else if (name.equals("hsv"))
			return CM0_HSV;
		else if (name.equals("hsva"))
			return CM0_HSVA;
		else if (name.equals("lum"))
			return CM0_LUM;
		else if (name.equals("day"))
			return CM0_DAY;
		else if (name.equals("month"))
			return CM0_MONTH;
		else if (name.equals("year"))
			return CM0_YEAR;
		else if (name.equals("hour"))
			return CM0_HOUR;
		else if (name.equals("minute"))
			return CM0_MINUTE;
		else if (name.equals("second"))
			return CM0_SECOND;
		else if (name.equals("microsecond"))
			return CM0_MICROSECOND;
		else if (name.equals("weekday"))
			return CM0_WEEKDAY;
		else if (name.equals("yearday"))
			return CM0_YEARDAY;
		else if (name.equals("render"))
			return CM0_RENDER;
		else
			throw new UnknownMethodException(name);
	}

	public static int callmeth1name2code(String name)
	{
		if (name.equals("split"))
			return CM1_SPLIT;
		else if (name.equals("rsplit"))
			return CM1_RSPLIT;
		else if (name.equals("strip"))
			return CM1_STRIP;
		else if (name.equals("lstrip"))
			return CM1_LSTRIP;
		else if (name.equals("rstrip"))
			return CM1_RSTRIP;
		else if (name.equals("startswith"))
			return CM1_STARTSWITH;
		else if (name.equals("endswith"))
			return CM1_ENDSWITH;
		else if (name.equals("find"))
			return CM1_FIND;
		else if (name.equals("rfind"))
			return CM1_RFIND;
		else if (name.equals("get"))
			return CM1_GET;
		else if (name.equals("withlum"))
			return CM1_WITHLUM;
		else if (name.equals("witha"))
			return CM1_WITHA;
		else if (name.equals("join"))
			return CM1_JOIN;
		else
			throw new UnknownMethodException(name);
	}

	public static int callmeth2name2code(String name)
	{
		if (name.equals("split"))
			return CM2_SPLIT;
		else if (name.equals("rsplit"))
			return CM2_RSPLIT;
		else if (name.equals("find"))
			return CM2_FIND;
		else if (name.equals("rfind"))
			return CM2_RFIND;
		else if (name.equals("replace"))
			return CM2_REPLACE;
		else if (name.equals("get"))
			return CM2_GET;
		else
			throw new UnknownMethodException(name);
	}

	public static int callmeth3name2code(String name)
	{
		if (name.equals("find"))
			return CM3_FIND;
		else if (name.equals("rfind"))
			return CM3_RFIND;
		else
			throw new UnknownMethodException(name);
	}

	public static int callmethkwname2code(String name)
	{
		if (name.equals("render"))
			return CMKW_RENDER;
		else
			throw new UnknownMethodException(name);
	}

	public static String code2name(int code)
	{
		if (_code2name.containsKey(code))
			return _code2name.get(code);
		throw new IllegalArgumentException("Opcode code " + code + " unknown!");
	}

	public Opcode(String name, int r1, int r2, int r3, int r4, int r5, String arg, Location location)
	{
		this(name2code(name), r1, r2, r3, r4, r5, arg, location);
	}

	public Opcode(int name, int r1, int r2, int r3, int r4, int r5, String arg, Location location)
	{
		this.name = name;
		this.r1 = r1;
		this.r2 = r2;
		this.r3 = r3;
		this.r4 = r4;
		this.r5 = r5;
		this.arg = arg;
		switch (name)
		{
			case OC_CALLFUNC0:
				this.argcode = callfunc0name2code(arg);
				break;
			case OC_CALLFUNC1:
				this.argcode = callfunc1name2code(arg);
				break;
			case OC_CALLFUNC2:
				this.argcode = callfunc2name2code(arg);
				break;
			case OC_CALLFUNC3:
				this.argcode = callfunc3name2code(arg);
				break;
			case OC_CALLFUNC4:
				this.argcode = callfunc4name2code(arg);
				break;
			case OC_CALLMETH0:
				this.argcode = callmeth0name2code(arg);
				break;
			case OC_CALLMETH1:
				this.argcode = callmeth1name2code(arg);
				break;
			case OC_CALLMETH2:
				this.argcode = callmeth2name2code(arg);
				break;
			case OC_CALLMETH3:
				this.argcode = callmeth3name2code(arg);
				break;
			case OC_CALLMETHKW:
				this.argcode = callmethkwname2code(arg);
				break;
		}
		this.location = location;
		this.jump = -1;
		this.template = null;
	}

	public String toString()
	{
		switch (name)
		{
			case OC_TEXT:
				return "print " + Utils.repr(location.getCode());
			case OC_LOADNONE:
				return "r" + r1 + " = None";
			case OC_LOADFALSE:
				return "r" + r1 + " = False";
			case OC_LOADTRUE:
				return "r" + r1 + " = True";
			case OC_LOADINT:
				return "r" + r1 + " = " + arg;
			case OC_LOADFLOAT:
				return "r" + r1 + " = " + arg;
			case OC_LOADSTR:
				return "r" + r1 + " = " + Utils.repr(arg);
			case OC_LOADDATE:
				return "r" + r1 + " = " + Utils.repr(arg);
			case OC_LOADCOLOR:
				return "r" + r1 + " = " + Color.fromdump(arg).repr();
			case OC_BUILDLIST:
				return "r" + r1 + " = []";
			case OC_BUILDDICT:
				return "r" + r1 + " = {}";
			case OC_ADDLIST:
				return "r" + r1 + ".append(r" + r2 + ")";
			case OC_ADDDICT:
				return "r" + r1 + "[r" + r2 + "] = r" + r3;
			case OC_UPDATEDICT:
				return "r" + r1 + ".update(r" + r2 + ")";
			case OC_LOADVAR:
				return "r" + r1 + " = vars[" + Utils.repr(arg) + "]";
			case OC_STOREVAR:
				return "vars[" + Utils.repr(arg) + "] = r" + r1;
			case OC_ADDVAR:
				return "vars[" + Utils.repr(arg) + "] += r" + r1;
			case OC_SUBVAR:
				return "vars[" + Utils.repr(arg) + "] -= r" + r1;
			case OC_MULVAR:
				return "vars[" + Utils.repr(arg) + "] *= r" + r1;
			case OC_TRUEDIVVAR:
				return "vars[" + Utils.repr(arg) + "] /= r" + r1;
			case OC_FLOORDIVVAR:
				return "vars[" + Utils.repr(arg) + "] //= r" + r1;
			case OC_MODVAR:
				return "vars[" + Utils.repr(arg) + "] %= r" + r1;
			case OC_DELVAR:
				return "del vars[" + Utils.repr(arg) + "]";
			case OC_GETATTR:
				return "r" + r1 + " = getattr(r" + r2 + ", " + Utils.repr(arg) + ")";
			case OC_GETITEM:
				return "r" + r1 + " = r" + r2 + "[r" + r3 + "]";
			case OC_GETSLICE12:
				return "r" + r1 + " = r" + r2 + "[r" + r3 + ":r" + r4 + "]";
			case OC_GETSLICE1:
				return "r" + r1 + " = r" + r2 + "[r" + r3 + ":]";
			case OC_GETSLICE2:
				return "r" + r1 + " = r" + r2 + "[:r" + r4 + "]";
			case OC_GETSLICE:
				return "r" + r1 + " = r" + r2 + "[:]";
			case OC_PRINT:
				return "print r" + r1;
			case OC_PRINTX:
				return "print xmlescape(r" + r1 + ")";
			case OC_NOT:
				return "r" + r1 + " = not r" + r2;
			case OC_NEG:
				return "r" + r1 + " = -r" + r2;
			case OC_EQ:
				return "r" + r1 + " = r" + r2 + " == r" + r3;
			case OC_NE:
				return "r" + r1 + " = r" + r2 + " != r" + r3;
			case OC_LT:
				return "r" + r1 + " = r" + r2 + " < r" + r3;
			case OC_LE:
				return "r" + r1 + " = r" + r2 + " <= r" + r3;
			case OC_GT:
				return "r" + r1 + " = r" + r2 + " > r" + r3;
			case OC_GE:
				return "r" + r1 + " = r" + r2 + " >= r" + r3;
			case OC_CONTAINS:
				return "r" + r1 + " = r" + r2 + " in r" + r3;
			case OC_NOTCONTAINS:
				return "r" + r1 + " = r" + r2 + " not in r" + r3;
			case OC_ADD:
				return "r" + r1 + " = r" + r2 + " + r" + r3;
			case OC_SUB:
				return "r" + r1 + " = r" + r2 + " - r" + r3;
			case OC_MUL:
				return "r" + r1 + " = r" + r2 + " == r" + r3;
			case OC_FLOORDIV:
				return "r" + r1 + " = r" + r2 + " // r" + r3;
			case OC_TRUEDIV:
				return "r" + r1 + " = r" + r2 + " / r" + r3;
			case OC_AND:
				return "r" + r1 + " = r" + r2 + " and r" + r3;
			case OC_OR:
				return "r" + r1 + " = r" + r2 + " or r" + r3;
			case OC_MOD:
				return "r" + r1 + " = r" + r2 + " % r" + r3;
			case OC_CALLFUNC0:
				return "r" + r1 + " = " + arg + "()";
			case OC_CALLFUNC1:
				return "r" + r1 + " = " + arg + "(r" + r2 + ")";
			case OC_CALLFUNC2:
				return "r" + r1 + " = " + arg + "(r" + r2 + ", r" + r3 + ")";
			case OC_CALLFUNC3:
				return "r" + r1 + " = " + arg + "(r" + r2 + ", r" + r3 + ", r" + r4 + ")";
			case OC_CALLFUNC4:
				return "r" + r1 + " = " + arg + "(r" + r2 + ", r" + r3 + ", r" + r4 + ", r" + r5 + ")";
			case OC_CALLMETH0:
				return "r" + r1 + " = r" + r2 + "." + arg + "()";
			case OC_CALLMETH1:
				return "r" + r1 + " = r" + r2 + "." + arg + "(r" + r3 + ")";
			case OC_CALLMETH2:
				return "r" + r1 + " = r" + r2 + "." + arg + "(r" + r3 + ", r" + r4 + ")";
			case OC_CALLMETH3:
				return "r" + r1 + " = r" + r2 + "." + arg + "(r" + r3 + ", r" + r4 + ", r" + r5 + ")";
			case OC_IF:
				return "if r" + r1;
			case OC_ELSE:
				return "else";
			case OC_ENDIF:
				return "endif";
			case OC_FOR:
				return "for r" + r1 + " in r" + r2;
			case OC_ENDFOR:
				return "endfor";
			case OC_BREAK:
				return "break";
			case OC_CONTINUE:
				return "continue";
			case OC_RENDER:
				return "render r" + r1 + "(r" + r2 + ")";
			case OC_DEF:
				return "def " + arg + "(**vars)";
			case OC_ENDDEF:
				return "enddef";
			default:
				throw new IllegalArgumentException("Opcode code " + name + " unknown!");
		}
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>();
			v.put("code", new ValueMaker(){public Object getValue(Object object){return ((Opcode)object).code2name(((Opcode)object).name);}});
			v.put("r1", new ValueMaker(){public Object getValue(Object object){return ((Opcode)object).r1 != -1 ? ((Opcode)object).r1 : null;}});
			v.put("r2", new ValueMaker(){public Object getValue(Object object){return ((Opcode)object).r2 != -1 ? ((Opcode)object).r2 : null;}});
			v.put("r3", new ValueMaker(){public Object getValue(Object object){return ((Opcode)object).r3 != -1 ? ((Opcode)object).r3 : null;}});
			v.put("r4", new ValueMaker(){public Object getValue(Object object){return ((Opcode)object).r4 != -1 ? ((Opcode)object).r4 : null;}});
			v.put("r5", new ValueMaker(){public Object getValue(Object object){return ((Opcode)object).r5 != -1 ? ((Opcode)object).r5 : null;}});
			v.put("arg", new ValueMaker(){public Object getValue(Object object){return ((Opcode)object).arg;}});
			v.put("location", new ValueMaker(){public Object getValue(Object object){return ((Opcode)object).location;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}