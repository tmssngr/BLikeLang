package de.regnis.b.node;

import node.NodeVisitor;

/**
 * @author Thomas Singer
 */
public abstract class StatementNode extends Node {
	public abstract void visit(NodeVisitor visitor);
}
