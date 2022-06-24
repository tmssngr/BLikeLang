package de.regnis.b.node;

/**
 * @author Thomas Singer
 */
public abstract class StatementNode extends Node {
	public abstract <O> O visit(StatementVisitor<O> visitor);
}
