package de.regnis.b.ir;

import de.regnis.b.ast.*;
import de.regnis.b.out.StringOutput;
import de.regnis.b.type.Type;
import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static de.regnis.utils.Utils.notNull;

/**
 * @author Thomas Singer
 */
public final class ControlFlowGraphVarUsageDetector {

	// Static =================================================================

	@NotNull
	public static ControlFlowGraphVarUsageDetector detectVarUsage(@NotNull ControlFlowGraph graph) {
		final ControlFlowGraphVarUsageDetector detector = new ControlFlowGraphVarUsageDetector(graph);
		detector.propagateLiveToPrev();
		return detector;
	}

	// Fields =================================================================

	private final Map<AbstractBlock, Usages> usageMap = new HashMap<>();
	private final Map<SimpleStatement, Set<String>> liveVars = new HashMap<>();
	private final Map<String, Type> nameToType = new HashMap<>();
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
	public Set<String> getVarsBefore(AbstractBlock block) {
		return Collections.unmodifiableSet(usageMap.get(block).lifeBefore);
	}

	@NotNull
	public Set<String> getVarsAfter(SimpleStatement statement) {
		return Collections.unmodifiableSet(liveVars.get(statement));
	}

	@NotNull
	public Map<String, Type> getNameToType() {
		return Collections.unmodifiableMap(nameToType);
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

				blocks.addAll(block.getPrev());
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
				detectRequiredVars(block.getCondition(), live);
			}

			@Override
			public void visitWhile(WhileBlock block) {
				detectRequiredVars(block.getCondition(), live);
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
		for (AbstractBlock nextBlock : block.getNext()) {
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

			statement.visit(new StatementVisitor<>() {
				@Override
				public Object visitAssignment(Assignment node) {
					if (node.operation == Assignment.Op.assign) {
						live.remove(node.name);
					}
					else {
						live.add(node.name);
					}
					detectRequiredVars(node.expression, live);
					return node;
				}

				@Override
				public Object visitMemAssignment(MemAssignment node) {
					live.add(node.name);
					detectRequiredVars(node.expression, live);
					return node;
				}

				@Override
				public Object visitStatementList(StatementList node) {
					throw new IllegalStateException();
				}

				@Override
				public Object visitLocalVarDeclaration(VarDeclaration node) {
					nameToType.put(node.name, notNull(node.type));
					live.remove(node.name);
					detectRequiredVars(node.expression, live);
					return node;
				}

				@Override
				public Object visitCall(CallStatement node) {
					for (Expression parameter : node.getParameters()) {
						detectRequiredVars(parameter, live);
					}
					return node;
				}

				@Override
				public Object visitReturn(ReturnStatement node) {
					if (node.expression != null) {
						detectRequiredVars(node.expression, live);
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

	private void detectRequiredVars(Expression expression, Set<String> live) {
		expression.visit(new ExpressionVisitor<>() {
			@Override
			public Object visitBinary(BinaryExpression node) {
				detectRequiredVars(node.left, live);
				detectRequiredVars(node.right, live);
				return node;
			}

			@Override
			public Object visitFunctionCall(FuncCall node) {
				for (Expression parameter : node.getParameters()) {
					detectRequiredVars(parameter, live);
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
				live.add(node.name);
				return node;
			}

			@Override
			public Object visitMemRead(MemRead node) {
				live.add(node.name);
				return node;
			}

			@Override
			public Object visitTypeCast(TypeCast node) {
				detectRequiredVars(node.expression, live);
				return node;
			}
		});
	}

	// Inner Classes ==========================================================

	public static final class Usages {
		private final Set<String> lifeBefore = new LinkedHashSet<>();
		private final Set<String> lifeAfter = new LinkedHashSet<>();

		public Usages() {
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
