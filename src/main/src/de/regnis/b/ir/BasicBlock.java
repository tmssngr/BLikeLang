package de.regnis.b.ir;

import de.regnis.b.ast.*;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringOutput;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
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

	// Accessing ==============================================================

	public void add(@NotNull SimpleStatement statement) {
		statements.add(statement);
	}

	public List<? extends SimpleStatement> getStatements() {
		return Collections.unmodifiableList(statements);
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
