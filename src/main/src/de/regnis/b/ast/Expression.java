package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public interface Expression {

	<O> O visit(@NotNull ExpressionVisitor<O> visitor);
}
