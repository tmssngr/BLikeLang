package de.regnis.b.ast;

/**
 * @author Thomas Singer
 */
public interface DeclarationVisitor<O> {

	O visitFunctionDeclaration(FuncDeclaration node);
}
