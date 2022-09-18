package de.regnis.b.ir;

import de.regnis.b.ast.*;
import de.regnis.utils.Tuple;
import de.regnis.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Thomas Singer
 */
public final class SsaRemovePhiFunctions implements Consumer<AbstractBlock> {

	// Static =================================================================

	public static void transform(@NotNull ControlFlowGraph graph) {
		graph.iterate(new SsaRemovePhiFunctions());
	}

	// Setup ==================================================================

	private SsaRemovePhiFunctions() {
	}

	// Implemented ============================================================

	@Override
	public void accept(AbstractBlock block) {
		if (!(block instanceof StatementsBlock statementsBlock)) {
			return;
		}

		final List<AbstractBlock> prevBlocks = statementsBlock.getPrevBlocks();

		final List<SimpleStatement> newStatements = new ArrayList<>();
		for (SimpleStatement statement : statementsBlock.getStatements()) {
			final Tuple<String, List<String>> phiFunction = StaticSingleAssignmentFactory.getPhiFunction(statement);
			if (phiFunction == null) {
				newStatements.add(statement);
				continue;
			}

			final List<String> variants = phiFunction.second;
			Utils.assertTrue(variants.size() == prevBlocks.size());

			for (int i = 0; i < prevBlocks.size(); i++) {
				final StatementsBlock prevBlock = (StatementsBlock) prevBlocks.get(i);
				final String variant = variants.get(i);
				applyPhiFunctionToPrevBlock(prevBlock, variant, phiFunction.first);
			}
		}
		statementsBlock.set(newStatements);
	}

	// Utils ==================================================================

	private void applyPhiFunctionToPrevBlock(StatementsBlock prevBlock, String prevVar, String newVar) {
		if (prevVar.equals(newVar)) {
			return;
		}

		final List<SimpleStatement> statements = new ArrayList<>(prevBlock.getStatements());
		statements.add(new VarDeclaration(newVar, new VarRead(prevVar)));
		prevBlock.set(statements);
	}
}
