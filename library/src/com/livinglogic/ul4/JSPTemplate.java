package com.livinglogic.ul4;

import java.util.Map;
import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

/**
 * Copyright 2009 by LivingLogic AG, Bayreuth/Germany
 *
 * All Rights Reserved
 *
 * See LICENSE for the license
 *
 * Base class for template code that has been converted to JSP.
 *
 * @author W. Dörwald
 * @version $Revision$ $Date$
 */

public abstract class JSPTemplate implements Template
{
	public String renders(Map variables)
	{
		StringWriter out = new StringWriter();

		try
		{
			execute(out, variables);
		}
		catch (IOException ex)
		{
			// does not happen!
		}
		String result = out.toString();
		return result;
	}

	public void renderjsp(JspWriter out, Map variables) throws java.io.IOException
	{
		execute(out, variables);
	}

	public abstract void execute(Writer out, Map variables) throws java.io.IOException;
}