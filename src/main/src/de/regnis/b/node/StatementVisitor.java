package de.regnis.b.node;

/**
 * @author Thomas Singer
 */
public interface StatementVisitor<O> {
	O visitAssignment(Assignment node);

	O visitStatementList(StatementList node);

	O visitLocalVarDeclaration(VarDeclaration node);

	O visitCall(CallStatement node);

	O visitReturn(ReturnStatement node);

	O visitIf(IfStatement node);
}
