package de.regnis.b.node;

/**
 * @author Thomas Singer
 */
public interface StatementVisitor<O> {
	O visitAssignment(AssignmentNode node);

	O visitStatementList(StatementListNode node);

	O visitLocalVarDeclaration(VarDeclarationNode node);
}
