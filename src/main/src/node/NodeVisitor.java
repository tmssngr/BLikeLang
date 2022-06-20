package node;

/**
 * @author Thomas Singer
 */
public interface NodeVisitor {
	void visitDeclaration(String var, int line, int column);

	void visitAssignment(String var, int line, int column);

	void visitVarRead(String var, int line, int column);
}
