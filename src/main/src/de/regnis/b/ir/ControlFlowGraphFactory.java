package de.regnis.b.ir;

import de.regnis.b.ast.*;

import java.util.List;

/**
 * @author Thomas Singer
 */
public class ControlFlowGraphFactory {

	// Accessing ==============================================================

	public BasicBlock createGraph(FuncDeclaration declaration) {
		final BasicBlock firstBlock = new BasicBlock();
		processStatements(firstBlock, declaration.statementList.getStatements());
		return firstBlock;
	}

	// Utils ==================================================================

	private BasicBlock processStatements(BasicBlock firstBlock, List<? extends Statement> statements) {
		final StatementVisitor<BasicBlock> visitor = new StatementVisitor<>() {
			private BasicBlock basicBlock = firstBlock;

			@Override
			public BasicBlock visitAssignment(Assignment node) {
				basicBlock.add(node);
				return basicBlock;
			}

			@Override
			public BasicBlock visitStatementList(StatementList node) {
				throw new UnsupportedOperationException();
			}

			@Override
			public BasicBlock visitLocalVarDeclaration(VarDeclaration node) {
				basicBlock.add(node);
				return basicBlock;
			}

			@Override
			public BasicBlock visitCall(CallStatement node) {
				basicBlock.add(node);
				return basicBlock;
			}

			@Override
			public BasicBlock visitReturn(ReturnStatement node) {
				return basicBlock;
			}

			@Override
			public BasicBlock visitIf(IfStatement node) {
				final IfBlock ifBlock = new IfBlock(basicBlock, node);
				final BasicBlock trueBlock = processStatements(new BasicBlock(ifBlock), node.ifStatements.getStatements());
				final BasicBlock falseBlock = processStatements(new BasicBlock(ifBlock), node.elseStatements.getStatements());
				basicBlock = new BasicBlock(trueBlock, falseBlock);
				return basicBlock;
			}

			@Override
			public BasicBlock visitWhile(WhileStatement node) {
				return basicBlock;
			}
		};
		BasicBlock block = firstBlock;
		for (Statement statement : statements) {
			block = statement.visit(visitor);
		}
		return block;
	}
}
