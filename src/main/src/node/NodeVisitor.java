package node;

import org.antlr.v4.runtime.Token;

/**
 * @author Thomas Singer
 */
public interface NodeVisitor {
	void visitAssignment(String var);

	void visitVarRead(String varName, Token token);
}
