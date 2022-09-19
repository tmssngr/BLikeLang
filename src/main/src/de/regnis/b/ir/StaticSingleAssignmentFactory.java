package de.regnis.b.ir;

import de.regnis.b.ast.*;
import de.regnis.utils.Tuple;
import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static de.regnis.utils.Utils.assertTrue;
import static de.regnis.utils.Utils.notNull;

/**
 * @author Thomas Singer
 */
public final class StaticSingleAssignmentFactory {

	// Constants ==============================================================

	private static final String PHI = "phi ";

	// Static =================================================================

	public static void transform(ControlFlowGraph graph) {
		final StaticSingleAssignmentFactory ssaFactory = new StaticSingleAssignmentFactory(graph);
		ssaFactory.transform();
		ssaFactory.removeObsoletePhiFunctions();
	}

	@Nullable
	public static Tuple<String, List<String>> getPhiFunction(SimpleStatement statement) {
		if (statement instanceof VarDeclaration declaration
				&& declaration.expression() instanceof FuncCall call
				&& call.name().equals(PHI)) {
			final List<String> variants = Utils.convert(call.parameters().getExpressions(), new ArrayList<>(),
			                                           expression -> ((VarRead) expression).name());
			return new Tuple<>(declaration.name(), variants);
		}
		return null;
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

		for (AbstractBlock block : graph.getLinearizedBlocks()) {
			final BlockInfo info = new BlockInfo(varToVariant);
			blockToInfo.put(block, info);

			final List<AbstractBlock> prevBlocks = block.getPrevBlocks();
			if (prevBlocks.isEmpty()) {
				info.initializeFromParameters(graph);
			}
			else {
				final Set<String> incomingVars = usages.getVarsBefore(block);
				if (prevBlocks.size() > 1) {
					if (!(block instanceof ExitBlock)) {
						info.initializePhiDeclarations(incomingVars);
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
		}

		for (AbstractBlock block : graph.getLinearizedBlocks()) {
			final BlockInfo info = blockToInfo.get(block);

			final List<AbstractBlock> prevBlocks = block.getPrevBlocks();
			if (prevBlocks.size() < 2) {
				continue;
			}

			if (info.phiFunctions.isEmpty()) {
				continue;
			}

			final StatementsBlock statementsBlock = (StatementsBlock) block;
			final List<SimpleStatement> statements = new ArrayList<>();

			for (PhiFunction phiFunction : info.phiFunctions) {
				final List<Expression> phiParameters = new ArrayList<>();
				final Set<String> ssaNames = new HashSet<>();
				for (AbstractBlock prevBlock : prevBlocks) {
					final BlockInfo prevInfo = blockToInfo.get(prevBlock);
					final String ssaName = prevInfo.getUsageName(phiFunction.originalName);
					ssaNames.add(ssaName);
					phiParameters.add(new VarRead(ssaName));
				}

				final Expression expression = ssaNames.size() > 1
						? new FuncCall(PHI, FuncCallParameters.of(phiParameters))
						: phiParameters.get(0);
				statements.add(new VarDeclaration(phiFunction.ssaName, expression));
			}

			statements.addAll(statementsBlock.getStatements());
			statementsBlock.set(statements);
		}
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
				final Tuple<String, List<String>> phiFunction = getPhiFunction(statement);
				if (phiFunction == null) {
					continue;
				}

				if (phiFunction.second.size() == 1) {
					final String var = phiFunction.second.get(0);
					if (obsoletePhiVariantToPreviousVariant.put(phiFunction.first, new VarRead(var)) != null) {
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
				assertTrue(node.operation() == Assignment.Op.assign);

				final Expression newExpression = visitExpression(node.expression(), varToVariant);
				final String newName = varToVariant.getAssignmentName(node.name());
				consumer.accept(new VarDeclaration(newName, newExpression));
				return node;
			}

			@Override
			public SimpleStatement visitLocalVarDeclaration(VarDeclaration node) {
				final Expression newExpression = visitExpression(node.expression(), varToVariant);
				final String newName = varToVariant.getDeclarationName(node.name());
				consumer.accept(new VarDeclaration(newName, newExpression));
				return node;
			}

			@Override
			public SimpleStatement visitCall(CallStatement node) {
				final FuncCallParameters parameters = node.parameters().transform(expression -> visitExpression(expression, varToVariant));
				consumer.accept(new CallStatement(node.name(), parameters));
				return node;
			}
		}));
	}

	private Expression visitExpression(Expression expression, BlockInfo varToVariant) {
		return expression.visit(new ExpressionVisitor<>() {
			@Override
			public Expression visitBinary(BinaryExpression node) {
				final Expression left = visitExpression(node.left(), varToVariant);
				final Expression right = visitExpression(node.right(), varToVariant);
				return new BinaryExpression(left, node.operator(), right);
			}

			@Override
			public Expression visitFunctionCall(FuncCall node) {
				final FuncCallParameters parameters = node.parameters().transform(expression -> visitExpression(expression, varToVariant));
				return new FuncCall(node.name(), parameters);
			}

			@Override
			public Expression visitNumber(NumberLiteral node) {
				return node;
			}

			@Override
			public Expression visitVarRead(VarRead node) {
				final String ssaName = varToVariant.getUsageName(node.name());
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
				getDeclarationName(parameter.name());
			}
		}

		public void initializeFrom(@NotNull BlockInfo prevInfo) {
			varToCurrent.putAll(prevInfo.varToCurrent);
		}

		private void initializePhiDeclarations(@NotNull Set<String> incomingVars) {
			for (String incomingVar : incomingVars) {
				final String ssaName = getAssignmentName(incomingVar);
				phiFunctions.add(new PhiFunction(incomingVar, ssaName));
			}
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

		private PhiFunction(String originalName, String ssaName) {
			this.originalName = originalName;
			this.ssaName      = ssaName;
		}

		@Override
		public String toString() {
			return ssaName + " = phi()";
		}
	}
}
