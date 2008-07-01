package com.livinglogic.ul4;

import java.util.Date;
import java.util.HashMap;

public class Main
{
	public static long time()
	{
		return new Date().getTime();
	}

	public static void main(String[] args)
	{
		Template tmpl = Compiler.compile("<?print now().isoformat()?>");
		long start = new Date().getTime();
		String output = tmpl.renders("<gu&rk> & 'foo'");
		System.out.println("rendered " + (time()-start));
		System.out.println(output);
	}
}
