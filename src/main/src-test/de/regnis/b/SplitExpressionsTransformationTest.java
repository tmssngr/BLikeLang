package de.regnis.b;

import de.regnis.b.ast.*;
import de.regnis.b.out.StringStringOutput;
import de.regnis.b.type.BasicTypes;
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
		assertEquals("a = (u8) 1", f -> f.
				assignment("a", new TypeCast(BasicTypes.UINT8, new NumberLiteral(1))));
		assertEquals("a = (u8) b", f -> f.
				assignment("a", new TypeCast(BasicTypes.UINT8, new VarRead("b"))));
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
				               a = false == $1""", f -> f.
				assignment("a",
				           new BinaryExpression(BooleanLiteral.FALSE,
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
		assertEquals("""
				             $1 := 1 - 2
				               a = (u8) $1""", f -> f.
				assignment("a", new TypeCast(BasicTypes.UINT8, new BinaryExpression(new NumberLiteral(1),
				                                                                    BinaryExpression.Op.sub,
				                                                                    new NumberLiteral(2)))));
		assertEquals("""
				             $1 := 1 - 2
				               return (u8) $1""", f -> f.
				returnStm(new TypeCast(BasicTypes.UINT8, new BinaryExpression(new NumberLiteral(1),
				                                                              BinaryExpression.Op.sub,
				                                                              new NumberLiteral(2)))));
		assertEquals("""
				             $1 : u8 = (u8) b
				               a = foo($1)""", f -> f.
				assignment("a", new FuncCall("foo",
				                             new FuncCallParameters()
						                             .add(new TypeCast(BasicTypes.UINT8, new VarRead("b"))))));
		assertEquals("""
				             $1 := b[]
				               a = $1 + 1""", f -> f.
				assignment("a", new BinaryExpression(new MemRead("b"),
				                                     BinaryExpression.Op.add,
				                                     new NumberLiteral(1))));
		assertEquals("""
				             $1 := b[]
				               $2 := c[]
				               a = $1 + $2""", f -> f.
				assignment("a", new BinaryExpression(new MemRead("b"),
				                                     BinaryExpression.Op.add,
				                                     new MemRead("c"))));
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
		assertEquals("""
				             $1 := 1 - 2
				               $2 : i8 = (i8) $1
				               a = foo($2)""", f -> f.
				assignment("a", new FuncCall("foo",
				                             new FuncCallParameters()
						                             .add(new TypeCast(BasicTypes.INT8, new BinaryExpression(new NumberLiteral(1),
						                                                                                     BinaryExpression.Op.sub,
						                                                                                     new NumberLiteral(2)))))));
	}

	@Test
	public void testGlobalVarExtractions() {
		assertEquals("""
				             a := 0
				             void __init_globals__() {
				               $1 := 2 * 3
				               a = 10 + $1
				             }
				             """, SplitExpressionsTransformation.transform(AstFactory.parseString("var a = 10 + 2 * 3;")));
	}

	@Test
	public void testDefineType() {
		assertEquals("""
				             i16 call(i16 p0) {
				               return p0
				             }
				             void main() {
				               $1 : u8 = 2 * 3
				               $2 : u8 = 10 + $1
				               $3 : i16 = (i16) $2
				               $4 : i16 = call(1)
				               v0 : i16 = $3 + $4
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
