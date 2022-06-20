package node;

import org.antlr.v4.runtime.Token;

/**
 * @author Thomas Singer
 */
public interface NodeVisitor {
	void visitDeclaration(String var, Token token);

	void visitAssignment(String var, Token token);

	void visitVarRead(String var, Token token);
}
