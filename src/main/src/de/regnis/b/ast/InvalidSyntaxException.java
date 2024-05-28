package de.regnis.b.ast;

import org.jetbrains.annotations.*;

/**
 * @author Thomas Singer
 */
public final class InvalidSyntaxException extends RuntimeException {

	public final Position position;

	public InvalidSyntaxException(@NotNull String message, @NotNull Position position) {
		super(message);
		this.position = position;
	}
}
