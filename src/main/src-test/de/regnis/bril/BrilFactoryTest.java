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
		                    List.of(BrilNode.argument("a", "int"),
		                            BrilNode.argument("b", "int")),
		                    List.of(
				                    BrilInstructions.id("result", "a"),
				                    BrilInstructions.lessThan("cond", "a", "b"),
				                    BrilInstructions.branch("cond", "then", "else"),

				                    BrilInstructions.label("then"),
				                    BrilInstructions.id("result", "b"),
				                    BrilInstructions.jump("next"),

				                    BrilInstructions.label("else"),

				                    BrilInstructions.label("next"),
				                    BrilInstructions.ret()
		                    ));
		factory.addFunction("main", "void",
		                    List.of(),
		                    List.of(
				                    BrilInstructions.constant("a", 1),
				                    BrilInstructions.constant("b", 2),
				                    BrilInstructions.call("max", List.of("a", "b")),
				                    BrilInstructions.ret()
		                    ));
	}
}
