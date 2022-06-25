package de.regnis.b.node;

import de.regnis.b.ExpressionVisitor;

/**
 * @author Thomas Singer
 */
public abstract class ExpressionNode extends Node {

	// Abstract ===============================================================

	public abstract <O> O visit(ExpressionVisitor<O> visitor);
}