package de.regnis.bril;

import de.regnis.b.ir.RegisterColoring;
import de.regnis.utils.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Thomas Singer
 */
public final class BrilToAsm {

	// Constants ==============================================================

	private static final String PREFIX_STACK_PARAMETER = "p.";
	private static final String PREFIX_REGISTER = "r.";
	private static final String PREFIX_VIRTUAL_REGISTER = "v.";
	private static final int MAX_PARAMETERS_IN_REGISTERS = 2;
	private static final int MAX_REGISTERS = 8;

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
//		final List<BrilNode> blocks = BrilCfg.getBlocks(cfgFunction);
//		BrilCfgDetectVarLiveness.detectLiveness(blocks, true);
		final BrilNode functionFromCfg = BrilCfg.flattenBlocks(cfgFunction);

		final String name = BrilFactory.getName(functionFromCfg);
		asm.label(name);

		final BrilVarMapping varMapping = BrilVarMapping.createVarMapping(functionFromCfg);
		varMapping.allocLocalVarSpace(asm);

		final List<BrilNode> instructions = BrilFactory.getInstructions(functionFromCfg);
		convertToAsm(instructions, varMapping, asm);

		varMapping.freeLocalVarSpace(asm);
		asm.ret();
	}

	private static void createVarMapping(BrilNode cfgFunction) {
		// (a, b, foo) -> (v.0, v.1, v.2)
		initialVarRename(cfgFunction);

		if (true) {
			return;
		}

		boolean spilled = false;
		while (true) {
			final List<BrilNode> blocks = BrilCfg.getBlocks(cfgFunction);
			final List<BrilNode> arguments = BrilFactory.getArguments(cfgFunction);
			final List<String> argNames = getArgNames(arguments);

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

			final Function<String, String> mapping = var -> {
				final int virtualRegister = registerColoring.getVirtualRegister(var);
				if (localVars.contains(var)) {
					return PREFIX_VIRTUAL_REGISTER + virtualRegister;
				}
				return var;
			};

			BrilFactory.renameArgs(mapping, cfgFunction);
			BrilCfg.foreachInstructionOverAllBlocks(blocks, instruction ->
					BrilInstructions.replaceInOutVars(mapping, instruction)
			);

			BrilCfgDetectVarLiveness.detectLiveness(blocks, true);

			final int maxAllowedRegisters = getMaxAllowedRegisters(cfgFunction, spilled);
			final String varToSpil = getVarToSpil(blocks, maxAllowedRegisters);
			if (varToSpil == null) {
				return;
			}

			spilled = true;

			final BrilRegisterIndirection registerIndirection = new BrilRegisterIndirection(argNames.size() + registerColoring.getRegisterCount(),
			                                                                                var -> var.equals(varToSpil));
			registerIndirection.transformBlocks(blocks);

			BrilCfg.debugPrint(blocks);
		}


/*
		final BrilVars brilVars = new BrilVars();
		brilVars.assign(argNames, localVars,
		                name -> registerAllocation.getVirtualRegister(name));
*/
	}

	private static int getMaxAllowedRegisters(BrilNode cfgFunction, boolean spilled) {
		final int argCount = BrilFactory.getArguments(cfgFunction).size();
		int registers = MAX_REGISTERS;
		final int maxParametersInRegisters = MAX_PARAMETERS_IN_REGISTERS;
		registers -= Math.min(maxParametersInRegisters, argCount);
		if (argCount > maxParametersInRegisters || spilled) {
			registers -= 1;
		}

		return registers;
	}

	@Nullable
	private static String getVarToSpil(List<BrilNode> blocks, int maxAllowedRegisters) {
		final Set<String> largestOut = new HashSet<>();
		BrilCfg.foreachInstructionOverAllBlocks(blocks, instruction -> {
			final Set<String> liveOut = BrilCfgDetectVarLiveness.getLiveOut(instruction);
			final Set<String> registerVars = new HashSet<>();
			for (String var : liveOut) {
				if (var.startsWith("s.")) {
					continue;
				}

				registerVars.add(var);
			}

			if (registerVars.size() <= maxAllowedRegisters) {
				return;
			}

			if (registerVars.size() > largestOut.size()) {
				largestOut.clear();
				largestOut.addAll(registerVars);
			}
		});

		final Iterator<String> iterator = largestOut.iterator();
		return iterator.hasNext() ? iterator.next() : null;
	}

	private static void initialVarRename(BrilNode cfgFunction) {
		final List<BrilNode> blocks = BrilCfg.getBlocks(cfgFunction);
		final List<BrilNode> arguments = BrilFactory.getArguments(cfgFunction);

		final Map<String, String> mapping = new HashMap<>();

		for (int i = 0; i < arguments.size(); i++) {
			final BrilNode argument = arguments.get(i);
			final String argName = BrilFactory.getArgName(argument);
			final String newName = (i < MAX_PARAMETERS_IN_REGISTERS ? PREFIX_REGISTER : PREFIX_STACK_PARAMETER) + i;
			mapping.put(argName, newName);
		}

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
					mapping.put(var, PREFIX_VIRTUAL_REGISTER + mapping.size());
				}
			}
		});

		BrilFactory.renameArgs(mapping::get, cfgFunction);

		final BrilRegisterIndirection registerIndirection = new BrilRegisterIndirection(mapping,
		                                                                                var -> var.startsWith(PREFIX_STACK_PARAMETER));
		registerIndirection.transformBlocks(blocks);

		BrilCfgDetectVarLiveness.detectLiveness(blocks, true);
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
			protected void id(String dest, String type, String src) {
				varMapping.id(dest, type, src, asm);
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
			protected void ret(String var, String type) {
				if (BrilInstructions.INT.equals(type)) {
					varMapping.ireturnValueFromVar(var, asm);
				}
				else {
					throw new UnsupportedOperationException(type);
				}
			}

			@Override
			protected void jump(String target) {
				asm.jump(target);
			}

			@Override
			protected void branch(String var, String thenLabel, String elseLabel) {
				varMapping.branch(var, thenLabel, elseLabel, asm);
			}

			@Override
			protected void call(String name, List<BrilNode> args) {
				if (name.equals(BrilRegisterIndirection.CALL_PUSH)) {
					Utils.assertTrue(args.size() == 1);
					final BrilNode arg = args.get(0);
					final String argName = BrilFactory.getArgName(arg);
					final int register = varToRegister(argName);
					asm.ipush(register);
					return;
				}

				asm.call(name);
			}

			@Override
			protected void call(String dest, String type, String name, List<BrilNode> args) {
				if (name.equals(BrilRegisterIndirection.CALL_POP)) {
					Utils.assertTrue(args.isEmpty());
					final int register = varToRegister(dest);
					asm.ipop(register);
					return;
				}

				asm.call(name);
			}

			private int varToRegister(String arg) {
				Utils.assertTrue(arg.startsWith(PREFIX_REGISTER));
				return 2 * Integer.parseInt(arg.substring(PREFIX_REGISTER.length()));
			}
		}
		final MyHandler handler = new MyHandler();
		for (BrilNode instruction : instructions) {
			handler.visit(instruction);
		}
	}
}
