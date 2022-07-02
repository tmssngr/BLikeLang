package de.regnis.b.node;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public abstract class Statement extends Node {

	// Abstract ===============================================================

	public abstract <O> O visit(StatementVisitor<O> visitor);

	// Setup ==================================================================

	protected Statement() {
	}

	// Accessing ==============================================================

	@NotNull
	public StatementList toStatementList() {
		final StatementList statementList = new StatementList();
		statementList.add(this);
		return statementList;
	}
}
