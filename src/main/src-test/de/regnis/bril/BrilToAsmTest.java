package de.regnis.bril;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author Thomas Singer
 */
public class BrilToAsmTest {

	@Test
	public void test() {
		Assert.assertEquals(new BrilAsm()
				                    .label("main")
				                    .allocSpace(6)

				                    .iconstX(2)
				                    .istoreX(0)

				                    .iconstX(3)
				                    .istoreX(2)

				                    .iloadX(0)
				                    .ipushX()
				                    .iloadX(2)
				                    .ipushX()
				                    .call("sum")
				                    .istoreX(4)
				                    .ipop()
				                    .ipop()

				                    .iloadX(4)
				                    .ipushX()
				                    .call("print")
				                    .ipop()

				                    .freeSpace(6)
				                    .ret()
				                    //==========
				                    .label("sum")
				                    .allocSpace(2)
				                    .iloadX(4)
				                    .iloadY(6)
				                    .iaddXY()
				                    .istoreX(0)
				                    .iloadX(0)
				                    .freeSpace(2)
				                    .ret()
				                    .toLines(),
		                    BrilToAsm.convertToAsm(List.of(
				                                           BrilFactory.createFunction("main", "void", List.of(),
				                                                                      new BrilInstructions()
						                                                                      .constant("value1", 2)
						                                                                      .constant("value2", 3)
						                                                                      .call("result", "sum", List.of("value1", "value2"))
						                                                                      .print("result")
						                                                                      .get()
				                                           ),
				                                           BrilFactory.createFunction("sum", "int", List.of(BrilFactory.argument("a", "int"), BrilFactory.argument("b", "int")),
				                                                                      new BrilInstructions()
						                                                                      .add("sum", "a", "b")
						                                                                      .ret("sum")
						                                                                      .get()
				                                           )
		                                           )
		                    )
		);
	}
}
