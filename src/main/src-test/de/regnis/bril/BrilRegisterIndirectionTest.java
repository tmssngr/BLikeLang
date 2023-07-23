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
		             new BrilRegisterIndirection("r.", "t.", 2, Map.of("a", "v.0",
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
		                                         var -> var.equals("sum"), "r.", "t.", 2)
				             .transformInstructions(new BrilInstructions()
						                                    .constant("a", 1)
						                                    .constant("b", 2)
						                                    .add("sum", "a", "b")
						                                    .sub("diff", "sum", "a")
						                                    .get()));
	}

	@Test
	public void testCall2Parameters() {
		assertEquals(new BrilInstructions()
				             .constant("a", 1)
				             .constant("b", 2)
				             //.call("sum", "add", List.of("b", "a"))
				             .idi("r.0", "b")
				             .idi("r.1", "a")
				             .calli("r.0", "add", List.of(BrilFactory.argi("r.0"), BrilFactory.argi("r.1")))
				             .idi("sum", "r.0")
				             .get(),
		             new BrilRegisterIndirection(4, var -> false, "r.", "t.", 2)
				             .transformInstructions(new BrilInstructions()
						                                    .constant("a", 1)
						                                    .constant("b", 2)
						                                    .calli("sum", "add", List.of(BrilFactory.argi("b"), BrilFactory.argi("a")))
						                                    .get()));
	}

	@Test
	public void testCall3Parameters() {
		assertEquals(new BrilInstructions()
				             .constant("v.0", true)
				             .constant("v.1", 10)
				             .constant("v.2", 20)
				             .idb("r.0", "v.0")
				             .idi("r.1", "v.1")
				             .call(BrilRegisterIndirection.CALL_PUSH, List.of(
						             BrilFactory.argi("v.2")
				             ))
				             .calli("r.0", "getLeftOrRight", List.of(
						             BrilFactory.argb("r.0"),
						             BrilFactory.argi("r.1"),
						             BrilFactory.argi("v.2")
				             ))
				             .idi("v.3", "r.0")
				             .calli("v.2", BrilRegisterIndirection.CALL_POP, List.of())
				             .idi("r.0", "v.3")
				             .reti("r.0")
				             .get(),
		             new BrilRegisterIndirection("r.", "v.", 2,
		                                         Map.of(
				                                         "leftOrRight", "v.0",
				                                         "left", "v.1",
				                                         "right", "v.2",
				                                         "result", "v.3"
		                                         ),
		                                         var -> var.startsWith("p."))
				             .transformInstructions(new BrilInstructions()
						                                    .constant("leftOrRight", true)
						                                    .constant("left", 10)
						                                    .constant("right", 20)
						                                    .calli("result",
						                                           "getLeftOrRight",
						                                           List.of(BrilFactory.argb("leftOrRight"),
						                                                   BrilFactory.argi("left"),
						                                                   BrilFactory.argi("right")))
						                                    .reti("result")
						                                    .get()));
	}
}
