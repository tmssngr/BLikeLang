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
				                    int sqr(int x) {
				                      x += 1
				                      return x * x
				                    }
				                    """,
		                    CodePrinter.print(
				                    new DeclarationList()
						                    .add(new GlobalVarDeclaration(new VarDeclaration("a", new NumberLiteral(0))))
						                    .add(new FuncDeclaration(BasicTypes.INT16, "sqr",
						                                             new FuncDeclarationParameters()
								                                             .add(new FuncDeclarationParameter("x")),
						                                             new StatementList()
								                                             .add(new Assignment(Assignment.Op.add, "x", new NumberLiteral(1)))
								                                             .add(new ReturnStatement(
										                                             new BinaryExpression(new VarRead("x"),
										                                                                  BinaryExpression.Op.multiply,
										                                                                  new VarRead("x"))))))
		                    ));
		Assert.assertEquals("""
				                    int foo() {
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
