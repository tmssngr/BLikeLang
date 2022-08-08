package de.regnis.b;

import de.regnis.b.ast.DeclarationList;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringStringOutput;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Thomas Singer
 */
public class ReplaceModifyAssignmentWithBinaryExpressionTransformationTest {

	// Accessing ==============================================================

	@Test
	public void testKeepAsIs() {
		assertEquals("""
				             void main() {
				               a := 10
				               b := 20
				               c := a + b
				             }
				             """,
		             """
				             void main() {
				               int a = 10;
				               int b = 20;
				               int c = a + b;
				             }""", false);
	}

	@Test
	public void testTransform() {
		assertEquals("""
				             void main() {
				               a := 10
				               a = a + 1
				               a = a - 1
				               a = a * 2
				               a = a / 2
				               if 1
				               {
				                 a = a << 1
				                 a = a >> 1
				                 a = a & 10
				                 a = a | 10
				                 a = a ^ 10
				               }
				             }
				             """,
		             """
				             void main() {
				               v0 := 10
				               v0 = v0 + 1
				               v0 = v0 - 1
				               v0 = v0 * 2
				               v0 = v0 / 2
				               if 1
				               {
				                 v0 = v0 << 1
				                 v0 = v0 >> 1
				                 v0 = v0 & 10
				                 v0 = v0 | 10
				                 v0 = v0 ^ 10
				               }
				             }
				             """,
		             """
				             void main() {
				               int a = 10;
				               a += 1;
				               a -= 1;
				               a *= 2;
				               a /= 2;
				               if (true) {
				                 a <<= 1;
				                 a >>= 1;
				                 a &= 10;
				                 a |= 10;
				                 a ^= 10;
				               }
				             }""");
	}

	// Utils ==================================================================

	private void assertEquals(String expected, String expectedTyped, String source) {
		assertEquals(expected, source, false);
		final DeclarationList root = assertEquals(expectedTyped, source, true);
	}

	private DeclarationList assertEquals(String expected, String source, boolean withType) {
		DeclarationList root = AstFactory.parseString(source);
		if (withType) {
			root = DetermineTypesTransformation.transform(root, new StringStringOutput());
		}
		final DeclarationList transformedRoot = ReplaceModifyAssignmentWithBinaryExpressionTransformation.transform(root);
		Assert.assertEquals(expected, CodePrinter.print(transformedRoot));
		return transformedRoot;
	}
}
