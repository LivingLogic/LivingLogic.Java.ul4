package com.livinglogic.ull;

public class TrueDiv extends Binary
{
	public TrueDiv(int start, int end, AST obj1, AST obj2)
	{
		super(start, end, obj1, obj2);
	}

	public int getType()
	{
		return Opcode.OC_TRUEDIV;
	}
}