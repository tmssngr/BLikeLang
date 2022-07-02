package de.regnis.b.node;

import org.jetbrains.annotations.Nullable;

/**
 * @author Thomas Singer
 */
public abstract class NodeVisitor<O> implements DeclarationVisitor<O>, StatementVisitor<O>, ExpressionVisitor<O> {

	// Implemented ============================================================

	@Nullable
	@Override
	public O visitGlobalVarDeclaration(GlobalVarDeclaration node) {
		node.node.expression.visit(this);
		return null;
	}

	@Nullable
	@Override
	public O visitFunctionDeclaration(FuncDeclaration node) {
		node.statementList.visit(this);
		return null;
	}

	@Nullable
	@Override
	public O visitAssignment(Assignment node) {
		node.expression.visit(this);
		return null;
	}

	@Nullable
	@Override
	public O visitStatementList(StatementList node) {
		for (Statement statement : node.getStatements()) {
			statement.visit(this);
		}
		return null;
	}

	@Nullable
	@Override
	public O visitLocalVarDeclaration(VarDeclaration node) {
		node.expression.visit(this);
		return null;
	}

	@Nullable
	@Override
	public O visitReturn(ReturnStatement node) {
		node.expression.visit(this);
		return null;
	}

	@Nullable
	@Override
	public O visitBinary(BinaryExpression node) {
		node.left.visit(this);
		node.right.visit(this);
		return null;
	}

	@Nullable
	@Override
	public O visitFunctionCall(FuncCall node) {
		for (Expression expression : node.getParameters()) {
			expression.visit(this);
		}
		return null;
	}

	@Nullable
	@Override
	public O visitNumber(NumberLiteral node) {
		return null;
	}

	@Nullable
	@Override
	public O visitVarRead(VarRead node) {
		return null;
	}

	// Accessing ==============================================================

	public void visitDeclarationList(DeclarationList node) {
		for (Declaration declaration : node.getDeclarations()) {
			declaration.visit(this);
		}
	}
}
