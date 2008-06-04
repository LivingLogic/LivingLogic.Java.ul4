package com.livinglogic.ull;

public class FloorDiv extends Binary
{
	public FloorDiv(int start, int end, AST obj1, AST obj2)
	{
		super(start, end, obj1, obj2);
	}

	public int getType()
	{
		return Opcode.OC_FLOORDIV;
	}
}