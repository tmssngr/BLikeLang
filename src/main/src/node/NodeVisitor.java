package node;

import de.regnis.b.node.ExpressionVisitor;
import de.regnis.b.node.*;
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
	public O visitFunctionDeclaration(FunctionDeclaration node) {
		node.statement.visit(this);
		return null;
	}

	@Nullable
	@Override
	public O visitAssignment(AssignmentNode node) {
		node.expression.visit(this);
		return null;
	}

	@Nullable
	@Override
	public O visitStatementList(StatementListNode node) {
		for (StatementNode statement : node.getStatements()) {
			statement.visit(this);
		}
		return null;
	}

	@Nullable
	@Override
	public O visitLocalVarDeclaration(VarDeclarationNode node) {
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
	public O visitBinary(BinaryExpressionNode node) {
		node.left.visit(this);
		node.right.visit(this);
		return null;
	}

	@Nullable
	@Override
	public O visitFunctionCall(FunctionCallNode node) {
		for (ExpressionNode expression : node.getParameters()) {
			expression.visit(this);
		}
		return null;
	}

	@Nullable
	@Override
	public O visitNumber(NumberNode node) {
		return null;
	}

	@Nullable
	@Override
	public O visitVarRead(VarReadNode node) {
		return null;
	}

	// Accessing ==============================================================

	public void visitDeclarationList(DeclarationList node) {
		for (Declaration declaration : node.getDeclarations()) {
			declaration.visit(this);
		}
	}
}
