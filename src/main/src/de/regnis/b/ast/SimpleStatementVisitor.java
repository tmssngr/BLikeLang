package de.regnis.b.ast;

/**
 * @author Thomas Singer
 */
public interface SimpleStatementVisitor<O> {
	O visitAssignment(Assignment node);

	O visitLocalVarDeclaration(VarDeclaration node);

	O visitCall(CallStatement node);
}
