package de.regnis.b.ir;

import de.regnis.b.ast.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Thomas Singer
 */
public final class ControlFlowGraphVarUsageDetector {

	// Static =================================================================

	public static void detectVarUsage(@NotNull ControlFlowGraph graph) {
		detectRequiredAndProvidedVars(graph);
		detectInputOutputVars(graph.getExitBlock());
	}

	// Utils ==================================================================

	private static void detectRequiredAndProvidedVars(ControlFlowGraph graph) {
		graph.iterate(block -> block.visit(new BlockVisitor() {
			@Override
			public void visitBasic(BasicBlock block) {
				detectRequiredAndProvidedVars(block);
			}

			@Override
			public void visitIf(IfBlock block) {
				detectRequiredVars(block.getCondition(), block);
			}

			@Override
			public void visitWhile(WhileBlock block) {
				detectRequiredVars(block.getCondition(), block);
			}

			@Override
			public void visitExit(ExitBlock block) {
			}
		}));
	}

	private static void detectRequiredAndProvidedVars(BasicBlock block) {
		for (SimpleStatement statement : block.getStatements()) {
			statement.visit(new StatementVisitor<>() {
				@Override
				public Object visitAssignment(Assignment node) {
					detectRequiredVars(node.expression, block);
					block.addWrittenVar(node.name);
					return node;
				}

				@Override
				public Object visitStatementList(StatementList node) {
					throw new IllegalStateException();
				}

				@Override
				public Object visitLocalVarDeclaration(VarDeclaration node) {
					detectRequiredVars(node.expression, block);
					block.addWrittenVar(node.name);
					return node;
				}

				@Override
				public Object visitCall(CallStatement node) {
					for (Expression parameter : node.getParameters()) {
						detectRequiredVars(parameter, block);
					}
					return node;
				}

				@Override
				public Object visitReturn(ReturnStatement node) {
					if (node.expression != null) {
						detectRequiredVars(node.expression, block);
					}
					return node;
				}

				@Override
				public Object visitIf(IfStatement node) {
					throw new IllegalStateException();
				}

				@Override
				public Object visitWhile(WhileStatement node) {
					throw new IllegalStateException();
				}

				@Override
				public Object visitBreak(BreakStatement node) {
					throw new IllegalStateException();
				}
			});
		}
	}

	private static void detectRequiredVars(Expression expression, AbstractBlock block) {
		expression.visit(new ExpressionVisitor<>() {
			@Override
			public Object visitBinary(BinaryExpression node) {
				detectRequiredVars(node.left, block);
				detectRequiredVars(node.right, block);
				return node;
			}

			@Override
			public Object visitFunctionCall(FuncCall node) {
				for (Expression parameter : node.getParameters()) {
					detectRequiredVars(parameter, block);
				}
				return node;
			}

			@Override
			public Object visitNumber(NumberLiteral node) {
				return node;
			}

			@Override
			public Object visitBoolean(BooleanLiteral node) {
				return node;
			}

			@Override
			public Object visitVarRead(VarRead node) {
				block.addReadVar(node.name);
				return node;
			}

			@Override
			public Object visitTypeCast(TypeCast node) {
				return node;
			}
		});
	}

	private static void detectInputOutputVars(ExitBlock lastBlock) {
		final List<AbstractBlock> blocks = new ArrayList<>();
		blocks.add(lastBlock);

		final Set<AbstractBlock> processedBlocks = new HashSet<>();

		while (blocks.size() > 0) {
			final AbstractBlock block = blocks.remove(0);
			if (!processedBlocks.add(block)) {
				continue;
			}

			final boolean changed = block.updateInputOutputFromNextBlocks();
			if (changed || processedBlocks.add(block)) {
				blocks.addAll(block.getPrev());
			}
		}
	}
}
