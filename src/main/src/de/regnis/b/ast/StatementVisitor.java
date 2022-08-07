package de.regnis.b.ast;

/**
 * @author Thomas Singer
 */
public interface StatementVisitor<O> extends SimpleStatementVisitor<O> {
	O visitStatementList(StatementList node);

	O visitReturn(ReturnStatement node);

	O visitIf(IfStatement node);

	O visitWhile(WhileStatement node);

	O visitBreak(BreakStatement node);
}
