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
				                    int sqr(int x) {
				                      x += 1
				                      return x * x
				                    }
				                    """,
		                    CodePrinter.print(
				                    new DeclarationList()
						                    .add(new FuncDeclaration(BasicTypes.INT16, "sqr",
						                                             FuncDeclarationParameters.of(new FuncDeclarationParameter("x")),
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
						                                             FuncDeclarationParameters.empty(),
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
