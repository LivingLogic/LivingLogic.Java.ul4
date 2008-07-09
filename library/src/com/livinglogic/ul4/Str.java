package com.livinglogic.ul4;

public class Str extends Const
{
	protected String value;

	public Str(int start, int end, String value)
	{
		super(start, end);
		this.value = value;
	}

	public int getType()
	{
		return Opcode.OC_LOADSTR;
	}

	public String getTokenType()
	{
		return "str";
	}

	public Object getValue()
	{
		return value;
	}

	public int compile(Template template, Registers registers, Location location)
	{
		int r = registers.alloc();
		template.opcode(Opcode.OC_LOADSTR, r, value, location);
		return r;
	}

	public String toString()
	{
		return "string \"" + value.replaceAll("\"", "\\\\\"") + "\"";
	}
}
