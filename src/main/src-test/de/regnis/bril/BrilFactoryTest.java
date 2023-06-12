package de.regnis.bril;

import org.junit.Test;

import java.util.List;

/**
 * @author Thomas Singer
 */
public class BrilFactoryTest {

	@Test
	public void test() {
		final BrilFactory factory = new BrilFactory();
		factory.addFunction("max", "int",
		                    List.of(BrilFactory.argument("a", "int"),
		                            BrilFactory.argument("b", "int")),
		                    new BrilInstructions()
				                    .id("result", "a")
				                    .lessThan("cond", "a", "b")
				                    .branch("cond", "then", "else")

				                    .label("then")
				                    .id("result", "b")
				                    .jump("next")

				                    .label("else")

				                    .label("next")
				                    .ret()
				                    .get());
		factory.addFunction("main", "void",
		                    List.of(),
		                    new BrilInstructions()
				                    .constant("a", 1)
				                    .constant("b", 2)
				                    .call("max", List.of("a", "b"))
				                    .ret()
				                    .get());
	}
}
