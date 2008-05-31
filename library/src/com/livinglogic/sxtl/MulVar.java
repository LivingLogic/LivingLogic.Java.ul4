package com.livinglogic.sxtl;

public class MulVar extends ChangeVar
{
	public MulVar(int start, int end, Name name, AST value)
	{
		super(start, end, name, value);
	}

	public Opcode.Type getType()
	{
		return Opcode.Type.MULVAR;
	}
}
