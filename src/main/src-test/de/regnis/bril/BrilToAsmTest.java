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
				                    //.constant("value1", 2)
				                    .iconst(4, 2)
				                    //.constant("value2", 3)
				                    .iconst(6, 3)

				                    //.call("result", "sum", List.of("value1", "value2"))
				                    .iload(10, 4)
				                    .iload(12, 6)
				                    .call("sum")
				                    .iload(8, 10)

				                    //.print("result")
				                    .iload(10, 8)
				                    .call("print")
				                    .ret()
				                    //==========
				                    .label("sum")
				                    //.add("sum", "a", "b")
				                    .iload(0, 10)
				                    .iadd(0, 12)
				                    .iload(4, 0)
				                    //.ret("sum")
				                    .iload(10, 4)
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
