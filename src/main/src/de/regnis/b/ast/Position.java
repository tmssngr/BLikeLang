package de.regnis.b.ast;

/**
 * @author Thomas Singer
 */
public record Position(int line, int column) {
	public static final Position DUMMY = new Position(-1, -1);
}
