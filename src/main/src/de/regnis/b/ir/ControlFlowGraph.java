package de.regnis.b.ir;

import de.regnis.b.ast.*;
import de.regnis.b.type.Type;
import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static de.regnis.utils.Utils.assertTrue;

/**
 * @author Thomas Singer
 */
public final class ControlFlowGraph {

	// Constants ==============================================================

	public static final String RESULT = "result";

	// Fields =================================================================

	private final List<Block> linearizedBlocks = new ArrayList<>();
	private final List<FuncDeclarationParameter> parameters;
	private final ExitBlock exitBlock;
	private final Type type;

	private Block firstBlock;

	// Setup ==================================================================

	public ControlFlowGraph(@NotNull FuncDeclaration declaration) {
		parameters = new ArrayList<>(declaration.parameters().getParameters());
		type       = declaration.type();

		final String prefix = declaration.name() + "_";

		exitBlock = new ExitBlock(prefix);

		final BasicBlock firstBlock = new BasicBlock(prefix);

		final Supplier<Integer> labelIndexSupplier = new Supplier<>() {
			private int index;

			@Override
			public Integer get() {
				index++;
				return index;
			}
		};
		final BasicBlock lastBlock = new Builder(firstBlock, exitBlock, null, prefix, labelIndexSupplier, linearizedBlocks)
				.processStatements(declaration.statementList());
		if (lastBlock != null) {
			exitBlock.addPrev(lastBlock);
		}

		this.firstBlock = firstBlock;

		linearizedBlocks.add(exitBlock);

		checkIntegrity();
	}

	// Accessing ==============================================================

	@NotNull
	public List<FuncDeclarationParameter> getParameters() {
		return Collections.unmodifiableList(parameters);
	}

	public void setParameters(@NotNull List<FuncDeclarationParameter> parameters) {
		assertTrue(parameters.size() == this.parameters.size());

		this.parameters.clear();
		this.parameters.addAll(parameters);
	}

	public Type getType() {
		return type;
	}

	public Block getFirstBlock() {
		return firstBlock;
	}

	public ExitBlock getExitBlock() {
		return exitBlock;
	}

	public void iterate(@NotNull Consumer<Block> consumer) {
		final List<Block> blocks = new ArrayList<>();
		blocks.add(firstBlock);

		final Set<Block> processedBlocks = new HashSet<>();

		while (blocks.size() > 0) {
			final Block block = blocks.remove(0);
			if (!processedBlocks.add(block)) {
				continue;
			}

			consumer.accept(block);
			blocks.addAll(block.getNextBlocks());
		}
	}

	public void iterate(@NotNull BlockVisitor visitor) {
		iterate(block -> block.visit(visitor));
	}

	public List<Block> getLinearizedBlocks() {
		return Collections.unmodifiableList(linearizedBlocks);
	}

	// Utils ==================================================================

	private void checkIntegrity() {
		final Set<Block> blocks = new HashSet<>();

		iterate(block -> {
			block.checkIntegrity();
			blocks.add(block);
		});

		if (!blocks.equals(new HashSet<>(linearizedBlocks))) {
			final String iteratedBlocks = blocksToString(blocks);
			final String linearizedBlocks = blocksToString(this.linearizedBlocks);
			throw new IllegalStateException("Iterated blocks: " + iteratedBlocks + "\nLinearized Blocks: " + linearizedBlocks);
		}
	}

	@NotNull
	private String blocksToString(Collection<Block> blocks) {
		final List<String> blockLabels = Utils.convert(blocks, new ArrayList<>(), block -> block.label);
		blockLabels.sort(Comparator.naturalOrder());
		return Utils.appendCommaSeparated(blockLabels, new StringBuilder()).toString();
	}

	// Inner Classes ==========================================================

	private static final class Builder implements StatementVisitor<BasicBlock> {
		private final ExitBlock exitBlock;
		private final BasicBlock breakBlock;
		private final String prefix;
		private final Supplier<Integer> labelIndexSupplier;
		private final List<Block> orderedBlocks;

		private BasicBlock basicBlock;

		private Builder(@NotNull BasicBlock basicBlock, @NotNull ExitBlock exitBlock, @Nullable BasicBlock breakBlock, @NotNull String prefix, @NotNull Supplier<Integer> labelIndexSupplier, @NotNull List<Block> orderedBlocks) {
			this.basicBlock         = basicBlock;
			this.exitBlock          = exitBlock;
			this.breakBlock         = breakBlock;
			this.prefix             = prefix;
			this.labelIndexSupplier = labelIndexSupplier;
			this.orderedBlocks      = orderedBlocks;
			orderedBlocks.add(basicBlock);
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
			final IfBlock ifBlock = new IfBlock(basicBlock, node, prefix, labelIndex);
			orderedBlocks.add(ifBlock);

			final BasicBlock trueBlock = new Builder((BasicBlock) ifBlock.getTrueBlock(), exitBlock, breakBlock, prefix, labelIndexSupplier, orderedBlocks)
					.processStatements(node.trueStatements());

			final BasicBlock falseBlock = new Builder((BasicBlock) ifBlock.getFalseBlock(), exitBlock, breakBlock, prefix, labelIndexSupplier, orderedBlocks)
					.processStatements(node.falseStatements());

			final String nextLabel = prefix + "after_if_" + labelIndex;
			final BasicBlock nextBlock;
			if (trueBlock == null) {
				if (falseBlock == null) {
					return null;
				}

				nextBlock = new BasicBlock(nextLabel, falseBlock);
			}
			else {
				nextBlock = new BasicBlock(nextLabel, trueBlock);
				if (falseBlock != null) {
					nextBlock.addPrev(falseBlock);
				}
			}
			orderedBlocks.add(nextBlock);
			return nextBlock;
		}

		@Override
		public BasicBlock visitWhile(WhileStatement node) {
			final WhileBlock whileBlock = new WhileBlock(basicBlock, node, prefix, labelIndexSupplier.get());
			orderedBlocks.add(whileBlock);
			final BasicBlock leaveBlock = (BasicBlock) whileBlock.getLeaveBlock();

			final BasicBlock lastInnerBlock = new Builder((BasicBlock) whileBlock.getInnerBlock(), exitBlock, leaveBlock, prefix, labelIndexSupplier, orderedBlocks)
					.processStatements(node.statements());

			if (lastInnerBlock != null) {
				whileBlock.addPrev(lastInnerBlock);
			}

			orderedBlocks.add(leaveBlock);
			return leaveBlock;
		}

		@Nullable
		@Override
		public BasicBlock visitReturn(ReturnStatement node) {
			if (node.expression() != null) {
				basicBlock.add(new Assignment(Assignment.Op.assign, RESULT, node.expression()));
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

		@Nullable
		private BasicBlock processStatements(@NotNull StatementList node) {
			for (Statement statement : node.getStatements()) {
				basicBlock = statement.visit(this);
				if (basicBlock == null) {
					return null;
				}
			}
			return basicBlock;
		}
	}
}
