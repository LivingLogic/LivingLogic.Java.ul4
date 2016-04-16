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

public class Mul extends Binary
{
	public Mul(InterpretedTemplate template, SourcePart origin, Node obj1, Node obj2)
	{
		super(template, origin, obj1, obj2);
	}

	public Type type()
	{
		Type type1 = obj1.type();
		Type type2 = obj2.type();

		if ((type1 == Type.BOOL || type1 == Type.INT))
		{
			if (type2 == Type.STR)
				return Type.STR;
			else if (type2 == Type.CLOB)
				return Type.CLOB;
			else if (type2 == Type.BOOL || type2 == Type.INT)
				return Type.INT;
		}
		else if (type1 == Type.NUMBER)
		{
			if (type2 == Type.BOOL || type2 == Type.INT || type2 == Type.NUMBER)
				return Type.NUMBER;
		}
		else if (type1 == Type.STR)
		{
			if (type2 == Type.BOOL || type2 == Type.INT)
				return Type.STR;
		}
		else if (type1 == Type.CLOB)
		{
			if (type2 == Type.BOOL || type2 == Type.INT)
				return Type.CLOB;
		}
		throw error("vsql.mul(" + type1 + ", " + type2 + ") not supported!");
	}

	protected void sqlOracle(StringBuilder buffer)
	{
		Type type1 = obj1.type();
		Type type2 = obj2.type();

		if (type1 == Type.BOOL)
		{
			if (type2 == Type.STR || type2 == Type.CLOB)
			{
				buffer.append("(case ");
				obj1.sqlOracle(buffer);
				buffer.append(" when 1 then ");
				obj2.sqlOracle(buffer);
				buffer.append(" else null end)");
			}
			else if (type2 == Type.BOOL || type2 == Type.INT || type2 == Type.NUMBER)
			{
				buffer.append("(");
				obj1.sqlOracle(buffer);
				buffer.append("*");
				obj2.sqlOracle(buffer);
				buffer.append(")");
			}
		}
		else if (type1 == Type.INT)
		{
			if (type2 == Type.STR)
			{
				buffer.append("ul4_pkg.mul_int_str(");
				obj1.sqlOracle(buffer);
				buffer.append(", ");
				obj2.sqlOracle(buffer);
				buffer.append(")");
			}
			else if (type2 == Type.CLOB)
			{
				buffer.append("ul4_pkg.mul_int_clob(");
				obj1.sqlOracle(buffer);
				buffer.append(", ");
				obj2.sqlOracle(buffer);
				buffer.append(")");
			}
			else if (type2 == Type.BOOL || type2 == Type.INT || type2 == Type.NUMBER)
			{
				buffer.append("(");
				obj1.sqlOracle(buffer);
				buffer.append("*");
				obj2.sqlOracle(buffer);
				buffer.append(")");
			}
		}
		else if (type1 == Type.NUMBER)
		{
			if (type2 == Type.BOOL || type2 == Type.INT || type2 == Type.NUMBER)
			{
				buffer.append("(");
				obj1.sqlOracle(buffer);
				buffer.append("*");
				obj2.sqlOracle(buffer);
				buffer.append(")");
			}
		}
		else if (type1 == Type.STR)
		{
			if (type2 == Type.BOOL)
			{
				buffer.append("(case ");
				obj2.sqlOracle(buffer);
				buffer.append(" when 1 then ");
				obj1.sqlOracle(buffer);
				buffer.append(" else null end)");
				buffer.append(")");
			}
			else if (type2 == Type.INT)
			{
				buffer.append("ul4_pkg.mul_int_str(");
				obj2.sqlOracle(buffer);
				buffer.append(", ");
				obj1.sqlOracle(buffer);
				buffer.append(")");
			}
		}
		else if (type1 == Type.CLOB)
		{
			if (type2 == Type.BOOL)
			{
				buffer.append("(case when ");
				obj2.sqlOracle(buffer);
				buffer.append(" != 0 then ");
				obj1.sqlOracle(buffer);
				buffer.append(" else null end)");
				buffer.append(")");
			}
			else if (type2 == Type.INT)
			{
				buffer.append("ul4_pkg.mul_int_clob(");
				obj2.sqlOracle(buffer);
				buffer.append(", ");
				obj1.sqlOracle(buffer);
				buffer.append(")");
			}
		}
		else
			throw error("vsql.mul(" + type1 + ", " + type2 + ") not supported!");
	}

	public static class Function extends Binary.Function
	{
		public String nameUL4()
		{
			return "vsql.add";
		}

		public Object evaluate(BoundArguments args)
		{
			return new Mul((InterpretedTemplate)args.get(3), (SourcePart)args.get(2), (Node)args.get(0), (Node)args.get(1));
		}
	}
}