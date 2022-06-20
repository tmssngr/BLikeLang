import com.syntevo.antlr.b.BLikeLangBaseVisitor;
import com.syntevo.antlr.b.BLikeLangLexer;
import com.syntevo.antlr.b.BLikeLangParser;
import node.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;

/**
 * @author Thomas Singer
 */
@SuppressWarnings("MethodDoesntCallSuperMethod")
public final class AstFactory extends BLikeLangBaseVisitor<Node> {

	// Fields =================================================================

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

	@Override
	public Node visitStatementAssign(BLikeLangParser.StatementAssignContext ctx) {
		final AssignmentNode node = visitAssignment(ctx.assignment());
		statementListNode.add(node);
		return null;
	}

	@Override
	public Node visitStatementDeclaration(BLikeLangParser.StatementDeclarationContext ctx) {
		final VarDeclarationNode node = visitVarDeclaration(ctx.varDeclaration());
		statementListNode.add(node);
		return null;
	}

	@Override
	public AssignmentNode visitAssignment(BLikeLangParser.AssignmentContext ctx) {
		final ExpressionNode expression = (ExpressionNode) visit(ctx.expression());
		return new AssignmentNode(ctx.var, expression);
	}

	@Override
	public VarDeclarationNode visitVarDeclaration(BLikeLangParser.VarDeclarationContext ctx) {
		final String type = ctx.type.getText();
		final ExpressionNode expression = (ExpressionNode) visit(ctx.expression());
		return new VarDeclarationNode(type, ctx.var, expression);
	}

	@Override
	public BinaryExpressionNode visitExprBinary(BLikeLangParser.ExprBinaryContext ctx) {
		final ExpressionNode left = (ExpressionNode) visit(ctx.left);
		final ExpressionNode right = (ExpressionNode) visit(ctx.right);

		return switch (ctx.operator.getType()) {
			case BLikeLangLexer.Plus -> new AddNode(left, right);
			case BLikeLangLexer.Minus -> new SubNode(left, right);
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
		return new VarReadNode(ctx.var);
	}
}
