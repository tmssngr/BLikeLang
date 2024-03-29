package de.regnis.b.type;

import de.regnis.b.ast.transformation.TransformationFailedException;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class BasicTypes {

	// Constants ==============================================================

	public static final NumericType UINT8 = new NumericType(0, 255, "u8");
	public static final NumericType UINT16 = new NumericType(0, 65535, "u16");
	public static final NumericType INT8 = new NumericType(-128, 127, "i8");
	public static final NumericType INT16 = new NumericType(-32768, 32767, "int");

	public static final Type VOID = new Type() {
		@Override
		public String toString() {
			return "void";
		}
	};
	public static final Type BOOLEAN = new Type() {
		@Override
		public String toString() {
			return "boolean";
		}
	};

	// Static =================================================================

	public static boolean canBeAssignedFrom(Type expectedType, Type providedType) {
		if (expectedType == providedType) {
			return true;
		}

		return expectedType instanceof final NumericType expected
				&& providedType instanceof final NumericType provided
				&& expected.min <= provided.min
				&& expected.max >= provided.max;
	}

	public static NumericType determineType(int value) {
		if (value < INT16.min || value > UINT16.max) {
			throw new InvalidTypeException("Value " + value + " is out of bounds.");
		}

		if (value < 0) {
			return value >= INT8.min
					? INT8
					: INT16;
		}

		if (value > INT16.max) {
			return UINT16;
		}

		return value <= UINT8.max
				? UINT8
				: INT16;
	}

	@NotNull
	public static Type getType(@NotNull String name, boolean allowVoid) {
		if (allowVoid && name.equals(VOID.toString())) {
			return VOID;
		}
		if (name.equals(BOOLEAN.toString())) {
			return BOOLEAN;
		}
		return getNumbericType(name);
	}

	@NotNull
	public static NumericType getNumbericType(@NotNull String name) {
		if (name.equals(UINT8.toString())) {
			return UINT8;
		}
		if (name.equals(UINT16.toString())) {
			return UINT16;
		}
		if (name.equals(INT8.toString())) {
			return INT8;
		}
		if (name.equals(INT16.toString())) {
			return INT16;
		}
		if (name.equals("int")) {
			return INT16;
		}
		throw new UnsupportedTypeException(name);
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

	// Inner Classes ==========================================================

	public static final class NumericType implements Type {
		public final int min;
		public final int max;
		private final String name;

		private NumericType(int min, int max, String name) {
			this.min  = min;
			this.max  = max;
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		public boolean isSigned() {
			return min < 0;
		}
	}

	public static class UnsupportedTypeException extends TransformationFailedException {
		public UnsupportedTypeException(@NotNull String message) {
			super(message);
		}
	}
}
