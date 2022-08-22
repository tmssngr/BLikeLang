package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public interface Statement {

	<O> O visit(@NotNull StatementVisitor<O> visitor);

	@NotNull
	default StatementList toStatementList() {
		final StatementList statementList = new StatementList();
		statementList.add(this);
		return statementList;
	}
}
