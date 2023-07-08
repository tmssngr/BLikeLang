package de.regnis.bril;

import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author Thomas Singer
 */
public class BrilRegisterIndirectionTest {

	@Test
	public void testReadWriteStackParameters() {
		assertEquals(new BrilInstructions()
				             // .constant("a", 1)
				             .constant("v.0", 1)
				             // .constant("b", 2)
				             .constant("v.1", 2)
				             // .add("sum", "a", "b")
				             .add("t.4", "v.0", "v.1")
				             .id("p.0", "t.4")
				             // .sub("diff", "sum", "a")
				             .id("t.5", "p.0")
				             .sub("t.5", "t.5", "v.0")
				             .id("p.1", "t.5")
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
}
