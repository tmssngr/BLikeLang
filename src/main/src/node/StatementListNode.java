package node;

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

	// Accessing ==============================================================

	public void visit(NodeVisitor visitor) {
		for (StatementNode node : statementList) {
			node.visit(visitor);
		}
	}

	public void add(StatementNode node) {
		statementList.add(node);
	}

	public List<? extends StatementNode> getStatements() {
		return Collections.unmodifiableList(statementList);
	}
}
