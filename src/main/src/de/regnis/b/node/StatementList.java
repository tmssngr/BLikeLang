package de.regnis.b.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class StatementList extends Statement {

	// Fields =================================================================

	private final List<Statement> statementList = new ArrayList<>();

	// Setup ==================================================================

	public StatementList() {
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		for (Statement node : statementList) {
			if (buffer.length() > 0) {
				buffer.append('\n');
			}
			buffer.append(node);
		}
		return buffer.toString();
	}

	@Override
	public <O> O visit(StatementVisitor<O> visitor) {
		return visitor.visitStatementList(this);
	}

	// Accessing ==============================================================

	public StatementList add(Statement node) {
		statementList.add(node);
		return this;
	}

	public List<? extends Statement> getStatements() {
		return Collections.unmodifiableList(statementList);
	}
}