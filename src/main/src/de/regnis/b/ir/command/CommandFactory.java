package de.regnis.b.ir.command;

import de.regnis.b.TransformationFailedException;
import de.regnis.b.ast.*;
import de.regnis.b.ir.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

/**
 * @author Thomas Singer
 */
public final class CommandFactory {

	// Constants ==============================================================

	private static final String MSB = ".0";
	private static final String LSB = ".1";

	// Fields =================================================================

	private final List<Command> commands = new ArrayList<>();

	// Setup ==================================================================

	public CommandFactory() {
	}

	// Accessing ==============================================================

	public List<Command> getCommands() {
		return Collections.unmodifiableList(commands);
	}

	public void add(SimpleStatement statement) {
		statement.visit(new SimpleStatementVisitor<>() {
			@Override
			public Object visitAssignment(Assignment node) {
				add(node.name, node.operation, node.expression);
				return node;
			}

			@Override
			public Object visitLocalVarDeclaration(VarDeclaration node) {
				add(node.name, Assignment.Op.assign, node.expression);
				return node;
			}

			@Override
			public Object visitCall(CallStatement node) {
				handleCall(node.name, node.getParameters(), Set.of());
				return node;
			}
		});
	}

	public void add(IfStatement node, Supplier<String> trueLabel, Supplier<String> falseLabel) {
		addIf(node.expression, trueLabel, falseLabel);
	}

	public void add(@NotNull AbstractBlock block) {
		block.visit(new BlockVisitor() {
			@Override
			public void visitBasic(BasicBlock block) {
				for (SimpleStatement statement : block.getStatements()) {
					add(statement);
				}
				addCommand(new JumpCommand(() -> block.getSingleNext().label));
			}

			@Override
			public void visitIf(IfBlock block) {
				addIf(block.getExpression(), () -> block.getTrueBlock().label, () -> block.getFalseBlock().label);
			}

			@Override
			public void visitWhile(WhileBlock block) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void visitExit(ExitBlock block) {
				addCommand(new SimpleCommand(SimpleCommand.Operation.ret));
			}
		});
	}

	// Utils ==================================================================

	private void addIf(Expression expression, Supplier<String> trueLabel, Supplier<String> falseLabel) {
		expression.visit(new ExpressionVisitor<>() {
			@Override
			public Object visitBinary(BinaryExpression node) {
				if (!(node.left instanceof VarRead leftVar)
						|| !(node.right instanceof SimpleExpression right)) {
					throw new IllegalStateException();
				}

				if (node.operator == BinaryExpression.Op.equal) {
					handleIfEqualComparison(leftVar, right, falseLabel);
					return node;
				}

				if (node.operator == BinaryExpression.Op.lessThan) {
					handleIfNumericComparison(leftVar, right, trueLabel, falseLabel,
					                          JumpCommand.Condition.lt, JumpCommand.Condition.uge);
					return node;
				}
				if (node.operator == BinaryExpression.Op.lessEqual) {
					handleIfNumericComparison(leftVar, right, trueLabel, falseLabel,
					                          JumpCommand.Condition.le, JumpCommand.Condition.ugt);
					return node;
				}
				if (node.operator == BinaryExpression.Op.greaterEqual) {
					handleIfNumericComparison(leftVar, right, trueLabel, falseLabel,
					                          JumpCommand.Condition.ge, JumpCommand.Condition.ult);
					return node;
				}
				if (node.operator == BinaryExpression.Op.greaterThan) {
					handleIfNumericComparison(leftVar, right, trueLabel, falseLabel,
					                          JumpCommand.Condition.gt, JumpCommand.Condition.ule);
					return node;
				}
				throw new UnsupportedOperationException();
			}

			@Override
			public Object visitFunctionCall(FuncCall node) {
				throw new IllegalStateException();
			}

			@Override
			public Object visitNumber(NumberLiteral node) {
				throw new IllegalStateException();
			}

			@Override
			public Object visitVarRead(VarRead node) {
				addCommand(new TestCommand(TestCommand.Operation.or, node.name, node.name));
				addCommand(new JumpCommand(JumpCommand.Condition.z, falseLabel));
				return node;
			}
		});
	}

	private void handleCall(String functionName, List<Expression> parameters, Set<String> writeVars) {
		int parameterIndex = 0;
		for (Expression parameter : parameters) {
			if (parameter instanceof VarRead var) {
				addCommand(new StoreCommand(CallCommand.CALL_PARAMETER + parameterIndex + MSB, var.name + MSB));
				parameterIndex++;
				addCommand(new StoreCommand(CallCommand.CALL_PARAMETER + parameterIndex + LSB, var.name + LSB));
				parameterIndex++;
				continue;
			}

			if (parameter instanceof NumberLiteral literal) {
				addCommand(new StoreCommand(CallCommand.CALL_PARAMETER + parameterIndex + MSB, literal.value >> 8));
				parameterIndex++;
				addCommand(new StoreCommand(CallCommand.CALL_PARAMETER + parameterIndex + LSB, literal.value));
				parameterIndex++;
				continue;
			}

			throw new IllegalStateException("unexpected expression " + parameter);
		}

		addCommand(new CallCommand(functionName, writeVars));
	}

	private void handleIfEqualComparison(VarRead leftVar, SimpleExpression right, Supplier<String> falseLabel) {
		varOrNumber(right,
		            rightVar -> {
			            addCommand(new TestCommand(TestCommand.Operation.cmp, leftVar.name + MSB, rightVar + MSB));
			            addCommand(new JumpCommand(JumpCommand.Condition.nz, falseLabel));

			            addCommand(new TestCommand(TestCommand.Operation.cmp, leftVar.name + LSB, rightVar + LSB));
			            addCommand(new JumpCommand(JumpCommand.Condition.nz, falseLabel));
		            },
		            literal -> {
			            addCommand(new TestCommand(TestCommand.Operation.cmp, leftVar.name + MSB, literal >> 8));
			            addCommand(new JumpCommand(JumpCommand.Condition.nz, falseLabel));

			            addCommand(new TestCommand(TestCommand.Operation.cmp, leftVar.name + LSB, literal));
			            addCommand(new JumpCommand(JumpCommand.Condition.nz, falseLabel));
		            });
	}

	private void handleIfNumericComparison(VarRead leftVar, SimpleExpression right, Supplier<String> trueLabel, Supplier<String> falseLabel,
	                                       JumpCommand.Condition condition, JumpCommand.Condition inverseLsbCondition) {
		varOrNumber(right,
		            rightVar -> {
			            addCommand(new TestCommand(TestCommand.Operation.cmp, leftVar.name + MSB, rightVar + MSB));
			            addCommand(new JumpCommand(condition, trueLabel));
			            addCommand(new JumpCommand(JumpCommand.Condition.nz, falseLabel));

			            addCommand(new TestCommand(TestCommand.Operation.cmp, leftVar.name + LSB, rightVar + LSB));
			            addCommand(new JumpCommand(inverseLsbCondition, falseLabel));
		            },
		            literal -> {
			            addCommand(new TestCommand(TestCommand.Operation.cmp, leftVar.name + MSB, literal >> 8));
			            addCommand(new JumpCommand(condition, trueLabel));
			            addCommand(new JumpCommand(JumpCommand.Condition.nz, falseLabel));

			            addCommand(new TestCommand(TestCommand.Operation.cmp, leftVar.name + LSB, literal));
			            addCommand(new JumpCommand(inverseLsbCondition, falseLabel));
		            });
	}

	private void add(@NotNull String destVar, @NotNull Assignment.Op op, @NotNull Expression expression) {
		expression.visit(new ExpressionVisitor<>() {
			@Override
			public Object visitBinary(BinaryExpression node) {
				throw new TransformationFailedException("todo " + node.operator);
			}

			@Override
			public Object visitFunctionCall(FuncCall node) {
				if (op == Assignment.Op.assign) {
					handleCall(node.name, node.getParameters(), Set.of(CallCommand.RETURN_VALUE + MSB, CallCommand.RETURN_VALUE + LSB));

					addCommand(new StoreCommand(destVar + MSB, CallCommand.RETURN_VALUE + MSB));
					addCommand(new StoreCommand(destVar + LSB, CallCommand.RETURN_VALUE + LSB));
				}
				else {
					throw new TransformationFailedException("unsupported " + op);
				}
				return node;
			}

			@Override
			public Object visitNumber(NumberLiteral node) {
				final int value = node.value;
				switch (op) {
					case assign -> storeNumberLiteral(destVar, value);
					case add -> handleArithmetic(destVar, value, ArithmeticCommand.Operation.add, ArithmeticCommand.Operation.adc);
					case sub -> handleArithmetic(destVar, value, ArithmeticCommand.Operation.sub, ArithmeticCommand.Operation.sbc);
					case bitAnd -> handleArithmetic(destVar, value, ArithmeticCommand.Operation.and, ArithmeticCommand.Operation.and);
					case bitOr -> handleArithmetic(destVar, value, ArithmeticCommand.Operation.or, ArithmeticCommand.Operation.or);
					case bitXor -> handleArithmetic(destVar, value, ArithmeticCommand.Operation.xor, ArithmeticCommand.Operation.xor);
					case multiply -> handlePointArithmetic("_multiply", destVar, value);
					case modulo -> handlePointArithmetic("_modulo", destVar, value);
					case divide -> handlePointArithmetic("_divide", destVar, value);
					case shiftL -> {
						for (int i = 0; i < node.value; i++) {
							addCommand(new SimpleCommand(SimpleCommand.Operation.ccf));
							addCommand(new ModifyRegisterCommand(ModifyRegisterCommand.Operation.rlc, destVar + LSB));
							addCommand(new ModifyRegisterCommand(ModifyRegisterCommand.Operation.rlc, destVar + MSB));
						}
					}
					case shiftR -> {
						for (int i = 0; i < node.value; i++) {
							addCommand(new SimpleCommand(SimpleCommand.Operation.ccf));
							addCommand(new ModifyRegisterCommand(ModifyRegisterCommand.Operation.rrc, destVar + MSB));
							addCommand(new ModifyRegisterCommand(ModifyRegisterCommand.Operation.rrc, destVar + LSB));
						}
					}
					default -> throw new TransformationFailedException("todo " + op);
				}
				return node;
			}

			@Override
			public Object visitVarRead(VarRead node) {
				final String var = node.name;
				switch (op) {
					case assign -> storeVar(destVar, var);
					case add -> handleArithmetic(destVar, var, ArithmeticCommand.Operation.add, ArithmeticCommand.Operation.adc);
					case sub -> handleArithmetic(destVar, var, ArithmeticCommand.Operation.sub, ArithmeticCommand.Operation.sbc);
					case bitAnd -> handleArithmetic(destVar, var, ArithmeticCommand.Operation.and, ArithmeticCommand.Operation.and);
					case bitOr -> handleArithmetic(destVar, var, ArithmeticCommand.Operation.or, ArithmeticCommand.Operation.or);
					case bitXor -> handleArithmetic(destVar, var, ArithmeticCommand.Operation.xor, ArithmeticCommand.Operation.xor);
					case multiply -> handlePointArithmetic("_multiply", destVar, var);
					case divide -> handlePointArithmetic("_divide", destVar, var);
					default -> throw new TransformationFailedException("todo " + op);
				}
				return node;
			}
		});
	}

	private void storeNumberLiteral(String destVar, int value) {
		store8(destVar + LSB, value);
		store8(destVar + MSB, value >> 8);
	}

	private void storeVar(String destVar, String var) {
		addCommand(new StoreCommand(destVar + LSB, var + LSB));
		addCommand(new StoreCommand(destVar + MSB, var + MSB));
	}

	private void store8(String destVar, int value) {
		addCommand(new StoreCommand(destVar, value));
	}

	private void varOrNumber(SimpleExpression expr, Consumer<String> varHandler, IntConsumer numberHandler) {
		if (expr instanceof VarRead var) {
			varHandler.accept(var.name);
			return;
		}

		if (expr instanceof NumberLiteral literal) {
			numberHandler.accept(literal.value);
			return;
		}

		throw new TransformationFailedException("unexpected expression " + expr);
	}

	private void handlePointArithmetic(String functionName, String name, int literal) {
		addCommand(new StoreCommand(CallCommand.CALL_PARAMETER + 0 + MSB, name + MSB));
		addCommand(new StoreCommand(CallCommand.CALL_PARAMETER + 0 + LSB, name + LSB));
		addCommand(new StoreCommand(CallCommand.CALL_PARAMETER + 1 + MSB, literal >> 8));
		addCommand(new StoreCommand(CallCommand.CALL_PARAMETER + 1 + LSB, literal));
		addCommand(new CallCommand(functionName, Set.of(CallCommand.RETURN_VALUE + MSB, CallCommand.RETURN_VALUE + LSB)));
		addCommand(new StoreCommand(name + MSB, CallCommand.RETURN_VALUE + MSB));
		addCommand(new StoreCommand(name + LSB, CallCommand.RETURN_VALUE + LSB));
	}

	private void handlePointArithmetic(String functionName, String name, String var) {
		addCommand(new StoreCommand(CallCommand.CALL_PARAMETER + 0 + MSB, name + MSB));
		addCommand(new StoreCommand(CallCommand.CALL_PARAMETER + 0 + LSB, name + LSB));
		addCommand(new StoreCommand(CallCommand.CALL_PARAMETER + 1 + MSB, var + MSB));
		addCommand(new StoreCommand(CallCommand.CALL_PARAMETER + 1 + LSB, var + LSB));
		addCommand(new CallCommand(functionName, Set.of(CallCommand.RETURN_VALUE + MSB, CallCommand.RETURN_VALUE + LSB)));
		addCommand(new StoreCommand(name + MSB, CallCommand.RETURN_VALUE + MSB));
		addCommand(new StoreCommand(name + LSB, CallCommand.RETURN_VALUE + LSB));
	}

	private void handleArithmetic(String name, int value, ArithmeticCommand.Operation lsbOperation, ArithmeticCommand.Operation msbOperation) {
		addCommand(new ArithmeticCommand(lsbOperation, name + LSB, value));
		addCommand(new ArithmeticCommand(msbOperation, name + MSB, value >> 8));
	}

	private void handleArithmetic(String name, String var, ArithmeticCommand.Operation lsbOperation, ArithmeticCommand.Operation msbOperation) {
		addCommand(new ArithmeticCommand(lsbOperation, name + LSB, var + LSB));
		addCommand(new ArithmeticCommand(msbOperation, name + MSB, var + MSB));
	}

	private boolean addCommand(Command command) {
		return commands.add(command);
	}
}
