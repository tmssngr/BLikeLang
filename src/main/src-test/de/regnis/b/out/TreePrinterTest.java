package de.regnis.b.out;

import de.regnis.b.node.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author Thomas Singer
 */
public class TreePrinterTest {

	@Test
	public void testPrint() {
		final TreePrinter printer = new TreePrinter();

		Assert.assertEquals(List.of("operator +",
		                            "+- read var a",
		                            "+- literal 5"), printer.getStrings(BinaryExpression.createAdd(new VarRead("a"), new NumberLiteral(5))));
		Assert.assertEquals(List.of("a =",
		                            "+- literal 1"), printer.getStrings(new Assignment("a", new NumberLiteral(1))));
		Assert.assertEquals(List.of("a :=",
		                            "+- literal 1"), printer.getStrings(new VarDeclaration("a", new NumberLiteral(1))));
		Assert.assertEquals(List.of("a :=",
		                            "+- literal true"), printer.getStrings(new VarDeclaration("a", BooleanLiteral.TRUE)));
		Assert.assertEquals(List.of("if",
		                            "+- operator >",
		                            "|  +- read var a",
		                            "|  +- literal 0",
		                            "+- then",
		                            "|  +- b :=",
		                            "|     +- literal 1",
		                            "+- else",
		                            "   +- return",
		                            "      +- read var a"), printer.getStrings(new IfStatement(BinaryExpression.createGt(new VarRead("a"),
		                                                                                                                 new NumberLiteral(0)),
		                                                                                       new StatementList()
				                                                                                       .add(new VarDeclaration("b", new NumberLiteral(1))),
		                                                                                       new StatementList()
				                                                                                       .add(new ReturnStatement(new VarRead("a"))))));
	}
}