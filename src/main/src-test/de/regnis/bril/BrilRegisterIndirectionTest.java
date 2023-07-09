package de.regnis.bril;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Thomas Singer
 */
public class BrilRegisterIndirectionTest {

	// Accessing ==============================================================

	@Test
	public void testReadWriteStackParameters() {
		assertEquals(new BrilInstructions()
				             // .constant("a", 1)
				             .constant("v.0", 1)
				             // .constant("b", 2)
				             .constant("v.1", 2)
				             // .add("sum", "a", "b")
				             .add("t.4", "v.0", "v.1")
				             .idi("p.0", "t.4")
				             // .sub("diff", "sum", "a")
				             .idi("t.5", "p.0")
				             .sub("t.5", "t.5", "v.0")
				             .idi("p.1", "t.5")
				             .get(),
		             new BrilRegisterIndirection(Map.of("a", "v.0",
		                                                "b", "v.1",
		                                                "sum", "p.0",
		                                                "diff", "p.1"),
		                                         var -> var.startsWith("p."))
				             .transformInstructions(new BrilInstructions()
						                                    .constant("a", 1)
						                                    .constant("b", 2)
						                                    .add("sum", "a", "b")
						                                    .sub("diff", "sum", "a")
						                                    .get()));
	}

	@Test
	public void testSpilVar() {
		assertEquals(new BrilInstructions()
				             // .constant("a", 1)
				             .constant("a", 1)
				             // .constant("b", 2)
				             .constant("b", 2)
				             // .add("sum", "a", "b")
				             .add("t.4", "a", "b")
				             .idi("sum", "t.4")
				             // .sub("diff", "sum", "a")
				             .idi("t.5", "sum")
				             .sub("diff", "t.5", "a")
				             .get(),
		             new BrilRegisterIndirection(4,
		                                         var -> var.equals("sum"))
				             .transformInstructions(new BrilInstructions()
						                                    .constant("a", 1)
						                                    .constant("b", 2)
						                                    .add("sum", "a", "b")
						                                    .sub("diff", "sum", "a")
						                                    .get()));
	}

	@Test
	public void testCall() {
		assertEquals(new BrilInstructions()
				             .constant("a", 1)
				             .constant("b", 2)
				             //.call("sum", "add", List.of("b", "a"))
				             .idi("r.0", "b")
				             .idi("r.1", "a")
				             .calli("r.0", "add", List.of(BrilFactory.argi("r.0"), BrilFactory.argi("r.1")))
				             .idi("sum", "r.0")
				             .get(),
		             new BrilRegisterIndirection(4, var -> false)
				             .transformInstructions(new BrilInstructions()
						                                    .constant("a", 1)
						                                    .constant("b", 2)
						                                    .calli("sum", "add", List.of(BrilFactory.argi("b"), BrilFactory.argi("a")))
						                                    .get()));
	}
}
