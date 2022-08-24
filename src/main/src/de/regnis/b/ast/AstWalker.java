package de.regnis.b.ast;

import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Singer
 */
public abstract class AstWalker implements DeclarationVisitor<Object>, StatementVisitor<Object>, ExpressionVisitor<Object> {

	// Implemented ============================================================

	@Override
	public Object visitFunctionDeclaration(FuncDeclaration node) {
		visitStatementList(node.statementList());
		return node;
	}

	@Override
	public Object visitAssignment(Assignment node) {
		visitExpression(node.expression());
		return node;
	}

	@Override
	public Object visitLocalVarDeclaration(VarDeclaration node) {
		visitExpression(node.expression());
		return node;
	}

	@Override
	public Object visitCall(CallStatement node) {
		visitCall(node.name(), node.parameters());
		return node;
	}

	@Override
	public Object visitStatementList(StatementList node) {
		for (Statement statement : node.getStatements()) {
			visitStatement(statement);
		}
		return node;
	}

	@Override
	public Object visitReturn(ReturnStatement node) {
		final Expression expression = node.expression();
		if (expression != null) {
			visitExpression(expression);
		}
		return node;
	}

	@Override
	public Object visitIf(IfStatement node) {
		visitExpression(node.expression());
		visitStatementList(node.trueStatements());
		visitStatementList(node.falseStatements());
		return node;
	}

	@Override
	public Object visitWhile(WhileStatement node) {
		visitExpression(node.expression());
		visitStatementList(node.statements());
		return node;
	}

	@Override
	public Object visitBreak(BreakStatement node) {
		return node;
	}

	@Override
	public Object visitBinary(BinaryExpression node) {
		visitExpression(node.left());
		visitExpression(node.right());
		return node;
	}

	@Override
	public Object visitFunctionCall(FuncCall node) {
		visitCall(node.name(), node.parameters());
		return node;
	}

	@Override
	public Object visitNumber(NumberLiteral node) {
		return node;
	}

	@Override
	public Object visitVarRead(VarRead node) {
		return node;
	}

	// Accessing ==============================================================

	protected void visitCall(@NotNull String name, FuncCallParameters node) {
		for (Expression expression : node.getExpressions()) {
			visitExpression(expression);
		}
	}

	public void visit(@NotNull DeclarationList root) {
		for (Declaration declaration : root.getDeclarations()) {
			declaration.visit(this);
		}
	}

	// Utils ==================================================================

	private void visitStatement(Statement statement) {
		statement.visit(this);
	}

	private void visitExpression(Expression expression) {
		expression.visit(this);
	}
}
