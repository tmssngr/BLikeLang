package de.regnis.b.ast;

/**
 * @author Thomas Singer
 */
public abstract class Declaration extends Node {

	public abstract <O> O visit(DeclarationVisitor<O> visitor);
}
