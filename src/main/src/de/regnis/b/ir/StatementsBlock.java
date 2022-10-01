package de.regnis.b.ir;

import de.regnis.b.ast.SimpleStatement;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author Thomas Singer
 */
public abstract class StatementsBlock extends Block {

	// Fields =================================================================

	private final List<SimpleStatement> statements = new ArrayList<>();

	// Setup ==================================================================

	protected StatementsBlock(@NotNull String label, @Nullable Block prevBlock) {
		super(label, prevBlock);
	}

	// Accessing ==============================================================

	public final List<? extends SimpleStatement> getStatements() {
		return Collections.unmodifiableList(statements);
	}

	public final void add(@NotNull SimpleStatement statement) {
		statements.add(statement);
	}

	public final void set(@NotNull List<SimpleStatement> newStatements) {
		statements.clear();
		statements.addAll(newStatements);
	}

	public final void replace(BiConsumer<SimpleStatement, Consumer<SimpleStatement>> consumer) {
		final List<SimpleStatement> newStatements = new ArrayList<>();

		final Consumer<SimpleStatement> newStatementsConsumer = newStatements::add;

		for (SimpleStatement statement : statements) {
			consumer.accept(statement, newStatementsConsumer);
		}

		set(newStatements);
	}

	public final StringOutput print(StringOutput output) {
		return print("", output);
	}

	public StringOutput print(String indentation, StringOutput output) {
		for (SimpleStatement statement : statements) {
			output.print(indentation);
			CodePrinter.print(statement, output);
		}
		return output;
	}
}
