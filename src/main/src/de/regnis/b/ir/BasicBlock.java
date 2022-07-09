package de.regnis.b.ir;

import de.regnis.b.ast.SimpleStatement;
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
