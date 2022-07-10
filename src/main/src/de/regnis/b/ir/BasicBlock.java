package de.regnis.b.ir;

import de.regnis.b.ast.*;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringOutput;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Singer
 */
public final class BasicBlock extends AbstractBlock {

	// Fields =================================================================

	private final List<SimpleStatement> statements = new ArrayList<>();

	// Setup ==================================================================

	public BasicBlock() {
		super(null);
	}

	public BasicBlock(@NotNull ControlFlowBlock prev) {
		super(prev);
	}

	public BasicBlock(@NotNull BasicBlock prev1, @NotNull BasicBlock prev2) {
		super(prev1);
		addPrev(prev2);
	}

	// Implemented ============================================================

	@Override
	public void detectRequiredVars() {
		for (SimpleStatement statement : statements) {
			statement.visit(new StatementVisitor<>() {
				@Override
				public Object visitAssignment(Assignment node) {
					detectRequiredVars(node.expression);
					addProvides(node.name);
					return node;
				}

				@Override
				public Object visitStatementList(StatementList node) {
					throw new IllegalStateException();
				}

				@Override
				public Object visitLocalVarDeclaration(VarDeclaration node) {
					detectRequiredVars(node.expression);
					addProvides(node.name);
					return node;
				}

				@Override
				public Object visitCall(CallStatement node) {
					for (Expression parameter : node.getParameters()) {
						detectRequiredVars(parameter);
					}
					return node;
				}

				@Override
				public Object visitReturn(ReturnStatement node) {
					if (node.expression != null) {
						detectRequiredVars(node.expression);
					}
					return node;
				}

				@Override
				public Object visitIf(IfStatement node) {
					throw new IllegalStateException();
				}

				@Override
				public Object visitWhile(WhileStatement node) {
					throw new IllegalStateException();
				}

				@Override
				public Object visitBreak(BreakStatement node) {
					throw new IllegalStateException();
				}
			});
		}
	}

	// Accessing ==============================================================

	public void add(@NotNull SimpleStatement statement) {
		statements.add(statement);
	}

	public StringOutput toString(StringOutput output) {
		for (SimpleStatement statement : statements) {
			CodePrinter.print(statement, output);
		}
		return output;
	}
}
