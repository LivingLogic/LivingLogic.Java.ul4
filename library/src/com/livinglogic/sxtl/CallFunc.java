package com.livinglogic.sxtl;

public class CallFunc extends AST
{
	protected Name name;
	protected AST arg1;
	protected AST arg2;
	protected AST arg3;
	protected int argcount;

	public CallFunc(int start, int end, Name name, AST arg1, AST arg2, AST arg3)
	{
		super(start, end);
		this.name = name;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.arg3 = arg3;
		this.argcount = 3;
	}

	public CallFunc(int start, int end, Name name, AST arg1, AST arg2)
	{
		super(start, end);
		this.name = name;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.arg3 = null;
		this.argcount = 2;
	}

	public CallFunc(int start, int end, Name name, AST arg1)
	{
		super(start, end);
		this.name = name;
		this.arg1 = arg1;
		this.arg2 = null;
		this.arg3 = null;
		this.argcount = 1;
	}

	public CallFunc(int start, int end, Name name)
	{
		super(start, end);
		this.name = name;
		this.arg1 = null;
		this.arg2 = null;
		this.arg3 = null;
		this.argcount = 0;
	}

	private static final Opcode.Type[] opcodes = {Opcode.Type.CALLFUNC0, Opcode.Type.CALLFUNC1, Opcode.Type.CALLFUNC2, Opcode.Type.CALLFUNC3};

	public int compile(Template template, Registers registers, Location location)
	{
		int r1 = arg1 != null ? arg1.compile(template, registers, location) : -1;
		int r2 = arg2 != null ? arg2.compile(template, registers, location) : -1;
		int r3 = arg3 != null ? arg3.compile(template, registers, location) : -1;
		int rr = argcount > 0 ? r1 : registers.alloc();
		template.opcode(opcodes[argcount], rr, r1, r2, r3, name.value, location);
		if (r2 != -1)
			registers.free(r2);
		if (r3 != -1)
			registers.free(r3);
		return rr;
	}
}
