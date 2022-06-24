package de.regnis.b.node;

import node.NodeVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class StatementListNode extends StatementNode {

	// Fields =================================================================

	private final List<StatementNode> statementList = new ArrayList<>();

	// Setup ==================================================================

	public StatementListNode() {
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		for (StatementNode node : statementList) {
			if (buffer.length() > 0) {
				buffer.append('\n');
			}
			buffer.append(node);
		}
		return buffer.toString();
	}

	public void visit(NodeVisitor visitor) {
		for (StatementNode node : statementList) {
			node.visit(visitor);
		}
	}

	// Accessing ==============================================================

	public StatementListNode add(StatementNode node) {
		statementList.add(node);
		return this;
	}

	public List<? extends StatementNode> getStatements() {
		return Collections.unmodifiableList(statementList);
	}
}
