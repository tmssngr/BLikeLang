package de.regnis.b.node;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Thomas Singer
 */
public class BasicTypesTest {

	// Accessing ==============================================================

	@Test
	public void testNumberLiterals() {
		assertSame(BasicTypes.UINT8, BasicTypes.determineType(0));
		assertSame(BasicTypes.UINT8, BasicTypes.determineType(1));
		assertSame(BasicTypes.UINT8, BasicTypes.determineType(127));
		assertSame(BasicTypes.UINT8, BasicTypes.determineType(128));
		assertSame(BasicTypes.UINT8, BasicTypes.determineType(255));
		assertSame(BasicTypes.INT16, BasicTypes.determineType(256));
		assertSame(BasicTypes.INT16, BasicTypes.determineType(32767));
		assertSame(BasicTypes.UINT16, BasicTypes.determineType(32768));
		assertSame(BasicTypes.UINT16, BasicTypes.determineType(65535));
		try {
			BasicTypes.determineType(65536);
			fail();
		}
		catch (InvalidTypeException ignored) {
		}

		assertSame(BasicTypes.INT8, BasicTypes.determineType(-1));
		assertSame(BasicTypes.INT8, BasicTypes.determineType(-128));
		assertSame(BasicTypes.INT16, BasicTypes.determineType(-129));
		assertSame(BasicTypes.INT16, BasicTypes.determineType(-32768));

		try {
			BasicTypes.determineType(-32769);
			fail();
		}
		catch (InvalidTypeException ignored) {
		}
	}
}
