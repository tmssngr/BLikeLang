package de.regnis.b.ast;

/**
 * @author Thomas Singer
 */
public interface DeclarationVisitor<O> {

	default O visitConst(ConstDeclaration node) {
		throw new UnsupportedOperationException();
	}

	O visitFunctionDeclaration(FuncDeclaration node);
}
