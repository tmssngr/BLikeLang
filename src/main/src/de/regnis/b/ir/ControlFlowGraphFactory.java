package de.regnis.b.ir;

import de.regnis.b.ast.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Thomas Singer
 */
public class ControlFlowGraphFactory {

	// Accessing ==============================================================

	public BasicBlock createGraph(FuncDeclaration declaration) {
		final BasicBlock firstBlock = new BasicBlock();
		final BasicBlock lastBlock = processStatements(firstBlock, declaration.statementList.getStatements());
		detectRequiredAndProvidedVars(firstBlock);
		detectInputOutputVars(lastBlock);
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
				final BasicBlock trueBlock = processStatements(new BasicBlock(ifBlock), node.trueStatements.getStatements());
				final BasicBlock falseBlock = processStatements(new BasicBlock(ifBlock), node.falseStatements.getStatements());
				basicBlock = new BasicBlock(trueBlock, falseBlock);
				return basicBlock;
			}

			@Override
			public BasicBlock visitWhile(WhileStatement node) {
				return basicBlock;
			}

			@Override
			public BasicBlock visitBreak(BreakStatement node) {
				return null;
			}
		};
		BasicBlock block = firstBlock;
		for (Statement statement : statements) {
			block = statement.visit(visitor);
		}
		return block;
	}

	private void detectRequiredAndProvidedVars(AbstractBlock firstBlock) {
		final List<AbstractBlock> blocks = new ArrayList<>();
		blocks.add(firstBlock);

		final Set<AbstractBlock> processedBlocks = new HashSet<>();

		while (blocks.size() > 0) {
			final AbstractBlock block = blocks.remove(0);
			if (!processedBlocks.add(block)) {
				continue;
			}

			block.detectRequiredVars();
			blocks.addAll(block.getNext());
		}
	}

	private void detectInputOutputVars(BasicBlock lastBlock) {
		final List<AbstractBlock> blocks = new ArrayList<>();
		blocks.add(lastBlock);

		final Set<AbstractBlock> processedBlocks = new HashSet<>();

		while (blocks.size() > 0) {
			final AbstractBlock block = blocks.remove(0);
			if (!processedBlocks.add(block)) {
				continue;
			}

			block.detectInputOutputVars();
			blocks.addAll(block.getPrev());
		}
	}
}
