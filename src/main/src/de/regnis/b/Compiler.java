package de.regnis.b;

import de.regnis.b.ast.*;
import de.regnis.b.ir.*;
import de.regnis.b.ir.command.Command;
import de.regnis.b.ir.command.CommandFactory;
import de.regnis.b.out.StringOutput;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Thomas Singer
 */
public final class Compiler {

	// Fields =================================================================

	@NotNull
	private final StringOutput output;

	// Setup ==================================================================

	public Compiler(@NotNull StringOutput output) {
		this.output = output;
	}

	// Accessing ==============================================================

	public void compile(@NotNull String input, @NotNull StringOutput asmOutput) {
		DeclarationList root = AstFactory.parseString(input);
		root = DetermineTypesTransformation.transform(root, output);
		root = ReplaceModifyAssignmentWithBinaryExpressionTransformation.transform(root);

		boolean addSeparator = false;
		for (Declaration declaration : root.getDeclarations()) {
			if (addSeparator) {
				asmOutput.println();
			}
			addSeparator = true;
			declaration.visit(new DeclarationVisitor<>() {
				@Override
				public Object visitFunctionDeclaration(FuncDeclaration node) {
					processFunction(node, asmOutput);
					return node;
				}
			});
		}
	}

	// Utils ==================================================================

	private void processFunction(FuncDeclaration declaration, StringOutput output) {
		final ControlFlowGraph cfg = new ControlFlowGraph(declaration);

		CfgSimplifyExpressions.transform(cfg);
		CfgParameterToSimpleExpression.transform(cfg);

		StaticSingleAssignmentFactory.transform(cfg);

		while (true) {
			boolean changed = false;
			final Set<String> unusedVariables = SsaUnusedVarDetector.detectUnusedVariables(cfg);
			if (unusedVariables.size() > 0) {
				changed = true;
				SsaSearchAndReplace.remove(cfg, unusedVariables);
			}

			final Map<String, SimpleExpression> fromTo = SsaConstantDetection.detectConstants(cfg);
			if (fromTo.size() > 0) {
				changed = true;
				SsaSearchAndReplace.replace(cfg, fromTo);
			}

			if (!changed) {
				break;
			}
		}

		SsaToModifyAssignments.transform(cfg);

//		System.out.println(ControlFlowGraphPrinter.print(cfg, new StringStringOutput()));

		final RegisterAllocation registerAllocation = new RegisterAllocation(cfg);
		registerAllocation.initializeParameters(declaration);
		final Map<String, Integer> registers = registerAllocation.run();
		final Map<String, String> registerNames = convertToString(registers);
		SsaSearchAndReplace.rename(cfg, registerNames);

//		System.out.println(ControlFlowGraphPrinter.print(cfg, new StringStringOutput()));

		cfg.compact();

		final List<AbstractBlock> blocks = CfgBlockLinearizer.linearize(cfg);
		for (AbstractBlock block : blocks) {
			final List<Command> commands = toCommands(block);
			printCommands(block.label, commands, output);
		}
	}

	@NotNull
	private List<Command> toCommands(AbstractBlock block) {
		final CommandFactory factory = new CommandFactory();
		factory.add(block);
		return factory.getCommands();
	}

	private void printCommands(String label, List<Command> commands, StringOutput output) {
		output.print(label);
		output.print(":");
		output.println();

		for (Command command : commands) {
			output.print(ControlFlowGraphPrinter.INDENTATION);
			output.print(command.toString());
			output.println();
		}
	}

	private Map<String, String> convertToString(Map<String, Integer> registers) {
		final Map<String, String> registerNames = new HashMap<>();
		for (Map.Entry<String, Integer> entry : registers.entrySet()) {
			registerNames.put(entry.getKey(), "r" + entry.getValue());
		}
		return registerNames;
	}
}
