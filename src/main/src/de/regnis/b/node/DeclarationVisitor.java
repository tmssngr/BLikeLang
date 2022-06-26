package de.regnis.b.node;

/**
 * @author Thomas Singer
 */
public interface DeclarationVisitor<O> {

	O visitGlobalVarDeclaration(GlobalVarDeclaration node);

	O visitFunctionDeclaration(FuncDeclaration node);
}
