package de.regnis.b.ir;

import de.regnis.b.ast.*;
import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static de.regnis.utils.Utils.assertTrue;
import static de.regnis.utils.Utils.notNull;

/**
 * @author Thomas Singer
 */
public final class StaticSingleAssignmentFactory {

	// Constants ==============================================================

	public static final String PHI = "phi ";

	// Static =================================================================

	public static void transform(ControlFlowGraph graph) {
		final StaticSingleAssignmentFactory ssaFactory = new StaticSingleAssignmentFactory(graph);
		ssaFactory.transform();
		ssaFactory.removeObsoletePhiFunctions();
	}

	// Fields =================================================================

	private final Map<AbstractBlock, BlockInfo> blockToInfo = new HashMap<>();
	private final ControlFlowGraph graph;

	// Setup ==================================================================

	private StaticSingleAssignmentFactory(@NotNull ControlFlowGraph graph) {
		this.graph = graph;
	}

	// Utils ==================================================================

	private void transform() {
		final ControlFlowGraphVarUsageDetector usages = ControlFlowGraphVarUsageDetector.detectVarUsages(graph);

		final Map<String, Integer> varToVariant = new HashMap<>();

		graph.iterate(block -> {
			final BlockInfo info = new BlockInfo(varToVariant);
			blockToInfo.put(block, info);

			final List<AbstractBlock> prevBlocks = block.getPrevBlocks();
			if (prevBlocks.isEmpty()) {
				info.initializeFromParameters(graph);
			}
			else {
				final Set<String> incomingVars = usages.getVarsBefore(block);
				if (prevBlocks.size() > 1) {
					for (String incomingVar : incomingVars) {
						final String ssaName = info.getAssignmentName(incomingVar);
						info.phiFunctions.add(new PhiFunction(incomingVar, ssaName));
					}
				}
				else {
					final BlockInfo prevInfo = blockToInfo.get(prevBlocks.get(0));
					info.initializeFrom(prevInfo);
				}
			}

			block.visit(new BlockVisitor() {
				@Override
				public void visitBasic(BasicBlock block) {
					transform(block, info);
				}

				@Override
				public void visitIf(IfBlock block) {
					final Expression expression = visitExpression(block.getExpression(), info);
					block.setExpression(expression);
				}

				@Override
				public void visitWhile(WhileBlock block) {
					final Expression expression = visitExpression(block.getExpression(), info);
					block.setExpression(expression);
				}

				@Override
				public void visitExit(ExitBlock block) {
				}
			});
		});

		graph.iterate(block -> {
			final BlockInfo info = blockToInfo.get(block);

			final List<AbstractBlock> prevBlocks = block.getPrevBlocks();
			if (prevBlocks.size() < 2) {
				return;
			}

			if (info.phiFunctions.isEmpty()) {
				return;
			}

			final StatementsBlock statementsBlock = (StatementsBlock) block;
			final List<SimpleStatement> statements = new ArrayList<>();

			for (PhiFunction phiFunction : info.phiFunctions) {
				for (AbstractBlock prevBlock : prevBlocks) {
					final BlockInfo prevInfo = blockToInfo.get(prevBlock);
					final String ssaName = prevInfo.getUsageName(phiFunction.originalName);
					phiFunction.ssaSources.add(ssaName);
				}

				final FuncCallParameters parameters = new FuncCallParameters();
				for (String ssaSource : Utils.toSortedList(phiFunction.ssaSources)) {
					parameters.add(new VarRead(ssaSource));
				}
				statements.add(new VarDeclaration(phiFunction.ssaName, new FuncCall(PHI, parameters)));
			}

			statements.addAll(statementsBlock.getStatements());
			statementsBlock.set(statements);
		});
	}

	private void removeObsoletePhiFunctions() {
		while (true) {
			final Map<String, VarRead> obsoletePhiFunctions = detectObsoletePhiFunctions();
			if (obsoletePhiFunctions.isEmpty()) {
				return;
			}

			SsaSearchAndReplace.replace(graph, obsoletePhiFunctions);
		}
	}

	private Map<String, VarRead> detectObsoletePhiFunctions() {
		final Map<String, VarRead> obsoletePhiVariantToPreviousVariant = new HashMap<>();
		graph.iterate(block -> {
			if (!(block instanceof StatementsBlock)) {
				return;
			}
			final StatementsBlock statementsBlock = (StatementsBlock) block;
			for (SimpleStatement statement : statementsBlock.getStatements()) {
				if (!(statement instanceof VarDeclaration)) {
					continue;
				}

				final VarDeclaration declaration = (VarDeclaration) statement;
				if (declaration.expression instanceof FuncCall call && call.name.equals(PHI) && call.getParameters().size() == 1) {
					final Expression expression = call.getParameters().get(0);
					final String var = ((VarRead) expression).name;
					if (obsoletePhiVariantToPreviousVariant.put(declaration.name, new VarRead(var)) != null) {
						throw new IllegalStateException();
					}
				}
			}
		});
		return obsoletePhiVariantToPreviousVariant;
	}

	private void transform(BasicBlock block, BlockInfo varToVariant) {
		block.replace((statement, consumer) -> statement.visit(new SimpleStatementVisitor<>() {
			@Override
			public SimpleStatement visitAssignment(Assignment node) {
				assertTrue(node.operation == Assignment.Op.assign);

				final Expression newExpression = visitExpression(node.expression, varToVariant);
				final String newName = varToVariant.getAssignmentName(node.name);
				consumer.accept(new VarDeclaration(newName, newExpression));
				return node;
			}

			@Override
			public SimpleStatement visitLocalVarDeclaration(VarDeclaration node) {
				final Expression newExpression = visitExpression(node.expression, varToVariant);
				final String newName = varToVariant.getDeclarationName(node.name);
				consumer.accept(new VarDeclaration(newName, newExpression));
				return node;
			}

			@Override
			public SimpleStatement visitCall(CallStatement node) {
				final FuncCallParameters parameters = new FuncCallParameters();
				for (Expression parameter : node.getParameters()) {
					parameters.add(visitExpression(parameter, varToVariant));
				}
				consumer.accept(new CallStatement(node.name, parameters));
				return node;
			}
		}));
	}

	private Expression visitExpression(Expression expression, BlockInfo varToVariant) {
		return expression.visit(new ExpressionVisitor<>() {
			@Override
			public Expression visitBinary(BinaryExpression node) {
				final Expression left = visitExpression(node.left, varToVariant);
				final Expression right = visitExpression(node.right, varToVariant);
				return new BinaryExpression(left, node.operator, right);
			}

			@Override
			public Expression visitFunctionCall(FuncCall node) {
				final FuncCallParameters parameters = new FuncCallParameters();
				for (Expression parameter : node.getParameters()) {
					parameters.add(visitExpression(parameter, varToVariant));
				}
				return new FuncCall(node.name, parameters);
			}

			@Override
			public Expression visitNumber(NumberLiteral node) {
				return node;
			}

			@Override
			public Expression visitVarRead(VarRead node) {
				final String ssaName = varToVariant.getUsageName(node.name);
				return new VarRead(ssaName);
			}
		});
	}

	// Inner Classes ==========================================================

	private static final class BlockInfo {
		private final List<PhiFunction> phiFunctions = new ArrayList<>();
		private final Map<String, Integer> varToCurrent = new HashMap<>();
		private final Map<String, Integer> varToHighest;

		private BlockInfo(@NotNull Map<String, Integer> varToHighest) {
			// shared!
			this.varToHighest = varToHighest;
		}

		public void initializeFromParameters(@NotNull ControlFlowGraph graph) {
			for (FuncDeclarationParameter parameter : graph.getParameters()) {
				getDeclarationName(parameter.name);
			}
		}

		public void initializeFrom(@NotNull BlockInfo prevInfo) {
			varToCurrent.putAll(prevInfo.varToCurrent);
		}

		@NotNull
		public String getDeclarationName(@NotNull String originalName) {
			final int variant = 0;
			final Integer prevValue = varToHighest.put(originalName, variant);
			assertTrue(prevValue == null);
			varToCurrent.put(originalName, variant);
			return getVariableName(originalName, variant);
		}

		@NotNull
		public String getAssignmentName(@NotNull String originalName) {
			if (originalName.equals(ControlFlowGraph.RESULT)) {
				return originalName;
			}

			final int variant = notNull(varToHighest.get(originalName)) + 1;
			varToHighest.put(originalName, variant);
			varToCurrent.put(originalName, variant);
			return getVariableName(originalName, variant);
		}

		@NotNull
		public String getUsageName(@NotNull String originalName) {
			final Integer variant = notNull(varToCurrent.get(originalName));
			return getVariableName(originalName, variant);
		}

		@NotNull
		private String getVariableName(@NotNull String originalName, int variant) {
			return originalName + "_" + variant;
		}
	}

	private static final class PhiFunction {
		public final String originalName;
		public final String ssaName;
		private final Set<String> ssaSources = new HashSet<>();

		private PhiFunction(String originalName, String ssaName) {
			this.originalName = originalName;
			this.ssaName = ssaName;
		}

		@Override
		public String toString() {
			final StringBuilder buffer = new StringBuilder();
			buffer.append(ssaName);
			buffer.append(" = phi(");
			Utils.appendCommaSeparated(Utils.toSortedList(ssaSources), buffer);
			buffer.append(")");
			return buffer.toString();
		}
	}
}