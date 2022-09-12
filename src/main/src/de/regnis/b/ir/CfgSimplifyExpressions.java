package de.regnis.b.ir;

import de.regnis.b.ast.*;
import de.regnis.b.ast.transformation.SimplifyExpression;
import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class CfgSimplifyExpressions implements BlockVisitor {

	// Static =================================================================

	public static void transform(@NotNull ControlFlowGraph graph) {
		graph.iterate(new CfgSimplifyExpressions());
	}

	// Setup ==================================================================

	private CfgSimplifyExpressions() {
	}

	// Implemented ============================================================

	@Override
	public void visitBasic(BasicBlock block) {
		block.replace((statement, consumer) -> statement.visit(new SimpleStatementVisitor<>() {
			@Override
			public Object visitAssignment(Assignment node) {
				final Expression expression = SimplifyExpression.toSimpleExpression(node.expression());
				consumer.accept(new Assignment(node.operation(), node.name(), expression));
				return node;
			}

			@Override
			public Object visitLocalVarDeclaration(VarDeclaration node) {
				final Expression expression = SimplifyExpression.toSimpleExpression(node.expression());
				consumer.accept(new VarDeclaration(node.name(), expression));
				return node;
			}

			@Override
			public Object visitCall(CallStatement node) {
				consumer.accept(node);
				return node;
			}
		}));
	}

	@Override
	public void visitIf(IfBlock block) {
		Utils.assertTrue(block.getStatements().isEmpty());
	}

	@Override
	public void visitWhile(WhileBlock block) {
		Utils.assertTrue(block.getStatements().isEmpty());
	}

	@Override
	public void visitExit(ExitBlock block) {
	}
}
