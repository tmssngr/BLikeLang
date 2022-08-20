package de.regnis.utils;

/**
 * @author Thomas Singer
 */
public final class Tuple<FIRST, SECOND> {

	// Fields =================================================================

	public final FIRST first;
	public final SECOND second;

	// Setup ==================================================================

	public Tuple(FIRST first, SECOND second) {
		this.first  = first;
		this.second = second;
	}
}
