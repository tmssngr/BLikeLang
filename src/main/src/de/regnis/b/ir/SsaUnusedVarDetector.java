package de.regnis.b.ir;

import de.regnis.b.ast.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Thomas Singer
 */
public final class SsaUnusedVarDetector implements SimpleStatementVisitor<SimpleStatement>, ExpressionVisitor<Expression> {

	// Static =================================================================

	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
	@NotNull
	public static Set<String> detectUnusedVariables(@NotNull ControlFlowGraph graph) {
		final SsaUnusedVarDetector detector = new SsaUnusedVarDetector();
		for (AbstractBlock block : graph.getLinearizedBlocks()) {
			detector.handleBlock(block);
		}
		detector.declaredVariables.removeAll(detector.usedVariables);
		return detector.declaredVariables;
	}

	// Fields =================================================================

	private final Set<String> declaredVariables = new HashSet<>();
	private final Set<String> usedVariables = new HashSet<>();

	// Setup ==================================================================

	private SsaUnusedVarDetector() {
	}

	// Implemented ============================================================

	@Override
	public SimpleStatement visitAssignment(Assignment node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SimpleStatement visitLocalVarDeclaration(VarDeclaration node) {
		node.expression().visit(this);

		if (!node.name().equals(ControlFlowGraph.RESULT)
				&& !declaredVariables.add(node.name())) {
			throw new UnsupportedOperationException("Variable " + node.name() + " declared multiple times");
		}
		return node;
	}

	@Override
	public SimpleStatement visitCall(CallStatement node) {
		processParameters(node.parameters());
		return node;
	}

	@Override
	public Expression visitBinary(BinaryExpression node) {
		node.left().visit(this);
		node.right().visit(this);
		return node;
	}

	@Override
	public Expression visitFunctionCall(FuncCall node) {
		processParameters(node.parameters());
		return node;
	}

	@Override
	public Expression visitNumber(NumberLiteral node) {
		return node;
	}

	@Override
	public Expression visitVarRead(VarRead node) {
		usedVariables.add(node.name());
		return node;
	}

	// Utils ==================================================================

	private void handleBlock(AbstractBlock block) {
		switch (block) {
			case BasicBlock basicBlock ->
					visitStatements(basicBlock);

			case ControlFlowBlock cfBlock -> {
				visitStatements(cfBlock);
				cfBlock.getExpression().visit(this);
			}
			case ExitBlock ignore -> {
			}
		}
	}
	private void processParameters(FuncCallParameters node) {
		for (Expression parameter : node.getExpressions()) {
			parameter.visit(this);
		}
	}

	private void visitStatements(@NotNull StatementsBlock block) {
		for (SimpleStatement statement : block.getStatements()) {
			statement.visit(this);
		}
	}
}
