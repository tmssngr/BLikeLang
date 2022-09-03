package de.regnis.b.ir;

import de.regnis.b.ConstantFoldingTransformation;
import de.regnis.b.ast.*;
import de.regnis.utils.Tuple;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Thomas Singer
 */
public final class SsaConstantDetection implements BlockVisitor, SimpleStatementVisitor<SimpleStatement> {

	// Static =================================================================

	@NotNull
	public static Map<String, SimpleExpression> detectConstants(@NotNull ControlFlowGraph graph) {
		final SsaConstantDetection detector = new SsaConstantDetection();
		graph.iterate(detector);
		return detector.getVarsWithReplacements();
	}

	// Fields =================================================================

	private final Map<String, SimpleExpression> constants = new HashMap<>();
	private final Set<String> varsUsedInPhiFunctions = new HashSet<>();

	// Setup ==================================================================

	private SsaConstantDetection() {
	}

	// Implemented ============================================================

	@Override
	public void visitBasic(BasicBlock block) {
		visitStatements(block);
	}

	@Override
	public void visitIf(IfBlock block) {
		visitStatements(block);
	}

	@Override
	public void visitWhile(WhileBlock block) {
		visitStatements(block);
	}

	@Override
	public void visitExit(ExitBlock block) {
	}

	@Override
	public SimpleStatement visitAssignment(Assignment node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SimpleStatement visitLocalVarDeclaration(VarDeclaration node) {
		final Tuple<String, List<String>> phiFunction = StaticSingleAssignmentFactory.getPhiFunction(node);
		if (phiFunction != null) {
			varsUsedInPhiFunctions.addAll(phiFunction.second);
		}

		final String varName = node.name();
		node.expression().visit(new ExpressionVisitor<Expression>() {
			@Override
			public Expression visitBinary(BinaryExpression node) {
				final Expression simplifiedExpression = ConstantFoldingTransformation.simplifyBinaryExpression(node);
				if (simplifiedExpression instanceof SimpleExpression) {
					addConstant(varName, (SimpleExpression) simplifiedExpression);
				}
				return node;
			}

			@Override
			public Expression visitFunctionCall(FuncCall node) {
				return node;
			}

			@Override
			public Expression visitNumber(NumberLiteral node) {
				addConstant(varName, node);
				return node;
			}

			@Override
			public Expression visitVarRead(VarRead node) {
				addConstant(varName, node);
				return node;
			}
		});
		return node;
	}

	@Override
	public SimpleStatement visitCall(CallStatement node) {
		return node;
	}

	// Utils ==================================================================

	private Map<String, SimpleExpression> getVarsWithReplacements() {
		final Map<String, SimpleExpression> varToReplacement = new HashMap<>(constants);

		for (String var : varsUsedInPhiFunctions) {
			final SimpleExpression simpleExpression = varToReplacement.get(var);
			if (simpleExpression == null) {
				continue;
			}

			if (simpleExpression instanceof VarRead) {
				continue;
			}

			varToReplacement.remove(var);
		}
		return varToReplacement;
	}

	private void addConstant(String name, SimpleExpression expression) {
		if (ControlFlowGraph.RESULT.equals(name)) {
			return;
		}

		if (constants.put(name, expression) != null) {
			throw new IllegalStateException("Variable " + name + " declared multiple times");
		}
	}

	private void visitStatements(@NotNull StatementsBlock block) {
		for (SimpleStatement statement : block.getStatements()) {
			statement.visit(this);
		}
	}
}
