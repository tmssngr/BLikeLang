package de.regnis.bril;

import de.regnis.b.ir.RegisterColoring;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Thomas Singer
 */
public final class BrilPrepareForAsm {

	// Fields =================================================================

	private final String prefixVirtualRegister;
	private final String prefixRegister;
	private final String prefixStackParameter;
	private final int maxRegisters;
	private final int maxParametersInRegisters;

	// Setup ==================================================================

	public BrilPrepareForAsm(String prefixVirtualRegister, String prefixRegister, String prefixStackParameter, int maxRegisters, int maxParametersInRegisters) {
		this.prefixVirtualRegister    = prefixVirtualRegister;
		this.prefixRegister           = prefixRegister;
		this.prefixStackParameter     = prefixStackParameter;
		this.maxRegisters             = maxRegisters;
		this.maxParametersInRegisters = maxParametersInRegisters;
	}

	// Accessing ==============================================================

	public BrilNode prepare(BrilNode function) {
		final BrilNode cfgFunction;
		try {
			cfgFunction = BrilCfg.buildBlocks(function);
		}
		catch (BrilCfg.DuplicateLabelException | BrilCfg.NoExitBlockException | BrilCfg.InvalidTargetLabelException e) {
			throw new AssertionError(e);
		}

		// (a, b, foo) -> (v.0, v.1, v.2)
		initialVarRename(cfgFunction);

		boolean spilled = false;
		while (true) {
			final List<BrilNode> blocks = BrilCfg.getBlocks(cfgFunction);
			final List<BrilNode> arguments = BrilFactory.getArguments(cfgFunction);
			final List<String> argNames = BrilFactory.getArgNames(arguments);

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

			final String returnValueVar = prefixRegister + 0;
			if (localVars.contains(returnValueVar) && !registerColoring.containsVar(returnValueVar)) {
				registerColoring.setRegister(returnValueVar, 0);
			}

			registerColoring.build();

			final Function<String, String> mapping = var -> {
				final int virtualRegister = registerColoring.getVirtualRegister(var);
				if (!localVars.contains(var) || !var.startsWith(prefixVirtualRegister)) {
					return var;
				}
				return prefixVirtualRegister + virtualRegister;
			};

			BrilFactory.renameArgs(mapping, cfgFunction);
			BrilCfg.foreachInstructionOverAllBlocks(blocks, instruction ->
					BrilInstructions.replaceInOutVars(mapping, instruction)
			);

			BrilCfgDetectVarLiveness.detectLiveness(blocks, true);

			final int maxAllowedRegisters = getMaxAllowedRegisters(cfgFunction, spilled);
			final String varToSpil = getVarToSpil(blocks, maxAllowedRegisters);
			if (varToSpil == null) {
				return cfgFunction;
			}

			spilled = true;

			final BrilRegisterIndirection registerIndirection = new BrilRegisterIndirection(argNames.size() + registerColoring.getRegisterCount(),
			                                                                                var -> var.equals(varToSpil), prefixRegister, prefixVirtualRegister, 2);
			registerIndirection.transformBlocks(blocks);

			BrilCfg.debugPrint(blocks);
		}


/*
		final BrilVars brilVars = new BrilVars();
		brilVars.assign(argNames, localVars,
		                name -> registerAllocation.getVirtualRegister(name));
*/
	}

	// Utils ==================================================================

	private int getMaxAllowedRegisters(BrilNode cfgFunction, boolean spilled) {
		final int argCount = BrilFactory.getArguments(cfgFunction).size();
		int registers = maxRegisters;
		registers -= Math.min(maxParametersInRegisters, argCount);
		if (argCount > maxParametersInRegisters || spilled) {
			registers -= 1;
		}

		return registers;
	}

	private void initialVarRename(BrilNode cfgFunction) {
		final List<BrilNode> blocks = BrilCfg.getBlocks(cfgFunction);
		final List<BrilNode> arguments = BrilFactory.getArguments(cfgFunction);

		final Map<String, String> mapping = new HashMap<>();

		for (int i = 0; i < arguments.size(); i++) {
			final BrilNode argument = arguments.get(i);
			final String argName = BrilFactory.getArgName(argument);
			final String newName = (i < maxParametersInRegisters ? prefixRegister : prefixStackParameter) + i;
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
					mapping.put(var, prefixVirtualRegister + mapping.size());
				}
			}
		});

		BrilFactory.renameArgs(mapping::get, cfgFunction);

		final BrilRegisterIndirection registerIndirection = new BrilRegisterIndirection(prefixRegister, prefixVirtualRegister, maxParametersInRegisters, mapping,
		                                                                                var -> var.startsWith(prefixStackParameter));
		registerIndirection.transformBlocks(blocks);

		BrilCfgDetectVarLiveness.detectLiveness(blocks, true);
	}

	@Nullable
	private String getVarToSpil(List<BrilNode> blocks, int maxAllowedRegisters) {
		final Set<String> largestOut = new HashSet<>();
		BrilCfg.foreachInstructionOverAllBlocks(blocks, instruction -> {
			final Set<String> liveOut = BrilCfgDetectVarLiveness.getLiveOut(instruction);
			final Set<String> registerVars = new HashSet<>();
			for (String var : liveOut) {
				if (var.startsWith(prefixStackParameter)) {
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
}
