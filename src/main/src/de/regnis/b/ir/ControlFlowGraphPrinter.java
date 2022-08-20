package de.regnis.b.ir;

import de.regnis.b.ast.SimpleStatement;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringOutput;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Thomas Singer
 */
public class ControlFlowGraphPrinter {

	// Constants ==============================================================

	private static final String INDENTATION = "    ";

	// Static =================================================================

	public static StringOutput print(ControlFlowGraph graph, StringOutput output) {
		final ControlFlowGraphPrinter printer = new ControlFlowGraphPrinter(graph, output);
		return printer.print();
	}

	// Fields =================================================================

	private final ControlFlowGraph graph;
	private final StringOutput output;

	// Setup ==================================================================

	public ControlFlowGraphPrinter(@NotNull ControlFlowGraph graph, @NotNull StringOutput output) {
		this.graph = graph;
		this.output = output;
	}

	// Accessing ==============================================================

	@NotNull
	public final StringOutput print() {
		final List<AbstractBlock> blocks = CfgBlockLinearizer.linearize(graph);

		final BlockVisitor visitor = new BlockVisitor() {
			@Override
			public void visitBasic(BasicBlock block) {
				printLabel(block);
				print(block);
				printGoto(block, block.getSingleNext());
			}

			@Override
			public void visitIf(IfBlock block) {
				printLabel(block);

				print(block);
				printlnIndented("if ! goto " + block.getFalseBlock().label);
				printGoto(block, block.getTrueBlock());
			}

			@Override
			public void visitWhile(WhileBlock block) {
				printLabel(block);

				print(block);
			}

			@Override
			public void visitExit(ExitBlock block) {
				printLabel(block);

				printlnIndented("return");
			}

			private void printGoto(AbstractBlock block, AbstractBlock next) {
				if (blocks.indexOf(next) != blocks.indexOf(block) + 1) {
					printlnIndented("goto " + next.label);
					output.println();
				}
			}

			private void printLabel(AbstractBlock block) {
				output.print(getLabelText(block));
				output.println();
			}
		};

		for (AbstractBlock block : blocks) {
			block.visit(visitor);
		}

		return output;
	}

	@NotNull
	protected String getLabelText(AbstractBlock block) {
		return block.label + ":";
	}

	protected final void printlnIndented(String s) {
		output.print(INDENTATION + s);
		output.println();
	}

	protected void printBefore(String indentation, AbstractBlock block) {
	}

	protected void print(String indentation, SimpleStatement statement) {
		output.print(indentation);
		CodePrinter.print(statement, output);
	}

	// Utils ==================================================================

	private void print(BasicBlock block) {
		printBefore(INDENTATION, block);

		for (SimpleStatement statement : block.getStatements()) {
			print(INDENTATION, statement);
		}
	}

	private void print(IfBlock block) {
		printBefore(INDENTATION, block);

		block.print(INDENTATION, output);
	}

	private void print(WhileBlock block) {
		printBefore(INDENTATION, block);

		block.print(INDENTATION, output);
	}
}
