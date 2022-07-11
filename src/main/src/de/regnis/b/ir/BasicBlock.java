package de.regnis.b.ir;

import de.regnis.b.ast.*;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
		super("start", null);
	}

	public BasicBlock(@NotNull String label, @NotNull ControlFlowBlock prev) {
		super(label, prev);
	}

	public BasicBlock(@NotNull String label, @NotNull BasicBlock prev) {
		super(label, prev);
	}

	// Implemented ============================================================

	@Override
	public void visit(@NotNull BlockVisitor visitor) {
		visitor.visitBasic(this);
	}

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

	public StringOutput print(StringOutput output) {
		return print("", output);
	}

	public StringOutput print(String indentation, StringOutput output) {
		for (SimpleStatement statement : statements) {
			output.print(indentation);
			CodePrinter.print(statement, output);
		}
		return output;
	}

	@NotNull
	public AbstractBlock getSingleNext() {
		final List<AbstractBlock> next = getNext();
		if (next.size() != 1) {
			throw new IllegalStateException();
		}
		return next.get(0);
	}
}
