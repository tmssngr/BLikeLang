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
}
