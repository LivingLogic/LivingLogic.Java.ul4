package com.livinglogic.ull;

public class Opcode
{
	public static final int OC_TEXT = 0;
	public static final int OC_LOADNONE = 1;
	public static final int OC_LOADFALSE = 2;
	public static final int OC_LOADTRUE = 3;
	public static final int OC_LOADINT = 4;
	public static final int OC_LOADFLOAT = 5;
	public static final int OC_LOADSTR = 6;
	public static final int OC_LOADVAR = 7;
	public static final int OC_STOREVAR = 8;
	public static final int OC_ADDVAR = 9;
	public static final int OC_SUBVAR = 10;
	public static final int OC_MULVAR = 11;
	public static final int OC_TRUEDIVVAR = 12;
	public static final int OC_FLOORDIVVAR = 13;
	public static final int OC_MODVAR = 14;
	public static final int OC_DELVAR = 15;
	public static final int OC_GETATTR = 16;
	public static final int OC_GETITEM = 17;
	public static final int OC_GETSLICE12 = 18;
	public static final int OC_GETSLICE1 = 19;
	public static final int OC_GETSLICE2 = 20;
	public static final int OC_GETSLICE = 21;
	public static final int OC_PRINT = 22;
	public static final int OC_FOR = 23;
	public static final int OC_ENDFOR = 24;
	public static final int OC_NOT = 25;
	public static final int OC_NEG = 26;
	public static final int OC_CONTAINS = 27;
	public static final int OC_NOTCONTAINS = 28;
	public static final int OC_EQUALS = 29;
	public static final int OC_NOTEQUALS = 30;
	public static final int OC_ADD = 31;
	public static final int OC_SUB = 32;
	public static final int OC_MUL = 33;
	public static final int OC_FLOORDIV = 34;
	public static final int OC_TRUEDIV = 35;
	public static final int OC_AND = 36;
	public static final int OC_OR = 37;
	public static final int OC_MOD = 38;
	public static final int OC_CALLFUNC0 = 39;
	public static final int OC_CALLFUNC1 = 40;
	public static final int OC_CALLFUNC2 = 41;
	public static final int OC_CALLFUNC3 = 42;
	public static final int OC_CALLMETH0 = 43;
	public static final int OC_CALLMETH1 = 44;
	public static final int OC_CALLMETH2 = 45;
	public static final int OC_CALLMETH3 = 46;
	public static final int OC_IF = 47;
	public static final int OC_ELSE = 48;
	public static final int OC_ENDIF = 49;
	public static final int OC_RENDER = 50;

	public int name;
	public int r1;
	public int r2;
	public int r3;
	public int r4;
	public int r5;
	public String arg;
	public Location location;
	public int jump;

	public static int name2code(String name)
	{
		if (name == null)
			return OC_TEXT;
		else if (name.equals("loadnone"))
			return OC_LOADNONE;
		else if (name.equals("loadfalse"))
			return OC_LOADFALSE;
		else if (name.equals("loadtrue"))
			return OC_LOADTRUE;
		else if (name.equals("loadint"))
			return OC_LOADINT;
		else if (name.equals("loadfloat"))
			return OC_LOADFLOAT;
		else if (name.equals("loadstr"))
			return OC_LOADSTR;
		else if (name.equals("loadvar"))
			return OC_LOADVAR;
		else if (name.equals("storevar"))
			return OC_STOREVAR;
		else if (name.equals("addvar"))
			return OC_ADDVAR;
		else if (name.equals("subvar"))
			return OC_SUBVAR;
		else if (name.equals("mulvar"))
			return OC_MULVAR;
		else if (name.equals("truedivvar"))
			return OC_TRUEDIVVAR;
		else if (name.equals("floordivvar"))
			return OC_FLOORDIVVAR;
		else if (name.equals("modvar"))
			return OC_MODVAR;
		else if (name.equals("delvar"))
			return OC_DELVAR;
		else if (name.equals("getattr"))
			return OC_GETATTR;
		else if (name.equals("getitem"))
			return OC_GETITEM;
		else if (name.equals("getslice12"))
			return OC_GETSLICE12;
		else if (name.equals("getslice1"))
			return OC_GETSLICE1;
		else if (name.equals("getslice2"))
			return OC_GETSLICE2;
		else if (name.equals("getslice"))
			return OC_GETSLICE;
		else if (name.equals("print"))
			return OC_PRINT;
		else if (name.equals("for"))
			return OC_FOR;
		else if (name.equals("endfor"))
			return OC_ENDFOR;
		else if (name.equals("not"))
			return OC_NOT;
		else if (name.equals("neg"))
			return OC_NEG;
		else if (name.equals("contains"))
			return OC_CONTAINS;
		else if (name.equals("notcontains"))
			return OC_NOTCONTAINS;
		else if (name.equals("equals"))
			return OC_EQUALS;
		else if (name.equals("notequals"))
			return OC_NOTEQUALS;
		else if (name.equals("add"))
			return OC_ADD;
		else if (name.equals("sub"))
			return OC_SUB;
		else if (name.equals("mul"))
			return OC_MUL;
		else if (name.equals("floordiv"))
			return OC_FLOORDIV;
		else if (name.equals("truediv"))
			return OC_TRUEDIV;
		else if (name.equals("and"))
			return OC_AND;
		else if (name.equals("or"))
			return OC_OR;
		else if (name.equals("mod"))
			return OC_MOD;
		else if (name.equals("callfunc0"))
			return OC_CALLFUNC0;
		else if (name.equals("callfunc1"))
			return OC_CALLFUNC1;
		else if (name.equals("callfunc2"))
			return OC_CALLFUNC2;
		else if (name.equals("callfunc3"))
			return OC_CALLFUNC3;
		else if (name.equals("callmeth0"))
			return OC_CALLMETH0;
		else if (name.equals("callmeth1"))
			return OC_CALLMETH1;
		else if (name.equals("callmeth2"))
			return OC_CALLMETH2;
		else if (name.equals("callmeth3"))
			return OC_CALLMETH3;
		else if (name.equals("if"))
			return OC_IF;
		else if (name.equals("else"))
			return OC_ELSE;
		else if (name.equals("endif"))
			return OC_ENDIF;
		else if (name.equals("render"))
			return OC_RENDER;
		else
			throw new IllegalArgumentException("Opcode name " + name + " unknown!");
	}

	public static String code2name(int code)
	{
		if (code == OC_TEXT)
			return null;
		else if (code == OC_LOADNONE)
			return "loadnone";
		else if (code == OC_LOADFALSE)
			return "loadfalse";
		else if (code == OC_LOADTRUE)
			return "loadtrue";
		else if (code == OC_LOADINT)
			return "loadint";
		else if (code == OC_LOADFLOAT)
			return "loadfloat";
		else if (code == OC_LOADSTR)
			return "loadstr";
		else if (code == OC_LOADVAR)
			return "loadvar";
		else if (code == OC_STOREVAR)
			return "storevar";
		else if (code == OC_ADDVAR)
			return "addvar";
		else if (code == OC_SUBVAR)
			return "subvar";
		else if (code == OC_MULVAR)
			return "mulvar";
		else if (code == OC_TRUEDIVVAR)
			return "truedivvar";
		else if (code == OC_FLOORDIVVAR)
			return "floordivvar";
		else if (code == OC_MODVAR)
			return "modvar";
		else if (code == OC_DELVAR)
			return "delvar";
		else if (code == OC_GETATTR)
			return "getattr";
		else if (code == OC_GETITEM)
			return "getitem";
		else if (code == OC_GETSLICE12)
			return "getslice12";
		else if (code == OC_GETSLICE1)
			return "getslice1";
		else if (code == OC_GETSLICE2)
			return "getslice2";
		else if (code == OC_GETSLICE)
			return "getslice";
		else if (code == OC_PRINT)
			return "print";
		else if (code == OC_FOR)
			return "for";
		else if (code == OC_ENDFOR)
			return "endfor";
		else if (code == OC_NOT)
			return "not";
		else if (code == OC_NEG)
			return "neg";
		else if (code == OC_CONTAINS)
			return "contains";
		else if (code == OC_NOTCONTAINS)
			return "notcontains";
		else if (code == OC_EQUALS)
			return "equals";
		else if (code == OC_NOTEQUALS)
			return "notequals";
		else if (code == OC_ADD)
			return "add";
		else if (code == OC_SUB)
			return "sub";
		else if (code == OC_MUL)
			return "mul";
		else if (code == OC_FLOORDIV)
			return "floordiv";
		else if (code == OC_TRUEDIV)
			return "truediv";
		else if (code == OC_AND)
			return "and";
		else if (code == OC_OR)
			return "or";
		else if (code == OC_MOD)
			return "mod";
		else if (code == OC_CALLFUNC0)
			return "callfunc0";
		else if (code == OC_CALLFUNC1)
			return "callfunc1";
		else if (code == OC_CALLFUNC2)
			return "callfunc2";
		else if (code == OC_CALLFUNC3)
			return "callfunc3";
		else if (code == OC_CALLMETH0)
			return "callmeth0";
		else if (code == OC_CALLMETH1)
			return "callmeth1";
		else if (code == OC_CALLMETH2)
			return "callmeth2";
		else if (code == OC_CALLMETH3)
			return "callmeth3";
		else if (code == OC_IF)
			return "if";
		else if (code == OC_ELSE)
			return "else";
		else if (code == OC_ENDIF)
			return "endif";
		else if (code == OC_RENDER)
			return "render";
		else
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
		this.location = location;
		this.jump = -1;
	}
}