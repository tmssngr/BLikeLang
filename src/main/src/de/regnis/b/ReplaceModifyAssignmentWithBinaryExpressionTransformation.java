package de.regnis.b;

import de.regnis.b.ast.*;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public final class ReplaceModifyAssignmentWithBinaryExpressionTransformation {

	// Static =================================================================

	public static DeclarationList transform(@NotNull DeclarationList root) {
		final DeclarationVisitor<Declaration> visitor = new DeclarationVisitor<>() {
			@Override
			public Declaration visitFunctionDeclaration(FuncDeclaration node) {
				final StatementList newStatementList = handleStatementList(node.statementList);
				return new FuncDeclaration(node.type, node.name, node.parameters, newStatementList);
			}
		};

		final DeclarationList newDeclarationList = new DeclarationList();
		for (Declaration declaration : root.getDeclarations()) {
			final Declaration newDeclaration = declaration.visit(visitor);
			newDeclarationList.add(newDeclaration);
		}
		return newDeclarationList;
	}

	private ReplaceModifyAssignmentWithBinaryExpressionTransformation() {
	}

	// Utils ==================================================================

	private static StatementList handleStatementList(StatementList statementList) {
		final StatementList newStatementList = new StatementList();
		for (Statement statement : statementList.getStatements()) {
			newStatementList.add(statement.visit(new StatementVisitor<>() {
				@Override
				public Statement visitAssignment(Assignment node) {
					return switch (node.operation) {
						case add -> replaceWithAssignment(BinaryExpression.Op.add, node.name, node.expression);
						case bitAnd -> replaceWithAssignment(BinaryExpression.Op.bitAnd, node.name, node.expression);
						case bitOr -> replaceWithAssignment(BinaryExpression.Op.bitOr, node.name, node.expression);
						case bitXor -> replaceWithAssignment(BinaryExpression.Op.bitXor, node.name, node.expression);
						case divide -> replaceWithAssignment(BinaryExpression.Op.divide, node.name, node.expression);
						case modulo -> replaceWithAssignment(BinaryExpression.Op.modulo, node.name, node.expression);
						case multiply -> replaceWithAssignment(BinaryExpression.Op.multiply, node.name, node.expression);
						case shiftL -> replaceWithAssignment(BinaryExpression.Op.shiftL, node.name, node.expression);
						case shiftR -> replaceWithAssignment(BinaryExpression.Op.shiftR, node.name, node.expression);
						case sub -> replaceWithAssignment(BinaryExpression.Op.sub, node.name, node.expression);
						default -> node;
					};
				}

				@Override
				public Statement visitStatementList(StatementList node) {
					throw new UnsupportedOperationException();
				}

				@Override
				public Statement visitLocalVarDeclaration(VarDeclaration node) {
					return node;
				}

				@Override
				public Statement visitCall(CallStatement node) {
					return node;
				}

				@Override
				public Statement visitReturn(ReturnStatement node) {
					return node;
				}

				@Override
				public Statement visitIf(IfStatement node) {
					final StatementList ifStatements = handleStatementList(node.trueStatements);
					final StatementList elseStatements = handleStatementList(node.falseStatements);
					return new IfStatement(node.expression, ifStatements, elseStatements);
				}

				@Override
				public Statement visitWhile(WhileStatement node) {
					return new WhileStatement(node.expression, handleStatementList(node.statements));
				}

				@Override
				public Statement visitBreak(BreakStatement node) {
					return node;
				}
			}));
		}
		return newStatementList;
	}

	private static Assignment replaceWithAssignment(BinaryExpression.Op operator, String name, Expression expression) {
		return new Assignment(Assignment.Op.assign,
		                      name,
		                      new BinaryExpression(new VarRead(name),
		                                           operator,
		                                           expression));
	}
}
