import com.syntevo.antlr.b.BLikeLangBaseVisitor;
import com.syntevo.antlr.b.BLikeLangLexer;
import com.syntevo.antlr.b.BLikeLangParser;
import node.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Thomas Singer
 */
@SuppressWarnings("MethodDoesntCallSuperMethod")
public final class AstFactory extends BLikeLangBaseVisitor<Node> {

	// Fields =================================================================

	@Nullable
	private StatementListNode statementListNode;

	// Setup ==================================================================

	public AstFactory() {
	}

	// Implemented ============================================================

	@Override
	public StatementListNode visitRoot(BLikeLangParser.RootContext ctx) {
		return visitStatements(ctx.statements());
	}

	@Override
	public StatementListNode visitStatements(BLikeLangParser.StatementsContext ctx) {
		final StatementListNode statementListNode = new StatementListNode();
		this.statementListNode = statementListNode;
		super.visitStatements(ctx);
		assert this.statementListNode == statementListNode;
		this.statementListNode = null;
		return statementListNode;
	}

	@Nullable
	@Override
	public Node visitStatementAssign(BLikeLangParser.StatementAssignContext ctx) {
		Objects.requireNonNull(statementListNode);

		final AssignmentNode node = visitAssignment(ctx.assignment());
		statementListNode.add(node);
		return null;
	}

	@Nullable
	@Override
	public Node visitStatementDeclaration(BLikeLangParser.StatementDeclarationContext ctx) {
		Objects.requireNonNull(statementListNode);

		final VarDeclarationNode node = visitVarDeclaration(ctx.varDeclaration());
		statementListNode.add(node);
		return null;
	}

	@Override
	public AssignmentNode visitAssignment(BLikeLangParser.AssignmentContext ctx) {
		final ExpressionNode expression = (ExpressionNode) visit(ctx.expression());
		return new AssignmentNode(ctx.var.getText(), expression, ctx.var.getLine(), ctx.var.getCharPositionInLine());
	}

	@Override
	public VarDeclarationNode visitVarDeclaration(BLikeLangParser.VarDeclarationContext ctx) {
		final String type = ctx.type.getText();
		final ExpressionNode expression = (ExpressionNode) visit(ctx.expression());
		return new VarDeclarationNode(type, ctx.var.getText(), expression, ctx.var.getLine(), ctx.var.getCharPositionInLine());
	}

	@Override
	public Node visitExprAddSub(BLikeLangParser.ExprAddSubContext ctx) {
		final ExpressionNode left = (ExpressionNode) visit(ctx.left);
		final ExpressionNode right = (ExpressionNode) visit(ctx.right);

		return switch (ctx.operator.getType()) {
			case BLikeLangLexer.Plus -> new AddNode(left, right);
			case BLikeLangLexer.Minus -> new SubNode(left, right);
			default -> throw new ParseCancellationException();
		};
	}

	@Override
	public ExpressionNode visitExprParen(BLikeLangParser.ExprParenContext ctx) {
		return (ExpressionNode)visit(ctx.expression());
	}

	@SuppressWarnings("SwitchStatementWithTooFewBranches")
	@Override
	public Node visitExprMultiply(BLikeLangParser.ExprMultiplyContext ctx) {
		final ExpressionNode left = (ExpressionNode) visit(ctx.left);
		final ExpressionNode right = (ExpressionNode) visit(ctx.right);

		return switch (ctx.operator.getType()) {
			case BLikeLangLexer.Multiply -> new MultiplyNode(left, right);
			default -> throw new ParseCancellationException();
		};
	}

	@Override
	public NumberNode visitExprNumber(BLikeLangParser.ExprNumberContext ctx) {
		final int value = Integer.parseUnsignedInt(ctx.Number().getText());
		return new NumberNode(value);
	}

	@Override
	public VarReadNode visitExprVar(BLikeLangParser.ExprVarContext ctx) {
		return new VarReadNode(ctx.var.getText(), ctx.var.getLine(), ctx.var.getCharPositionInLine());
	}
}
