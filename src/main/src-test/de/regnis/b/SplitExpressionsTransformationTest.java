package de.regnis.b;

import de.regnis.b.ast.*;
import de.regnis.b.out.StringStringOutput;
import org.junit.Test;

import java.util.function.Consumer;

/**
 * @author Thomas Singer
 */
public class SplitExpressionsTransformationTest extends AbstractTransformationTest {

	// Accessing ==============================================================

	@Test
	public void testKeepAsIs() {
		assertEquals("a = 1", f -> f.
				assignment("a", new NumberLiteral(1)));

		assertEquals("a = b", f -> f.
				assignment("a", new VarRead("b")));

		assertEquals("a = 1 + 2", f -> f.
				assignment("a",
				           new BinaryExpression(new NumberLiteral(1),
				                                BinaryExpression.Op.add,
				                                new NumberLiteral(2))));
		assertEquals("a = foo()", f -> f.
				assignment("a", new FuncCall("foo", new FuncCallParameters())));

		assertEquals("a = foo(1)", f -> f.
				assignment("a", new FuncCall("foo",
				                             new FuncCallParameters()
						                             .add(new NumberLiteral(1)))));
		assertEquals("a = foo(b)", f -> f.
				assignment("a", new FuncCall("foo",
				                             new FuncCallParameters()
						                             .add(new VarRead("b")))));
	}

	@Test
	public void testSingleExtraction() {
		assertEquals("""
				             $1 := 2 + 3
				               a = 1 + $1""", f -> f.
				assignment("a",
				           new BinaryExpression(new NumberLiteral(1),
				                                BinaryExpression.Op.add,
				                                new BinaryExpression(new NumberLiteral(2),
				                                                     BinaryExpression.Op.add,
				                                                     new NumberLiteral(3)))));
		assertEquals("""
				             $1 := 2 <= 3
				               a = 0 == $1""", f -> f.
				assignment("a",
				           new BinaryExpression(NumberLiteral.FALSE,
				                                BinaryExpression.Op.equal,
				                                new BinaryExpression(new NumberLiteral(2),
				                                                     BinaryExpression.Op.lessEqual,
				                                                     new NumberLiteral(3)))));
		assertEquals("""
				             $1 := 1 + 2
				               a = $1 + 3""", f -> f.
				assignment("a",
				           new BinaryExpression(new BinaryExpression(new NumberLiteral(1),
				                                                     BinaryExpression.Op.add,
				                                                     new NumberLiteral(2)),
				                                BinaryExpression.Op.add,
				                                new NumberLiteral(3))));
		assertEquals("""
				             $1 := foo()
				               a = $1 + 3""", f -> f.
				assignment("a",
				           new BinaryExpression(new FuncCall("foo",
				                                             new FuncCallParameters()),
				                                BinaryExpression.Op.add,
				                                new NumberLiteral(3))));
		assertEquals("""
				             $1 := bar(1)
				               a = foo($1)""", f -> f.
				assignment("a", new FuncCall("foo",
				                             new FuncCallParameters()
						                             .add(new FuncCall("bar",
						                                               new FuncCallParameters()
								                                               .add(new NumberLiteral(1)))))));
		assertEquals("""
				             $1 := 1 - 2
				               a = foo($1)""", f -> f.
				assignment("a", new FuncCall("foo",
				                             new FuncCallParameters()
						                             .add(new BinaryExpression(new NumberLiteral(1),
						                                                       BinaryExpression.Op.sub,
						                                                       new NumberLiteral(2))))));
	}

	@Test
	public void testMultipleExtractions() {
		assertEquals("""
				             $1 := 1 * 2
				               $2 := 3 * 4
				               a := $1 + $2""", f -> f.
				varDeclaration("a",
				               new BinaryExpression(new BinaryExpression(new NumberLiteral(1),
				                                                         BinaryExpression.Op.multiply,
				                                                         new NumberLiteral(2)),
				                                    BinaryExpression.Op.add,
				                                    new BinaryExpression(new NumberLiteral(3),
				                                                         BinaryExpression.Op.multiply,
				                                                         new NumberLiteral(4)))));

		assertEquals("""
				             $1 := 1 + b
				               $2 := 3 * 4
				               a := foo($1, $2)""", f -> f.
				varDeclaration("a",
				               new FuncCall("foo",
				                            new FuncCallParameters()
						                            .add(new BinaryExpression(new NumberLiteral(1),
						                                                      BinaryExpression.Op.add,
						                                                      new VarRead("b")))
						                            .add(new BinaryExpression(new NumberLiteral(3),
						                                                      BinaryExpression.Op.multiply,
						                                                      new NumberLiteral(4))))));
		assertEquals("""
				             void main() {
				               $1 := 2 * 3
				               $2 := 10 + $1
				               call($2)
				             }
				             """, SplitExpressionsTransformation.transform(
				AstFactory.parseString("""
						                       void main() {
						                         call(10 + 2 * 3);
						                       }""")));
	}

	@Test
	public void testDefineType() {
		assertEquals("""
				             int call(int p0) {
				               return p0
				             }
				             void main() {
				               $1 := 2 * 3
				               $2 := 10 + $1
				               $3 := call(1)
				               v0 := $2 + $3
				             }
				             """,
		             SplitExpressionsTransformation.transform(
				             DetermineTypesTransformation.transform(
						             AstFactory.parseString("""
								                                    int call(int a) {
								                                      return a;
								                                    }
								                                    void main() {
								                                      var a = 10 + 2 * 3 + call(1);
								                                    }"""), new StringStringOutput())));
	}

	// Utils ==================================================================

	private void assertEquals(String expected, Consumer<StatementListFactory> factory) {
		final DeclarationList root = createDocument(factory);
		assertEquals(PREFIX + expected + SUFFIX, SplitExpressionsTransformation.transform(root));
	}
}
