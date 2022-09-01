package de.regnis.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Thomas Singer
 */
public class UtilsTest {

	@Test
	public void testParseString() {
		Assert.assertEquals("", Utils.parseString("''", '\''));
		Assert.assertEquals(" ", Utils.parseString("' '", '\''));
		Assert.assertEquals("'\\\t\n\r\"", Utils.parseString("'\\'\\\\\\t\\n\\r\\\"'", '\''));

		Assert.assertNull(Utils.parseString("", '\''));
		Assert.assertNull(Utils.parseString("'", '\''));
		Assert.assertNull(Utils.parseString(" ", '\''));
		Assert.assertNull(Utils.parseString("' ", '\''));
		Assert.assertNull(Utils.parseString(" '", '\''));
		Assert.assertNull(Utils.parseString("\\1", '\''));
	}

	@Test
	public void testToHex() {
		Assert.assertEquals('0', Utils.toHex(0));
		Assert.assertEquals('0', Utils.toHex(16));
		Assert.assertEquals('0', Utils.toHex(0xF0));
		Assert.assertEquals('9', Utils.toHex(0xF9));
		Assert.assertEquals('A', Utils.toHex(0xFA));
		Assert.assertEquals('F', Utils.toHex(0xFF));

		Assert.assertEquals("ABCD", Utils.toHex4(0xabcd, new StringBuilder()).toString());
	}
}
