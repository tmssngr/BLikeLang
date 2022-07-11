package de.regnis.b.ir;

import de.regnis.b.ast.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

/**
 * @author Thomas Singer
 */
public final class ControlFlowGraph {

	// Fields =================================================================

	private final BasicBlock firstBlock;
	private final ExitBlock exitBlock;

	// Setup ==================================================================

	public ControlFlowGraph(@NotNull FuncDeclaration declaration) {
		firstBlock = new BasicBlock();

		exitBlock = new ExitBlock();

		final Supplier<Integer> labelIndexSupplier = new Supplier<>() {
			private int index;

			@Override
			public Integer get() {
				index++;
				return index;
			}
		};
		final BasicBlock lastBlock = new Builder(firstBlock, exitBlock, null, labelIndexSupplier)
				.processStatements(declaration.statementList);
		if (lastBlock != null) {
			exitBlock.addPrev(lastBlock);
		}
	}

	// Accessing ==============================================================

	public BasicBlock getFirstBlock() {
		return firstBlock;
	}

	public ExitBlock getExitBlock() {
		return exitBlock;
	}

	// Inner Classes ==========================================================

	private static final class Builder implements StatementVisitor<BasicBlock> {
		private final ExitBlock exitBlock;
		private final BasicBlock breakBlock;
		private final Supplier<Integer> labelIndexSupplier;

		private BasicBlock basicBlock;

		private Builder(@NotNull BasicBlock basicBlock, @NotNull ExitBlock exitBlock, @Nullable BasicBlock breakBlock, @NotNull Supplier<Integer> labelIndexSupplier) {
			this.basicBlock = basicBlock;
			this.exitBlock = exitBlock;
			this.breakBlock = breakBlock;
			this.labelIndexSupplier = labelIndexSupplier;
		}

		@Override
		public BasicBlock visitAssignment(Assignment node) {
			basicBlock.add(node);
			return basicBlock;
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

		@Nullable
		@Override
		public BasicBlock visitIf(IfStatement node) {
			final Integer labelIndex = labelIndexSupplier.get();
			final IfBlock ifBlock = new IfBlock(basicBlock, node, labelIndex);

			final BasicBlock trueBlock = new Builder(ifBlock.getTrueBlock(), exitBlock, breakBlock, labelIndexSupplier)
					.processStatements(node.trueStatements);

			final BasicBlock falseBlock = new Builder(ifBlock.getFalseBlock(), exitBlock, breakBlock, labelIndexSupplier)
					.processStatements(node.falseStatements);

			final String nextLabel = "after_if_" + labelIndex;
			if (trueBlock == null) {
				if (falseBlock == null) {
					return null;
				}

				return new BasicBlock(nextLabel, falseBlock);
			}

			final BasicBlock block = new BasicBlock(nextLabel, trueBlock);
			if (falseBlock != null) {
				block.addPrev(falseBlock);
			}
			return block;
		}

		@Override
		public BasicBlock visitWhile(WhileStatement node) {
			final WhileBlock whileBlock = new WhileBlock(basicBlock, node, labelIndexSupplier.get());
			final BasicBlock leaveBlock = whileBlock.getLeaveBlock();

			final BasicBlock lastInnerBlock = new Builder(whileBlock.getInnerBlock(), exitBlock, leaveBlock, labelIndexSupplier)
					.processStatements(node.statements);

			if (lastInnerBlock != null) {
				whileBlock.addPrev(lastInnerBlock);
			}

			return leaveBlock;
		}

		@Nullable
		@Override
		public BasicBlock visitReturn(ReturnStatement node) {
			if (node.expression != null) {
				basicBlock.add(new Assignment("result", node.expression));
			}
			exitBlock.addPrev(basicBlock);
			return null;
		}

		@Nullable
		@Override
		public BasicBlock visitBreak(BreakStatement node) {
			Objects.requireNonNull(breakBlock);

			breakBlock.addPrev(basicBlock);
			return null;
		}

		@Override
		public BasicBlock visitStatementList(StatementList node) {
			throw new UnsupportedOperationException();
		}

		private BasicBlock processStatements(@NotNull StatementList node) {
			for (Statement statement : node.getStatements()) {
				basicBlock = statement.visit(this);
				if (basicBlock == null) {
					break;
				}
			}
			return basicBlock;
		}
	}
}
