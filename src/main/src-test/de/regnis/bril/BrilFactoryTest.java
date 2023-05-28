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
		factory.addFunction("max", "int")
				.addArgument("a", "int")
				.addArgument("b", "int")
				.addInstructions(instructions -> {
					instructions.id("result", "a");
					instructions.lt("cond", "a", "b");
					instructions.br("cond", "then", "else");

					instructions.label("then");
					instructions.id("result", "b");
					instructions.jmp("next");

					instructions.label("else");

					instructions.label("next");
					instructions.ret();
				});
		factory.addFunction("main", "void")
				.addInstructions(instructions -> {
					instructions.constant("a", 1);
					instructions.constant("b", 2);
					instructions.call("max", List.of("a", "b"));
					instructions.ret();
				});
	}
}
