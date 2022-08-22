package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public interface SimpleStatement extends Statement {

	<O> O visit(@NotNull SimpleStatementVisitor<O> visitor);
}
