package de.regnis.b.ast;

/**
 * @author Thomas Singer
 */
public interface Declaration {

	<O> O visit(DeclarationVisitor<O> visitor);
}
