package com.livinglogic.sxtl;

public class Main
{
	public static void main(String[] args)
	{
		CompilerFactory factory = new CompilerFactory();
		String bytecode = factory.compile("<?for i in data?><?print i?><?end for?>");
		System.out.println(bytecode);
	}
}
