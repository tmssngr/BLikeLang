package de.regnis.b.out;

import de.regnis.b.ast.*;
import de.regnis.b.type.BasicTypes;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Thomas Singer
 */
public class CodePrinterTest {

	// Accessing ==============================================================

	@Test
	public void testDeclaration() {
		Assert.assertEquals("""
				                    a := 0
				                    i16 sqr(i16 x) {
				                      return x * x
				                    }
				                    """,
		                    CodePrinter.print(
				                    new DeclarationList()
						                    .add(new GlobalVarDeclaration(new VarDeclaration("a", new NumberLiteral(0))))
						                    .add(new FuncDeclaration(BasicTypes.INT16, "sqr",
						                                             new FuncDeclarationParameters()
								                                             .add(new FuncDeclarationParameter(BasicTypes.INT16, "x")),
						                                             new StatementList()
								                                             .add(new ReturnStatement(
										                                             new BinaryExpression(new VarRead("x"),
										                                                                  BinaryExpression.Op.multiply,
										                                                                  new VarRead("x"))))))
		                    ));
		Assert.assertEquals("a := (u8) -1\n",
		                    CodePrinter.print(new DeclarationList()
				                                      .add(new GlobalVarDeclaration(new VarDeclaration("a",
				                                                                                       new TypeCast("u8",
				                                                                                                    new NumberLiteral(-1)))))));
		Assert.assertEquals("a := false\n",
		                    CodePrinter.print(new DeclarationList()
				                                      .add(new GlobalVarDeclaration(new VarDeclaration("a",
				                                                                                       BooleanLiteral.FALSE)))));
		Assert.assertEquals("""
				                    i16 foo() {
				                      if a > 0
				                      {
				                        b := 1
				                      }
				                      else
				                      {
				                        return a
				                      }
				                    }
				                    """,
		                    CodePrinter.print(
				                    new DeclarationList()
						                    .add(new FuncDeclaration(BasicTypes.INT16, "foo",
						                                             new FuncDeclarationParameters(),
						                                             new StatementList()
								                                             .add(new IfStatement(new BinaryExpression(new VarRead("a"),
								                                                                                       BinaryExpression.Op.greaterThan,
								                                                                                       new NumberLiteral(0)),
								                                                                  new StatementList()
										                                                                  .add(new VarDeclaration("b", new NumberLiteral(1))),
								                                                                  new StatementList()
										                                                                  .add(new ReturnStatement(new VarRead("a"))))
								                                             )))));
	}
}
