package de.regnis.b.ast.transformation;

import de.regnis.b.Messages;
import de.regnis.b.ast.AstFactory;
import de.regnis.b.ast.BuiltInFunctions;
import de.regnis.b.ast.DeclarationList;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringOutput;
import de.regnis.b.out.StringStringOutput;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Thomas Singer
 */
public final class RemovedUnusedFunctionsTransformationTest {

	// Constants ==============================================================

	private static final String NO_WARNING = "";

	// Accessing ==============================================================

	@Test
	public void nothingToRemove() {
		assertSuccessfullyTransformed("""
				                              void main() {
				                              }
				                              """,
		                              NO_WARNING,
		                              """
				                              void main() {
				                              }
				                              """);
		assertSuccessfullyTransformed("""
				                              int one() {
				                                return 1
				                              }
				                              void main() {
				                                one()
				                              }
				                              """,
		                              NO_WARNING,
		                              """
				                              int one() return 1
				                              void main() {
				                                one();
				                              }
				                              """);
		assertSuccessfullyTransformed("""
				                              int getInput() {
				                                return 192
				                              }
				                              void print(int chr) {
				                              }
				                              void printHex4(int i) {
				                                i = i & 15
				                                chr := 0
				                                if i < 10
				                                {
				                                  chr = i + 48
				                                }
				                                else
				                                {
				                                  chr = i - 10 + 65
				                                }
				                                print(chr)
				                              }
				                              void printHex8(int i) {
				                                printHex4(i >> 4)
				                                printHex4(i)
				                              }
				                              void printHex16(int i) {
				                                printHex8(i >> 8)
				                                printHex8(i)
				                              }
				                              void main() {
				                                input := getInput()
				                                printHex16(input)
				                              }
				                              """,
		                              NO_WARNING,
		                              """
				                              int getInput() {
				                                return 192
				                              }

				                              void print(int chr) {
				                              }

				                              void printHex4(int i) {
				                                i = i & 15
				                                int chr = 0
				                                if (i < 10) {
				                                  chr = i + '0'
				                                }
				                                else {
				                                  chr = i - 10 + 'A'
				                                }
				                                print(chr);
				                              }

				                              void printHex8(int i) {
				                                printHex4(i >> 4)
				                                printHex4(i);
				                              }

				                              void printHex16(int i) {
				                                printHex8(i >> 8);
				                                printHex8(i);
				                              }

				                              void main() {
				                                int input = getInput()
				                                printHex16(input);
				                              }""");
	}

	@Test
	public void testRemoved() {
		assertSuccessfullyTransformed("""
				                              void main() {
				                              }
				                              """,
		                              Messages.warningUnusedFunction(1, 4, "twice") + "\n" +
				                              Messages.warningUnusedFunction(2, 4, "zero") + "\n",
		                              """
				                              int twice(int a, int b) return a * 2
				                              int zero() {
				                              var a = 0;
				                              return a;
				                              }
				                              void main() {
				                              }
				                              """);

		assertSuccessfullyTransformed("""
				                              void main() {
				                              }
				                              """,
		                              Messages.warningUnusedFunction(1, 4, "twice") + "\n" +
				                              Messages.warningUnusedFunction(2, 4, "zero") + "\n",
		                              """
				                              int twice(int a, int b) return a * 2
				                              int zero() {
				                                var b = 1000;
				                                {
				                                  var a = 1;
				                                  b = twice(a, a);
				                                }
				                                var a = 0;
				                                return a;
				                              }
				                              void main() {
				                              }
				                              """);
	}

	@Test
	public void testMain() {
		assertTransformationFailedException(Messages.errorMissingMain(), NO_WARNING, "");
		assertTransformationFailedException(Messages.errorMissingMain(), NO_WARNING, """
				void foo() {
				}""");
		assertTransformationFailedException(Messages.errorMainHasWrongSignature(1, 4), NO_WARNING, """
				int main() {
				}""");
		assertTransformationFailedException(Messages.errorMainHasWrongSignature(1, 5), NO_WARNING, """
				void main(int a) {
				}""");
	}

	// Utils ==================================================================

	private void assertSuccessfullyTransformed(String expectedCode, String expectedWarnings, String code) {
		final DeclarationList root = AstFactory.parseString(code);
		final StringOutput out = new StringStringOutput();
		final DeclarationList newRoot = RemoveUnusedFunctionsTransformation.transform(root, new BuiltInFunctions(), out);
		assertEquals(expectedCode, CodePrinter.print(newRoot));
		assertEquals(expectedWarnings, out.toString());
	}

	private void assertTransformationFailedException(String expectedExceptionMsg, String expectedWarnings, String code) {
		final StringOutput out = new StringStringOutput();
		try {
			RemoveUnusedFunctionsTransformation.transform(AstFactory.parseString(code), new BuiltInFunctions(), out);
			fail();
		}
		catch (TransformationFailedException ex) {
			assertEquals(expectedExceptionMsg, ex.getMessage());
		}
		assertEquals(expectedWarnings, out.toString());
	}
}
