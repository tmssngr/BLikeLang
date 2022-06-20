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
	public AssignmentNode visitAssignment(BLikeLangParser.AssignmentContext ctx) {
		final String var = ctx.var.getText();
		final ExpressionNode expression = (ExpressionNode) visitChildren(ctx.expression());
		return new AssignmentNode(var, expression);
	}

	@SuppressWarnings("SwitchStatementWithTooFewBranches")
	@Override
	public BinaryExpressionNode visitExprBinary(BLikeLangParser.ExprBinaryContext ctx) {
		final ExpressionNode left = (ExpressionNode) visitChildren(ctx.left);
		final ExpressionNode right = (ExpressionNode) visitChildren(ctx.right);

		return switch (ctx.operator.getType()) {
			case BLikeLangLexer.Plus -> new AddNode();
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
		final String varName = ctx.getText();
		return new VarReadNode(varName);
	}
}
