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
		Assert.assertEquals("a := 0\n" +
				                    "i16 sqr(i16 x) {\n" +
				                    "  return x * x\n" +
				                    "}\n",
		                    CodePrinter.print(
				                    new DeclarationList()
						                    .add(new GlobalVarDeclaration(new VarDeclaration("a", new NumberLiteral(0))))
						                    .add(new FuncDeclaration(BasicTypes.INT16, "sqr",
						                                             new FuncDeclarationParameters()
								                                             .add(new FuncDeclarationParameter(BasicTypes.INT16, "x")),
						                                             new StatementList()
								                                             .add(new ReturnStatement(
										                                             BinaryExpression
												                                             .createMultiply(new VarRead("x"),
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
		Assert.assertEquals("i16 foo() {\n" +
				                    "  if a > 0\n" +
				                    "  {\n" +
				                    "    b := 1\n" +
				                    "  }\n" +
				                    "  else\n" +
				                    "  {\n" +
				                    "    return a\n" +
				                    "  }\n" +
				                    "}\n",
		                    CodePrinter.print(
				                    new DeclarationList()
						                    .add(new FuncDeclaration(BasicTypes.INT16, "foo",
						                                             new FuncDeclarationParameters(),
						                                             new StatementList()
								                                             .add(new IfStatement(BinaryExpression.createGt(new VarRead("a"),
								                                                                                            new NumberLiteral(0)),
								                                                                  new StatementList()
										                                                                  .add(new VarDeclaration("b", new NumberLiteral(1))),
								                                                                  new StatementList()
										                                                                  .add(new ReturnStatement(new VarRead("a"))))
								                                             )))));
	}
}
