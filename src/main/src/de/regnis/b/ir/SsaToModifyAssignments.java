package de.regnis.b.ir;

import de.regnis.b.ast.*;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * @author Thomas Singer
 */
public class SsaToModifyAssignments implements BlockVisitor {

	// Static =================================================================

	public static void transform(ControlFlowGraph graph) {
		graph.iterate(new SsaToModifyAssignments());
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
		if (node.expression() instanceof BinaryExpression binEx) {
			handleBinaryExpression(node.name(), binEx, consumer);
		}
		else {
			consumer.accept(node);
		}
	}

	@Nullable
	private Assignment.Op toAssignmentOperator(BinaryExpression binEx) {
		return switch (binEx.operator()) {
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
	}

	private void handleBinaryExpression(String name, BinaryExpression expression, Consumer<SimpleStatement> consumer) {
		processDeclaration(new VarDeclaration(name, expression.left()), consumer);

		final Assignment.Op op = toAssignmentOperator(expression);
		if (op == null) {
			throw new IllegalStateException();
		}

		consumer.accept(new Assignment(op, name, expression.right()));
	}
}
