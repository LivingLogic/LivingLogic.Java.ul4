package tests;

import static com.livinglogic.ul4on.Utils.dumps;
import static com.livinglogic.utils.MapUtils.makeMap;
import static com.livinglogic.utils.SetUtils.makeSet;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.livinglogic.ul4.ArgumentCountMismatchException;
import com.livinglogic.ul4.ArgumentTypeMismatchException;
import com.livinglogic.ul4.BlockException;
import com.livinglogic.ul4.Color;
import com.livinglogic.ul4.FunctionDate;
import com.livinglogic.ul4.InterpretedTemplate;
import com.livinglogic.ul4.MissingArgumentException;
import com.livinglogic.ul4.MonthDelta;
import com.livinglogic.ul4.SyntaxException;
import com.livinglogic.ul4.TimeDelta;
import com.livinglogic.ul4.TooManyArgumentsException;
import com.livinglogic.ul4.UL4Attributes;
import com.livinglogic.ul4.UndefinedKey;


@RunWith(CauseTestRunner.class)
public class UL4Test
{
	private static class Point implements UL4Attributes
	{
		int x;
		int y;

		Point(int x, int y)
		{
			this.x = x;
			this.y = y;
		}

		public Set<String> getAttributeNamesUL4()
		{
			return makeSet("x", "y");
		}

		public Object getItemStringUL4(String key)
		{
			if ("x".equals(key))
				return x;
			else if ("y".equals(key))
				return y;
			return new UndefinedKey(key);
		}
	}

	private static InterpretedTemplate getTemplate(String source, String name, boolean keepWhitespace)
	{
		try
		{
			InterpretedTemplate template = new InterpretedTemplate(source, name, keepWhitespace);
			// System.out.println(template);
			return template;
		}
		catch (RecognitionException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	private static InterpretedTemplate getTemplate(String source, boolean keepWhitespace)
	{
		return getTemplate(source, null, keepWhitespace);
	}

	private static InterpretedTemplate getTemplate(String source, String name)
	{
		return getTemplate(source, name, false);
	}

	private static InterpretedTemplate getTemplate(String source)
	{
		return getTemplate(source, null, false);
	}

	private static String getTemplateOutput(String source, Object... args)
	{
		InterpretedTemplate template = getTemplate(source);
		return template.renders(makeMap(args));
	}

	private static void checkTemplateOutput(String expected, String source, Object... args)
	{
		// Render the template once by directly compiling and rendering it
		InterpretedTemplate template1 = getTemplate(source);
		String output1 = template1.renders(makeMap(args));
		assertEquals(expected, output1);

		// Recreate the template from the dump of the compiled template
		InterpretedTemplate template2 = InterpretedTemplate.loads(template1.dumps());

		// Check that the templates format the same
		assertEquals(template1.toString(), template2.toString());

		// Check that they have the same output
		String output2 = template2.renders(makeMap(args));
		assertEquals(expected, output2);
	}

	private static void checkTemplateOutput2(String expected1, String expected2, String source, Object... args)
	{
		// Render the template once by directly compiling and rendering it
		InterpretedTemplate template1 = getTemplate(source);
		String output1 = template1.renders(makeMap(args));
		if (!output1.equals(expected1) && !output1.equals(expected2))
			fail("expected <" + expected1 + "> or <" + expected2 + ">, got <" + output1 + ">");

		// Recreate the template from the dump of the compiled template
		InterpretedTemplate template2 = InterpretedTemplate.loads(template1.dumps());

		// Check that the templates format the same
		assertEquals(template1.toString(), template2.toString());

		// Check that theyhave the same output
		String output2 = template2.renders(makeMap(args));
		if (!output1.equals(expected1) && !output1.equals(expected2))
			fail("expected <" + expected1 + "> or <" + expected2 + ">, got <" + output2 + ">");
	}

	private static Object getTemplateResult(String source, Object... args)
	{
		InterpretedTemplate template = getTemplate(source);
		return template.call(makeMap(args));
	}

	private static void checkTemplateResult(Object expected, String source, Object... args)
	{
		// Execute the template once by directly compiling and calling it
		InterpretedTemplate template1 = getTemplate(source);
		Object output1 = template1.call(makeMap(args));
		assertEquals(expected, output1);

		// Recreate the template from the dump of the compiled template
		InterpretedTemplate template2 = InterpretedTemplate.loads(template1.dumps());

		// Check that the templates format the same
		assertEquals(template1.toString(), template2.toString());

		// Check that they have the same output
		Object output2 = template2.call(makeMap(args));
		assertEquals(expected, output2);
	}

	@Test
	public void tag_text()
	{
		checkTemplateOutput("gurk", "gurk");
		checkTemplateOutput("g\tu rk", "g\t\n\t u \n  r\n\t\tk");
	}

	@Test
	public void whitespace()
	{
		checkTemplateOutput("40", "<?print\na\n+\nb\n?>", "a", 17, "b", 23);
	}

	@Test
	public void type_none()
	{
		checkTemplateOutput("no", "<?if None?>yes<?else?>no<?end if?>");
		checkTemplateOutput("", "<?print None?>");
	}

	@Test
	public void type_bool()
	{
		checkTemplateOutput("False", "<?print False?>");
		checkTemplateOutput("no", "<?if False?>yes<?else?>no<?end if?>");
		checkTemplateOutput("True", "<?print True?>");
		checkTemplateOutput("yes", "<?if True?>yes<?else?>no<?end if?>");
	}

	@Test
	public void type_int()
	{
		checkTemplateOutput("0", "<?print 0?>");
		checkTemplateOutput("42", "<?print 42?>");
		checkTemplateOutput("-42", "<?print -42?>");
		checkTemplateOutput("134217727", "<?print 134217727?>");
		checkTemplateOutput("134217728", "<?print 134217728?>");
		checkTemplateOutput("-134217728", "<?print -134217728?>");
		checkTemplateOutput("-134217729", "<?print -134217729?>");
		checkTemplateOutput("576460752303423487", "<?print 576460752303423487?>");
		checkTemplateOutput("576460752303423488", "<?print 576460752303423488?>");
		checkTemplateOutput("-576460752303423488", "<?print -576460752303423488?>");
		checkTemplateOutput("-576460752303423489", "<?print -576460752303423489?>");
		checkTemplateOutput("9999999999", "<?print 9999999999?>");
		checkTemplateOutput("-9999999999", "<?print -9999999999?>");
		checkTemplateOutput("99999999999999999999", "<?print 99999999999999999999?>");
		checkTemplateOutput("-99999999999999999999", "<?print -99999999999999999999?>");
		checkTemplateOutput("255", "<?print 0xff?>");
		checkTemplateOutput("255", "<?print 0Xff?>");
		checkTemplateOutput("-255", "<?print -0xff?>");
		checkTemplateOutput("-255", "<?print -0Xff?>");
		checkTemplateOutput("63", "<?print 0o77?>");
		checkTemplateOutput("63", "<?print 0O77?>");
		checkTemplateOutput("-63", "<?print -0o77?>");
		checkTemplateOutput("-63", "<?print -0O77?>");
		checkTemplateOutput("7", "<?print 0b111?>");
		checkTemplateOutput("7", "<?print 0B111?>");
		checkTemplateOutput("-7", "<?print -0b111?>");
		checkTemplateOutput("-7", "<?print -0B111?>");
		checkTemplateOutput("no", "<?if 0?>yes<?else?>no<?end if?>");
		checkTemplateOutput("yes", "<?if 1?>yes<?else?>no<?end if?>");
		checkTemplateOutput("yes", "<?if -1?>yes<?else?>no<?end if?>");
	}

	@Test
	public void type_float()
	{
		checkTemplateOutput("0.0", "<?print 0.?>");
		checkTemplateOutput("42.0", "<?print 42.?>");
		checkTemplateOutput("-42.0", "<?print -42.?>");
		checkTemplateOutput("-42.5", "<?print -42.5?>");
		checkTemplateOutput("1e42", "<?print 1E42?>");
		checkTemplateOutput("1e42", "<?print 1e42?>");
		checkTemplateOutput("-1e42", "<?print -1E42?>");
		checkTemplateOutput("-1e42", "<?print -1e42?>");
		checkTemplateOutput("no", "<?if 0.?>yes<?else?>no<?end if?>");
		checkTemplateOutput("yes", "<?if 1.?>yes<?else?>no<?end if?>");
		checkTemplateOutput("yes", "<?if -1.?>yes<?else?>no<?end if?>");
	}

	@Test
	public void type_string()
	{
		checkTemplateOutput("foo", "<?print \"foo\"?>");
		checkTemplateOutput("\n", "<?print \"\\n\"?>");
		checkTemplateOutput("\r", "<?print \"\\r\"?>");
		checkTemplateOutput("\t", "<?print \"\\t\"?>");
		checkTemplateOutput("\f", "<?print \"\\f\"?>");
		checkTemplateOutput("\u0008", "<?print \"\\b\"?>");
		checkTemplateOutput("\u0007", "<?print \"\\a\"?>");
		checkTemplateOutput("\u0000", "<?print \"\\x00\"?>");
		checkTemplateOutput("\"", "<?print \"\\\"\"?>");
		checkTemplateOutput("'", "<?print \"\\'\"?>");
		checkTemplateOutput("\u20ac", "<?print \"\u20ac\"?>");
		checkTemplateOutput("\u00ff", "<?print \"\\xff\"?>");
		checkTemplateOutput("\u20ac", "<?print \"\\u20ac\"?>");
		checkTemplateOutput("gu\trk", "<?print 'gu\trk'?>");
		checkTemplateOutput("gu\n\r\t\\rk", "<?print 'gu\\n\\r\\t\\\\rk'?>");
		checkTemplateOutput("gu\r\nrk", "<?print '''gu\r\nrk'''?>");
		checkTemplateOutput("gu\r\nrk", "<?print \"\"\"gu\r\nrk\"\"\"?>");
		checkTemplateOutput("gu\r\nrk", "<?print str('''gu\r\nrk''')?>");
		checkTemplateOutput("gu\r\nrk", "<?print str('''gu\\r\\nrk''')?>");
		checkTemplateOutput("no", "<?if ''?>yes<?else?>no<?end if?>");
		checkTemplateOutput("yes", "<?if 'foo'?>yes<?else?>no<?end if?>");
	}

	@Test
	public void type_date()
	{
		checkTemplateOutput("2000-02-29", "<?print @(2000-02-29).isoformat()?>");
		checkTemplateOutput("2000-02-29", "<?print @(2000-02-29T).isoformat()?>");
		checkTemplateOutput("2000-02-29T12:34:00", "<?print @(2000-02-29T12:34).isoformat()?>");
		checkTemplateOutput("2000-02-29T12:34:56", "<?print @(2000-02-29T12:34:56).isoformat()?>");
		checkTemplateOutput("2000-02-29T12:34:56.987000", "<?print @(2000-02-29T12:34:56.987000).isoformat()?>");
		checkTemplateOutput("yes", "<?if @(2000-02-29T12:34:56.987654)?>yes<?else?>no<?end if?>");
	}

	@Test
	public void type_color()
	{
		checkTemplateOutput("255,255,255,255", "<?code c = #fff?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>");
		checkTemplateOutput("255,255,255,255", "<?code c = #ffffff?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>");
		checkTemplateOutput("18,52,86,255", "<?code c = #123456?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>");
		checkTemplateOutput("17,34,51,68", "<?code c = #1234?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>");
		checkTemplateOutput("18,52,86,120", "<?code c = #12345678?><?print c[0]?>,<?print c[1]?>,<?print c[2]?>,<?print c[3]?>");
		checkTemplateOutput("yes", "<?if #fff?>yes<?else?>no<?end if?>");
	}

	@Test
	public void type_list()
	{
		checkTemplateOutput("", "<?for item in []?><?print item?>;<?end for?>");
		checkTemplateOutput("1;", "<?for item in [1]?><?print item?>;<?end for?>");
		checkTemplateOutput("1;", "<?for item in [1,]?><?print item?>;<?end for?>");
		checkTemplateOutput("1;2;", "<?for item in [1, 2]?><?print item?>;<?end for?>");
		checkTemplateOutput("1;2;", "<?for item in [1, 2,]?><?print item?>;<?end for?>");
		checkTemplateOutput("no", "<?if []?>yes<?else?>no<?end if?>");
		checkTemplateOutput("yes", "<?if [1]?>yes<?else?>no<?end if?>");
	}

	@Test
	public void type_listcomprehension()
	{
		checkTemplateOutput("[2, 6]", "<?code d = [2*i for i in range(4) if i%2]?><?print d?>");
		checkTemplateOutput("[0, 2, 4, 6]", "<?code d = [2*i for i in range(4)]?><?print d?>");
	}

	@Test
	public void type_dict()
	{
		checkTemplateOutput("", "<?for (key, value) in {}.items()?><?print key?>:<?print value?>!<?end for?>");
		checkTemplateOutput("1:2!", "<?for (key, value) in {1:2}.items()?><?print key?>:<?print value?>!<?end for?>");
		checkTemplateOutput("1:2!", "<?for (key, value) in {1:2,}.items()?><?print key?>:<?print value?>!<?end for?>");
		// With duplicate keys, later ones simply overwrite earlier ones
		checkTemplateOutput("1:3!", "<?for (key, value) in {1:2, 1: 3}.items()?><?print key?>:<?print value?>!<?end for?>");
		// Test **
		checkTemplateOutput("1:2!", "<?for (key, value) in {**{1:2}}.items()?><?print key?>:<?print value?>!<?end for?>");
		checkTemplateOutput("1:4!", "<?for (key, value) in {1:1, **{1:2}, 1:3, **{1:4}}.items()?><?print key?>:<?print value?>!<?end for?>");
		checkTemplateOutput("no", "<?if {}?>yes<?else?>no<?end if?>");
		checkTemplateOutput("yes", "<?if {1:2}?>yes<?else?>no<?end if?>");
	}

	@Test
	public void type_dictcomprehension()
	{
		checkTemplateOutput("", "<?code d = {i:2*i for i in range(10) if i%2}?><?if 2 in d?><?print d[2]?><?end if?>");
		checkTemplateOutput("6", "<?code d = {i:2*i for i in range(10) if i%2}?><?if 3 in d?><?print d[3]?><?end if?>");
		checkTemplateOutput("6", "<?code d = {i:2*i for i in range(10)}?><?print d[3]?>");
	}

	@Test
	public void generatorexpression()
	{
		checkTemplateOutput("2, 6", "<?code ge = (str(2*i) for i in range(4) if i%2)?><?print ', '.join(ge)?>");
		checkTemplateOutput("2, 6", "<?print ', '.join(str(2*i) for i in range(4) if i%2)?>");
		checkTemplateOutput("0, 2, 4, 6", "<?print ', '.join(str(2*i) for i in range(4))?>");
		checkTemplateOutput("0, 2, 4, 6", "<?print ', '.join((str(2*i) for i in range(4)))?>");
	}

	@Test
	public void tag_storevar()
	{
		// checkTemplateOutput("42", "<?code x = 42?><?print x?>");
		// checkTemplateOutput("xyzzy", "<?code x = 'xyzzy'?><?print x?>");
		checkTemplateOutput("42", "<?code (x,) = [42]?><?print x?>");
		// checkTemplateOutput("17,23", "<?code (x,y) = [17, 23]?><?print x?>,<?print y?>");
		// checkTemplateOutput("17,23,37,42,105", "<?code ((v, w), (x,), (y,), z) = [[17, 23], [37], [42], 105]?><?print v?>,<?print w?>,<?print x?>,<?print y?>,<?print z?>");
	}

	@Test
	public void tag_addvar()
	{
		String source = "<?code x += y?><?print x?>";
		checkTemplateOutput("40", source, "x", 17, "y", 23);
		checkTemplateOutput("40.0", source, "x", 17, "y", 23.0);
		checkTemplateOutput("40.0", source, "x", 17.0, "y", 23);
		checkTemplateOutput("40.0", source, "x", 17.0, "y", 23.0);
		checkTemplateOutput("17", source, "x", 17, "y", false);
		checkTemplateOutput("18", source, "x", 17, "y", true);
		checkTemplateOutput("23", source, "x", false, "y", 23);
		checkTemplateOutput("24", source, "x", true, "y", 23);
	}

	@Test
	public void tag_subvar()
	{
		String source = "<?code x -= y?><?print x?>";
		checkTemplateOutput("-6", source, "x", 17, "y", 23);
		checkTemplateOutput("-6.0", source, "x", 17, "y", 23.0);
		checkTemplateOutput("-6.0", source, "x", 17.0, "y", 23);
		checkTemplateOutput("-6.0", source, "x", 17.0, "y", 23.0);
		checkTemplateOutput("17", source, "x", 17, "y", false);
		checkTemplateOutput("16", source, "x", 17, "y", true);
		checkTemplateOutput("-23", source, "x", false, "y", 23);
		checkTemplateOutput("-22", source, "x", true, "y", 23);
	}

	@Test
	public void tag_mulvar()
	{
		String source = "<?code x *= y?><?print x?>";
		checkTemplateOutput("391", source, "x", 17, "y", 23);
		checkTemplateOutput("391.0", source, "x", 17, "y", 23.0);
		checkTemplateOutput("391.0", source, "x", 17.0, "y", 23);
		checkTemplateOutput("391.0", source, "x", 17.0, "y", 23.0);
		checkTemplateOutput("0", source, "x", 17, "y", false);
		checkTemplateOutput("17", source, "x", 17, "y", true);
		checkTemplateOutput("0", source, "x", false, "y", 23);
		checkTemplateOutput("23", source, "x", true, "y", 23);
		checkTemplateOutput("xyzzyxyzzyxyzzy", source, "x", 3, "y", "xyzzy");
		checkTemplateOutput("", source, "x", false, "y", "xyzzy");
		checkTemplateOutput("xyzzy", source, "x", true, "y", "xyzzy");
		checkTemplateOutput("xyzzyxyzzyxyzzy", source, "x", "xyzzy", "y", 3);
		checkTemplateOutput("", source, "x", "xyzzy", "y", false);
		checkTemplateOutput("xyzzy", source, "x", "xyzzy", "y", true);
	}

	@Test
	public void tag_floordivvar()
	{
		String source = "<?code x //= y?><?print x?>";
		checkTemplateOutput("2", source, "x", 5, "y", 2);
		checkTemplateOutput("-3", source, "x", 5, "y", -2);
		checkTemplateOutput("-3", source, "x", -5, "y", 2);
		checkTemplateOutput("2", source, "x", -5, "y", -2);
		checkTemplateOutput("2.0", source, "x", 5., "y", 2.);
		checkTemplateOutput("-3.0", source, "x", 5., "y", -2.);
		checkTemplateOutput("-3.0", source, "x", -5., "y", 2.);
		checkTemplateOutput("2.0", source, "x", -5., "y", -2.);
		checkTemplateOutput("1", source, "x", true, "y", 1);
		checkTemplateOutput("0", source, "x", false, "y", 1);
	}

	@Test
	public void tag_truedivvar()
	{
		String source = "<?code x /= y?><?print x?>";
		checkTemplateOutput("2.5", source, "x", 5, "y", 2);
		checkTemplateOutput("-2.5", source, "x", 5, "y", -2);
		checkTemplateOutput("-2.5", source, "x", -5, "y", 2);
		checkTemplateOutput("2.5", source, "x", -5, "y", -2);
		checkTemplateOutput("2.5", source, "x", 5., "y", 2.);
		checkTemplateOutput("-2.5", source, "x", 5., "y", -2.);
		checkTemplateOutput("-2.5", source, "x", -5., "y", 2.);
		checkTemplateOutput("2.5", source, "x", -5., "y", -2.);
		checkTemplateOutput("1.0", source, "x", true, "y", 1);
		checkTemplateOutput("0.0", source, "x", false, "y", 1);
	}


	@Test
	public void tag_modvar()
	{
		String source = "<?code x %= y?><?print x?>";
		checkTemplateOutput("4", source, "x", 1729, "y", 23);
		checkTemplateOutput("19", source, "x", -1729, "y", 23);
		checkTemplateOutput("19", source, "x", -1729, "y", 23);
		checkTemplateOutput("-4", source, "x", -1729, "y", -23);
		checkTemplateOutput("1.5", source, "x", 6.5, "y", 2.5);
		checkTemplateOutput("1.0", source, "x", -6.5, "y", 2.5);
		checkTemplateOutput("-1.0", source, "x", 6.5, "y", -2.5);
		checkTemplateOutput("-1.5", source, "x", -6.5, "y", -2.5);
		checkTemplateOutput("1", source, "x", true, "y", 23);
		checkTemplateOutput("0", source, "x", false, "y", 23);
	}

	@Test
	public void tag_for_string()
	{
		String source = "<?for c in data?>(<?print c?>)<?end for?>";
		checkTemplateOutput("", source, "data", "");
		checkTemplateOutput("(g)(u)(r)(k)", source, "data", "gurk");
	}

	@Test
	public void tag_for_list()
	{
		String source = "<?for c in data?>(<?print c?>)<?end for?>";
		checkTemplateOutput("", source, "data", asList());
		checkTemplateOutput("(g)(u)(r)(k)", source, "data", asList("g", "u", "r", "k"));
	}

	@Test
	public void tag_for_dict()
	{
		String source = "<?for c in sorted(data)?>(<?print c?>)<?end for?>";
		checkTemplateOutput("", source, "data", makeMap());
		checkTemplateOutput("(a)(b)(c)", source, "data", makeMap("a", 1, "b", 2, "c", 3));
	}

	@Test
	public void tag_for_nested_loop()
	{
		String source = "<?for list in data?>[<?for n in list?>(<?print n?>)<?end for?>]<?end for?>";
		checkTemplateOutput("[(1)(2)][(3)(4)]", source, "data", asList(asList(1, 2), asList(3, 4)));
	}

	@Test
	public void tag_for_unpacking()
	{
		Object data1 = asList(
			asList("spam"),
			asList("gurk"),
			asList("hinz")
		);

		Object data2 = asList(
			asList("spam", "eggs"),
			asList("gurk", "hurz"),
			asList("hinz", "kunz")
		);

		Object data3 = asList(
			asList("spam", "eggs", 17),
			asList("gurk", "hurz", 23),
			asList("hinz", "kunz", 42)
		);

		Object data4 = asList(
			asList("spam", "eggs", 17, null),
			asList("gurk", "hurz", 23, false),
			asList("hinz", "kunz", 42, true)
		);

		checkTemplateOutput("(spam)(gurk)(hinz)", "<?for (a,) in data?>(<?print a?>)<?end for?>", "data", data1);
		checkTemplateOutput("(spam,eggs)(gurk,hurz)(hinz,kunz)", "<?for (a, b) in data?>(<?print a?>,<?print b?>)<?end for?>", "data", data2);
		checkTemplateOutput("(spam,eggs,17)(gurk,hurz,23)(hinz,kunz,42)", "<?for (a, b, c) in data?>(<?print a?>,<?print b?>,<?print c?>)<?end for?>", "data", data3);
		checkTemplateOutput("(spam,eggs,17,)(gurk,hurz,23,False)(hinz,kunz,42,True)", "<?for (a, b, c, d) in data?>(<?print a?>,<?print b?>,<?print c?>,<?print d?>)<?end for?>", "data", data4);
	}

	@Test
	public void tag_for_nested_unpacking()
	{
		Object data = asList(
			asList(asList("spam", "eggs"), asList(17), null),
			asList(asList("gurk", "hurz"), asList(23), false),
			asList(asList("hinz", "kunz"), asList(42), true)
		);

		checkTemplateOutput("(spam,eggs,17,)(gurk,hurz,23,False)(hinz,kunz,42,True)", "<?for ((a, b), (c,), d) in data?>(<?print a?>,<?print b?>,<?print c?>,<?print d?>)<?end for?>", "data", data);
	}

	@Test
	public void tag_break()
	{
		checkTemplateOutput("1, 2, ", "<?for i in [1,2,3]?><?print i?>, <?if i==2?><?break?><?end if?><?end for?>");
	}

	@Test
	public void tag_break_nested()
	{
		checkTemplateOutput("1, 1, 2, 1, 2, 3, ", "<?for i in [1,2,3,4]?><?for j in [1,2,3,4]?><?print j?>, <?if j>=i?><?break?><?end if?><?end for?><?if i>=3?><?break?><?end if?><?end for?>");
	}

	@CauseTest(expectedCause=BlockException.class)
	public void tag_break_outside_loop()
	{
		checkTemplateOutput("", "<?break?>");
	}

	@CauseTest(expectedCause=BlockException.class)
	public void tag_break_outside_loop_in_template()
	{
		checkTemplateOutput("", "<?def gurk?><?break?><?end def?>");
	}

	@Test
	public void tag_continue()
	{
		checkTemplateOutput("1, 3, ", "<?for i in [1,2,3]?><?if i==2?><?continue?><?end if?><?print i?>, <?end for?>");
	}

	@Test
	public void tag_continue_nested()
	{
		checkTemplateOutput("1, 3, !1, 3, !", "<?for i in [1,2,3]?><?if i==2?><?continue?><?end if?><?for j in [1,2,3]?><?if j==2?><?continue?><?end if?><?print j?>, <?end for?>!<?end for?>");
	}

	@CauseTest(expectedCause=BlockException.class)
	public void tag_continue_outside_loop()
	{
		checkTemplateOutput("", "<?continue?>");
	}

	@CauseTest(expectedCause=BlockException.class)
	public void tag_continue_outside_loop_in_template()
	{
		checkTemplateOutput("", "<?def gurk?><?continue?><?end def?>");
	}

	@Test
	public void tag_if()
	{
		checkTemplateOutput("42", "<?if data?><?print data?><?end if?>", "data", 42);
	}

	@Test
	public void tag_else()
	{
		String source = "<?if data?><?print data?><?else?>no<?end if?>";
		checkTemplateOutput("42", source, "data", 42);
		checkTemplateOutput("no", source, "data", 0);
	}

	// // FIXME: Doesn't work, because of chained exceptions, needs to be split into n tests
	// // @Test(expected=BlockException)
	// // public void block_errors()
	// // {
	// // 	checkTemplateOutput("", "<?for x in data?>"); // "BlockError: block unclosed"
	// // 	checkTemplateOutput("", "<?for x in data?><?end if?>"); // "BlockError: endif doesn't match any if"
	// // 	checkTemplateOutput("", "<?end?>"); // "BlockError: not in any block"
	// // 	checkTemplateOutput("", "<?end for?>"); // "BlockError: not in any block"
	// // 	checkTemplateOutput("", "<?end if?>"); // "BlockError: not in any block"
	// // 	checkTemplateOutput("", "<?else?>"); // "BlockError: else doesn't match any if"
	// // 	checkTemplateOutput("", "<?if data?>"); // "BlockError: block unclosed"
	// // 	checkTemplateOutput("", "<?if data?><?else?>"); // "BlockError: block unclosed"
	// // 	checkTemplateOutput("", "<?if data?><?else?><?else?>"); // "BlockError: duplicate else"
	// // 	checkTemplateOutput("", "<?if data?><?else?><?elif data?>"); // "BlockError: else already seen in elif"
	// // 	checkTemplateOutput("", "<?if data?><?elif data?><?elif data?><?else?><?elif data?>"); // "BlockError: else already seen in elif"
	// // }


	// // FIXME: Doesn't work, because of chained exceptions, needs to be split into n tests
	// // @Test(expected=BlockException)
	// // public void empty()
	// // {
	// // 	checkTemplateOutput("", "<?print?>"); // "expression required"
	// // 	checkTemplateOutput("", "<?if?>"); // "expression required"
	// // 	checkTemplateOutput("", "<?if x?><?elif?><?end if?>"); // "expression required"
	// // 	checkTemplateOutput("", "<?for?>"); // "loop expression required"
	// // 	checkTemplateOutput("", "<?code?>"); // "statement required"
	// // }

	@Test
	public void operator_add()
	{
		String source = "<?print x + y?>";

		checkTemplateOutput("0", source, "x", false, "y", false);
		checkTemplateOutput("1", source, "x", false, "y", true);
		checkTemplateOutput("1", source, "x", true, "y", false);
		checkTemplateOutput("2", source, "x", true, "y", true);
		checkTemplateOutput("18", source, "x", 17, "y", true);
		checkTemplateOutput("40", source, "x", 17, "y", 23);
		checkTemplateOutput("18.0", source, "x", 17, "y", 1.0);
		checkTemplateOutput("24", source, "x", true, "y", 23);
		checkTemplateOutput("22.0", source, "x", -1.0, "y", 23);
		checkTemplateOutput("foobar", source, "x", "foo", "y", "bar");
		checkTemplateOutput("2012-10-18 00:00:00", source, "x", FunctionDate.call(2012, 10, 17), "y", new TimeDelta(1));
		checkTemplateOutput("2013-10-17 00:00:00", source, "x", FunctionDate.call(2012, 10, 17), "y", new TimeDelta(365));
		checkTemplateOutput("2012-10-17 12:00:00", source, "x", FunctionDate.call(2012, 10, 17), "y", new TimeDelta(0, 12*60*60));
		checkTemplateOutput("2012-10-17 00:00:01", source, "x", FunctionDate.call(2012, 10, 17), "y", new TimeDelta(0, 1));
		checkTemplateOutput("2012-10-17 00:00:00.500000", source, "x", FunctionDate.call(2012, 10, 17), "y", new TimeDelta(0, 0, 500000));
		checkTemplateOutput("2012-10-17 00:00:00.001000", source, "x", FunctionDate.call(2012, 10, 17), "y", new TimeDelta(0, 0, 1000));
		checkTemplateOutput("2 days, 0:00:00", source, "x", new TimeDelta(1), "y", new TimeDelta(1));
		checkTemplateOutput("1 day, 0:00:01", source, "x", new TimeDelta(1), "y", new TimeDelta(0, 1));
		checkTemplateOutput("1 day, 0:00:00.000001", source, "x", new TimeDelta(1), "y", new TimeDelta(0, 0, 1));
		checkTemplateOutput("2 months", source, "x", new MonthDelta(1), "y", new MonthDelta(1));
		// List addition is not implemented: checkTemplateOutput("(foo)(bar)(gurk)(hurz)", "<?for i in a+b?>(<?print i?>)<?end for?>", "a", asList("foo", "bar"), "b", asList("gurk", "hurz"));
		// This checks constant folding
		checkTemplateOutput("3", "<?print 1+2?>");
		checkTemplateOutput("2", "<?print 1+True?>");
		checkTemplateOutput("3.0", "<?print 1+2.0?>");
	}

	@Test
	public void operator_sub()
	{
		String source = "<?print x - y?>";

		checkTemplateOutput("0", source, "x", false, "y", false);
		checkTemplateOutput("-1", source, "x", false, "y", true);
		checkTemplateOutput("1", source, "x", true, "y", false);
		checkTemplateOutput("0", source, "x", true, "y", true);
		checkTemplateOutput("16", source, "x", 17, "y", true);
		checkTemplateOutput("-6", source, "x", 17, "y", 23);
		checkTemplateOutput("16.0", source, "x", 17, "y", 1.0);
		checkTemplateOutput("-22", source, "x", true, "y", 23);
		checkTemplateOutput("-24.0", source, "x", -1.0, "y", 23);
		checkTemplateOutput("2012-10-16 00:00:00", source, "x", FunctionDate.call(2012, 10, 17), "y", new TimeDelta(1));
		checkTemplateOutput("2011-10-17 00:00:00", source, "x", FunctionDate.call(2012, 10, 17), "y", new TimeDelta(366));
		checkTemplateOutput("2012-10-16 12:00:00", source, "x", FunctionDate.call(2012, 10, 17), "y", new TimeDelta(0, 12*60*60));
		checkTemplateOutput("2012-10-16 23:59:59", source, "x", FunctionDate.call(2012, 10, 17), "y", new TimeDelta(0, 1));
		checkTemplateOutput("2012-10-16 23:59:59.500000", source, "x", FunctionDate.call(2012, 10, 17), "y", new TimeDelta(0, 0, 500000));
		checkTemplateOutput("2012-10-16 23:59:59.999000", source, "x", FunctionDate.call(2012, 10, 17), "y", new TimeDelta(0, 0, 1000));
		checkTemplateOutput("0:00:00", source, "x", new TimeDelta(1), "y", new TimeDelta(1));
		checkTemplateOutput("1 day, 0:00:00", source, "x", new TimeDelta(2), "y", new TimeDelta(1));
		checkTemplateOutput("23:59:59", source, "x", new TimeDelta(1), "y", new TimeDelta(0, 1));
		checkTemplateOutput("23:59:59.999999", source, "x", new TimeDelta(1), "y", new TimeDelta(0, 0, 1));
		checkTemplateOutput("-1 day, 23:59:59", source, "x", new TimeDelta(0), "y", new TimeDelta(0, 1));
		checkTemplateOutput("-1 day, 23:59:59.999999", source, "x", new TimeDelta(0), "y", new TimeDelta(0, 0, 1));
		// This checks constant folding
		checkTemplateOutput("-1", "<?print 1-2?>");
		checkTemplateOutput("1", "<?print 2-True?>");
		checkTemplateOutput("-1.0", "<?print 1-2.0?>");
	}

	@Test
	public void operator_neg()
	{
		String source = "<?print -x?>";

		checkTemplateOutput("0", source, "x", false);
		checkTemplateOutput("-1", source, "x", true);
		checkTemplateOutput("-17", source, "x", 17);
		checkTemplateOutput("-17.0", source, "x", 17.0);
		checkTemplateOutput("0:00:00", source, "x", new TimeDelta());
		checkTemplateOutput("-1 day, 0:00:00", source, "x", new TimeDelta(1));
		checkTemplateOutput("-1 day, 23:59:59", source, "x", new TimeDelta(0, 1));
		checkTemplateOutput("-1 day, 23:59:59.999999", source, "x", new TimeDelta(0, 0, 1));
		// This checks constant folding
		checkTemplateOutput("0", "<?print -False?>");
		checkTemplateOutput("-1", "<?print -True?>");
		checkTemplateOutput("-2", "<?print -2?>");
		checkTemplateOutput("-2.0", "<?print -2.0?>");
	}

	@Test
	public void operator_mul()
	{
		String source = "<?print x * y?>";

		checkTemplateOutput("0", source, "x", false, "y", false);
		checkTemplateOutput("0", source, "x", false, "y", true);
		checkTemplateOutput("0", source, "x", true, "y", false);
		checkTemplateOutput("1", source, "x", true, "y", true);
		checkTemplateOutput("17", source, "x", 17, "y", true);
		checkTemplateOutput("391", source, "x", 17, "y", 23);
		checkTemplateOutput("17.0", source, "x", 17, "y", 1.0);
		checkTemplateOutput("23", source, "x", true, "y", 23);
		checkTemplateOutput("-23.0", source, "x", -1.0, "y", 23);
		checkTemplateOutput("foofoofoo", source, "x", 3, "y", "foo");
		checkTemplateOutput("foofoofoo", source, "x", "foo", "y", 3);
		checkTemplateOutput("0:00:00", source, "x", 4, "y", new TimeDelta());
		checkTemplateOutput("4 days, 0:00:00", source, "x", 4, "y", new TimeDelta(1));
		checkTemplateOutput("2 days, 0:00:00", source, "x", 4, "y", new TimeDelta(0, 12*60*60));
		checkTemplateOutput("0:00:02", source, "x", 4, "y", new TimeDelta(0, 0, 500000));
		checkTemplateOutput("12:00:00", source, "x", 0.5, "y", new TimeDelta(1));
		checkTemplateOutput("0:00:00", source, "x", new TimeDelta(), "y", 4);
		checkTemplateOutput("4 days, 0:00:00", source, "x", new TimeDelta(1), "y", 4);
		checkTemplateOutput("2 days, 0:00:00", source, "x", new TimeDelta(0, 12*60*60), "y", 4);
		checkTemplateOutput("0:00:02", source, "x", new TimeDelta(0, 0, 500000), "y", 4);
		checkTemplateOutput("12:00:00", source, "x", new TimeDelta(1), "y", 0.5);
		checkTemplateOutput("(foo)(bar)(foo)(bar)(foo)(bar)", "<?for i in 3*data?>(<?print i?>)<?end for?>", "data", asList("foo", "bar"));
		// This checks constant folding
		checkTemplateOutput("391", "<?print 17*23?>");
		checkTemplateOutput("17", "<?print 17*True?>");
		checkTemplateOutput("391.0", "<?print 17.0*23.0?>");
	}

	@Test
	public void operator_truediv()
	{
		String source = "<?print x / y?>";

		checkTemplateOutput("0.0", source, "x", false, "y", true);
		checkTemplateOutput("1.0", source, "x", true, "y", true);
		checkTemplateOutput("17.0", source, "x", 17, "y", true);
		checkTemplateOutput("17.0", source, "x", 391, "y", 23);
		checkTemplateOutput("17.0", source, "x", 17, "y", 1.0);
		checkTemplateOutput("0.5", source, "x", 1, "y", 2);
		checkTemplateOutput("0:00:00", source, "x", new TimeDelta(), "y", 4);
		checkTemplateOutput("2 days, 0:00:00", source, "x", new TimeDelta(8), "y", 4);
		checkTemplateOutput("12:00:00", source, "x", new TimeDelta(4), "y", 8);
		checkTemplateOutput("0:00:00.500000", source, "x", new TimeDelta(0, 4), "y", 8);
		checkTemplateOutput("2 days, 0:00:00", source, "x", new TimeDelta(1), "y", 0.5);
		checkTemplateOutput("9:36:00", source, "x", new TimeDelta(1), "y", 2.5);
		// This checks constant folding
		checkTemplateOutput("0.5", "<?print 1/2?>");
		checkTemplateOutput("2.0", "<?print 2.0/True?>");
	}

	@Test
	public void operator_floordiv()
	{
		String source = "<?print x // y?>";

		checkTemplateOutput("0", source, "x", false, "y", true);
		checkTemplateOutput("1", source, "x", true, "y", true);
		checkTemplateOutput("17", source, "x", 17, "y", true);
		checkTemplateOutput("17", source, "x", 392, "y", 23);
		checkTemplateOutput("17.0", source, "x", 17, "y", 1.0);
		checkTemplateOutput("0", source, "x", 1, "y", 2);
		checkTemplateOutput("0:00:00", source, "x", new TimeDelta(), "y", 4);
		checkTemplateOutput("2 days, 0:00:00", source, "x", new TimeDelta(8), "y", 4);
		checkTemplateOutput("12:00:00", source, "x", new TimeDelta(4), "y", 8);
		checkTemplateOutput("0:00:00.500000", source, "x", new TimeDelta(0, 4), "y", 8);
		// This checks constant folding
		checkTemplateOutput("0.5", "<?print 1/2?>");
		checkTemplateOutput("2.0", "<?print 2.0/True?>");
	}

	@Test
	public void operator_mod()
	{
		String source = "<?print x % y?>";

		checkTemplateOutput("0", source, "x", false, "y", true);
		checkTemplateOutput("0", source, "x", true, "y", true);
		checkTemplateOutput("0", source, "x", 17, "y", true);
		checkTemplateOutput("6", source, "x", 23, "y", 17);
		checkTemplateOutput("0.5", source, "x", 5.5, "y", 2.5);
		// This checks constant folding
		checkTemplateOutput("6", "<?print 23 % 17?>");
	}

	@Test
	public void operator_eq()
	{
		String source = "<?print x == y?>";

		checkTemplateOutput("False", source, "x", false, "y", true);
		checkTemplateOutput("True", source, "x", true, "y", true);
		checkTemplateOutput("True", source, "x", 1, "y", true);
		checkTemplateOutput("False", source, "x", 1, "y", false);
		checkTemplateOutput("False", source, "x", 17, "y", 23);
		checkTemplateOutput("True", source, "x", 17, "y", 17);
		checkTemplateOutput("True", source, "x", 17, "y", 17.0);
		// This checks constant folding
		checkTemplateOutput("False", "<?print 17 == 23?>");
		checkTemplateOutput("True", "<?print 17 == 17.?>");
	}

	@Test
	public void operator_ne()
	{
		String source = "<?print x != y?>";

		checkTemplateOutput("True", source, "x", false, "y", true);
		checkTemplateOutput("False", source, "x", true, "y", true);
		checkTemplateOutput("False", source, "x", 1, "y", true);
		checkTemplateOutput("True", source, "x", 1, "y", false);
		checkTemplateOutput("True", source, "x", 17, "y", 23);
		checkTemplateOutput("False", source, "x", 17, "y", 17);
		checkTemplateOutput("False", source, "x", 17, "y", 17.0);
		// This checks constant folding
		checkTemplateOutput("True", "<?print 17 != 23?>");
		checkTemplateOutput("False", "<?print 17 != 17.?>");
	}

	@Test
	public void operator_lt()
	{
		String source = "<?print x < y?>";

		checkTemplateOutput("True", source, "x", false, "y", true);
		checkTemplateOutput("False", source, "x", true, "y", true);
		checkTemplateOutput("False", source, "x", 1, "y", true);
		checkTemplateOutput("True", source, "x", true, "y", 2);
		checkTemplateOutput("True", source, "x", 17, "y", 23);
		checkTemplateOutput("False", source, "x", 23, "y", 17);
		checkTemplateOutput("False", source, "x", 17, "y", 17.0);
		checkTemplateOutput("True", source, "x", 17, "y", 23.0);
		checkTemplateOutput("False", source, "x", new TimeDelta(0), "y", new TimeDelta(0));
		checkTemplateOutput("True", source, "x", new TimeDelta(0), "y", new TimeDelta(1));
		checkTemplateOutput("True", source, "x", new TimeDelta(0), "y", new TimeDelta(0, 1));
		checkTemplateOutput("True", source, "x", new TimeDelta(0), "y", new TimeDelta(0, 0, 1));
		checkTemplateOutput("False", source, "x", new MonthDelta(0), "y", new MonthDelta(0));
		checkTemplateOutput("True", source, "x", new MonthDelta(0), "y", new MonthDelta(1));
		// This checks constant folding
		checkTemplateOutput("True", "<?print 17 < 23?>");
		checkTemplateOutput("False", "<?print 17 < 17.?>");
	}

	@Test
	public void operator_le()
	{
		String source = "<?print x <= y?>";

		checkTemplateOutput("True", source, "x", false, "y", true);
		checkTemplateOutput("True", source, "x", true, "y", true);
		checkTemplateOutput("True", source, "x", 1, "y", true);
		checkTemplateOutput("True", source, "x", true, "y", 2);
		checkTemplateOutput("True", source, "x", 17, "y", 23);
		checkTemplateOutput("False", source, "x", 23, "y", 17);
		checkTemplateOutput("True", source, "x", 17, "y", 17);
		checkTemplateOutput("True", source, "x", 17, "y", 17.0);
		checkTemplateOutput("False", source, "x", new TimeDelta(1), "y", new TimeDelta(0));
		checkTemplateOutput("True", source, "x", new TimeDelta(0), "y", new TimeDelta(1));
		checkTemplateOutput("True", source, "x", new TimeDelta(0), "y", new TimeDelta(0, 1));
		checkTemplateOutput("True", source, "x", new TimeDelta(0), "y", new TimeDelta(0, 0, 1));
		checkTemplateOutput("False", source, "x", new MonthDelta(1), "y", new MonthDelta(0));
		checkTemplateOutput("True", source, "x", new MonthDelta(0), "y", new MonthDelta(1));
		// This checks constant folding
		checkTemplateOutput("True", "<?print 17 <= 23?>");
		checkTemplateOutput("True", "<?print 17 <= 17.?>");
		checkTemplateOutput("True", "<?print 17 <= 23.?>");
		checkTemplateOutput("False", "<?print 18 <= 17.?>");
	}

	@Test
	public void operator_gt()
	{
		String source = "<?print x > y?>";

		checkTemplateOutput("False", source, "x", false, "y", true);
		checkTemplateOutput("False", source, "x", true, "y", true);
		checkTemplateOutput("False", source, "x", 1, "y", true);
		checkTemplateOutput("True", source, "x", 2, "y", true);
		checkTemplateOutput("False", source, "x", 17, "y", 23);
		checkTemplateOutput("True", source, "x", 23, "y", 17);
		checkTemplateOutput("False", source, "x", 17, "y", 17.0);
		checkTemplateOutput("True", source, "x", 23.0, "y", 17);
		checkTemplateOutput("False", source, "x", new TimeDelta(0), "y", new TimeDelta(0));
		checkTemplateOutput("True", source, "x", new TimeDelta(1), "y", new TimeDelta(0));
		checkTemplateOutput("True", source, "x", new TimeDelta(0, 1), "y", new TimeDelta(0));
		checkTemplateOutput("True", source, "x", new TimeDelta(0, 0, 1), "y", new TimeDelta(0));
		checkTemplateOutput("False", source, "x", new MonthDelta(0), "y", new MonthDelta(0));
		checkTemplateOutput("True", source, "x", new MonthDelta(1), "y", new MonthDelta(0));
		// This checks constant folding
		checkTemplateOutput("False", "<?print 17 > 23?>");
		checkTemplateOutput("False", "<?print 17 > 17.?>");
		checkTemplateOutput("False", "<?print 17 > 23.?>");
		checkTemplateOutput("True", "<?print 18 > 17.?>");
	}

	@Test
	public void operator_ge()
	{
		String source = "<?print x >= y?>";

		checkTemplateOutput("False", source, "x", false, "y", true);
		checkTemplateOutput("True", source, "x", true, "y", true);
		checkTemplateOutput("True", source, "x", 1, "y", true);
		checkTemplateOutput("False", source, "x", true, "y", 2);
		checkTemplateOutput("False", source, "x", 17, "y", 23);
		checkTemplateOutput("True", source, "x", 23, "y", 17);
		checkTemplateOutput("True", source, "x", 17, "y", 17);
		checkTemplateOutput("True", source, "x", 17, "y", 17.0);
		checkTemplateOutput("False", source, "x", new TimeDelta(0), "y", new TimeDelta(1));
		checkTemplateOutput("True", source, "x", new TimeDelta(1), "y", new TimeDelta(0));
		checkTemplateOutput("True", source, "x", new TimeDelta(0, 1), "y", new TimeDelta(0));
		checkTemplateOutput("True", source, "x", new TimeDelta(0, 0, 1), "y", new TimeDelta(0));
		checkTemplateOutput("False", source, "x", new MonthDelta(0), "y", new MonthDelta(1));
		checkTemplateOutput("True", source, "x", new MonthDelta(1), "y", new MonthDelta(0));
		// This checks constant folding
		checkTemplateOutput("False", "<?print 17 >= 23?>");
		checkTemplateOutput("True", "<?print 17 >= 17.?>");
		checkTemplateOutput("False", "<?print 17 >= 23.?>");
		checkTemplateOutput("True", "<?print 18 >= 17.?>");
	}

	@Test
	public void operator_contains()
	{
		String source = "<?print x in y?>";

		checkTemplateOutput("True", source, "x", 2, "y", asList(1, 2, 3));
		checkTemplateOutput("False", source, "x", 4, "y", asList(1, 2, 3));
		checkTemplateOutput("True", source, "x", 2, "y", new Integer[]{1, 2, 3});
		checkTemplateOutput("False", source, "x", 4, "y", new Integer[]{1, 2, 3});
		checkTemplateOutput("True", source, "x", "ur", "y", "gurk");
		checkTemplateOutput("False", source, "x", "un", "y", "gurk");
		checkTemplateOutput("True", source, "x", "a", "y", makeMap("a", 1, "b", 2));
		checkTemplateOutput("False", source, "x", "c", "y", makeMap("a", 1, "b", 2));
		checkTemplateOutput("True", source, "x", 0xff, "y", new Color(0x00, 0x80, 0xff, 0x42));
		checkTemplateOutput("False", source, "x", 0x23, "y", new Color(0x00, 0x80, 0xff, 0x42));
		checkTemplateOutput("True", "<?print 'x' in p?>", "p", new Point(17, 23));
		checkTemplateOutput("False", "<?print 'z' in p?>", "p", new Point(17, 23));
	}

	@Test
	public void operator_notcontains()
	{
		String source = "<?print x not in y?>";

		checkTemplateOutput("False", source, "x", 2, "y", asList(1, 2, 3));
		checkTemplateOutput("True", source, "x", 4, "y", asList(1, 2, 3));
		checkTemplateOutput("False", source, "x", 2, "y", new Integer[]{1, 2, 3});
		checkTemplateOutput("True", source, "x", 4, "y", new Integer[]{1, 2, 3});
		checkTemplateOutput("False", source, "x", "ur", "y", "gurk");
		checkTemplateOutput("True", source, "x", "un", "y", "gurk");
		checkTemplateOutput("False", source, "x", "a", "y", makeMap("a", 1, "b", 2));
		checkTemplateOutput("True", source, "x", "c", "y", makeMap("a", 1, "b", 2));
		checkTemplateOutput("False", source, "x", 0xff, "y", new Color(0x00, 0x80, 0xff, 0x42));
		checkTemplateOutput("True", source, "x", 0x23, "y", new Color(0x00, 0x80, 0xff, 0x42));
		checkTemplateOutput("False", "<?print 'x' not in p?>", "p", new Point(17, 23));
		checkTemplateOutput("True", "<?print 'z' not in p?>", "p", new Point(17, 23));
	}

	@Test
	public void operator_and()
	{
		String source = "<?print x and y?>";

		checkTemplateOutput("False", source, "x", false, "y", false);
		checkTemplateOutput("False", source, "x", false, "y", true);
		checkTemplateOutput("0", source, "x", 0, "y", true);
	}

	@Test
	public void operator_or()
	{
		String source = "<?print x or y?>";

		checkTemplateOutput("False", source, "x", false, "y", false);
		checkTemplateOutput("True", source, "x", false, "y", true);
		checkTemplateOutput("42", source, "x", 42, "y", true);
	}

	@Test
	public void operator_not()
	{
		String source = "<?print not x?>";

		checkTemplateOutput("True", source, "x", false);
		checkTemplateOutput("False", source, "x", 42);
	}

	@Test
	public void operator_getitem()
	{
		checkTemplateOutput("u", "<?print 'gurk'[1]?>");
		checkTemplateOutput("u", "<?print x[1]?>", "x", "gurk");
		checkTemplateOutput("u", "<?print 'gurk'[-3]?>");
		checkTemplateOutput("u", "<?print x[-3]?>", "x", "gurk");
		checkTemplateOutput("", "<?print 'gurk'[4]?>");
		checkTemplateOutput("", "<?print x[4]?>", "x", "gurk");
		checkTemplateOutput("", "<?print 'gurk'[-5]?>");
		checkTemplateOutput("", "<?print x[-5]?>", "x", "gurk");
	}

	@Test
	public void operator_getslice()
	{
		checkTemplateOutput("ur", "<?print 'gurk'[1:3]?>");
		checkTemplateOutput("ur", "<?print x[1:3]?>", "x", "gurk");
		checkTemplateOutput("ur", "<?print 'gurk'[-3:-1]?>");
		checkTemplateOutput("ur", "<?print x[-3:-1]?>", "x", "gurk");
		checkTemplateOutput("", "<?print 'gurk'[4:10]?>");
		checkTemplateOutput("", "<?print x[4:10]?>", "x", "gurk");
		checkTemplateOutput("", "<?print 'gurk'[-10:-5]?>");
		checkTemplateOutput("", "<?print x[-10:-5]?>", "x", "gurk");
		checkTemplateOutput("urk", "<?print 'gurk'[1:]?>");
		checkTemplateOutput("urk", "<?print x[1:]?>", "x", "gurk");
		checkTemplateOutput("urk", "<?print 'gurk'[-3:]?>");
		checkTemplateOutput("urk", "<?print x[-3:]?>", "x", "gurk");
		checkTemplateOutput("", "<?print 'gurk'[4:]?>");
		checkTemplateOutput("", "<?print x[4:]?>", "x", "gurk");
		checkTemplateOutput("gurk", "<?print 'gurk'[-10:]?>");
		checkTemplateOutput("gurk", "<?print x[-10:]?>", "x", "gurk");
		checkTemplateOutput("gur", "<?print 'gurk'[:3]?>");
		checkTemplateOutput("gur", "<?print x[:3]?>", "x", "gurk");
		checkTemplateOutput("gur", "<?print 'gurk'[:-1]?>");
		checkTemplateOutput("gur", "<?print x[:-1]?>", "x", "gurk");
		checkTemplateOutput("gurk", "<?print 'gurk'[:10]?>");
		checkTemplateOutput("gurk", "<?print x[:10]?>", "x", "gurk");
		checkTemplateOutput("", "<?print 'gurk'[:-5]?>");
		checkTemplateOutput("", "<?print x[:-5]?>", "x", "gurk");
	}

	@Test
	public void nested()
	{
		String sc = "4";
		String sv = "x";
		int n = 4;
		int depth = 10;
		for (int i = 0; i < depth; ++i)
		{
			sc = "(" + sc + ")+(" + sc + ")";
			sv = "(" + sv + ")+(" + sv + ")";
			n = n + n;
		}
		String expected = Integer.toString(n);
		checkTemplateOutput(expected, "<?print " + sc + "?>");
		checkTemplateOutput(expected, "<?print " + sv + "?>", "x", 4);
	}

	@Test
	public void precedence()
	{
		checkTemplateOutput("10", "<?print 2*3+4?>");
		checkTemplateOutput("14", "<?print 2+3*4?>");
		checkTemplateOutput("20", "<?print (2+3)*4?>");
		checkTemplateOutput("10", "<?print -2+-3*-4?>");
		checkTemplateOutput("14", "<?print --2+--3*--4?>");
		checkTemplateOutput("14", "<?print (-(-2))+(-((-3)*-(-4)))?>");
		checkTemplateOutput("42", "<?print 2*data.value?>", "data", makeMap("value", 21));
		checkTemplateOutput("42", "<?print data.value[0]?>", "data", makeMap("value", asList(42)));
		checkTemplateOutput("42", "<?print data[0].value?>", "data", asList(makeMap("value", 42)));
		checkTemplateOutput("42", "<?print data[0][0][0]?>", "data", asList(asList(asList(42))));
		checkTemplateOutput("42", "<?print data.value.value[0]?>", "data", makeMap("value", makeMap("value", asList(42))));
		checkTemplateOutput("42", "<?print data.value.value[0].value.value[0]?>", "data", makeMap("value", makeMap("value", asList(makeMap("value", makeMap("value", asList(42)))))));
	}

	@Test
	public void associativity()
	{
		checkTemplateOutput("9", "<?print 2+3+4?>");
		checkTemplateOutput("-5", "<?print 2-3-4?>");
		checkTemplateOutput("24", "<?print 2*3*4?>");
		checkTemplateOutput("2.0", "<?print 24/6/2?>");
		checkTemplateOutput("2", "<?print 24//6//2?>");
	}

	@Test
	public void bracket()
	{
		String sc = "42";
		String sv = "x";
		for (int i = 0; i < 10; ++i)
		{
			sc = "(" + sc + ")";
			sv = "(" + sv + ")";
		}

		checkTemplateOutput("42", "<?print " + sc + "?>");
		checkTemplateOutput("42", "<?print " + sv + "?>", "x", 42);
	}

	@Test
	public void function_now()
	{
		String output = getTemplateOutput("<?print now()?>");
		assertTrue(output.compareTo("2012-03-28") > 0);
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_now_1_args()
	{
		checkTemplateOutput("", "<?print now(1)?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_now_2_args()
	{
		checkTemplateOutput("", "<?print now(1, 2)?>");
	}

	@Test
	public void function_utcnow()
	{
		String output = getTemplateOutput("<?print utcnow()?>");
		assertTrue(output.compareTo("2012-03-28") > 0);
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_utcnow_1_args()
	{
		checkTemplateOutput("", "<?print utcnow(1)?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_utcnow_2_args()
	{
		checkTemplateOutput("", "<?print utcnow(1, 2)?>");
	}

	@Test
	public void function_date()
	{
		checkTemplateOutput("@(2012-10-06)", "<?print repr(date(2012, 10, 6))?>");
		checkTemplateOutput("@(2012-10-06T12:00:00)", "<?print repr(date(2012, 10, 6, 12))?>");
		checkTemplateOutput("@(2012-10-06T12:34:00)", "<?print repr(date(2012, 10, 6, 12, 34))?>");
		checkTemplateOutput("@(2012-10-06T12:34:56)", "<?print repr(date(2012, 10, 6, 12, 34, 56))?>");
		checkTemplateOutput("@(2012-10-06T12:34:56.987000)", "<?print repr(date(2012, 10, 6, 12, 34, 56, 987000))?>");
		checkTemplateOutput("@(2012-10-06T12:34:56.987000)", "<?print repr(date(year=2012, month=10, day=6, hour=12, minute=34, second=56, microsecond=987000))?>");
	}

	@Test
	public void function_timedelta()
	{
		checkTemplateOutput("0:00:00", "<?print timedelta()?>");
		checkTemplateOutput("1 day, 0:00:00", "<?print timedelta(1)?>");
		checkTemplateOutput("2 days, 0:00:00", "<?print timedelta(2)?>");
		checkTemplateOutput("0:00:01", "<?print timedelta(0, 1)?>");
		checkTemplateOutput("0:01:00", "<?print timedelta(0, 60)?>");
		checkTemplateOutput("1:00:00", "<?print timedelta(0, 60*60)?>");
		checkTemplateOutput("1 day, 1:01:01.000001", "<?print timedelta(1, 60*60+60+1, 1)?>");
		checkTemplateOutput("0:00:00.000001", "<?print timedelta(0, 0, 1)?>");
		checkTemplateOutput("0:00:01", "<?print timedelta(0, 0, 1000000)?>");
		checkTemplateOutput("1 day, 0:00:00", "<?print timedelta(0, 0, 24*60*60*1000000)?>");
		checkTemplateOutput("1 day, 0:00:00", "<?print timedelta(0, 24*60*60)?>");
		checkTemplateOutput("-1 day, 0:00:00", "<?print timedelta(-1)?>");
		checkTemplateOutput("-1 day, 23:59:59", "<?print timedelta(0, -1)?>");
		checkTemplateOutput("-1 day, 23:59:59.999999", "<?print timedelta(0, 0, -1)?>");
		checkTemplateOutput("12:00:00", "<?print timedelta(0.5)?>");
		checkTemplateOutput("0:00:00.500000", "<?print timedelta(0, 0.5)?>");
		checkTemplateOutput("0:00:00.500000", "<?print timedelta(0.5/(24*60*60))?>");
		checkTemplateOutput("-1 day, 12:00:00", "<?print timedelta(-0.5)?>");
		checkTemplateOutput("-1 day, 23:59:59.500000", "<?print timedelta(0, -0.5)?>");
		checkTemplateOutput("1 day, 0:00:01.000001", "<?print timedelta(days=1, seconds=1, microseconds=1)?>");
	}
	@Test
	public void function_monthdelta()
	{
		checkTemplateOutput("0 months", "<?print monthdelta()?>");
		checkTemplateOutput("1 month", "<?print monthdelta(1)?>");
		checkTemplateOutput("2 months", "<?print monthdelta(2)?>");
		checkTemplateOutput("-1 month", "<?print monthdelta(-1)?>");
		checkTemplateOutput("1 month", "<?print monthdelta(months=1)?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_random_1_args()
	{
		checkTemplateOutput("", "<?print random(1)?>");
	}

	@Test
	public void function_randrange()
	{
		checkTemplateOutput("ok", "<?code r = randrange(4)?><?if r>=0 and r<4?>ok<?else?>fail<?end if?>");
		checkTemplateOutput("ok", "<?code r = randrange(17, 23)?><?if r>=17 and r<23?>ok<?else?>fail<?end if?>");
		checkTemplateOutput("ok", "<?code r = randrange(17, 23, 2)?><?if r>=17 and r<23 and r%2?>ok<?else?>fail<?end if?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_randrange_0_args()
	{
		checkTemplateOutput("", "<?print randrange()?>");
	}

	@CauseTest(expectedCause=ArgumentCountMismatchException.class)
	public void function_randrange_4_args()
	{
		checkTemplateOutput("", "<?print randrange(1, 2, 3, 4)?>");
	}

	@Test
	public void function_randchoice()
	{
		checkTemplateOutput("ok", "<?code r = randchoice('abc')?><?if r in 'abc'?>ok<?else?>fail<?end if?>");
		checkTemplateOutput("ok", "<?code s = [17, 23, 42]?><?code r = randchoice(s)?><?if r in s?>ok<?else?>fail<?end if?>");
		checkTemplateOutput("ok", "<?code s = #12345678?><?code sl = [0x12, 0x34, 0x56, 0x78]?><?code r = randchoice(s)?><?if r in sl?>ok<?else?>fail<?end if?>");
		checkTemplateOutput("ok", "<?code r = randchoice(sequence='abc')?><?if r in 'abc'?>ok<?else?>fail<?end if?>");
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	@Test
	public void function_randchoice_0_args()
	{
		checkTemplateOutput("", "<?print randchoice()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	@Test
	public void function_randchoice_2_args()
	{
		checkTemplateOutput("", "<?print randchoice(1, 2)?>");
	}

	@Test
	public void function_xmlescape()
	{
		checkTemplateOutput("&lt;&lt;&gt;&gt;&amp;&#39;&quot;gurk", "<?print xmlescape(data)?>", "data", "<<>>&'\"gurk");
		checkTemplateOutput("42", "<?print xmlescape(obj=data)?>", "data", 42);
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	@Test
	public void function_xmlescape_0_args()
	{
		checkTemplateOutput("", "<?print xmlescape()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	@Test
	public void function_xmlescape_2_args()
	{
		checkTemplateOutput("", "<?print xmlescape(1, 2)?>");
	}

	@Test
	public void function_csv()
	{
		checkTemplateOutput("", "<?print csv(data)?>", "data", null);
		checkTemplateOutput("False", "<?print csv(data)?>", "data", false);
		checkTemplateOutput("True", "<?print csv(data)?>", "data", true);
		checkTemplateOutput("42", "<?print csv(data)?>", "data", 42);
		// no check for float
		checkTemplateOutput("abc", "<?print csv(data)?>", "data", "abc");
		checkTemplateOutput("\"a,b,c\"", "<?print csv(data)?>", "data", "a,b,c");
		checkTemplateOutput("\"a\"\"b\"\"c\"", "<?print csv(data)?>", "data", "a\"b\"c");
		checkTemplateOutput("\"a\nb\nc\"", "<?print csv(data)?>", "data", "a\nb\nc");
		checkTemplateOutput("42", "<?print csv(obj=data)?>", "data", 42);
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	@Test
	public void function_csv_0_args()
	{
		checkTemplateOutput("", "<?print csv()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	@Test
	public void function_csv_2_args()
	{
		checkTemplateOutput("", "<?print csv(1, 2)?>");
	}

	@Test
	public void function_asjson()
	{
		checkTemplateOutput("null", "<?print asjson(data)?>", "data", null);
		checkTemplateOutput("false", "<?print asjson(data)?>", "data", false);
		checkTemplateOutput("true", "<?print asjson(data)?>", "data", true);
		checkTemplateOutput("42", "<?print asjson(data)?>", "data", 42);
		// no check for float
		checkTemplateOutput("\"abc\"", "<?print asjson(data)?>", "data", "abc");
		checkTemplateOutput("[1, 2, 3]", "<?print asjson(data)?>", "data", asList(1, 2, 3));
		checkTemplateOutput("[1, 2, 3]", "<?print asjson(data)?>", "data", new Integer[]{1, 2, 3});
		checkTemplateOutput("{\"one\": 1}", "<?print asjson(data)?>", "data", makeMap("one", 1));
		checkTemplateOutput("null", "<?print asjson(obj=data)?>", "data", null);
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	@Test
	public void function_asjson_0_args()
	{
		checkTemplateOutput("", "<?print asjson()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	@Test
	public void function_asjson_2_args()
	{
		checkTemplateOutput("", "<?print asjson(1, 2)?>");
	}

	@Test
	public void function_fromjson()
	{
		checkTemplateOutput("None", "<?print repr(fromjson(data))?>", "data", "null");
		checkTemplateOutput("False", "<?print repr(fromjson(data))?>", "data", "false");
		checkTemplateOutput("True", "<?print repr(fromjson(data))?>", "data", "true");
		checkTemplateOutput("42", "<?print repr(fromjson(data))?>", "data", "42");
		checkTemplateOutput("\"abc\"", "<?print repr(fromjson(data))?>", "data", "\"abc\"");
		checkTemplateOutput("[1, 2, 3]", "<?print repr(fromjson(data))?>", "data", "[1,2,3]");
		checkTemplateOutput("{\"eins\": 42}", "<?print repr(fromjson(data))?>", "data", "{\"eins\": 42}");
		checkTemplateOutput("None", "<?print repr(fromjson(string=data))?>", "data", "null");
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	@Test
	public void function_fromjson_0_args()
	{
		checkTemplateOutput("", "<?print fromjson()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	@Test
	public void function_fromjson_2_args()
	{
		checkTemplateOutput("", "<?print fromjson(1, 2)?>");
	}

	@Test
	public void function_asul4on()
	{
		checkTemplateOutput(dumps(null), "<?print asul4on(data)?>", "data", null);
		checkTemplateOutput(dumps(false), "<?print asul4on(data)?>", "data", false);
		checkTemplateOutput(dumps(true), "<?print asul4on(data)?>", "data", true);
		checkTemplateOutput(dumps(42), "<?print asul4on(data)?>", "data", 42);
		checkTemplateOutput(dumps(42.5), "<?print asul4on(data)?>", "data", 42.5);
		checkTemplateOutput(dumps("abc"), "<?print asul4on(data)?>", "data", "abc");
		checkTemplateOutput(dumps(asList(1, 2, 3)), "<?print asul4on(data)?>", "data", asList(1, 2, 3));
		checkTemplateOutput(dumps(makeMap("one", 1)), "<?print asul4on(data)?>", "data", makeMap("one", 1));
		checkTemplateOutput(dumps(null), "<?print asul4on(obj=data)?>", "data", null);
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	@Test
	public void function_asul4on_0_args()
	{
		checkTemplateOutput("", "<?print asul4on()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	@Test
	public void function_asul4on_2_args()
	{
		checkTemplateOutput("", "<?print asul4on(1, 2)?>");
	}

	@Test
	public void function_str()
	{
		String source = "<?print str(data)?>";

		checkTemplateOutput("", "<?print str()?>");
		checkTemplateOutput("", source, "data", null);
		checkTemplateOutput("True", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("42", source, "data", 42);
		checkTemplateOutput("4.2", source, "data", 4.2);
		checkTemplateOutput("foo", source, "data", "foo");
		checkTemplateOutput("2011-02-09 00:00:00", source, "data", FunctionDate.call(2011, 2, 9));
		checkTemplateOutput("2011-02-09 12:34:56", source, "data", FunctionDate.call(2011, 2, 9, 12, 34, 56));
		checkTemplateOutput("2011-02-09 12:34:56.987000", source, "data", FunctionDate.call(2011, 2, 9, 12, 34, 56, 987000));
		checkTemplateOutput("foo", "<?print str(obj=data)?>", "data", "foo");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	@Test
	public void function_str_2_args()
	{
		checkTemplateOutput("", "<?print str(1, 2)?>");
	}

	@Test
	public void function_bool()
	{
		checkTemplateOutput("False", "<?print bool()?>");
		checkTemplateOutput("True", "<?print bool(data)?>", "data", true);
		checkTemplateOutput("False", "<?print bool(data)?>", "data", false);
		checkTemplateOutput("False", "<?print bool(data)?>", "data", 0);
		checkTemplateOutput("True", "<?print bool(data)?>", "data", 42);
		checkTemplateOutput("False", "<?print bool(data)?>", "data", new BigInteger("0"));
		checkTemplateOutput("True", "<?print bool(data)?>", "data", new BigInteger("42"));
		checkTemplateOutput("False", "<?print bool(data)?>", "data", 0.0);
		checkTemplateOutput("True", "<?print bool(data)?>", "data", 4.2);
		checkTemplateOutput("False", "<?print bool(data)?>", "data", new BigDecimal("0.000"));
		checkTemplateOutput("True", "<?print bool(data)?>", "data", new BigDecimal("42.5"));
		checkTemplateOutput("False", "<?print bool(data)?>", "data", "");
		checkTemplateOutput("True", "<?print bool(data)?>", "data", "foo");
		checkTemplateOutput("False", "<?print bool(data)?>", "data", asList());
		checkTemplateOutput("True", "<?print bool(data)?>", "data", asList("foo", "bar"));
		checkTemplateOutput("False", "<?print bool(data)?>", "data", new Integer[]{});
		checkTemplateOutput("True", "<?print bool(data)?>", "data", new Integer[]{1, 2, 3});
		checkTemplateOutput("False", "<?print bool(data)?>", "data", makeMap());
		checkTemplateOutput("True", "<?print bool(data)?>", "data", makeMap("foo", "bar"));
		checkTemplateOutput("True", "<?print bool(obj=data)?>", "data", true);
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	@Test
	public void function_bool_2_args()
	{
		checkTemplateOutput("", "<?print bool(1, 2)?>");
	}

	@Test
	public void function_int()
	{
		checkTemplateOutput("0", "<?print int()?>");
		checkTemplateOutput("1", "<?print int(data)?>", "data", true);
		checkTemplateOutput("0", "<?print int(data)?>", "data", false);
		checkTemplateOutput("42", "<?print int(data)?>", "data", 42);
		checkTemplateOutput("4", "<?print int(data)?>", "data", 4.2);
		checkTemplateOutput("42", "<?print int(data)?>", "data", "42");
		checkTemplateOutput("66", "<?print int(data, 16)?>", "data", "42");
		checkTemplateOutput("42", "<?print int(obj=data, base=None)?>", "data", "42");
		checkTemplateOutput("66", "<?print int(obj=data, base=16)?>", "data", "42");
		checkTemplateOutput("9999999999", "<?print int(data)?>", "data", "9999999999");
		checkTemplateOutput("999999999999999999999999", "<?print int(data)?>", "data", "999999999999999999999999");
		checkTemplateOutput("999999999999999999999999", "<?print int(data)?>", "data", new BigInteger("999999999999999999999999"));
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_int_null()
	{
		checkTemplateOutput("", "<?print int(data)?>", "data", null);
	}

	@CauseTest(expectedCause=NumberFormatException.class)
	public void function_int_badstring()
	{
		checkTemplateOutput("", "<?print int(data)?>", "data", "foo");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	@Test
	public void function_int_3_args()
	{
		checkTemplateOutput("", "<?print int(1, 2, 3)?>");
	}

	@Test
	public void function_float()
	{
		String source = "<?print float(data)?>";

		checkTemplateOutput("0.0", "<?print float()?>");
		checkTemplateOutput("1.0", source, "data", true);
		checkTemplateOutput("0.0", source, "data", false);
		checkTemplateOutput("42.0", source, "data", 42);
		checkTemplateOutput("42.0", source, "data", "42");
		checkTemplateOutput("1.0", "<?print float(obj=data)?>", "data", true);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_float_null()
	{
		checkTemplateOutput("", "<?print float(data)?>", "data", null);
	}

	@CauseTest(expectedCause=NumberFormatException.class)
	public void function_float_badstring()
	{
		checkTemplateOutput("", "<?print float(data)?>", "data", "foo");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_float_2_args()
	{
		checkTemplateOutput("", "<?print float(1, 2)?>");
	}

	@Test
	public void function_list()
	{
		checkTemplateOutput("[]", "<?print list()?>");
		checkTemplateOutput("[1, 2]", "<?print list(data)?>", "data", asList(1, 2));
		checkTemplateOutput("[\"g\", \"u\", \"r\", \"k\"]", "<?print list(data)?>", "data", "gurk");
		checkTemplateOutput("[[\"foo\", 42]]", "<?print repr(list(data.items()))?>", "data", makeMap("foo", 42));
		checkTemplateOutput("[0, 1, 2]", "<?print repr(list(range(3)))?>");
		checkTemplateOutput("[1, 2, 3]", "<?print list(data)?>", "data", new Integer[]{1, 2, 3});
		checkTemplateOutput("[\"x\", \"y\"]", "<?print repr(list(data))?>", "data", new Point(17, 23));
		checkTemplateOutput("[\"g\", \"u\", \"r\", \"k\"]", "<?print list(iterable=data)?>", "data", "gurk");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_list_2_args()
	{
		checkTemplateOutput("", "<?print list(1, 2)?>");
	}

	@Test
	public void function_len()
	{
		String source = "<?print len(data)?>";

		checkTemplateOutput("3", source, "data", "foo");
		checkTemplateOutput("3", source, "data", asList(1, 2, 3));
		checkTemplateOutput("3", source, "data", new Integer[]{1, 2, 3});
		checkTemplateOutput("3", source, "data", makeMap("a", 1, "b", 2, "c", 3));
		checkTemplateOutput("2", source, "data", new Point(17, 23));
		checkTemplateOutput("3", "<?print len(sequence=data)?>", "data", "foo");
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_len_0_args()
	{
		checkTemplateOutput("", "<?print len()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_len_2_args()
	{
		checkTemplateOutput("", "<?print len(1, 2)?>");
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_len_null()
	{
		checkTemplateOutput("", "<?print len(data)?>", "data", null);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_len_true()
	{
		checkTemplateOutput("", "<?print len(data)?>", "data", true);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_len_false()
	{
		checkTemplateOutput("", "<?print len(data)?>", "data", false);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_len_int()
	{
		checkTemplateOutput("", "<?print len(data)?>", "data", 42);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_len_float()
	{
		checkTemplateOutput("", "<?print len(data)?>", "data", 42.4);
	}

	@Test
	public void function_any()
	{
		checkTemplateOutput("False", "<?print any('')?>");
		checkTemplateOutput("True", "<?print any('foo')?>");
		checkTemplateOutput("True", "<?print any(i > 7 for i in range(10))?>");
		checkTemplateOutput("False", "<?print any(i > 17 for i in range(10))?>");
		checkTemplateOutput("True", "<?print any(iterable='foo')?>");
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_any_0_args()
	{
		checkTemplateOutput("", "<?print any()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_any_2_args()
	{
		checkTemplateOutput("", "<?print any(1, 2)?>");
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_any_none()
	{
		checkTemplateOutput("", "<?print any(None)?>");
	}

	@Test
	public void function_all()
	{
		checkTemplateOutput("True", "<?print all('')?>");
		checkTemplateOutput("True", "<?print all('foo')?>");
		checkTemplateOutput("False", "<?print all(i < 7 for i in range(10))?>");
		checkTemplateOutput("True", "<?print all(i < 17 for i in range(10))?>");
		checkTemplateOutput("True", "<?print all(iterable='foo')?>");
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_all_0_args()
	{
		checkTemplateOutput("", "<?print all()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_all_2_args()
	{
		checkTemplateOutput("", "<?print all(1, 2)?>");
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_all_none()
	{
		checkTemplateOutput("", "<?print all(None)?>");
	}

	@Test
	public void function_enumerate()
	{
		String source1 = "<?for (i, value) in enumerate(data)?>(<?print value?>=<?print i?>)<?end for?>";
		checkTemplateOutput("(f=0)(o=1)(o=2)", source1, "data", "foo");
		checkTemplateOutput("(foo=0)(bar=1)", source1, "data", asList("foo", "bar"));
		checkTemplateOutput("(foo=0)", source1, "data", makeMap("foo", true));

		String source2 = "<?for (i, value) in enumerate(data, 42)?>(<?print value?>=<?print i?>)<?end for?>";
		checkTemplateOutput("(f=42)(o=43)(o=44)", source2, "data", "foo");

		String source2kw = "<?for (i, value) in enumerate(iterable=data, start=42)?>(<?print value?>=<?print i?>)<?end for?>";
		checkTemplateOutput("(f=42)(o=43)(o=44)", source2kw, "data", "foo");
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_enumerate_0_args()
	{
		checkTemplateOutput("", "<?print enumerate()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_enumerate_3_args()
	{
		checkTemplateOutput("", "<?print enumerate(1, 2, 3)?>");
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_enumerate_null()
	{
		checkTemplateOutput("", "<?print enumerate(data)?>", "data", null);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_enumerate_true()
	{
		checkTemplateOutput("", "<?print enumerate(data)?>", "data", true);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_enumerate_false()
	{
		checkTemplateOutput("", "<?print enumerate(data)?>", "data", false);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_enumerate_int()
	{
		checkTemplateOutput("", "<?print enumerate(data)?>", "data", 42);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_enumerate_float()
	{
		checkTemplateOutput("", "<?print enumerate(data)?>", "data", 42.4);
	}

	@Test
	public void function_enumfl()
	{
		String source1 = "<?for (i, f, l, value) in enumfl(data)?><?if f?>[<?end if?>(<?print value?>=<?print i?>)<?if l?>]<?end if?><?end for?>";
		checkTemplateOutput("", source1, "data", "");
		checkTemplateOutput("[(?=0)]", source1, "data", "?");
		checkTemplateOutput("[(f=0)(o=1)(o=2)]", source1, "data", "foo");
		checkTemplateOutput("[(foo=0)(bar=1)]", source1, "data", asList("foo", "bar"));
		checkTemplateOutput("[(foo=0)]", source1, "data", makeMap("foo", true));

		String source2 = "<?for (i, f, l, value) in enumfl(data, 42)?><?if f?>[<?end if?>(<?print value?>=<?print i?>)<?if l?>]<?end if?><?end for?>";
		checkTemplateOutput("[(f=42)(o=43)(o=44)]", source2, "data", "foo");

		String source2kw = "<?for (i, f, l, value) in enumfl(iterable=data, start=42)?><?if f?>[<?end if?>(<?print value?>=<?print i?>)<?if l?>]<?end if?><?end for?>";
		checkTemplateOutput("[(f=42)(o=43)(o=44)]", source2kw, "data", "foo");
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_enumfl_0_args()
	{
		checkTemplateOutput("", "<?print enumfl()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_enumfl_3_args()
	{
		checkTemplateOutput("", "<?print enumfl(1, 2, 3)?>");
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_enumfl_null()
	{
		checkTemplateOutput("", "<?print enumfl(data)?>", "data", null);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_enumfl_true()
	{
		checkTemplateOutput("", "<?print enumfl(data)?>", "data", true);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_enumfl_false()
	{
		checkTemplateOutput("", "<?print enumfl(data)?>", "data", false);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_enumfl_int()
	{
		checkTemplateOutput("", "<?print enumfl(data)?>", "data", 42);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_enumfl_float()
	{
		checkTemplateOutput("", "<?print enumfl(data)?>", "data", 42.4);
	}

	@Test
	public void function_isfirstlast()
	{
		String source = "<?for (f, l, value) in isfirstlast(data)?><?if f?>[<?end if?>(<?print value?>)<?if l?>]<?end if?><?end for?>";

		checkTemplateOutput("", source, "data", "");
		checkTemplateOutput("[(?)]", source, "data", "?");
		checkTemplateOutput("[(f)(o)(o)]", source, "data", "foo");
		checkTemplateOutput("[(foo)(bar)]", source, "data", asList("foo", "bar"));
		checkTemplateOutput("[(foo)]", source, "data", makeMap("foo", true));

		String sourcekw = "<?for (f, l, value) in isfirstlast(iterable=data)?><?if f?>[<?end if?>(<?print value?>)<?if l?>]<?end if?><?end for?>";
		checkTemplateOutput("[(f)(o)(o)]", sourcekw, "data", "foo");
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isfirstlast_0_args()
	{
		checkTemplateOutput("", "<?print isfirstlast()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isfirstlast_2_args()
	{
		checkTemplateOutput("", "<?print isfirstlast(1, 2)?>");
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_isfirstlast_null()
	{
		checkTemplateOutput("", "<?print isfirstlast(data)?>", "data", null);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_isfirstlast_true()
	{
		checkTemplateOutput("", "<?print isfirstlast(data)?>", "data", true);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_isfirstlast_false()
	{
		checkTemplateOutput("", "<?print isfirstlast(data)?>", "data", false);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_isfirstlast_int()
	{
		checkTemplateOutput("", "<?print isfirstlast(data)?>", "data", 42);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_isfirstlast_float()
	{
		checkTemplateOutput("", "<?print isfirstlast(data)?>", "data", 42.4);
	}

	@Test
	public void function_isfirst()
	{
		String source = "<?for (f, value) in isfirst(data)?><?if f?>[<?end if?>(<?print value?>)<?end for?>";

		checkTemplateOutput("", source, "data", "");
		checkTemplateOutput("[(?)", source, "data", "?");
		checkTemplateOutput("[(f)(o)(o)", source, "data", "foo");
		checkTemplateOutput("[(foo)(bar)", source, "data", asList("foo", "bar"));
		checkTemplateOutput("[(foo)", source, "data", makeMap("foo", true));

		String sourcekw = "<?for (f, value) in isfirst(iterable=data)?><?if f?>[<?end if?>(<?print value?>)<?end for?>";
		checkTemplateOutput("[(f)(o)(o)", sourcekw, "data", "foo");
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isfirst_0_args()
	{
		checkTemplateOutput("", "<?print isfirst()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isfirst_2_args()
	{
		checkTemplateOutput("", "<?print isfirst(1, 2)?>");
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_isfirst_null()
	{
		checkTemplateOutput("", "<?print isfirst(data)?>", "data", null);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_isfirst_true()
	{
		checkTemplateOutput("", "<?print isfirst(data)?>", "data", true);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_isfirst_false()
	{
		checkTemplateOutput("", "<?print isfirst(data)?>", "data", false);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_isfirst_int()
	{
		checkTemplateOutput("", "<?print isfirst(data)?>", "data", 42);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_isfirst_float()
	{
		checkTemplateOutput("", "<?print isfirst(data)?>", "data", 42.4);
	}

	@Test
	public void function_islast()
	{
		String source = "<?for (l, value) in islast(data)?>(<?print value?>)<?if l?>]<?end if?><?end for?>";

		checkTemplateOutput("", source, "data", "");
		checkTemplateOutput("(?)]", source, "data", "?");
		checkTemplateOutput("(f)(o)(o)]", source, "data", "foo");
		checkTemplateOutput("(foo)(bar)]", source, "data", asList("foo", "bar"));
		checkTemplateOutput("(foo)]", source, "data", makeMap("foo", true));

		String sourcekw = "<?for (l, value) in islast(iterable=data)?>(<?print value?>)<?if l?>]<?end if?><?end for?>";
		checkTemplateOutput("(f)(o)(o)]", sourcekw, "data", "foo");
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_islast_0_args()
	{
		checkTemplateOutput("", "<?print islast()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_islast_2_args()
	{
		checkTemplateOutput("", "<?print islast(1, 2)?>");
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_islast_null()
	{
		checkTemplateOutput("", "<?print islast(data)?>", "data", null);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_islast_true()
	{
		checkTemplateOutput("", "<?print islast(data)?>", "data", true);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_islast_false()
	{
		checkTemplateOutput("", "<?print islast(data)?>", "data", false);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_islast_int()
	{
		checkTemplateOutput("", "<?print islast(data)?>", "data", 42);
	}

	@CauseTest(expectedCause=ArgumentTypeMismatchException.class)
	public void function_islast_float()
	{
		checkTemplateOutput("", "<?print islast(data)?>", "data", 42.4);
	}

	@Test
	public void function_isundefined()
	{
		String source = "<?print isundefined(data)?>";

		checkTemplateOutput("True", source, "data", new UndefinedKey("foo"));
		checkTemplateOutput("False", source, "data", null);
		checkTemplateOutput("False", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("False", source, "data", 42);
		checkTemplateOutput("False", source, "data", 4.2);
		checkTemplateOutput("False", source, "data", "foo");
		checkTemplateOutput("False", source, "data", new Date());
		checkTemplateOutput("False", source, "data", new TimeDelta(1));
		checkTemplateOutput("False", source, "data", new MonthDelta(1));
		checkTemplateOutput("False", source, "data", asList());
		checkTemplateOutput("False", source, "data", makeMap());
		checkTemplateOutput("False", source, "data", getTemplate(""));
		checkTemplateOutput("False", source, "data", getTemplate(""));
		checkTemplateOutput("False", "<?print isundefined(repr)?>");
		checkTemplateOutput("False", "<?print isundefined(obj=data)?>", "data", null);
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isundefined_0_args()
	{
		checkTemplateOutput("", "<?print isundefined()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isundefined_2_args()
	{
		checkTemplateOutput("", "<?print isundefined(1, 2)?>");
	}

	@Test
	public void function_isdefined()
	{
		String source = "<?print isdefined(data)?>";

		checkTemplateOutput("False", source, "data", new UndefinedKey("foo"));
		checkTemplateOutput("True", source, "data", null);
		checkTemplateOutput("True", source, "data", true);
		checkTemplateOutput("True", source, "data", false);
		checkTemplateOutput("True", source, "data", 42);
		checkTemplateOutput("True", source, "data", 4.2);
		checkTemplateOutput("True", source, "data", "foo");
		checkTemplateOutput("True", source, "data", new Date());
		checkTemplateOutput("True", source, "data", new TimeDelta(1));
		checkTemplateOutput("True", source, "data", new MonthDelta(1));
		checkTemplateOutput("True", source, "data", asList());
		checkTemplateOutput("True", source, "data", makeMap());
		checkTemplateOutput("True", source, "data", getTemplate(""));
		checkTemplateOutput("True", "<?print isdefined(repr)?>");
		checkTemplateOutput("True", source, "data", new Color(0, 0, 0));
		checkTemplateOutput("True", "<?print isdefined(obj=data)?>", "data", null);
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isdefined_0_args()
	{
		checkTemplateOutput("", "<?print isdefined()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isdefined_2_args()
	{
		checkTemplateOutput("", "<?print isdefined(1, 2)?>");
	}

	@Test
	public void function_isnone()
	{
		String source = "<?print isnone(data)?>";

		checkTemplateOutput("False", source, "data", new UndefinedKey("foo"));
		checkTemplateOutput("True", source, "data", null);
		checkTemplateOutput("False", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("False", source, "data", 42);
		checkTemplateOutput("False", source, "data", 4.2);
		checkTemplateOutput("False", source, "data", "foo");
		checkTemplateOutput("False", source, "data", new Date());
		checkTemplateOutput("False", source, "data", new TimeDelta(1));
		checkTemplateOutput("False", source, "data", new MonthDelta(1));
		checkTemplateOutput("False", source, "data", asList());
		checkTemplateOutput("False", source, "data", makeMap());
		checkTemplateOutput("False", source, "data", getTemplate(""));
		checkTemplateOutput("False", "<?print isnone(repr)?>");
		checkTemplateOutput("False", source, "data", new Color(0, 0, 0));
		checkTemplateOutput("True", "<?print isnone(obj=data)?>", "data", null);
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isnone_0_args()
	{
		checkTemplateOutput("", "<?print isnone()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isnone_2_args()
	{
		checkTemplateOutput("", "<?print isnone(1, 2)?>");
	}

	@Test
	public void function_isbool()
	{
		String source = "<?print isbool(data)?>";

		checkTemplateOutput("False", source, "data", new UndefinedKey("foo"));
		checkTemplateOutput("False", source, "data", null);
		checkTemplateOutput("True", source, "data", true);
		checkTemplateOutput("True", source, "data", false);
		checkTemplateOutput("False", source, "data", 42);
		checkTemplateOutput("False", source, "data", 4.2);
		checkTemplateOutput("False", source, "data", "foo");
		checkTemplateOutput("False", source, "data", new Date());
		checkTemplateOutput("False", source, "data", new TimeDelta(1));
		checkTemplateOutput("False", source, "data", new MonthDelta(1));
		checkTemplateOutput("False", source, "data", asList());
		checkTemplateOutput("False", source, "data", makeMap());
		checkTemplateOutput("False", source, "data", getTemplate(""));
		checkTemplateOutput("False", "<?print isbool(repr)?>");
		checkTemplateOutput("False", source, "data", new Color(0, 0, 0));
		checkTemplateOutput("False", "<?print isbool(obj=data)?>", "data", null);
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isbool_0_args()
	{
		checkTemplateOutput("", "<?print isbool()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isbool_2_args()
	{
		checkTemplateOutput("", "<?print isbool(1, 2)?>");
	}

	@Test
	public void function_isint()
	{
		String source = "<?print isint(data)?>";

		checkTemplateOutput("False", source, "data", new UndefinedKey("foo"));
		checkTemplateOutput("False", source, "data", null);
		checkTemplateOutput("False", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("True", source, "data", 42);
		checkTemplateOutput("False", source, "data", 4.2);
		checkTemplateOutput("False", source, "data", "foo");
		checkTemplateOutput("False", source, "data", new Date());
		checkTemplateOutput("False", source, "data", new TimeDelta(1));
		checkTemplateOutput("False", source, "data", new MonthDelta(1));
		checkTemplateOutput("False", source, "data", asList());
		checkTemplateOutput("False", source, "data", makeMap());
		checkTemplateOutput("False", source, "data", getTemplate(""));
		checkTemplateOutput("False", "<?print isint(repr)?>");
		checkTemplateOutput("False", source, "data", new Color(0, 0, 0));
		checkTemplateOutput("False", "<?print isint(obj=data)?>", "data", null);
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isint_0_args()
	{
		checkTemplateOutput("", "<?print isint()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isint_2_args()
	{
		checkTemplateOutput("", "<?print isint(1, 2)?>");
	}

	@Test
	public void function_isfloat()
	{
		String source = "<?print isfloat(data)?>";

		checkTemplateOutput("False", source, "data", new UndefinedKey("foo"));
		checkTemplateOutput("False", source, "data", null);
		checkTemplateOutput("False", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("False", source, "data", 42);
		checkTemplateOutput("True", source, "data", 4.2);
		checkTemplateOutput("False", source, "data", "foo");
		checkTemplateOutput("False", source, "data", new Date());
		checkTemplateOutput("False", source, "data", new TimeDelta(1));
		checkTemplateOutput("False", source, "data", new MonthDelta(1));
		checkTemplateOutput("False", source, "data", asList());
		checkTemplateOutput("False", source, "data", makeMap());
		checkTemplateOutput("False", source, "data", getTemplate(""));
		checkTemplateOutput("False", "<?print isfloat(repr)?>");
		checkTemplateOutput("False", source, "data", new Color(0, 0, 0));
		checkTemplateOutput("False", "<?print isfloat(obj=data)?>", "data", null);
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isfloat_0_args()
	{
		checkTemplateOutput("", "<?print isfloat()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isfloat_2_args()
	{
		checkTemplateOutput("", "<?print isfloat(1, 2)?>");
	}

	@Test
	public void function_isstr()
	{
		String source = "<?print isstr(data)?>";

		checkTemplateOutput("False", source, "data", new UndefinedKey("foo"));
		checkTemplateOutput("False", source, "data", null);
		checkTemplateOutput("False", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("False", source, "data", 42);
		checkTemplateOutput("False", source, "data", 4.2);
		checkTemplateOutput("True", source, "data", "foo");
		checkTemplateOutput("False", source, "data", new Date());
		checkTemplateOutput("False", source, "data", new TimeDelta(1));
		checkTemplateOutput("False", source, "data", new MonthDelta(1));
		checkTemplateOutput("False", source, "data", asList());
		checkTemplateOutput("False", source, "data", makeMap());
		checkTemplateOutput("False", source, "data", getTemplate(""));
		checkTemplateOutput("False", "<?print isstr(repr)?>");
		checkTemplateOutput("False", source, "data", new Color(0, 0, 0));
		checkTemplateOutput("False", "<?print isstr(obj=data)?>", "data", null);
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isstr_0_args()
	{
		checkTemplateOutput("", "<?print isstr()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isstr_2_args()
	{
		checkTemplateOutput("", "<?print isstr(1, 2)?>");
	}

	@Test
	public void function_isdate()
	{
		String source = "<?print isdate(data)?>";

		checkTemplateOutput("False", source, "data", new UndefinedKey("foo"));
		checkTemplateOutput("False", source, "data", null);
		checkTemplateOutput("False", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("False", source, "data", 42);
		checkTemplateOutput("False", source, "data", 4.2);
		checkTemplateOutput("False", source, "data", "foo");
		checkTemplateOutput("True", source, "data", new Date());
		checkTemplateOutput("False", source, "data", new TimeDelta(1));
		checkTemplateOutput("False", source, "data", new MonthDelta(1));
		checkTemplateOutput("False", source, "data", asList());
		checkTemplateOutput("False", source, "data", makeMap());
		checkTemplateOutput("False", source, "data", getTemplate(""));
		checkTemplateOutput("False", "<?print isdate(repr)?>");
		checkTemplateOutput("False", source, "data", new Color(0, 0, 0));
		checkTemplateOutput("False", "<?print isdate(obj=data)?>", "data", null);
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isdate_0_args()
	{
		checkTemplateOutput("", "<?print isdate()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isdate_2_args()
	{
		checkTemplateOutput("", "<?print isdate(1, 2)?>");
	}

	@Test
	public void function_islist()
	{
		String source = "<?print islist(data)?>";

		checkTemplateOutput("False", source, "data", new UndefinedKey("foo"));
		checkTemplateOutput("False", source, "data", null);
		checkTemplateOutput("False", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("False", source, "data", 42);
		checkTemplateOutput("False", source, "data", 4.2);
		checkTemplateOutput("False", source, "data", "foo");
		checkTemplateOutput("False", source, "data", new Date());
		checkTemplateOutput("False", source, "data", new TimeDelta(1));
		checkTemplateOutput("False", source, "data", new MonthDelta(1));
		checkTemplateOutput("True", source, "data", asList());
		checkTemplateOutput("True", source, "data", new Integer[]{1, 2, 3});
		checkTemplateOutput("False", source, "data", makeMap());
		checkTemplateOutput("False", source, "data", getTemplate(""));
		checkTemplateOutput("False", "<?print islist(repr)?>");
		checkTemplateOutput("False", source, "data", new Color(0, 0, 0));
		checkTemplateOutput("False", "<?print islist(obj=data)?>", "data", null);
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_islist_0_args()
	{
		checkTemplateOutput("", "<?print islist()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_islist_2_args()
	{
		checkTemplateOutput("", "<?print islist(1, 2)?>");
	}

	@Test
	public void function_isdict()
	{
		String source = "<?print isdict(data)?>";

		checkTemplateOutput("False", source, "data", new UndefinedKey("foo"));
		checkTemplateOutput("False", source, "data", null);
		checkTemplateOutput("False", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("False", source, "data", 42);
		checkTemplateOutput("False", source, "data", 4.2);
		checkTemplateOutput("False", source, "data", "foo");
		checkTemplateOutput("False", source, "data", new Date());
		checkTemplateOutput("False", source, "data", new TimeDelta(1));
		checkTemplateOutput("False", source, "data", new MonthDelta(1));
		checkTemplateOutput("False", source, "data", asList());
		checkTemplateOutput("True", source, "data", makeMap());
		checkTemplateOutput("False", source, "data", getTemplate(""));
		checkTemplateOutput("False", "<?print isdict(repr)?>");
		checkTemplateOutput("False", source, "data", new Color(0, 0, 0));
		checkTemplateOutput("False", "<?print isdict(obj=data)?>", "data", null);
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isdict_0_args()
	{
		checkTemplateOutput("", "<?print isdict()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isdict_2_args()
	{
		checkTemplateOutput("", "<?print isdict(1, 2)?>");
	}

	@Test
	public void function_istemplate()
	{
		String source = "<?print istemplate(data)?>";

		checkTemplateOutput("False", source, "data", new UndefinedKey("foo"));
		checkTemplateOutput("False", source, "data", null);
		checkTemplateOutput("False", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("False", source, "data", 42);
		checkTemplateOutput("False", source, "data", 4.2);
		checkTemplateOutput("False", source, "data", "foo");
		checkTemplateOutput("False", source, "data", new Date());
		checkTemplateOutput("False", source, "data", new TimeDelta(1));
		checkTemplateOutput("False", source, "data", new MonthDelta(1));
		checkTemplateOutput("False", source, "data", asList());
		checkTemplateOutput("False", source, "data", makeMap());
		checkTemplateOutput("True", source, "data", getTemplate(""));
		checkTemplateOutput("False", "<?print istemplate(repr)?>");
		checkTemplateOutput("False", source, "data", new Color(0, 0, 0));
		checkTemplateOutput("False", "<?print istemplate(obj=data)?>", "data", null);
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_istemplate_0_args()
	{
		checkTemplateOutput("", "<?print istemplate()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_istemplate_2_args()
	{
		checkTemplateOutput("", "<?print istemplate(1, 2)?>");
	}

	@Test
	public void function_isfunction()
	{
		String source = "<?print isfunction(data)?>";

		checkTemplateOutput("False", source, "data", new UndefinedKey("foo"));
		checkTemplateOutput("False", source, "data", null);
		checkTemplateOutput("False", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("False", source, "data", 42);
		checkTemplateOutput("False", source, "data", 4.2);
		checkTemplateOutput("False", source, "data", "foo");
		checkTemplateOutput("False", source, "data", new Date());
		checkTemplateOutput("False", source, "data", new TimeDelta(1));
		checkTemplateOutput("False", source, "data", new MonthDelta(1));
		checkTemplateOutput("False", source, "data", asList());
		checkTemplateOutput("False", source, "data", makeMap());
		checkTemplateOutput("True", source, "data", getTemplate(""));
		checkTemplateOutput("True", "<?print isfunction(repr)?>");
		checkTemplateOutput("False", source, "data", new Color(0, 0, 0));
		checkTemplateOutput("False", "<?print isfunction(obj=data)?>", "data", null);
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_isfunction_0_args()
	{
		checkTemplateOutput("", "<?print isfunction()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_isfunction_2_args()
	{
		checkTemplateOutput("", "<?print isfunction(1, 2)?>");
	}

	@Test
	public void function_iscolor()
	{
		String source = "<?print iscolor(data)?>";

		checkTemplateOutput("False", source, "data", new UndefinedKey("foo"));
		checkTemplateOutput("False", source, "data", null);
		checkTemplateOutput("False", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("False", source, "data", 42);
		checkTemplateOutput("False", source, "data", 4.2);
		checkTemplateOutput("False", source, "data", "foo");
		checkTemplateOutput("False", source, "data", new Date());
		checkTemplateOutput("False", source, "data", new TimeDelta(1));
		checkTemplateOutput("False", source, "data", new MonthDelta(1));
		checkTemplateOutput("False", source, "data", asList());
		checkTemplateOutput("False", source, "data", makeMap());
		checkTemplateOutput("False", source, "data", getTemplate(""));
		checkTemplateOutput("False", "<?print iscolor(repr)?>");
		checkTemplateOutput("True", source, "data", new Color(0, 0, 0));
		checkTemplateOutput("False", "<?print iscolor(obj=data)?>", "data", null);
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_iscolor_0_args()
	{
		checkTemplateOutput("", "<?print iscolor()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_iscolor_2_args()
	{
		checkTemplateOutput("", "<?print iscolor(1, 2)?>");
	}

	@Test
	public void function_istimedelta()
	{
		String source = "<?print istimedelta(data)?>";

		checkTemplateOutput("False", source, "data", new UndefinedKey("foo"));
		checkTemplateOutput("False", source, "data", null);
		checkTemplateOutput("False", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("False", source, "data", 42);
		checkTemplateOutput("False", source, "data", 4.2);
		checkTemplateOutput("False", source, "data", "foo");
		checkTemplateOutput("False", source, "data", new Date());
		checkTemplateOutput("True", source, "data", new TimeDelta(1));
		checkTemplateOutput("False", source, "data", new MonthDelta(1));
		checkTemplateOutput("False", source, "data", asList());
		checkTemplateOutput("False", source, "data", makeMap());
		checkTemplateOutput("False", source, "data", getTemplate(""));
		checkTemplateOutput("False", "<?print istimedelta(repr)?>");
		checkTemplateOutput("False", source, "data", new Color(0, 0, 0));
		checkTemplateOutput("False", "<?print istimedelta(obj=data)?>", "data", null);
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_istimedelta_0_args()
	{
		checkTemplateOutput("", "<?print istimedelta()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_istimedelta_2_args()
	{
		checkTemplateOutput("", "<?print istimedelta(1, 2)?>");
	}

	@Test
	public void function_ismonthdelta()
	{
		String source = "<?print ismonthdelta(data)?>";

		checkTemplateOutput("False", source, "data", new UndefinedKey("foo"));
		checkTemplateOutput("False", source, "data", null);
		checkTemplateOutput("False", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("False", source, "data", 42);
		checkTemplateOutput("False", source, "data", 4.2);
		checkTemplateOutput("False", source, "data", "foo");
		checkTemplateOutput("False", source, "data", new Date());
		checkTemplateOutput("False", source, "data", new TimeDelta(1));
		checkTemplateOutput("True", source, "data", new MonthDelta(1));
		checkTemplateOutput("False", source, "data", asList());
		checkTemplateOutput("False", source, "data", makeMap());
		checkTemplateOutput("False", source, "data", getTemplate(""));
		checkTemplateOutput("False", "<?print ismonthdelta(repr)?>");
		checkTemplateOutput("False", source, "data", new Color(0, 0, 0));
		checkTemplateOutput("False", "<?print ismonthdelta(obj=data)?>", "data", null);
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_ismonthdelta_0_args()
	{
		checkTemplateOutput("", "<?print ismonthdelta()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_ismonthdelta_2_args()
	{
		checkTemplateOutput("", "<?print ismonthdelta(1, 2)?>");
	}

	@Test
	public void function_repr()
	{
		String source = "<?print repr(data)?>";

		checkTemplateOutput("None", source, "data", null);
		checkTemplateOutput("True", source, "data", true);
		checkTemplateOutput("False", source, "data", false);
		checkTemplateOutput("42", source, "data", 42);
		checkTemplateOutput("42.5", source, "data", 42.5);
		checkTemplateOutput("\"foo\"", source, "data", "foo");
		checkTemplateOutput("[1, 2, 3]", source, "data", asList(1, 2, 3));
		checkTemplateOutput("[1, 2, 3]", source, "data", new Integer[]{1, 2, 3});
		checkTemplateOutput("{\"a\": 1}", source, "data", makeMap("a", 1));
		checkTemplateOutput2("{\"a\": 1, \"b\": 2}", "{\"b\": 2, \"a\": 1}", source, "data", makeMap("a", 1, "b", 2));
		checkTemplateOutput("@(2011-02-07T12:34:56.123000)", source, "data", FunctionDate.call(2011, 2, 7, 12, 34, 56, 123000));
		checkTemplateOutput("@(2011-02-07T12:34:56)", source, "data", FunctionDate.call(2011, 2, 7, 12, 34, 56));
		checkTemplateOutput("@(2011-02-07)", source, "data", FunctionDate.call(2011, 2, 7));
		checkTemplateOutput("@(2011-02-07)", source, "data", FunctionDate.call(2011, 2, 7));
		checkTemplateOutput("None", "<?print repr(obj=data)?>", "data", null);
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_repr_0_args()
	{
		checkTemplateOutput("", "<?print repr()?>");
	}

	@CauseTest(expectedCause=TooManyArgumentsException.class)
	public void function_repr_2_args()
	{
		checkTemplateOutput("", "<?print repr(1, 2)?>");
	}

	@Test
	public void function_format_date()
	{
		Date t = FunctionDate.call(2011, 1, 25, 13, 34, 56, 987000);

		String source2 = "<?print format(data, fmt)?>";
		String source3 = "<?print format(data, fmt, lang)?>";
		String source3kw = "<?print format(obj=data, fmt=fmt, lang=lang)?>";

		checkTemplateOutput("2011", source2, "data", t, "fmt", "%Y");
		checkTemplateOutput("01", source2, "data", t, "fmt", "%m");
		checkTemplateOutput("25", source2, "data", t, "fmt", "%d");
		checkTemplateOutput("13", source2, "data", t, "fmt", "%H");
		checkTemplateOutput("34", source2, "data", t, "fmt", "%M");
		checkTemplateOutput("56", source2, "data", t, "fmt", "%S");
		checkTemplateOutput("987000", source2, "data", t, "fmt", "%f");
		checkTemplateOutput("Tue", source2, "data", t, "fmt", "%a");
		checkTemplateOutput("Tue", source3, "data", t, "fmt", "%a", "lang", null);
		checkTemplateOutput("Tue", source3, "data", t, "fmt", "%a", "lang", "en");
		checkTemplateOutput("Di", source3, "data", t, "fmt", "%a", "lang", "de");
		checkTemplateOutput("Di", source3, "data", t, "fmt", "%a", "lang", "de_DE");
		checkTemplateOutput("Tuesday", source2, "data", t, "fmt", "%A");
		checkTemplateOutput("Tuesday", source3, "data", t, "fmt", "%A", "lang", null);
		checkTemplateOutput("Tuesday", source3, "data", t, "fmt", "%A", "lang", "en");
		checkTemplateOutput("Dienstag", source3, "data", t, "fmt", "%A", "lang", "de");
		checkTemplateOutput("Dienstag", source3, "data", t, "fmt", "%A", "lang", "de_DE");
		checkTemplateOutput("Jan", source2, "data", t, "fmt", "%b");
		checkTemplateOutput("Jan", source3, "data", t, "fmt", "%b", "lang", null);
		checkTemplateOutput("Jan", source3, "data", t, "fmt", "%b", "lang", "en");
		checkTemplateOutput("Jan", source3, "data", t, "fmt", "%b", "lang", "de");
		checkTemplateOutput("Jan", source3, "data", t, "fmt", "%b", "lang", "de_DE");
		checkTemplateOutput("January", source2, "data", t, "fmt", "%B");
		checkTemplateOutput("January", source3, "data", t, "fmt", "%B", "lang", null);
		checkTemplateOutput("January", source3, "data", t, "fmt", "%B", "lang", "en");
		checkTemplateOutput("Januar", source3, "data", t, "fmt", "%B", "lang", "de");
		checkTemplateOutput("Januar", source3, "data", t, "fmt", "%B", "lang", "de_DE");
		checkTemplateOutput("01", source2, "data", t, "fmt", "%I");
		checkTemplateOutput("025", source2, "data", t, "fmt", "%j");
		checkTemplateOutput("PM", source2, "data", t, "fmt", "%p");
		checkTemplateOutput("04", source2, "data", t, "fmt", "%U");
		checkTemplateOutput("2", source2, "data", t, "fmt", "%w");
		checkTemplateOutput("04", source2, "data", t, "fmt", "%W");
		checkTemplateOutput("11", source2, "data", t, "fmt", "%y");
		checkTemplateOutput("Tue 25 Jan 2011 01:34:56 PM", source2, "data", t, "fmt", "%c");
		checkTemplateOutput("01/25/2011", source2, "data", t, "fmt", "%x");
		checkTemplateOutput("01/25/2011", source3, "data", t, "fmt", "%x", "lang", null);
		checkTemplateOutput("01/25/2011", source3, "data", t, "fmt", "%x", "lang", "en");
		checkTemplateOutput("25.01.2011", source3, "data", t, "fmt", "%x", "lang", "de");
		checkTemplateOutput("25.01.2011", source3, "data", t, "fmt", "%x", "lang", "de_DE");
		checkTemplateOutput("13:34:56", source2, "data", t, "fmt", "%X");
		checkTemplateOutput("13:34:56", source3, "data", t, "fmt", "%X", "lang", null);
		checkTemplateOutput("13:34:56", source3, "data", t, "fmt", "%X", "lang", "en");
		checkTemplateOutput("13:34:56", source3, "data", t, "fmt", "%X", "lang", "de");
		checkTemplateOutput("13:34:56", source3, "data", t, "fmt", "%X", "lang", "de_DE");
		checkTemplateOutput("%", source2, "fmt", "%%", "data", t);
		checkTemplateOutput("2011", source3kw, "data", t, "fmt", "%Y", "lang", "de_DE");
	}

	@Test
	public void function_format_int()
	{
		String source2 = "<?print format(data, fmt)?>";
		String source3 = "<?print format(data, fmt, lang)?>";

		checkTemplateOutput("42", source2, "data", 42, "fmt", "");
		checkTemplateOutput("-42", source2, "data", -42, "fmt", "");
		checkTemplateOutput("   42", source2, "data", 42, "fmt", "5");
		checkTemplateOutput("00042", source2, "data", 42, "fmt", "05");
		checkTemplateOutput("-0042", source2, "data", -42, "fmt", "05");
		checkTemplateOutput("+0042", source2, "data", 42, "fmt", "+05");
		checkTemplateOutput(" +101010", source2, "data", 42, "fmt", "+8b");
		checkTemplateOutput(" +0b101010", source2, "data", 42, "fmt", "+#10b");
		checkTemplateOutput("52", source2, "data", 42, "fmt", "o");
		checkTemplateOutput("+0x2a", source2, "data", 42, "fmt", "+#x");
		checkTemplateOutput("+0X2A", source2, "data", 42, "fmt", "+#X");
		checkTemplateOutput("42   ", source2, "data", 42, "fmt", "<5");
		checkTemplateOutput("   42", source2, "data", 42, "fmt", ">5");
		checkTemplateOutput("???42", source2, "data", 42, "fmt", "?>5");
		checkTemplateOutput(" 42  ", source2, "data", 42, "fmt", "^5");
		checkTemplateOutput(" ??42", source2, "data", 42, "fmt", "?= 5");
		checkTemplateOutput(" 0b??101010", source2, "data", 42, "fmt", "?= #11b");
		checkTemplateOutput("00001", source2, "data", true, "fmt", "05");
		checkTemplateOutput("00042", source2, "data", new BigInteger("42"), "fmt", "05");
	}

	@Test
	public void function_chr()
	{
		String source = "<?print chr(data)?>";
		checkTemplateOutput("\u0000", source, "data", 0);
		checkTemplateOutput("a", source, "data", (int)'a');
		checkTemplateOutput("\u20ac", source, "data", 0x20ac);

		String sourcekw = "<?print chr(i=data)?>";
		checkTemplateOutput("\u0000", sourcekw, "data", 0);
	}

	@Test
	public void function_ord()
	{
		String source = "<?print ord(data)?>";
		checkTemplateOutput("0", source, "data", "\u0000");
		checkTemplateOutput("97", source, "data", "a");
		checkTemplateOutput("8364", source, "data", "\u20ac");

		String sourcekw = "<?print ord(c=data)?>";
		checkTemplateOutput("0", sourcekw, "data", "\u0000");
	}

	@Test
	public void function_hex()
	{
		String source = "<?print hex(data)?>";
		checkTemplateOutput("0x0", source, "data", 0);
		checkTemplateOutput("0xff", source, "data", 0xff);
		checkTemplateOutput("0xffff", source, "data", 0xffff);
		checkTemplateOutput("-0xffff", source, "data", -0xffff);

		String sourcekw = "<?print hex(number=data)?>";
		checkTemplateOutput("0x0", sourcekw, "data", 0);
	}

	@Test
	public void function_oct()
	{
		String source = "<?print oct(data)?>";
		checkTemplateOutput("0o0", source, "data", 0);
		checkTemplateOutput("0o77", source, "data", 077);
		checkTemplateOutput("0o7777", source, "data", 07777);
		checkTemplateOutput("-0o7777", source, "data", -07777);


		String sourcekw = "<?print oct(number=data)?>";
		checkTemplateOutput("0o0", sourcekw, "data", 0);
	}

	@Test
	public void function_bin()
	{
		String source = "<?print bin(data)?>";

		checkTemplateOutput("0b0", source, "data", 0);
		checkTemplateOutput("0b11", source, "data", 3);
		checkTemplateOutput("-0b1111", source, "data", -15);


		String sourcekw = "<?print bin(number=data)?>";
		checkTemplateOutput("0b0", sourcekw, "data", 0);
	}

	@Test
	public void function_abs()
	{
		String source = "<?print abs(data)?>";
		checkTemplateOutput("0", source, "data", 0);
		checkTemplateOutput("42", source, "data", 42);
		checkTemplateOutput("42", source, "data", -42);
		checkTemplateOutput("1 month", source, "data", new MonthDelta(-1));
		checkTemplateOutput("1 day, 0:00:01.000001", source, "data", new TimeDelta(-1, -1, -1));

		String sourcekw = "<?print abs(number=data)?>";
		checkTemplateOutput("0", sourcekw, "data", 0);
	}

	@Test
	public void function_min()
	{
		checkTemplateOutput("1", "<?print min('123')?>");
		checkTemplateOutput("1", "<?print min(1, 2, 3)?>");
		checkTemplateOutput("0", "<?print min(0, False, 1, True)?>");
		checkTemplateOutput("False", "<?print min(False, 0, True, 1)?>");
		checkTemplateOutput("False", "<?print min([False, 0, True, 1])?>");
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_min_0_args()
	{
		checkTemplateOutput("", "<?print min()?>");
	}

	@Test
	public void function_max()
	{
		checkTemplateOutput("3", "<?print max('123')?>");
		checkTemplateOutput("3", "<?print max(1, 2, 3)?>");
		checkTemplateOutput("1", "<?print max(0, False, 1, True)?>");
		checkTemplateOutput("True", "<?print max(False, 0, True, 1)?>");
		checkTemplateOutput("True", "<?print max([False, 0, True, 1])?>");
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_max_0_args()
	{
		checkTemplateOutput("", "<?print max()?>");
	}

	@Test
	public void function_sum()
	{
		checkTemplateOutput("0", "<?print sum([])?>");
		checkTemplateOutput("6", "<?print sum([1, 2, 3])?>");
		checkTemplateOutput("12", "<?print sum([1, 2, 3], 6)?>");
		checkTemplateOutput("5050", "<?print sum(range(101))?>");

		checkTemplateOutput("12", "<?print sum(iterable=[1, 2, 3], start=6)?>");
	}

	@CauseTest(expectedCause=MissingArgumentException.class)
	public void function_sum_0_args()
	{
		checkTemplateOutput("", "<?print sum()?>");
	}

	@Test
	public void function_sorted()
	{
		String source = "<?for i in sorted(data)?><?print i?><?end for?>";
		checkTemplateOutput("gkru", source, "data", "gurk");
		checkTemplateOutput("24679", source, "data", "92746");
		checkTemplateOutput("172342", source, "data", asList(42, 17, 23));
		checkTemplateOutput("012", source, "data", makeMap(0, "zero", 1, "one", 2, "two"));

		String sourcekw = "<?for i in sorted(iterable=data)?><?print i?><?end for?>";
		checkTemplateOutput("gkru", sourcekw, "data", "gurk");
	}

	@Test
	public void function_range()
	{
		String source1 = "<?for i in range(data)?><?print i?>;<?end for?>";
		String source2 = "<?for i in range(data[0], data[1])?><?print i?>;<?end for?>";
		String source3 = "<?for i in range(data[0], data[1], data[2])?><?print i?>;<?end for?>";

		checkTemplateOutput("", source1, "data", -10);
		checkTemplateOutput("", source1, "data", 0);
		checkTemplateOutput("0;", source1, "data", 1);
		checkTemplateOutput("0;1;2;3;4;", source1, "data", 5);
		checkTemplateOutput("", source2, "data", asList(0, -10));
		checkTemplateOutput("", source2, "data", asList(0, 0));
		checkTemplateOutput("0;1;2;3;4;", source2, "data", asList(0, 5));
		checkTemplateOutput("-5;-4;-3;-2;-1;0;1;2;3;4;", source2, "data", asList(-5, 5));
		checkTemplateOutput("", source3, "data", asList(0, -10, 1));
		checkTemplateOutput("", source3, "data", asList(0, 0, 1));
		checkTemplateOutput("0;2;4;6;8;", source3, "data", asList(0, 10, 2));
		checkTemplateOutput("", source3, "data", asList(0, 10, -2));
		checkTemplateOutput("10;8;6;4;2;", source3, "data", asList(10, 0, -2));
		checkTemplateOutput("", source3, "data", asList(10, 0, 2));
		checkTemplateOutput("0;1;", "<?for i in range(0, *[2, 1])?><?print i?>;<?end for?>");
	}

	@Test
	public void function_slice()
	{
		String source2 = "<?for i in slice(data[0], data[1])?><?print i?>;<?end for?>";
		String source3 = "<?for i in slice(data[0], data[1], data[2])?><?print i?>;<?end for?>";
		String source4 = "<?for i in slice(data[0], data[1], data[2], data[3])?><?print i?>;<?end for?>";

		checkTemplateOutput("g;u;r;k;", source2, "data", asList("gurk", null));
		checkTemplateOutput("g;u;", source2, "data", asList("gurk", 2));
		checkTemplateOutput("u;r;", source3, "data", asList("gurk", 1, 3));
		checkTemplateOutput("u;r;k;", source3, "data", asList("gurk", 1, null));
		checkTemplateOutput("g;u;", source3, "data", asList("gurk", null, 2));
		checkTemplateOutput("u;u;", source4, "data", asList("gurkgurk", 1, 6, 4));
	}

	@Test
	public void function_zip()
	{
		String source2 = "<?for (ix, iy) in zip(x, y)?><?print ix?>-<?print iy?>;<?end for?>";
		String source3 = "<?for (ix, iy, iz) in zip(x, y, z)?><?print ix?>-<?print iy?>+<?print iz?>;<?end for?>";

		checkTemplateOutput("", source2, "x", asList(), "y", asList());
		checkTemplateOutput("1-3;2-4;", source2, "x", asList(1, 2), "y", asList(3, 4));
		checkTemplateOutput("1-4;2-5;", source2, "x", asList(1, 2, 3), "y", asList(4, 5));
		checkTemplateOutput("", source3, "x", asList(), "y", asList(), "z", asList());
		checkTemplateOutput("1-3+5;2-4+6;", source3, "x", asList(1, 2), "y", asList(3, 4), "z", asList(5, 6));
		checkTemplateOutput("1-4+6;", source3, "x", asList(1, 2, 3), "y", asList(4, 5), "z", asList(6));
	}

	@Test
	public void function_type()
	{
		String source = "<?print type(data)?>";
		checkTemplateOutput("undefined", source);
		checkTemplateOutput("none", source, "data", null);
		checkTemplateOutput("bool", source, "data", false);
		checkTemplateOutput("bool", source, "data", true);
		checkTemplateOutput("int", source, "data", 42);
		checkTemplateOutput("float", source, "data", 4.2);
		checkTemplateOutput("str", source, "data", "foo");
		checkTemplateOutput("date", source, "data", new Date());
		checkTemplateOutput("list", source, "data", asList(1, 2));
		checkTemplateOutput("dict", source, "data", makeMap(1, 2));
		checkTemplateOutput("template", source, "data", getTemplate(""));
		checkTemplateOutput("color", source, "data", new Color(0, 0, 0));

		String sourcekw = "<?print type(obj=data)?>";
		checkTemplateOutput("none", sourcekw, "data", null);
	}

	@Test
	public void function_reversed()
	{
		String source = "<?for i in reversed(x)?>(<?print i?>)<?end for?>";
		checkTemplateOutput("(3)(2)(1)", source, "x", "123");
		checkTemplateOutput("(3)(2)(1)", source, "x", asList(1, 2, 3));

		String sourcekw = "<?for i in reversed(sequence=x)?>(<?print i?>)<?end for?>";
		checkTemplateOutput("(3)(2)(1)", sourcekw, "x", "123");
	}

	@Test
	public void function_urlquote()
	{
		checkTemplateOutput("gurk", "<?print urlquote('gurk')?>");
		checkTemplateOutput("%3C%3D%3E%2B%3F", "<?print urlquote('<=>+?')?>");
		checkTemplateOutput("%7F%C3%BF%EF%BF%BF", "<?print urlquote('\u007f\u00ff\uffff')?>");

		checkTemplateOutput("gurk", "<?print urlquote(string='gurk')?>");
	}

	@Test
	public void function_urlunquote()
	{
		checkTemplateOutput("gurk", "<?print urlunquote('gurk')?>");
		checkTemplateOutput("<=>+?", "<?print urlunquote('%3C%3D%3E%2B%3F')?>");
		checkTemplateOutput("\u007f\u00ff\uffff", "<?print urlunquote('%7F%C3%BF%EF%BF%BF')?>");

		checkTemplateOutput("gurk", "<?print urlunquote(string='gurk')?>");
	}

	@Test
	public void function_rgb()
	{
		checkTemplateOutput("#369", "<?print repr(rgb(0.2, 0.4, 0.6))?>");
		checkTemplateOutput("#369c", "<?print repr(rgb(0.2, 0.4, 0.6, 0.8))?>");

		checkTemplateOutput("#369c", "<?print repr(rgb(r=0.2, g=0.4, b=0.6, a=0.8))?>");
	}

	@Test
	public void function_hls()
	{
		checkTemplateOutput("#fff", "<?print repr(hls(0, 1, 0))?>");
		checkTemplateOutput("#fff0", "<?print repr(hls(0, 1, 0, 0))?>");

		checkTemplateOutput("#fff0", "<?print repr(hls(h=0, l=1, s=0, a=0))?>");
	}

	@Test
	public void function_hsv()
	{
		checkTemplateOutput("#fff", "<?print repr(hsv(0, 0, 1))?>");
		checkTemplateOutput("#fff0", "<?print repr(hsv(0, 0, 1, 0))?>");

		checkTemplateOutput("#fff0", "<?print repr(hsv(h=0, s=0, v=1, a=0))?>");
	}

	@Test
	public void method_upper()
	{
		checkTemplateOutput("GURK", "<?print 'gurk'.upper()?>");
	}

	@Test
	public void method_lower()
	{
		checkTemplateOutput("gurk", "<?print 'GURK'.lower()?>");
	}

	@Test
	public void method_capitalize()
	{
		checkTemplateOutput("Gurk", "<?print 'gURK'.capitalize()?>");
	}

	@Test
	public void method_startswith()
	{
		checkTemplateOutput("True", "<?print 'gurkhurz'.startswith('gurk')?>");
		checkTemplateOutput("False", "<?print 'gurkhurz'.startswith('hurz')?>");

		checkTemplateOutput("False", "<?print 'gurkhurz'.startswith(prefix='hurz')?>");
	}

	@Test
	public void method_endswith()
	{
		checkTemplateOutput("True", "<?print 'gurkhurz'.endswith('hurz')?>");
		checkTemplateOutput("False", "<?print 'gurkhurz'.endswith('gurk')?>");

		checkTemplateOutput("False", "<?print 'gurkhurz'.endswith(suffix='gurk')?>");
	}

	@Test
	public void method_strip()
	{
		checkTemplateOutput("gurk", "<?print obj.strip()?>", "obj", " \t\r\ngurk \t\r\n");
		checkTemplateOutput("gurk", "<?print obj.strip('xyz')?>", "obj", "xyzzygurkxyzzy");

		checkTemplateOutput("gurk", "<?print obj.strip(chars='xyz')?>", "obj", "xyzzygurkxyzzy");
	}

	@Test
	public void method_lstrip()
	{
		checkTemplateOutput("gurk \t\r\n", "<?print obj.lstrip()?>", "obj", " \t\r\ngurk \t\r\n");
		checkTemplateOutput("gurkxyzzy", "<?print obj.lstrip(arg)?>", "obj", "xyzzygurkxyzzy", "arg", "xyz");

		checkTemplateOutput("gurkxyzzy", "<?print obj.lstrip(chars=arg)?>", "obj", "xyzzygurkxyzzy", "arg", "xyz");
	}

	@Test
	public void method_rstrip()
	{
		checkTemplateOutput(" \t\r\ngurk", "<?print obj.rstrip()?>", "obj", " \t\r\ngurk \t\r\n");
		checkTemplateOutput("xyzzygurk", "<?print obj.rstrip(arg)?>", "obj", "xyzzygurkxyzzy", "arg", "xyz");

		checkTemplateOutput("xyzzygurk", "<?print obj.rstrip(chars=arg)?>", "obj", "xyzzygurkxyzzy", "arg", "xyz");
	}

	@Test
	public void method_split()
	{
		checkTemplateOutput("(f)(o)(o)", "<?for item in obj.split()?>(<?print item?>)<?end for?>", "obj", " \t\r\nf \t\r\no \t\r\no \t\r\n");
		checkTemplateOutput("(f)(o \t\r\no \t\r\n)", "<?for item in obj.split(None, 1)?>(<?print item?>)<?end for?>", "obj", " \t\r\nf \t\r\no \t\r\no \t\r\n");
		checkTemplateOutput("()(f)(o)(o)()", "<?for item in obj.split(arg)?>(<?print item?>)<?end for?>", "obj", "xxfxxoxxoxx", "arg", "xx");
		checkTemplateOutput("()(f)(o)(o)()", "<?for item in obj.split(arg, None)?>(<?print item?>)<?end for?>", "obj", "xxfxxoxxoxx", "arg", "xx");
		checkTemplateOutput("()(f)(oxxoxx)", "<?for item in obj.split(arg, 2)?>(<?print item?>)<?end for?>", "obj", "xxfxxoxxoxx", "arg", "xx");

		checkTemplateOutput("()(f)(oxxoxx)", "<?for item in obj.split(sep=arg, count=2)?>(<?print item?>)<?end for?>", "obj", "xxfxxoxxoxx", "arg", "xx");
	}

	@Test
	public void method_rsplit()
	{
		checkTemplateOutput("(f)(o)(o)", "<?for item in obj.rsplit()?>(<?print item?>)<?end for?>", "obj", " \t\r\nf \t\r\no \t\r\no \t\r\n");
		checkTemplateOutput("( \t\r\nf \t\r\no)(o)", "<?for item in obj.rsplit(None, 1)?>(<?print item?>)<?end for?>", "obj", " \t\r\nf \t\r\no \t\r\no \t\r\n");
		checkTemplateOutput("()(f)(o)(o)()", "<?for item in obj.rsplit(arg)?>(<?print item?>)<?end for?>", "obj", "xxfxxoxxoxx", "arg", "xx");
		checkTemplateOutput("()(f)(o)(o)()", "<?for item in obj.rsplit(arg, None)?>(<?print item?>)<?end for?>", "obj", "xxfxxoxxoxx", "arg", "xx");
		checkTemplateOutput("(xxfxxo)(o)()", "<?for item in obj.rsplit(arg, 2)?>(<?print item?>)<?end for?>", "obj", "xxfxxoxxoxx", "arg", "xx");

		checkTemplateOutput("(xxfxxo)(o)()", "<?for item in obj.rsplit(sep=arg, count=2)?>(<?print item?>)<?end for?>", "obj", "xxfxxoxxoxx", "arg", "xx");
	}

	@Test
	public void method_replace()
	{
		checkTemplateOutput("goork", "<?print 'gurk'.replace('u', 'oo')?>");
		checkTemplateOutput("fuuuu", "<?print 'foo'.replace('o', 'uu', None)?>");
		checkTemplateOutput("fuuo", "<?print 'foo'.replace('o', 'uu', 1)?>");
		checkTemplateOutput("fuuo", "<?print 'foo'.replace(old='o', new='uu', count=1)?>");
	}

	@Test
	public void method_renders()
	{
		InterpretedTemplate t1 = getTemplate("(<?print data?>)", "t1");

		checkTemplateOutput("(GURK)", "<?print t.renders(data='gurk').upper()?>", "t", t1);

		InterpretedTemplate t2 = getTemplate("(gurk)", "t2");
		checkTemplateOutput("(GURK)", "<?print t.renders().upper()?>", "t", t2);
	}

	@Test
	public void method_render()
	{
		InterpretedTemplate t1 = getTemplate("<?print prefix?><?print data?><?print suffix?>");
		InterpretedTemplate t2 = getTemplate("<?print 'foo'?>");

		checkTemplateOutput("(f)(o)(o)", "<?for c in data?><?code t.render(data=c, prefix='(', suffix=')')?><?end for?>", "t", t1, "data", "foo");
		checkTemplateOutput("foo", "<?print t.render()?>", "t", t2);
		checkTemplateOutput("foo", "<?print t.render \n\t(\n \t)\n\t ?>", "t", t2);

		checkTemplateOutput("42", "<?code globals.template.render(value=42)?>", "globals", makeMap("template", getTemplate("<?print value?>")));
		checkTemplateOutput("", "<?code globals.template.render(value=42)?>", "globals", makeMap("template", getTemplate("")));
	}

	@Test
	public void method_render_local_vars()
	{
		InterpretedTemplate t = getTemplate("<?code x += 1?><?print x?>");

		checkTemplateOutput("42,43,42", "<?print x?>,<?code t.render(x=x)?>,<?print x?>", "t", t, "x", 42);
	}

	@Test
	public void method_render_localtemplate()
	{
		checkTemplateOutput("foo", "<?def lower?><?print x.lower()?><?end def?><?print lower.renders(x='FOO')?>");
	}

	@Test
	public void method_render_nested()
	{
		String source = (
			"<?def outer?>" +
				"<?def inner?>" +
					"<?code x += 1?>" +
					"<?code y += 1?>" +
					"<?print x?>!" +
					"<?print y?>!" +
				"<?end def?>" +
				"<?code x += 1?>" +
				"<?code y += 1?>" +
				"<?code inner.render(x=x)?>" +
				"<?print x?>!" +
				"<?print y?>!" +
			"<?end def?>" +
			"<?code x += 1?>" +
			"<?code y += 1?>" +
			"<?code outer.render(x=x)?>" +
			"<?print x?>!" +
			"<?print y?>!"
		);
		checkTemplateOutput("45!43!44!43!43!43!", source, "x", 42, "y", 42);
	}

	@Test
	public void method_mimeformat()
	{
		Date t = FunctionDate.call(2010, 2, 22, 12, 34, 56);
		checkTemplateOutput("Mon, 22 Feb 2010 12:34:56 GMT", "<?print data.mimeformat()?>", "data", t);
	}

	@Test
	public void method_items()
	{
		checkTemplateOutput("a:42;b:17;c:23;", "<?for (key, value) in sorted(data.items())?><?print key?>:<?print value?>;<?end for?>", "data", makeMap("a", 42, "b", 17, "c", 23));
		checkTemplateOutput("x:17;y:23;", "<?for (key, value) in data.items()?><?print key?>:<?print value?>;<?end for?>", "data", new Point(17, 23));
	}

	@Test
	public void method_values()
	{
		checkTemplateOutput("17;23;42;", "<?for value in sorted(data.values())?><?print value?>;<?end for?>", "data", makeMap("a", 42, "b", 17, "c", 23));
		checkTemplateOutput("17;23;", "<?for value in data.values()?><?print value?>;<?end for?>", "data", new Point(17, 23));
	}

	@Test
	public void method_get()
	{
		checkTemplateOutput("42", "<?print {}.get('foo', 42)?>");
		checkTemplateOutput("17", "<?print {'foo': 17}.get('foo', 42)?>");
		checkTemplateOutput("", "<?print {}.get('foo')?>");
		checkTemplateOutput("17", "<?print {'foo': 17}.get('foo')?>");

		checkTemplateOutput("17", "<?print {'foo': 17}.get(key='foo', default=42)?>");
	}

	@Test
	public void method_r_g_b_a()
	{
		checkTemplateOutput("0x11", "<?code c = #123?><?print hex(c.r())?>");
		checkTemplateOutput("0x22", "<?code c = #123?><?print hex(c.g())?>");
		checkTemplateOutput("0x33", "<?code c = #123?><?print hex(c.b())?>");
		checkTemplateOutput("0xff", "<?code c = #123?><?print hex(c.a())?>");
	}

	@Test
	public void method_hls()
	{
		checkTemplateOutput("0", "<?code c = #fff?><?print int(c.hls()[0])?>");
		checkTemplateOutput("1", "<?code c = #fff?><?print int(c.hls()[1])?>");
		checkTemplateOutput("0", "<?code c = #fff?><?print int(c.hls()[2])?>");
	}

	@Test
	public void method_hlsa()
	{
		checkTemplateOutput("0", "<?code c = #fff?><?print int(c.hlsa()[0])?>");
		checkTemplateOutput("1", "<?code c = #fff?><?print int(c.hlsa()[1])?>");
		checkTemplateOutput("0", "<?code c = #fff?><?print int(c.hlsa()[2])?>");
		checkTemplateOutput("1", "<?code c = #fff?><?print int(c.hlsa()[3])?>");
	}

	@Test
	public void method_hsv()
	{
		checkTemplateOutput("0", "<?code c = #fff?><?print int(c.hsv()[0])?>");
		checkTemplateOutput("0", "<?code c = #fff?><?print int(c.hsv()[1])?>");
		checkTemplateOutput("1", "<?code c = #fff?><?print int(c.hsv()[2])?>");
	}

	@Test
	public void method_hsva()
	{
		checkTemplateOutput("0", "<?code c = #fff?><?print int(c.hsva()[0])?>");
		checkTemplateOutput("0", "<?code c = #fff?><?print int(c.hsva()[1])?>");
		checkTemplateOutput("1", "<?code c = #fff?><?print int(c.hsva()[2])?>");
		checkTemplateOutput("1", "<?code c = #fff?><?print int(c.hsva()[3])?>");
	}

	@Test
	public void method_lum()
	{
		checkTemplateOutput("True", "<?print #fff.lum() == 1?>");
	}

	@Test
	public void method_withlum()
	{
		checkTemplateOutput("#fff", "<?print #000.withlum(1)?>");

		checkTemplateOutput("#fff", "<?print #000.withlum(lum=1)?>");
	}

	@Test
	public void method_witha()
	{
		checkTemplateOutput("#0063a82a", "<?print repr(#0063a8.witha(42))?>");

		checkTemplateOutput("#0063a82a", "<?print repr(#0063a8.witha(a=42))?>");
	}

	@Test
	public void method_join()
	{
		checkTemplateOutput("1,2,3,4", "<?print ','.join('1234')?>");
		checkTemplateOutput("1,2,3,4", "<?print ','.join(['1', '2', '3', '4'])?>");

		checkTemplateOutput("1,2,3,4", "<?print ','.join(iterable='1234')?>");
	}

	@Test
	public void method_find()
	{
		checkTemplateOutput("-1", "<?print s.find('ks')?>", "s", "gurkgurk");
		checkTemplateOutput("2", "<?print s.find('rk')?>", "s", "gurkgurk");
		checkTemplateOutput("2", "<?print s.find('rk', 2)?>", "s", "gurkgurk");
		checkTemplateOutput("2", "<?print s.find('rk', 2, 4)?>", "s", "gurkgurk");
		checkTemplateOutput("6", "<?print s.find('rk', 4, 8)?>", "s", "gurkgurk");
		checkTemplateOutput("5", "<?print s.find('ur', -4, -1)?>", "s", "gurkgurk");
		checkTemplateOutput("-1", "<?print s.find('rk', 2, 3)?>", "s", "gurkgurk");
		checkTemplateOutput("-1", "<?print s.find('rk', 7)?>", "s", "gurkgurk");

		checkTemplateOutput("-1", "<?print l.find('x')?>", "l", asList("g", "u", "r", "k", "g", "u", "r", "k"));
		checkTemplateOutput("2", "<?print l.find('r')?>", "l", asList("g", "u", "r", "k", "g", "u", "r", "k"));
		checkTemplateOutput("2", "<?print l.find('r', 2)?>", "l", asList("g", "u", "r", "k", "g", "u", "r", "k"));
		checkTemplateOutput("2", "<?print l.find('r', 2, 4)?>", "l", asList("g", "u", "r", "k", "g", "u", "r", "k"));
		checkTemplateOutput("6", "<?print l.find('r', 4, 8)?>", "l", asList("g", "u", "r", "k", "g", "u", "r", "k"));
		checkTemplateOutput("6", "<?print l.find('r', -3, -1)?>", "l", asList("g", "u", "r", "k", "g", "u", "r", "k"));
		checkTemplateOutput("-1", "<?print l.find('r', 2, 2)?>", "l", asList("g", "u", "r", "k", "g", "u", "r", "k"));
		checkTemplateOutput("-1", "<?print l.find('r', 7)?>", "l", asList("g", "u", "r", "k", "g", "u", "r", "k"));
		checkTemplateOutput("2", "<?print l.find(None)?>", "l", asList("g", "u", null, "k", "g", "u", "r", "k"));

		checkTemplateOutput("2", "<?print s.find(sub='rk', start=2, end=4)?>", "s", "gurkgurk");
	}

	@Test
	public void method_rfind()
	{
		checkTemplateOutput("-1", "<?print s.rfind('ks')?>", "s", "gurkgurk");
		checkTemplateOutput("6", "<?print s.rfind('rk')?>", "s", "gurkgurk");
		checkTemplateOutput("6", "<?print s.rfind('rk', 2)?>", "s", "gurkgurk");
		checkTemplateOutput("2", "<?print s.rfind('rk', 2, 4)?>", "s", "gurkgurk");
		checkTemplateOutput("6", "<?print s.rfind('rk', 4, 8)?>", "s", "gurkgurk");
		checkTemplateOutput("5", "<?print s.rfind('ur', -4, -1)?>", "s", "gurkgurk");
		checkTemplateOutput("-1", "<?print s.rfind('rk', 2, 3)?>", "s", "gurkgurk");
		checkTemplateOutput("-1", "<?print s.rfind('rk', 7)?>", "s", "gurkgurk");

		checkTemplateOutput("-1", "<?print l.rfind('x')?>", "l", asList("g", "u", "r", "k", "g", "u", "r", "k"));
		checkTemplateOutput("6", "<?print l.rfind('r')?>", "l", asList("g", "u", "r", "k", "g", "u", "r", "k"));
		checkTemplateOutput("6", "<?print l.rfind('r', 2)?>", "l", asList("g", "u", "r", "k", "g", "u", "r", "k"));
		checkTemplateOutput("2", "<?print l.rfind('r', 2, 4)?>", "l", asList("g", "u", "r", "k", "g", "u", "r", "k"));
		checkTemplateOutput("6", "<?print l.rfind('r', 4, 8)?>", "l", asList("g", "u", "r", "k", "g", "u", "r", "k"));
		checkTemplateOutput("6", "<?print l.rfind('r', -3, -1)?>", "l", asList("g", "u", "r", "k", "g", "u", "r", "k"));
		checkTemplateOutput("-1", "<?print l.rfind('r', 2, 2)?>", "l", asList("g", "u", "r", "k", "g", "u", "r", "k"));
		checkTemplateOutput("-1", "<?print l.rfind('r', 7)?>", "l", asList("g", "u", "r", "k", "g", "u", "r", "k"));
		checkTemplateOutput("2", "<?print l.rfind(None)?>", "l", asList("g", "u", null, "k", "g", "u", "r", "k"));

		checkTemplateOutput("2", "<?print s.rfind(sub='rk', start=2, end=4)?>", "s", "gurkgurk");
	}

	@Test
	public void method_day()
	{
		checkTemplateOutput("12", "<?print @(2010-05-12).day()?>");
		checkTemplateOutput("12", "<?print d.day()?>", "d", FunctionDate.call(2010, 5, 12));
	}

	@Test
	public void method_month()
	{
		checkTemplateOutput("5", "<?print @(2010-05-12).month()?>");
		checkTemplateOutput("5", "<?print d.month()?>", "d", FunctionDate.call(2010, 5, 12));
	}

	@Test
	public void method_year()
	{
		checkTemplateOutput("2010", "<?print @(2010-05-12).year()?>");
		checkTemplateOutput("2010", "<?print d.year()?>", "d", FunctionDate.call(2010, 5, 12));
	}

	@Test
	public void method_hour()
	{
		checkTemplateOutput("16", "<?print @(2010-05-12T16:47:56).hour()?>");
		checkTemplateOutput("16", "<?print d.hour()?>", "d", FunctionDate.call(2010, 5, 12, 16, 47, 56));
	}

	@Test
	public void method_minute()
	{
		checkTemplateOutput("47", "<?print @(2010-05-12T16:47:56).minute()?>");
		checkTemplateOutput("47", "<?print d.minute()?>", "d", FunctionDate.call(2010, 5, 12, 16, 47, 56));
	}

	@Test
	public void method_second()
	{
		checkTemplateOutput("56", "<?print @(2010-05-12T16:47:56).second()?>");
		checkTemplateOutput("56", "<?print d.second()?>", "d", FunctionDate.call(2010, 5, 12, 16, 47, 56));
	}

	@Test
	public void method_microsecond()
	{
		checkTemplateOutput("123000", "<?print @(2010-05-12T16:47:56.123000).microsecond()?>");
		checkTemplateOutput("123000", "<?print d.microsecond()?>", "d", FunctionDate.call(2010, 5, 12, 16, 47, 56, 123000));
	}

	@Test
	public void method_weekday()
	{
		checkTemplateOutput("2", "<?print @(2010-05-12).weekday()?>");
		checkTemplateOutput("2", "<?print d.weekday()?>", "d", FunctionDate.call(2010, 5, 12));
	}

	@Test
	public void method_yearday()
	{
		checkTemplateOutput("1", "<?print @(2010-01-01).yearday()?>");
		checkTemplateOutput("366", "<?print @(2008-12-31).yearday()?>");
		checkTemplateOutput("365", "<?print @(2010-12-31).yearday()?>");
		checkTemplateOutput("132", "<?print @(2010-05-12).yearday()?>");
		checkTemplateOutput("132", "<?print @(2010-05-12T16:47:56).yearday()?>");
		checkTemplateOutput("132", "<?print d.yearday()?>", "d", FunctionDate.call(2010, 5, 12));
		checkTemplateOutput("132", "<?print d.yearday()?>", "d", FunctionDate.call(2010, 5, 12, 16, 47, 56));
	}

	@Test
	public void method_append()
	{
		checkTemplateOutput("[17, 23, 42]", "<?code l = [17]?><?code l.append(23, 42)?><?print l?>");
	}

	@Test
	public void method_insert()
	{
		checkTemplateOutput("[1, 2, 3, 4]", "<?code l = [1,4]?><?code l.insert(1, 2, 3)?><?print l?>");
	}

	@Test
	public void method_pop()
	{
		checkTemplateOutput("42;17;23;", "<?code l = [17, 23, 42]?><?print l.pop()?>;<?print l.pop(-2)?>;<?print l.pop(0)?>;");
	}

	@Test
	public void method_update()
	{
		checkTemplateOutput("0", "<?code d = {}?><?code d.update()?><?print len(d)?>");
		checkTemplateOutput("1", "<?code d = {}?><?code d.update([['one', 1]])?><?print d.one?>");
		checkTemplateOutput("1", "<?code d = {}?><?code d.update({'one': 1})?><?print d.one?>");
		checkTemplateOutput("1", "<?code d = {}?><?code d.update(one=1)?><?print d.one?>");
		checkTemplateOutput("1", "<?code d = {}?><?code d.update([['one', 0]], {'one': 0}, one=1)?><?print d.one?>");
	}

	@Test
	public void parse()
	{
		checkTemplateOutput("42", "<?print data.Noner?>", "data", makeMap("Noner", 42));
	}

	@CauseTest(expectedCause=SyntaxException.class)
	public void lexer_error()
	{
		checkTemplateOutput("", "<?print ??>");
	}

	@CauseTest(expectedCause=SyntaxException.class)
	public void parser_error()
	{
		checkTemplateOutput("", "<?print 1++2?>");
	}

	@Test
	public void tag_note()
	{
		checkTemplateOutput("foo", "f<?note This is?>o<?note a comment?>o");
	}

	@Test
	public void templateattributes_1()
	{
		String source = "<?print x?>";
		InterpretedTemplate t = getTemplate(source);

		checkTemplateOutput("<?", "<?print template.startdelim?>", "template", t);
		checkTemplateOutput("?>", "<?print template.enddelim?>", "template", t);
		checkTemplateOutput(source, "<?print template.source?>", "template", t);
		checkTemplateOutput("1", "<?print len(template.content)?>", "template", t);
		checkTemplateOutput("print", "<?print template.content[0].type?>", "template", t);
		checkTemplateOutput(source, "<?print template.content[0].location.tag?>", "template", t);
		checkTemplateOutput("x", "<?print template.content[0].location.code?>", "template", t);
		checkTemplateOutput("var", "<?print template.content[0].obj.type?>", "template", t);
		checkTemplateOutput("x", "<?print template.content[0].obj.name?>", "template", t);
	}

	@Test
	public void templateattributes_2()
	{
		String source = "<?printx 42?>";
		InterpretedTemplate t = getTemplate(source);

		checkTemplateOutput("printx", "<?print template.content[0].type?>", "template", t);
		checkTemplateOutput("const", "<?print template.content[0].obj.type?>", "template", t);
		checkTemplateOutput("42", "<?print template.content[0].obj.value?>", "template", t);
	}

	@Test
	public void templateattributes_3()
	{
		String source = "foo";
		InterpretedTemplate t = getTemplate(source);

		checkTemplateOutput("text", "<?print template.content[0].type?>", "template", t);
		checkTemplateOutput("foo", "<?print template.content[0].text?>", "template", t);
	}

	@Test
	public void templateattributes_localtemplate()
	{
		String source = "<?def lower?><?print t.lower()?><?end def?>";

		checkTemplateOutput(source + "<?print lower.source?>", source + "<?print lower.source?>");
		checkTemplateOutput(source, source + "<?print lower.source[lower.location.starttag:lower.endlocation.endtag]?>");
		checkTemplateOutput("<?print t.lower()?>", source + "<?print lower.source[lower.location.endtag:lower.endlocation.starttag]?>");
		checkTemplateOutput("lower", source + "<?print lower.name?>");
	}

	@Test
	public void nestedscopes()
	{
		checkTemplateOutput("0;1;2;", "<?for i in range(3)?><?def x?><?print i?>;<?end def?><?code x.render()?><?end for?>");
		checkTemplateOutput("1;", "<?for i in range(3)?><?if i == 1?><?def x?><?print i?>;<?end def?><?end if?><?end for?><?code x.render()?>");
		checkTemplateOutput("1", "<?code i = 1?><?def x?><?print i?><?end def?><?code i = 2?><?code x.render()?>");
		checkTemplateOutput("1", "<?code i = 1?><?def x?><?def y?><?print i?><?end def?><?code i = 2?><?code y.render()?><?end def?><?code i = 3?><?code x.render()?>");
	}

	@Test
	public void pass_functions()
	{
		checkTemplateOutput("&lt;", "<?def x?><?print x('<')?><?end def?><?code x.render(x=xmlescape)?>");
	}

	@Test
	public void function()
	{
		checkTemplateResult(42, "<?return 42?>");
	}

	@Test
	public void function_value()
	{
		checkTemplateResult(84, "<?return 2*x?>", "x", 42);
	}

	@Test
	public void function_multiple_returnvalues()
	{
		checkTemplateResult(84, "<?return 2*x?><?return 3*x?>", "x", 42);
	}

	@Test
	public void function_name()
	{
		checkTemplateResult("f", "<?def f?><?return f.name?><?end def?><?return f(f=f)?>");
	}

	@Test
	public void function_closure()
	{
		checkTemplateResult(24, "<?code y=3?><?def inner?><?return 2*x*y?><?end def?><?return inner(x=4)?>");
		checkTemplateResult(24, "<?def outer?><?code y=3?><?def inner?><?return 2*x*y?><?end def?><?return inner?><?end def?><?return outer()(x=4)?>");
	}

	@Test
	public void return_in_template()
	{
		checkTemplateOutput("gurk", "gurk<?return 42?>hurz");
	}

	@Test
	public void keepWhitespace()
	{
		InterpretedTemplate template1 = getTemplate("<?if True?> foo<?end if?>");
		assertEquals(template1.renders(), " foo");

		InterpretedTemplate template2 = getTemplate("<?if True?> foo\n bar<?end if?>");
		assertEquals(template2.renders(), " foobar");

		InterpretedTemplate template3 = getTemplate("<?if True?>\n foo\n bar<?end if?>");
		assertEquals(template3.renders(), "foobar");
	}

	private InterpretedTemplate universaltemplate()
	{
		return getTemplate(
			"text" +
			"<?xml version='1.0' encoding='utf-8'?>" + // will not be recognized as a tag
			"<?code x = 'gurk'?>" +
			"<?code x = 42?>" +
			"<?code x = 4.2?>" +
			"<?code x = None?>" +
			"<?code x = False?>" +
			"<?code x = True?>" +
			"<?code x = @(2009-01-04)?>" +
			"<?code x = @(2009-01-04T)?>" +
			"<?code x = @(2009-01-04T12:34)?>" +
			"<?code x = @(2009-01-04T12:34:56)?>" +
			"<?code x = @(2009-01-04T12:34:56.987654)?>" +
			"<?code x = #0063a8?>" +
			"<?code x = [42]?>" +
			"<?code x = {'fortytwo': 42}?>" +
			"<?code x = {**{'fortytwo': 42}}?>" +
			"<?code x = y?>" +
			"<?code x += 42?>" +
			"<?code x -= 42?>" +
			"<?code x *= 42?>" +
			"<?code x /= 42?>" +
			"<?code x //= 42?>" +
			"<?code x %= 42?>" +
			"<?print x.gurk?>" +
			"<?print x['gurk']?>" +
			"<?print x[1:2]?>" +
			"<?print x[1:]?>" +
			"<?print x[:2]?>" +
			"<?print x[:]?>" +
			"<?printx x?>" +
			"<?for x in '12'?><?print x?><?break?><?continue?><?end for?>" +
			"<?print not x?>" +
			"<?print -x?>" +
			"<?print x in y?>" +
			"<?print x not in y?>" +
			"<?print x==y?>" +
			"<?print x!=y?>" +
			"<?print x<y?>" +
			"<?print x<=y?>" +
			"<?print x>y?>" +
			"<?print x>=y?>" +
			"<?print x+y?>" +
			"<?print x*y?>" +
			"<?print x/y?>" +
			"<?print x//y?>" +
			"<?print x and y?>" +
			"<?print x or y?>" +
			"<?print x % y?>" +
			"<?print now()?>" +
			"<?print repr(1)?>" +
			"<?print range(1, 2)?>" +
			"<?print range(1, 2, 3)?>" +
			"<?print rgb(1, 2, 3, 4)?>" +
			"<?print x.r()?>" +
			"<?print x.find(1)?>" +
			"<?print x.find(1, 2)?>" +
			"<?print x.find(1, 2, 3)?>" +
			"<?if x?>gurk<?elif y?>hurz<?else?>hinz<?end if?>" +
			"<?def x?>foo<?end def?>"
		);
	}

	@Test
	public void template_str()
	{
		universaltemplate().toString();
	}

	@Test
	public void reader() throws IOException
	{
		String li = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
		InterpretedTemplate template = getTemplate("<?for i in range(100)?>" + li + "<?end for?>", "reader");

		Reader reader = template.reader(null);

		int c;
		StringBuilder buffer = new StringBuilder();
		while ((c = reader.read()) != -1)
		{
			buffer.append((char)c);
		}

		StringBuilder expected = new StringBuilder();
		for (int i = 0; i < 100; i++)
			expected.append(li);

		assertEquals(buffer.toString(), expected.toString());
	}
}
