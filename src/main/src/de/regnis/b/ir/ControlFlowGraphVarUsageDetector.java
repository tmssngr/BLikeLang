package de.regnis.b.ir;

import de.regnis.b.ast.*;
import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Thomas Singer
 */
public final class ControlFlowGraphVarUsageDetector {

	// Static =================================================================

	@NotNull
	public static ControlFlowGraphVarUsageDetector detectVarUsage(@NotNull ControlFlowGraph graph) {
		final ControlFlowGraphVarUsageDetector detector = new ControlFlowGraphVarUsageDetector(graph);
		detector.detectRequiredAndProvidedVars();
		detector.propagateInputToPrev();
		detector.removeUnused();
		return detector;
	}

	// Fields =================================================================

	private final Map<AbstractBlock, Usages> usageMap = new HashMap<>();
	private final ControlFlowGraph graph;

	// Setup ==================================================================

	private ControlFlowGraphVarUsageDetector(ControlFlowGraph graph) {
		this.graph = graph;
	}

	// Accessing ==============================================================

	public String getVarInputOutput() {
		final StringBuilder buffer = new StringBuilder();
		graph.iterate(block -> {
			buffer.append(block.label);
			buffer.append(": ");
			final Usages usages = getUsages(block);
			usages.getVarInputOutput(buffer);
			buffer.append('\n');
		});
		return buffer.toString();
	}

	// Utils ==================================================================

	private void detectRequiredAndProvidedVars() {
		graph.iterate(block -> block.visit(new BlockVisitor() {
			@Override
			public void visitBasic(BasicBlock block) {
				detectRequiredAndProvidedVars(block);
			}

			@Override
			public void visitIf(IfBlock block) {
				detectRequiredVars(block.getCondition(), block);
			}

			@Override
			public void visitWhile(WhileBlock block) {
				detectRequiredVars(block.getCondition(), block);
			}

			@Override
			public void visitExit(ExitBlock block) {
			}
		}));
	}

	private void propagateInputToPrev() {
		boolean repeat = true;
		while (repeat) {
			final List<AbstractBlock> blocks = new ArrayList<>();
			blocks.add(graph.getExitBlock());

			repeat = false;

			final Set<AbstractBlock> processedBlocks = new HashSet<>();

			while (blocks.size() > 0) {
				final AbstractBlock block = blocks.remove(0);

				if (!processedBlocks.add(block)) {
					continue;
				}

				if (addUsedFromNext(block)) {
					repeat = true;
				}

				blocks.addAll(block.getPrev());
			}
		}
	}

	private Usages getUsages(AbstractBlock block) {
		return usageMap.computeIfAbsent(block, block1 -> new Usages());
	}

	private boolean addUsedFromNext(AbstractBlock block) {
		final Set<String> requiredByNext = getRequiredByAllNext(block);

		final Usages usages = getUsages(block);

		boolean changed = false;
		for (String required : requiredByNext) {
			if (!usages.output.contains(required) && !usages.tunnel.contains(required)) {
				changed = true;
				usages.tunnel.add(required);
			}
		}

		return changed;
	}

	@NotNull
	private Set<String> getRequiredByAllNext(AbstractBlock block) {
		final Set<String> requiredByNext = new HashSet<>();
		for (AbstractBlock nextBlock : block.getNext()) {
			final Usages nextUsages = getUsages(nextBlock);
			requiredByNext.addAll(nextUsages.input);
			requiredByNext.addAll(nextUsages.tunnel);
		}
		return requiredByNext;
	}

	private void removeUnused() {
		graph.iterate(this::removeUnusedFromNext);
	}

	private void removeUnusedFromNext(AbstractBlock block) {
		final Set<String> requiredByNext = getRequiredByAllNext(block);

		final Usages usages = getUsages(block);
		usages.output.retainAll(requiredByNext);
	}

	private void addWrittenVar(String name, BasicBlock block) {
		final Usages usages = getUsages(block);
		usages.output.add(name);
	}

	private void addReadVar(String name, AbstractBlock block) {
		final Usages usages = getUsages(block);
		if (!usages.output.contains(name)) {
			usages.input.add(name);
		}
	}

	private void detectRequiredAndProvidedVars(BasicBlock block) {
		for (SimpleStatement statement : block.getStatements()) {
			statement.visit(new StatementVisitor<>() {
				@Override
				public Object visitAssignment(Assignment node) {
					detectRequiredVars(node.expression, block);
					addWrittenVar(node.name, block);
					return node;
				}

				@Override
				public Object visitMemAssignment(MemAssignment node) {
					detectRequiredVars(node.expression, block);
					return node;
				}

				@Override
				public Object visitStatementList(StatementList node) {
					throw new IllegalStateException();
				}

				@Override
				public Object visitLocalVarDeclaration(VarDeclaration node) {
					detectRequiredVars(node.expression, block);
					addWrittenVar(node.name, block);
					return node;
				}

				@Override
				public Object visitCall(CallStatement node) {
					for (Expression parameter : node.getParameters()) {
						detectRequiredVars(parameter, block);
					}
					return node;
				}

				@Override
				public Object visitReturn(ReturnStatement node) {
					if (node.expression != null) {
						detectRequiredVars(node.expression, block);
					}
					return node;
				}

				@Override
				public Object visitIf(IfStatement node) {
					throw new IllegalStateException();
				}

				@Override
				public Object visitWhile(WhileStatement node) {
					throw new IllegalStateException();
				}

				@Override
				public Object visitBreak(BreakStatement node) {
					throw new IllegalStateException();
				}
			});
		}
	}

	private void detectRequiredVars(Expression expression, AbstractBlock block) {
		expression.visit(new ExpressionVisitor<>() {
			@Override
			public Object visitBinary(BinaryExpression node) {
				detectRequiredVars(node.left, block);
				detectRequiredVars(node.right, block);
				return node;
			}

			@Override
			public Object visitFunctionCall(FuncCall node) {
				for (Expression parameter : node.getParameters()) {
					detectRequiredVars(parameter, block);
				}
				return node;
			}

			@Override
			public Object visitNumber(NumberLiteral node) {
				return node;
			}

			@Override
			public Object visitBoolean(BooleanLiteral node) {
				return node;
			}

			@Override
			public Object visitVarRead(VarRead node) {
				addReadVar(node.name, block);
				return node;
			}

			@Override
			public Object visitMemRead(MemRead node) {
				addReadVar(node.name, block);
				return node;
			}

			@Override
			public Object visitTypeCast(TypeCast node) {
				return node;
			}
		});
	}

	// Inner Classes ==========================================================

	public static final class Usages {
		private final Set<String> input = new LinkedHashSet<>();
		private final Set<String> output = new LinkedHashSet<>();
		private final Set<String> tunnel = new LinkedHashSet<>();

		public Usages() {
		}

		@Override
		public String toString() {
			final StringBuilder buffer = new StringBuilder();
			getVarInputOutput(buffer);
			return buffer.toString();
		}

		public Set<String> getInput() {
			return Collections.unmodifiableSet(input);
		}

		public Set<String> getOutput() {
			return Collections.unmodifiableSet(output);
		}

		public Set<String> getTunnel() {
			return Collections.unmodifiableSet(tunnel);
		}

		public void getVarInputOutput(StringBuilder buffer) {
			buffer.append("in: [");
			Utils.appendCommaSeparated(input, buffer);
			buffer.append("], out: [");
			Utils.appendCommaSeparated(output, buffer);
			buffer.append("], tunnel: [");
			Utils.appendCommaSeparated(tunnel, buffer);
			buffer.append("]");
		}
	}
}
