package de.regnis.b.ast;

/**
 * @author Thomas Singer
 */
public abstract class Declaration {

	public abstract <O> O visit(DeclarationVisitor<O> visitor);
}
