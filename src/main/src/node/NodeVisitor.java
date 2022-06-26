package node;

import de.regnis.b.ExpressionVisitor;
import de.regnis.b.node.*;
import org.jetbrains.annotations.Nullable;

/**
 * @author Thomas Singer
 */
public abstract class NodeVisitor<O> implements StatementVisitor<O>, ExpressionVisitor<O> {
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
}
