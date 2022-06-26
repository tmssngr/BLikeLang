package de.regnis.b.node;

/**
 * @author Thomas Singer
 */
public abstract class Declaration extends Node {

	public abstract <O> O visit(DeclarationVisitor<O> visitor);
}
