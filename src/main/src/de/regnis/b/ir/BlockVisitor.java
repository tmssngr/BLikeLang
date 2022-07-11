package de.regnis.b.ir;

/**
 * @author Thomas Singer
 */
public interface BlockVisitor {

	void visitBasic(BasicBlock block);

	void visitIf(IfBlock block);

	void visitWhile(WhileBlock block);

	void visitExit(ExitBlock block);
}
