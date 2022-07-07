package de.regnis.b;

import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

/**
 * @author Thomas Singer
 */
public class PureFunctionDetectionTest {

	@Test
	public void testOnlyPure() {
		Assert.assertEquals(Set.of("max", "main"),
		                    PureFunctionDetection.detectPureFunctions(AstFactory.parseString(
				                    """
						                    int max(int p0, int p1) {
						                      if (p0 > p1) {
						                        return p0;
						                      }
						                      return p1;
						                    }
						                    void main() {
						                      max(1, 3);
						                    }""")));
	}

	@Test
	public void testMixed() {
		Assert.assertEquals(Set.of("max"),
		                    PureFunctionDetection.detectPureFunctions(AstFactory.parseString(
				                    """
						                    var a = 1;
						                    var b = 3;
						                    int max(int p0, int p1) {
						                      if (p0 > p1) {
						                        return p0;
						                      }
						                      return p1;
						                    }
						                    void main() {
						                      max(a, b);
						                    }""")));
	}
}
