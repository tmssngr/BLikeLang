package node;

import org.antlr.v4.runtime.Token;

/**
 * @author Thomas Singer
 */
public final class VarReadNode extends ExpressionNode {

	// Fields =================================================================

	private final Token varName;

	// Setup ==================================================================

	public VarReadNode(Token varName) {
		this.varName = varName;
	}

	// Implemented ============================================================

	@Override
	public String toString() {
		return "read(" + getVarName() + ")";
	}

	@Override
	public void visit(NodeVisitor visitor) {
		visitor.visitVarRead(getVarName(), varName);
	}

	// Accessing ==============================================================

	public String getVarName() {
		return varName.getText();
	}
}
