package de.regnis.b.node;

/**
 * @author Thomas Singer
 */
public final class BasicTypes implements Type {

	// Constants ==============================================================

	public static final Type UINT8 = new BasicTypes("uint8");
	public static final Type UINT16 = new BasicTypes("uint16");
	public static final Type INT8 = new BasicTypes("int8");
	public static final Type INT16 = new BasicTypes("int16");

	// Static =================================================================

	public static Type determineType(int value) {
		if (value < -(2 << 14) || value >= 2 << 15) {
			throw new InvalidTypeException("Value " + value + " is out of bounds.");
		}

		if (value < 0) {
			return value >= -(2 << 6)
					? INT8
					: INT16;
		}

		if (value >= 2 << 14) {
			return UINT16;
		}

		if (value >= 2 << 7) {
			return INT16;
		}

		return UINT8;
	}

	// Fields =================================================================

	private final String name;

	// Setup ==================================================================

	private BasicTypes(String name) {
		this.name = name;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return name;
	}
}
