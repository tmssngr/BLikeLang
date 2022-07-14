package de.regnis.b.ast;

/**
 * @author Thomas Singer
 */
public interface StatementVisitor<O> {
	O visitAssignment(Assignment node);

	O visitMemAssignment(MemAssignment node);

	O visitStatementList(StatementList node);

	O visitLocalVarDeclaration(VarDeclaration node);

	O visitCall(CallStatement node);

	O visitReturn(ReturnStatement node);

	O visitIf(IfStatement node);

	O visitWhile(WhileStatement node);

	O visitBreak(BreakStatement node);
}
