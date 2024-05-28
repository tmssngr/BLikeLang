package de.regnis.b;

import de.regnis.b.ast.*;

import org.jetbrains.annotations.*;

/**
 * @author Thomas Singer
 */
public final class InvalidTokenException extends RuntimeException {
	public final Position position;

	public InvalidTokenException(@NotNull String message, @NotNull Position position) {
		super(message);
		this.position = position;
	}
}
