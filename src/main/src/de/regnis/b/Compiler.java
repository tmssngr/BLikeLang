package de.regnis.b;

import de.regnis.b.ast.*;
import de.regnis.b.ast.transformation.DetermineTypesTransformation;
import de.regnis.b.ast.transformation.RemoveUnusedFunctionsTransformation;
import de.regnis.b.ast.transformation.ReplaceModifyAssignmentWithBinaryExpressionTransformation;
import de.regnis.b.ir.*;
import de.regnis.b.ir.command.*;
import de.regnis.b.out.PathStringOutput;
import de.regnis.b.out.StringOutput;
import de.regnis.b.out.StringStringOutput;
import de.regnis.b.type.BasicTypes;
import de.regnis.b.type.Type;
import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static de.regnis.utils.Utils.notNull;

/**
 * @author Thomas Singer
 */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public final class Compiler {

	// Static =================================================================

	public static boolean DEBUG;

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.out.println("Missing input file");
			return;
		}

		final Path inputFile = Paths.get(args[0]);
		final String input = Files.readString(inputFile);

		final var compiler = new Compiler(new StringOutput() {
			@Override
			public void print(@NotNull String s) {
				System.out.print(s);
			}

			@Override
			public void println() {
				System.out.println();
			}
		});
		try (final PathStringOutput output = new PathStringOutput(inputFile.resolveSibling("output.asm"))) {
			output.print(ControlFlowGraphPrinter.INDENTATION);
			output.print(".org %8000");
			output.println();

			output.print(ControlFlowGraphPrinter.INDENTATION);
			// set RP
			output.print("ld %FD, #%20");
			output.println();

			compiler.compile(input, output);
		}
	}

	// Fields =================================================================

	private final StringOutput output;
	private final BuiltInFunctions builtInFunctions;

	// Setup ==================================================================

	public Compiler(@NotNull StringOutput output) {
		this.output = output;

		builtInFunctions = new BuiltInFunctions();
		builtInFunctions.add(BasicTypes.VOID, "printChar", 1, new BuiltInFunctions.FunctionCommandFactory() {
			@Override
			public void handleCall(@NotNull List<Expression> parameters, @Nullable String assignReturnToVar, @NotNull BuiltInFunctions.CommandFactory factory) {
				Utils.assertTrue(parameters.size() == 1);
				Utils.assertTrue(assignReturnToVar == null);

				factory.loadToRegister(parameters.get(0), 0);
				factory.addCommand(new Ld(0x15, CommandFactory.workingRegister(1)));
				factory.addCommand(new CallCommand("%0818"));
			}
		});
		builtInFunctions.add(BasicTypes.INT16, "getMem", 1, new BuiltInFunctions.FunctionCommandFactory() {
			@Override
			public void handleCall(@NotNull List<Expression> parameters, @Nullable String assignReturnToVar, @NotNull BuiltInFunctions.CommandFactory factory) {
				Utils.assertTrue(parameters.size() == 1);
				Utils.assertTrue(assignReturnToVar != null);

				factory.loadToRegister(parameters.get(0), 0);
				factory.addCommand(new LdFromMem(1, 0));
				factory.addCommand(new LdLiteral(CommandFactory.workingRegister(0), 0));
				factory.saveToVar(assignReturnToVar, 0);
			}
		});
		builtInFunctions.add(BasicTypes.VOID, "setMem", 2, new BuiltInFunctions.FunctionCommandFactory() {
			@Override
			public void handleCall(@NotNull List<Expression> parameters, @Nullable String assignReturnToVar, @NotNull BuiltInFunctions.CommandFactory factory) {
				Utils.assertTrue(parameters.size() == 2);
				Utils.assertTrue(assignReturnToVar == null);

				factory.loadToRegister(parameters.get(0), 0);
				factory.loadToRegister(parameters.get(1), 2);
				factory.addCommand(new LdToMem(0, 3));
			}
		});
	}

	// Accessing ==============================================================

	public void compile(@NotNull String input, @NotNull StringOutput asmOutput) {
		DeclarationList root = AstFactory.parseString(input);
		root = DetermineTypesTransformation.transform(root, builtInFunctions, output);
		root = RemoveUnusedFunctionsTransformation.transform(root, builtInFunctions, output);
		root = ReplaceModifyAssignmentWithBinaryExpressionTransformation.transform(root);

		final Function<String, Type> functionNameToReturnType = new FunctionNameToReturnType(root);

		final List<Declaration> declarations = new ArrayList<>(root.getDeclarations());
		final FuncDeclaration mainFunction = notNull(root.getFunction("main"));
		declarations.remove(mainFunction);
		declarations.add(0, mainFunction);

		boolean addSeparator = false;
		for (Declaration declaration : declarations) {
			if (addSeparator) {
				asmOutput.println();
			}
			addSeparator = true;
			declaration.visit(new DeclarationVisitor<>() {
				@Override
				public Object visitFunctionDeclaration(FuncDeclaration node) {
					processFunction(node, asmOutput, functionNameToReturnType);
					return node;
				}
			});
		}
	}

	// Utils ==================================================================

	private void processFunction(FuncDeclaration declaration, StringOutput output, Function<String, Type> functionNameToReturnType) {
		final ControlFlowGraph cfg = new ControlFlowGraph(declaration);

		CfgSimplifyExpressions.transform(cfg);
		CfgParameterToSimpleExpression.transform(cfg);
		if (DEBUG) {
			System.out.println("before ssa");
			System.out.println(ControlFlowGraphPrinter.print(cfg, new StringStringOutput()));
		}

		StaticSingleAssignmentFactory.transform(cfg);
		if (DEBUG) {
			System.out.println("ssa");
			System.out.println(ControlFlowGraphPrinter.print(cfg, new StringStringOutput()));
		}

		simplify(cfg);
		if (DEBUG) {
			System.out.println("ssa optimized");
			System.out.println(ControlFlowGraphPrinter.print(cfg, new StringStringOutput()));
		}

		SplitExpressionsTransformation.transform(cfg);
		if (DEBUG) {
			System.out.println("ssa split expressions");
			System.out.println(ControlFlowGraphPrinter.print(cfg, new StringStringOutput()));
		}

		SsaToModifyAssignments.transform(cfg);
		if (DEBUG) {
			System.out.println("ssa modify");
			System.out.println(ControlFlowGraphPrinter.print(cfg, new StringStringOutput()));
		}

		SsaRemovePhiFunctions.transform(cfg);
		if (DEBUG) {
			System.out.println("removed phi functions");
			System.out.println(ControlFlowGraphPrinter.print(cfg, new StringStringOutput()));
		}

		final RegisterAllocation.Result registers = RegisterAllocation.run(cfg);

		CfgReuseVarsTransformation.transform(cfg, registers);
		if (DEBUG) {
			System.out.println("reuse vars");
			System.out.println(ControlFlowGraphPrinter.print(cfg, new StringStringOutput()));
		}

		final StackPositionProvider stackPositionProvider = new StackPositionProviderImpl(registers, 5, 4);

		CommandList commandList = new CommandList();

		final CommandFactory commandFactory = new CommandFactory(stackPositionProvider, functionNameToReturnType, builtInFunctions, commandList);
		commandFactory.addPrelude(declaration);

		final List<AbstractBlock> blocks = cfg.getLinearizedBlocks();
		for (AbstractBlock block : blocks) {
			commandFactory.add(block);
		}

		if (DEBUG) {
			System.out.println("command line");
			commandList.print(StringOutput.out);
		}

		commandList = CommandListOptimizations.optimize(commandList);
		commandList.print(output);
	}

	private void simplify(ControlFlowGraph cfg) {
		while (true) {
			boolean changed = false;
			final Set<String> unusedVariables = SsaUnusedVarDetector.detectUnusedVariables(cfg);
			if (unusedVariables.size() > 0) {
				changed = true;
				SsaSearchAndReplace.remove(cfg, unusedVariables);
			}

			if (SsaConstantDetection.transform(cfg)) {
				changed = true;
			}

			if (!changed) {
				break;
			}
		}
	}

	// Inner Classes ==========================================================

	private record FunctionNameToReturnType(DeclarationList root) implements Function<String, Type> {
			private FunctionNameToReturnType(@NotNull DeclarationList root) {
				this.root = root;
			}

			@Nullable
			@Override
			public Type apply(String functionName) {
				final FuncDeclaration function = root.getFunction(functionName);
				return function != null ? function.type() : null;
			}
		}
}