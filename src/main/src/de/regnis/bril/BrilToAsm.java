package de.regnis.bril;

import de.regnis.b.ir.RegisterColoring;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Thomas Singer
 */
public final class BrilToAsm {

	// Static =================================================================

	public static List<String> convertToAsm(List<BrilNode> functions) {
		final BrilAsm asm = new BrilAsm();
		for (BrilNode function : functions) {
			convertToAsm(function, asm);
		}
		asm.simplify(BrilAsmSimplifier.create());
		return asm.toLines();
	}

	// Utils ==================================================================

	private static void convertToAsm(BrilNode function, BrilAsm asm) {
		final BrilNode cfgFunction;
		try {
			cfgFunction = BrilCfg.buildBlocks(function);
		}
		catch (BrilCfg.DuplicateLabelException | BrilCfg.NoExitBlockException | BrilCfg.InvalidTargetLabelException e) {
			throw new AssertionError(e);
		}
		createVarMapping(cfgFunction);
		final List<BrilNode> blocks = BrilCfg.getBlocks(cfgFunction);
//		BrilCfgDetectVarLiveness.detectLiveness(blocks, true);
		final BrilNode functionFromCfg = BrilCfg.flattenBlocks(cfgFunction);

		final String name = BrilFactory.getName(functionFromCfg);
		asm.label(name);

		final BrilVarMapping varMapping = BrilVarMapping.createVarMapping(functionFromCfg);
		varMapping.allocLocalVarSpace(asm);

		final List<BrilNode> instructions = BrilFactory.getInstructions(functionFromCfg);
		final String exitLabel = name + " exit";
		convertToAsm(instructions, varMapping, asm);

		asm.label(exitLabel);
		varMapping.freeLocalVarSpace(asm);
		asm.ret();
	}

	private static void createVarMapping(BrilNode cfgFunction) {
		final List<BrilNode> blocks = BrilCfg.getBlocks(cfgFunction);
		final List<BrilNode> arguments = BrilFactory.getArguments(cfgFunction);
		final List<String> argNames = getArgNames(arguments);

		initialVarRename(blocks, argNames, cfgFunction);

		BrilCfgDetectVarLiveness.detectLiveness(blocks, true);

		if (false) {
			final RegisterColoring registerColoring = new RegisterColoring();
			registerColoring.addEdgesBetween(new HashSet<>(argNames));
			for (int i = 0; i < argNames.size(); i++) {
				final String argName = argNames.get(i);
				registerColoring.setRegister(argName, i);
			}

			final Set<String> localVars = new HashSet<>();
			BrilCfg.foreachInstructionOverAllBlocks(blocks, instruction -> {
				final Set<String> liveOut = BrilCfgDetectVarLiveness.getLiveOut(instruction);
				registerColoring.addEdgesBetween(liveOut);
				localVars.addAll(liveOut);
			});
			argNames.forEach(localVars::remove);

			registerColoring.build();

			BrilCfg.foreachInstructionOverAllBlocks(blocks, instruction ->
					BrilInstructions.replaceInOutVars(new Function<>() {
						@Override
						public String apply(String var) {
							final int virtualRegister = registerColoring.getVirtualRegister(var);
							if (localVars.contains(var)) {
								return "v." + virtualRegister;
							}
							return var;
						}
					}, instruction)
			);

			BrilCfgDetectVarLiveness.detectLiveness(blocks, true);

			BrilCfg.debugPrint(blocks);
		}

/*
		final BrilVars brilVars = new BrilVars();
		brilVars.assign(argNames, localVars,
		                name -> registerAllocation.getVirtualRegister(name));
*/
	}

	private static void initialVarRename(List<BrilNode> blocks, List<String> argNames, BrilNode cfgFunction) {
		final Map<String, String> mapping = new HashMap<>();
		final Consumer<String> addMapping = var -> mapping.put(var, "v." + mapping.size());
		argNames.forEach(addMapping);

		BrilCfg.foreachInstructionOverAllBlocks(blocks, new Consumer<>() {
			@Override
			public void accept(BrilNode instruction) {
				final String dest = BrilInstructions.getDest(instruction);
				if (dest != null) {
					addRenameMapping(dest);
				}

				for (String requiredVar : BrilInstructions.getRequiredVars(instruction)) {
					addRenameMapping(requiredVar);
				}
			}

			private void addRenameMapping(String var) {
				if (!mapping.containsKey(var)) {
					addMapping.accept(var);
				}
			}
		});

		BrilCfg.replaceAllVars(cfgFunction,
		                       var -> mapping.get(var));
	}

	private static Set<String> getLocalVars(List<BrilNode> blocks, List<String> argNames) {
		final Set<String> localVars = new HashSet<>();
		BrilCfg.foreachInstructionOverAllBlocks(blocks, new Consumer<>() {
			@Override
			public void accept(BrilNode instruction) {
				final String dest = BrilInstructions.getDest(instruction);
				if (dest != null) {
					localVars.add(dest);
				}
				final Set<String> requiredVars = BrilInstructions.getRequiredVars(instruction);
				localVars.addAll(requiredVars);
			}
		});
		argNames.forEach(localVars::remove);
		return localVars;
	}

	private static List<String> getArgNames(List<BrilNode> arguments) {
		final List<String> argNames = new ArrayList<>();
		for (BrilNode argument : arguments) {
			argNames.add(BrilFactory.getArgName(argument));
		}
		return argNames;
	}
	private static void convertToAsm(List<BrilNode> instructions, BrilVarMapping varMapping, BrilAsm asm) {
		final class MyHandler extends BrilInstructions.Handler {
			@Override
			protected void label(String name) {
				asm.label(name);
			}

			@Override
			protected void constant(String dest, int value) {
				varMapping.loadConstant(dest, value, asm);
			}

			@Override
			protected void id(String dest, String var) {
				varMapping.id(dest, var, asm);
			}

			@Override
			protected void add(String dest, String var1, String var2) {
				varMapping.i_binaryOperator(dest, var1, var2, asm,
				                            (destReg, srcReg, asm1) -> asm1.iadd(destReg, srcReg));
			}

			@Override
			protected void sub(String dest, String var1, String var2) {
				varMapping.i_binaryOperator(dest, var1, var2, asm,
				                            (destReg, srcReg, asm1) -> asm1.isub(destReg, srcReg));
			}

			@Override
			protected void mul(String dest, String var1, String var2) {
				throw new UnsupportedOperationException();
			}

			@Override
			protected void and(String dest, String var1, String var2) {
				throw new UnsupportedOperationException();
			}

			@Override
			protected void lessThan(String dest, String var1, String var2) {
				varMapping.b_binaryOperator(dest, var1, var2, asm,
				                            (destReg, leftReg, rightReg, asm12) -> asm12.ilt(destReg, leftReg, rightReg));
			}

			@Override
			protected void ret(String var) {
				final String varType = varMapping.getType(var);
				if (BrilInstructions.INT.equals(varType)) {
					varMapping.ireturnValueFromVar(var, asm);
				}
				else {
					throw new UnsupportedOperationException(varType);
				}
			}

			@Override
			protected void jump(String target) {
				asm.jump(target);
			}

			@Override
			protected void branch(String var, String thenLabel, String elseLabel) {
				final String varType = varMapping.getType(var);
				if (!BrilInstructions.BOOL.equals(varType)) {
					throw new IllegalArgumentException("Expected a bool variable for the br command");
				}

				varMapping.branch(var, thenLabel, elseLabel, asm);
			}

			@Override
			protected void call(String name, List<String> args) {
				throw new UnsupportedOperationException();
			}

			@Override
			protected void call(String dest, String name, List<String> args) {
				for (int i = 0; i < args.size(); i++) {
					final String arg = args.get(i);
					final String argType = varMapping.getType(arg);
					if (BrilInstructions.INT.equals(argType)) {
						varMapping.callArgument(arg, i, asm);
					}
					else {
						throw new UnsupportedOperationException(argType);
					}
				}

				asm.call(name);

				final String type = varMapping.getType(dest);
				if (BrilInstructions.INT.equals(type)) {
					varMapping.istoreReturnValueInVar(dest, asm);
				}
				else {
					throw new UnsupportedOperationException(type);
				}

				for (int i = 0; i < args.size(); i++) {
					final String arg = args.get(i);
					final String argType = varMapping.getType(arg);
					if (BrilInstructions.INT.equals(argType)) {
						if (i > 1) {
							asm.ipop(0);
						}
					}
					else {
						throw new UnsupportedOperationException(argType);
					}
				}
			}

			@Override
			protected void print(String var) {
				final String varType = varMapping.getType(var);
				if (BrilInstructions.INT.equals(varType)) {
					varMapping.callArgument(var, 0, asm);
				}
				else {
					throw new UnsupportedOperationException(varType);
				}

				asm.call("print");
			}
		}
		final MyHandler handler = new MyHandler();
		for (BrilNode instruction : instructions) {
			handler.visit(instruction);
		}
	}
}
