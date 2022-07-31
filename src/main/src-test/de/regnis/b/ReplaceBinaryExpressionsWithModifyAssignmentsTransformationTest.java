package de.regnis.b;

import de.regnis.b.ast.*;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringStringOutput;
import de.regnis.b.out.TreePrinter;
import de.regnis.b.type.BasicTypes;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Consumer;

/**
 * @author Thomas Singer
 */
public class ReplaceBinaryExpressionsWithModifyAssignmentsTransformationTest extends AbstractTransformationTest {

	// Accessing ==============================================================

	@Test
	public void testKeepAsIs() {
		assertEquals("a = 1", f -> f.
				assignment("a", new NumberLiteral(1)));

		assertEquals("a = b", f -> f.
				assignment("a", new VarRead("b")));

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
		assertEquals("a += 2", f -> f.
				assignment("a",
				           new BinaryExpression(new VarRead("a"),
				                                BinaryExpression.Op.add,
				                                new NumberLiteral(2))));
		assertEquals("""
				             $1 : u8 = 1
				               $1 += 2
				               a = $1""", f -> f.
				assignment("a",
				           new BinaryExpression(new NumberLiteral(1),
				                                BinaryExpression.Op.add,
				                                new NumberLiteral(2))));
		assertEquals("""
				             $1 : u8 = 2
				               $1 += 3
				               $2 : u8 = 1
				               $2 += $1
				               a = $2""", f -> f.
				assignment("a",
				           new BinaryExpression(new NumberLiteral(1),
				                                BinaryExpression.Op.add,
				                                new BinaryExpression(new NumberLiteral(2),
				                                                     BinaryExpression.Op.add,
				                                                     new NumberLiteral(3)))));
		assertEquals("""
				             $1 : u8 = 2
				               $1 *= 3
				               a += $1""", f -> f.
				assignment("a",
				           new BinaryExpression(new VarRead("a"),
				                                BinaryExpression.Op.add,
				                                new BinaryExpression(new NumberLiteral(2),
				                                                     BinaryExpression.Op.multiply,
				                                                     new NumberLiteral(3)))));
		assertEquals("""
				             $1 := 2 <= 3
				               $2 := false == $1
				               a = $2""", f -> f.
				assignment("a",
				           new BinaryExpression(BooleanLiteral.FALSE,
				                                BinaryExpression.Op.equal,
				                                new BinaryExpression(new NumberLiteral(2),
				                                                     BinaryExpression.Op.lessEqual,
				                                                     new NumberLiteral(3)))));
		assertEquals("""
				             $1 : u8 = 1
				               $1 += 2
				               $1 += 3
				               a = $1""", f -> f.
				assignment("a",
				           new BinaryExpression(new BinaryExpression(new NumberLiteral(1),
				                                                     BinaryExpression.Op.add,
				                                                     new NumberLiteral(2)),
				                                BinaryExpression.Op.add,
				                                new NumberLiteral(3))));
		assertEquals("""
				             $1 := foo()
				               $1 += 3
				               a = $1""", f -> f.
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
				             $1 : u8 = 1
				               $1 -= 2
				               a = foo($1)""", f -> f.
				assignment("a", new FuncCall("foo",
				                             new FuncCallParameters()
						                             .add(new BinaryExpression(new NumberLiteral(1),
						                                                       BinaryExpression.Op.sub,
						                                                       new NumberLiteral(2))))));
		assertEquals("""
				             $1 := b[]
				               $1 += 1
				               a = $1""", f -> f.
				assignment("a", new BinaryExpression(new MemRead("b"),
				                                     BinaryExpression.Op.add,
				                                     new NumberLiteral(1))));
		assertEquals("""
				             $1 := b[]
				               $2 := c[]
				               $1 += $2
				               a = $1""", f -> f.
				assignment("a", new BinaryExpression(new MemRead("b"),
				                                     BinaryExpression.Op.add,
				                                     new MemRead("c"))));

		assertEquals("""
				             $1 : u8 = 1
				               $1 *= 2
				               $2 : u8 = 3
				               $2 *= 4
				               $1 += $2
				               a := $1""", f -> f.
				varDeclaration("a",
				               new BinaryExpression(new BinaryExpression(new NumberLiteral(1),
				                                                         BinaryExpression.Op.multiply,
				                                                         new NumberLiteral(2)),
				                                    BinaryExpression.Op.add,
				                                    new BinaryExpression(new NumberLiteral(3),
				                                                         BinaryExpression.Op.multiply,
				                                                         new NumberLiteral(4)))));

		assertEquals("""
				             $1 : u8 = 1
				               $1 += b
				               $2 : u8 = 3
				               $2 *= 4
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
				               $1 : u8 = 2
				               $1 *= 3
				               $2 : u8 = 10
				               $2 += $1
				               call($2)
				             }
				             """, ReplaceBinaryExpressionsWithModifyAssignmentsTransformation.transform(
				AstFactory.parseString("""
						                       void main() {
						                         call(10 + 2 * 3);
						                       }""")));
	}

	@Test
	public void testCast() {
		assertEquals("""
				             $1 : u8 = 1
				               $1 -= 2
				               a = (u8) $1""", f -> f.
				assignment("a", new TypeCast(BasicTypes.UINT8, new BinaryExpression(new NumberLiteral(1),
				                                                                    BinaryExpression.Op.sub,
				                                                                    new NumberLiteral(2)))));
		assertEquals("""
				             $1 : u8 = 1
				               $1 -= 2
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
				             $1 : u8 = 1
				               $1 -= 2
				               $2 : i8 = (i8) $1
				               a = foo($2)""", f -> f.
				assignment("a", new FuncCall("foo",
				                             new FuncCallParameters()
						                             .add(new TypeCast(BasicTypes.INT8, new BinaryExpression(new NumberLiteral(1),
						                                                                                     BinaryExpression.Op.sub,
						                                                                                     new NumberLiteral(2)))))));
	}

	@Test
	public void testLargerExpression1() {
		final DeclarationList root = AstFactory.parseString("""
				                                                    u16 calc(u16 x, u16 y) {
				                                                      return x * x + y * y;
				                                                    }""");
		Assert.assertEquals("""
				                    +- u16 calc(u16 x, u16 y)
				                       +- statementList
				                          +- return
				                             +- operator +
				                                +- operator *
				                                |  +- read var x
				                                |  +- read var x
				                                +- operator *
				                                   +- read var y
				                                   +- read var y
				                    """,
		                    TreePrinter.print(root));
		Assert.assertEquals("""
				                    u16 calc(u16 x, u16 y) {
				                      $1 := x
				                      $1 *= x
				                      $2 := y
				                      $2 *= y
				                      $1 += $2
				                      return $1
				                    }
				                    """,
		                    CodePrinter.print(
				                    ReplaceBinaryExpressionsWithModifyAssignmentsTransformation.transform(
						                    root
				                    )));
	}

	@Test
	public void testLargerExpression2() {
		final DeclarationList root = AstFactory.parseString("""
				                                                    u8 calc(u16 year) {
				                                                      return (year + year/4 - h + v + 1) % 7;
				                                                    }""");
		Assert.assertEquals("""
				                    +- u8 calc(u16 year)
				                       +- statementList
				                          +- return
				                             +- operator %
				                                +- operator +
				                                |  +- operator +
				                                |  |  +- operator -
				                                |  |  |  +- operator +
				                                |  |  |  |  +- read var year
				                                |  |  |  |  +- operator /
				                                |  |  |  |     +- read var year
				                                |  |  |  |     +- literal 4
				                                |  |  |  +- read var h
				                                |  |  +- read var v
				                                |  +- literal 1
				                                +- literal 7
				                    """,
		                    TreePrinter.print(root));
		Assert.assertEquals("""
				                    +- u8 calc(u16 year)
				                       +- statementList
				                          +- $1 :=
				                          |  +- read var year
				                          +- $1 /=
				                          |  +- literal 4
				                          +- $2 :=
				                          |  +- read var year
				                          +- $2 +=
				                          |  +- read var $1
				                          +- $2 -=
				                          |  +- read var h
				                          +- $2 +=
				                          |  +- read var v
				                          +- $2 +=
				                          |  +- literal 1
				                          +- $2 %=
				                          |  +- literal 7
				                          +- return
				                             +- read var $2
				                    """,
		                    TreePrinter.print(
				                    ReplaceBinaryExpressionsWithModifyAssignmentsTransformation.transform(
						                    root
				                    )));
	}

	@Test
	public void testLargerExpression3() {
		final DeclarationList root = AstFactory.parseString("""
				                                                    void calc() {
				                                                      x = z + y * y - z;
				                                                    }""");
		Assert.assertEquals("""
				                    +- void calc()
				                       +- statementList
				                          +- x =
				                             +- operator -
				                                +- operator +
				                                |  +- read var z
				                                |  +- operator *
				                                |     +- read var y
				                                |     +- read var y
				                                +- read var z
				                    """,
		                    TreePrinter.print(root));
		Assert.assertEquals("""
				                    +- void calc()
				                       +- statementList
				                          +- $1 :=
				                          |  +- read var y
				                          +- $1 *=
				                          |  +- read var y
				                          +- $2 :=
				                          |  +- read var z
				                          +- $2 +=
				                          |  +- read var $1
				                          +- $2 -=
				                          |  +- read var z
				                          +- x =
				                             +- read var $2
				                    """,
		                    TreePrinter.print(
				                    ReplaceBinaryExpressionsWithModifyAssignmentsTransformation.transform(
						                    root
				                    )));
	}

	@Test
	public void testGlobalVarExtractions() {
		assertEquals("""
				             a := 0
				             void __init_globals__() {
				               $1 : u8 = 2
				               $1 *= 3
				               $2 : u8 = 10
				               $2 += $1
				               a = $2
				             }
				             """, ReplaceBinaryExpressionsWithModifyAssignmentsTransformation.transform(AstFactory.parseString("var a = 10 + 2 * 3;")));
	}

	@Test
	public void testDefineType() {
		assertEquals("""
				             i16 call(i16 p0) {
				               return p0
				             }
				             void main() {
				               $1 : u8 = 2
				               $1 *= 3
				               $2 : u8 = 10
				               $2 += $1
				               $3 : i16 = (i16) $2
				               $4 : i16 = call(1)
				               $3 += $4
				               v0 : i16 = $3
				             }
				             """,
		             ReplaceBinaryExpressionsWithModifyAssignmentsTransformation.transform(
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
		assertEquals(PREFIX + expected + SUFFIX, ReplaceBinaryExpressionsWithModifyAssignmentsTransformation.transform(root));
	}
}
