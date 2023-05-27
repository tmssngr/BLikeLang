package de.regnis.b.ir;

import de.regnis.b.ast.*;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author Thomas Singer
 */
public class CommonSubexpressionEliminationTransformationTest {

	// Accessing ==============================================================

	@Test
	public void testBinReplacement() {
		assertTransform(List.of(
				assignment("a", new NumberLiteral(1)),
				assignment("b", new NumberLiteral(2)),
				assignmentAdd("sum1", new VarRead("a"), new VarRead("b")),
				assignment("sum2", new VarRead("sum1"))
		), List.of(
				assignment("a", new NumberLiteral(1)),
				assignment("b", new NumberLiteral(2)),
				assignmentAdd("sum1", new VarRead("a"), new VarRead("b")),
				assignmentAdd("sum2", new VarRead("a"), new VarRead("b"))
		));
	}

	@Test
	public void testSwappedBinReplacement() {
		assertTransform(List.of(
				assignment("a", new NumberLiteral(1)),
				assignment("b", new VarRead("a")),
				assignmentAdd("sum1", new VarRead("a"), new VarRead("a")),
				assignment("sum2", new VarRead("sum1"))
		), List.of(
				assignment("a", new NumberLiteral(1)),
				assignment("b", new VarRead("a")),
				assignmentAdd("sum1", new VarRead("a"), new VarRead("b")),
				assignmentAdd("sum2", new VarRead("b"), new VarRead("a"))
		));
	}

	@Test
	public void testIndirectBinReplacement() {
		assertTransform(List.of(
				assignment("a", new NumberLiteral(1)),
				assignment("b", new NumberLiteral(2)),
				assignment("c", new VarRead("b")),
				assignmentAdd("sum1", new VarRead("a"), new VarRead("b")),
				assignment("sum2", new VarRead("sum1"))
		), List.of(
				assignment("a", new NumberLiteral(1)),
				assignment("b", new NumberLiteral(2)),
				assignment("c", new VarRead("b")),
				assignmentAdd("sum1", new VarRead("a"), new VarRead("b")),
				assignmentAdd("sum2", new VarRead("a"), new VarRead("c"))
		));
	}

	@Test
	public void testReassignment() {
		assertTransform(List.of(
				assignment("a", new NumberLiteral(1)),
				assignment("b", new VarRead("a")),
				assignmentAdd("anext", new VarRead("a"), new NumberLiteral(1)),
				assignment("a", new NumberLiteral(3)),
				assignmentAdd("anext", new VarRead("a"), new NumberLiteral(1))
		), List.of(
				assignment("a", new NumberLiteral(1)),
				assignment("b", new VarRead("a")),
				assignmentAdd("anext", new VarRead("a"), new NumberLiteral(1)),
				assignment("a", new NumberLiteral(3)),
				assignmentAdd("anext", new VarRead("a"), new NumberLiteral(1))
		));
	}

	// Utils ==================================================================

	private static void assertTransform(List<Assignment> expected, List<SimpleStatement> input) {
		final var t = new CommonSubexpressionEliminationTransformation();
		Assert.assertEquals(expected, t.transform(input));
	}

	@NotNull
	private static Assignment assignment(String name, SimpleExpression exp) {
		return new Assignment(Assignment.Op.assign, name, exp);
	}

	@NotNull
	private static Assignment assignmentAdd(String name, SimpleExpression left, SimpleExpression right) {
		return new Assignment(Assignment.Op.assign, name, new BinaryExpression(left, BinaryExpression.Op.add, right));
	}
}
