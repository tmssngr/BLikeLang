package de.regnis.b.ast;

/**
 * @author Thomas Singer
 */
public abstract class SimpleStatement extends Statement {

	public abstract <O> O visit(SimpleStatementVisitor<O> visitor);
}
