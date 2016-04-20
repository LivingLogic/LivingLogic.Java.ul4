/*
** Copyright 2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.Set;

import com.livinglogic.ul4.InterpretedTemplate;
import com.livinglogic.ul4.SourcePart;
import com.livinglogic.ul4.BoundMethod;
import com.livinglogic.ul4.Signature;
import com.livinglogic.ul4.UL4Attributes;
import com.livinglogic.ul4.UL4GetItemString;
import com.livinglogic.ul4.UL4Repr;
import com.livinglogic.ul4.BoundArguments;
import com.livinglogic.ul4.ArgumentTypeMismatchException;
import com.livinglogic.ul4.UndefinedKey;
import com.livinglogic.ul4.Utils;
import com.livinglogic.ul4.FunctionStr;

public class Add extends Binary
{
	public Add(InterpretedTemplate template, SourcePart origin, Node obj1, Node obj2)
	{
		super(template, origin, obj1, obj2);
	}

	protected SQLSnippet sqlOracle()
	{
		SQLSnippet snippet1 = obj1.sqlOracle();
		SQLSnippet snippet2 = obj2.sqlOracle();

		switch (snippet1.type)
		{
			case NULL:
				complain(snippet1, snippet2);
			case BOOL:
			case INT:
				switch (snippet2.type)
				{
					case BOOL:
					case INT:
						return new SQLSnippet(Type.INT, "(", snippet1, "+", snippet2, ")");
					case NUMBER:
						return new SQLSnippet(Type.NUMBER, "(", snippet1, "+", snippet2, ")");
					default:
						complain(snippet1, snippet2);
				}
			case NUMBER:
				switch (snippet2.type)
				{
					case BOOL:
					case INT:
					case NUMBER:
						return new SQLSnippet(Type.NUMBER, "(", snippet1, "+", snippet2, ")");
					default:
						complain(snippet1, snippet2);
				}
			case DATE:
				switch (snippet2.type)
				{
					case DAYDELTA:
						return new SQLSnippet(Type.DATE, "(", snippet1, "+", snippet2, ")");
					case TIMEDELTA:
						return new SQLSnippet(Type.DATETIME, "(", snippet1, "+", snippet2, ")");
					case TIMESTAMPDELTA:
						return new SQLSnippet(Type.TIMESTAMP, "(cast(", snippet1, " as timestamp)+", snippet2, ")");
					case MONTHDELTA:
						return new SQLSnippet(snippet1.type, "add_months(", snippet1, ", ", snippet2, ")");
					default:
						complain(snippet1, snippet2);
				}
			case DATETIME:
				switch (snippet2.type)
				{
					case DAYDELTA:
						return new SQLSnippet(Type.DATETIME, "(", snippet1, "+", snippet2, ")");
					case TIMEDELTA:
						return new SQLSnippet(Type.DATETIME, "(", snippet1, "+", snippet2, ")");
					case TIMESTAMPDELTA:
						return new SQLSnippet(Type.TIMESTAMP, "(cast(", snippet1, " as timestamp)+", snippet2, ")");
					case MONTHDELTA:
						return new SQLSnippet(snippet1.type, "add_months(", snippet1, ", ", snippet2, ")");
					default:
						complain(snippet1, snippet2);
				}
			case TIMESTAMP:
				switch (snippet2.type)
				{
					case DAYDELTA:
					case TIMEDELTA:
					case TIMESTAMPDELTA:
						return new SQLSnippet(Type.TIMESTAMP, "(", snippet1, "+", snippet2, ")");
					case MONTHDELTA:
						return new SQLSnippet(snippet1.type, "add_months(", snippet1, ", ", snippet2, ")");
					default:
						complain(snippet1, snippet2);
				}
			case DAYDELTA:
				switch (snippet2.type)
				{
					case DAYDELTA:
						return new SQLSnippet(Type.DAYDELTA, "(", snippet1, "+", snippet2, ")");
					case TIMEDELTA:
						return new SQLSnippet(Type.TIMEDELTA, "(", snippet1, "+", snippet2, ")");
					case TIMESTAMPDELTA:
						return new SQLSnippet(Type.TIMESTAMPDELTA, "(", snippet1, "+", snippet2, ")");
					default:
						complain(snippet1, snippet2);
				}
			case TIMEDELTA:
				switch (snippet2.type)
				{
					case DAYDELTA:
					case TIMEDELTA:
						return new SQLSnippet(Type.TIMEDELTA, "(", snippet1, "+", snippet2, ")");
					case TIMESTAMPDELTA:
						return new SQLSnippet(Type.TIMESTAMPDELTA, "(", snippet1, "+", snippet2, ")");
					default:
						complain(snippet1, snippet2);
				}
			case TIMESTAMPDELTA:
				switch (snippet2.type)
				{
					case DAYDELTA:
					case TIMEDELTA:
					case TIMESTAMPDELTA:
						return new SQLSnippet(Type.TIMESTAMPDELTA, "(", snippet1, "+", snippet2, ")");
					default:
						complain(snippet1, snippet2);
				}
			case MONTHDELTA:
				switch (snippet2.type)
				{
					case MONTHDELTA:
						return new SQLSnippet(snippet1.type, "(", snippet1, "+", snippet2, ")");
					default:
						complain(snippet1, snippet2);
				}
			case STR:
				switch (snippet2.type)
				{
					case STR:
						return new SQLSnippet(Type.STR, "(", snippet1, "||", snippet2, ")");
					case CLOB:
						return new SQLSnippet(Type.CLOB, "(", snippet1, "||", snippet2, ")");
					default:
						complain(snippet1, snippet2);
				}
			case CLOB:
				switch (snippet2.type)
				{
					case STR:
						return new SQLSnippet(Type.CLOB, "(", snippet1, "||", snippet2, ")");
					case CLOB:
						return new SQLSnippet(Type.CLOB, "(", snippet1, "||", snippet2, ")");
					default:
						complain(snippet1, snippet2);
				}
		}
		return null;
	}

	private void complain(SQLSnippet snippet1, SQLSnippet snippet2)
	{
		throw error("vsql.add({}, {}) not supported!", snippet1.type, snippet2.type);
	}

	public static class Function extends Binary.Function
	{
		public String nameUL4()
		{
			return "vsql.add";
		}

		public Object evaluate(BoundArguments args)
		{
			return new Add((InterpretedTemplate)args.get(3), (SourcePart)args.get(2), (Node)args.get(0), (Node)args.get(1));
		}
	}
}
