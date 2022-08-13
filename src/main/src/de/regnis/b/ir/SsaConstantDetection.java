package de.regnis.b.ir;

import de.regnis.b.ConstantFoldingTransformation;
import de.regnis.b.ast.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Singer
 */
public final class SsaConstantDetection implements BlockVisitor, SimpleStatementVisitor<SimpleStatement> {

	// Static =================================================================

	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
	@NotNull
	public static Map<String, SimpleExpression> detectConstants(@NotNull ControlFlowGraph graph) {
		final SsaConstantDetection detector = new SsaConstantDetection();
		graph.iterate(block -> block.visit(detector));
		return detector.constants;
	}

	// Fields =================================================================

	private final Map<String, SimpleExpression> constants = new HashMap<>();

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
		final String varName = node.name;
		node.expression.visit(new ExpressionVisitor<Expression>() {
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
