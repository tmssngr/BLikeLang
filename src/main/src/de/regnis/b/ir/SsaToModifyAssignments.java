package de.regnis.b.ir;

import de.regnis.b.ast.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author Thomas Singer
 */
public class SsaToModifyAssignments implements BlockVisitor {

	// Static =================================================================

	public static void transform(@NotNull ControlFlowGraph graph) {
		graph.iterate(new SsaToModifyAssignments());
	}

	public static void transform(@NotNull StatementsBlock block) {
		block.visit(new SsaToModifyAssignments());
	}

	// Implemented ============================================================

	@Override
	public void visitBasic(BasicBlock block) {
		processStatements(block);
	}

	@Override
	public void visitIf(IfBlock block) {
		processStatements(block);
	}

	@Override
	public void visitWhile(WhileBlock block) {
		processStatements(block);
	}

	@Override
	public void visitExit(ExitBlock block) {
	}

	// Utils ==================================================================

	private void processStatements(StatementsBlock block) {
		block.replace(((statement, consumer) -> statement.visit(new SimpleStatementVisitor<>() {
			@Override
			public Object visitAssignment(Assignment node) {
				throw new IllegalStateException();
			}

			@Override
			public Object visitLocalVarDeclaration(VarDeclaration node) {
				processDeclaration(node, consumer);
				return node;
			}

			@Override
			public Object visitCall(CallStatement node) {
				consumer.accept(node);
				return node;
			}
		})));
	}

	private void processDeclaration(VarDeclaration node, Consumer<SimpleStatement> consumer) {
		if (node.expression() instanceof BinaryExpression binEx
				&& handleBinaryExpression(node.name(), binEx, consumer)) {
			return;
		}

		consumer.accept(node);
	}

	private boolean handleBinaryExpression(String name, BinaryExpression expression, Consumer<SimpleStatement> consumer) {
		final Assignment.Op op = switch (expression.operator()) {
			case add -> Assignment.Op.add;
			case sub -> Assignment.Op.sub;
			case multiply -> Assignment.Op.multiply;
			case divide -> Assignment.Op.divide;
			case modulo -> Assignment.Op.modulo;
			case shiftL -> Assignment.Op.shiftL;
			case shiftR -> Assignment.Op.shiftR;
			case bitAnd -> Assignment.Op.bitAnd;
			case bitOr -> Assignment.Op.bitOr;
			case bitXor -> Assignment.Op.bitXor;
			default -> null;
		};
		if (op == null) {
			return false;
		}

		processDeclaration(new VarDeclaration(name, expression.left()), consumer);

		consumer.accept(new Assignment(op, name, expression.right()));
		return true;
	}
}
