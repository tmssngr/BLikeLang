package node;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class StatementListNode extends Node {

	// Fields =================================================================

	private final List<StatementNode> statementList = new ArrayList<>();

	// Setup ==================================================================

	public StatementListNode() {
	}

	// Accessing ==============================================================

	public void add(StatementNode node) {
		statementList.add(node);
	}
}
