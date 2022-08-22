package de.regnis.b.ir;

import de.regnis.b.ast.*;
import de.regnis.b.out.StringOutput;
import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Thomas Singer
 */
public final class ControlFlowGraphVarUsageDetector {

	// Static =================================================================

	@NotNull
	public static ControlFlowGraphVarUsageDetector detectVarUsages(@NotNull ControlFlowGraph graph) {
		final ControlFlowGraphVarUsageDetector detector = new ControlFlowGraphVarUsageDetector(graph);
		detector.propagateLiveToPrev();
		return detector;
	}

	// Fields =================================================================

	private final Map<AbstractBlock, Usages> usageMap = new HashMap<>();
	private final Map<SimpleStatement, Set<String>> liveVars = new HashMap<>();
	private final ControlFlowGraph graph;

	// Setup ==================================================================

	private ControlFlowGraphVarUsageDetector(ControlFlowGraph graph) {
		this.graph = graph;
	}

	// Accessing ==============================================================

	public ControlFlowGraphPrinter createPrinter(StringOutput output) {
		return new ControlFlowGraphPrinter(graph, output) {
			@Override
			protected void printBefore(String indentation, AbstractBlock block) {
				print(indentation, getUsages(block).lifeBefore);
			}

			@Override
			protected void print(String indentation, SimpleStatement statement) {
				super.print(indentation, statement);

				final Set<String> liveVars = ControlFlowGraphVarUsageDetector.this.liveVars.get(statement);
				print(indentation, liveVars);
			}

			private void print(String indentation, Set<String> strings) {
				final List<String> sortedStrings = new ArrayList<>(strings);
				sortedStrings.sort(String::compareTo);

				output.print(indentation);
				output.print("// [");
				output.print(Utils.appendCommaSeparated(sortedStrings, new StringBuilder()).toString());
				output.print("]");
				output.println();
			}
		};
	}

	@NotNull
	public Set<String> getVarsBefore(@NotNull AbstractBlock block) {
		return Collections.unmodifiableSet(usageMap.get(block).lifeBefore);
	}

	@NotNull
	public Set<String> getVarsAfter(@NotNull AbstractBlock block) {
		return Collections.unmodifiableSet(usageMap.get(block).lifeAfter);
	}

	@NotNull
	public Set<String> getVarsAfter(@NotNull SimpleStatement statement) {
		return Collections.unmodifiableSet(liveVars.get(statement));
	}

	// Utils ==================================================================

	private Usages getUsages(AbstractBlock block) {
		return usageMap.computeIfAbsent(block, block1 -> new Usages());
	}

	private void propagateLiveToPrev() {
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

				if (process(block)) {
					repeat = true;
				}

				blocks.addAll(block.getPrevBlocks());
			}
		}
	}

	private boolean process(AbstractBlock block) {
		final Set<String> live = getLifeFromAllNext(block);

		final Usages usages = getUsages(block);
		boolean changed = usages.lifeAfter.addAll(live);

		block.visit(new BlockVisitor() {
			@Override
			public void visitBasic(BasicBlock block) {
				detectRequiredAndProvidedVars(block, live);
			}

			@Override
			public void visitIf(IfBlock block) {
				detectRequiredVars(block.getExpression(), live);
			}

			@Override
			public void visitWhile(WhileBlock block) {
				detectRequiredVars(block.getExpression(), live);
			}

			@Override
			public void visitExit(ExitBlock block) {
			}
		});

		if (usages.lifeBefore.addAll(live)) {
			changed = true;
		}

		return changed;
	}

	@NotNull
	private Set<String> getLifeFromAllNext(AbstractBlock block) {
		final Set<String> lifeFromNext = new HashSet<>();
		for (AbstractBlock nextBlock : block.getNextBlocks()) {
			final Usages nextUsages = getUsages(nextBlock);
			lifeFromNext.addAll(nextUsages.lifeBefore);
		}
		return lifeFromNext;
	}

	private void setLiveVars(SimpleStatement node, Set<String> live) {
		liveVars.put(node, new HashSet<>(live));
	}

	private void detectRequiredAndProvidedVars(BasicBlock block, Set<String> live) {
		final List<? extends SimpleStatement> statements = new ArrayList<>(block.getStatements());
		Collections.reverse(statements);
		for (SimpleStatement statement : statements) {
			setLiveVars(statement, live);

			statement.visit(new SimpleStatementVisitor<>() {
				@Override
				public Object visitAssignment(Assignment node) {
					if (node.operation() == Assignment.Op.assign) {
						live.remove(node.name());
					}
					else {
						live.add(node.name());
					}
					detectRequiredVars(node.expression(), live);
					return node;
				}

				@Override
				public Object visitLocalVarDeclaration(VarDeclaration node) {
					live.remove(node.name());
					detectRequiredVars(node.expression(), live);
					return node;
				}

				@Override
				public Object visitCall(CallStatement node) {
					for (Expression parameter : node.parameters().getExpressions()) {
						detectRequiredVars(parameter, live);
					}
					return node;
				}
			});
		}
	}

	private void detectRequiredVars(Expression expression, Set<String> live) {
		expression.visit(new ExpressionVisitor<>() {
			@Override
			public Object visitBinary(BinaryExpression node) {
				detectRequiredVars(node.left(), live);
				detectRequiredVars(node.right(), live);
				return node;
			}

			@Override
			public Object visitFunctionCall(FuncCall node) {
				for (Expression parameter : node.parameters().getExpressions()) {
					detectRequiredVars(parameter, live);
				}
				return node;
			}

			@Override
			public Object visitNumber(NumberLiteral node) {
				return node;
			}

			@Override
			public Object visitVarRead(VarRead node) {
				live.add(node.name());
				return node;
			}
		});
	}

	// Inner Classes ==========================================================

	public static final class Usages {
		private final Set<String> lifeBefore = new LinkedHashSet<>();
		private final Set<String> lifeAfter = new LinkedHashSet<>();

		private Usages() {
		}

		@Override
		public String toString() {
			final StringBuilder buffer = new StringBuilder();
			getVarInputOutput(buffer);
			return buffer.toString();
		}

		public void getVarInputOutput(StringBuilder buffer) {
			buffer.append("in: [");
			Utils.appendCommaSeparated(lifeBefore, buffer);
			buffer.append("], out: [");
			Utils.appendCommaSeparated(lifeAfter, buffer);
			buffer.append("]");
		}
	}
}
