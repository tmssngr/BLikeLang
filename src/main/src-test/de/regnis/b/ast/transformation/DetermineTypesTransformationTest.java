package de.regnis.b.ast.transformation;

import de.regnis.b.ast.AstFactory;
import de.regnis.b.Messages;
import de.regnis.b.ast.DeclarationList;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringOutput;
import de.regnis.b.out.StringStringOutput;
import de.regnis.b.type.BasicTypes;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Thomas Singer
 */
public final class DetermineTypesTransformationTest {

	// Constants ==============================================================

	private static final String NO_WARNING = "";
	private static final String VOID_MAIN = """
			void main() {
			}
			""";

	// Accessing ==============================================================

	@Test
	public void testDeclaredFunctions() {
		assertSuccessfullyTransformed("""
				                              void init() {
				                              }
				                              void main() {
				                                init()
				                              }
				                              """, NO_WARNING, """
				                              void init() {
				                              }
				                              void main() {
				                              init();
				                              }""");

		assertSuccessfullyTransformed("""
				                              void main() {
				                                init()
				                              }
				                              void init() {
				                              }
				                              """, NO_WARNING, """
				                              void main() {
				                                init();
				                              }
				                              void init() {
				                              }""");

		assertTransformationFailedException(Messages.errorUndeclaredFunction(2, 2, "init"), NO_WARNING, """
				void main() {
				  init();
				}""");
		assertTransformationFailedException(Messages.errorUndeclaredFunction(2, 12, "init"), NO_WARNING, """
				void main() {
				  var foo = init();
				}""");
	}

	@Test
	public void testLocalVars() {
		assertSuccessfullyTransformed("""
				                              int add(int p0, int p1) {
				                                v0 := p0 + p1
				                                return v0
				                              }
				                              int getFalse() {
				                                v0 := 0
				                                return v0
				                              }
				                              void main() {
				                                v0 := add(1, 2)
				                                v1 := getFalse()
				                              }
				                              """,
		                              Messages.warningUnusedVar(10, 6, "a") + "\n" +
				                              Messages.warningUnusedVar(11, 6, "b") + "\n",
		                              """
				                              int add(int a, int b) {
				                                var sum = a + b;
				                                return sum;
				                              }
				                              int getFalse() {
				                                int v = false;
				                                return v;
				                              }
				                              void main() {
				                                var a = add(1, 2);
				                                var b = getFalse();
				                              }
				                              """);
	}

	@Test
	public void testReturn() {
		assertSuccessfullyTransformed("""
				                              int isEqual(int p0, int p1) {
				                                return p0 == p1
				                              }
				                              void main() {
				                                isEqual(1, 2)
				                              }
				                              """,
		                              Messages.warningIgnoredReturnValue(5, 2, "isEqual", BasicTypes.INT16) + "\n",
		                              """
				                              int isEqual(int a, int b) {
				                                return a == b;
				                              }
				                              void main() {
				                                isEqual(1, 2);
				                              }
				                              """);

		assertSuccessfullyTransformed("""
				                              int isSingleDigit(int p0) {
				                                return p0 <= 10
				                              }
				                              void main() {
				                                isSingleDigit(9)
				                              }
				                              """,
		                              Messages.warningIgnoredReturnValue(5, 2, "isSingleDigit", BasicTypes.INT16) + "\n",
		                              """
				                              int isSingleDigit(int a) {
				                                return a <= 10;
				                              }
				                              void main() {
				                                isSingleDigit(9);
				                              }
				                              """);

		assertSuccessfullyTransformed("""
				                              int print(int p0) {
				                                return 0
				                              }
				                              void anotherPrint(int p0) {
				                                v0 := print(p0)
				                                return
				                              }
				                              void main() {
				                                anotherPrint(1)
				                              }
				                              """,
		                              Messages.warningUnusedParameter(1, 14, "a") + "\n" +
				                              Messages.warningUnusedVar(5, 6, "ignored") + "\n",
		                              """
				                              int print(int a) {
				                                return 0;
				                              }
				                              void anotherPrint(int a) {
				                                var ignored = print(a);
				                                return;
				                              }
				                              void main() {
				                                anotherPrint(1);
				                              }
				                              """);

		assertSuccessfullyTransformed("""
				                              int max(int p0, int p1) {
				                                if p0 > p1
				                                {
				                                  return p0
				                                }
				                                else
				                                {
				                                  return p1
				                                }
				                              }
				                              void main() {
				                                max(1, 2)
				                              }
				                              """,
		                              Messages.warningIgnoredReturnValue(9, 2, "max", BasicTypes.INT16) + "\n",
		                              """
				                              int max(int a, int b) {
				                                if (a > b) {
				                                  return a;
				                                } else {
				                                  return b;
				                                }
				                              }
				                              void main() {
				                                max(1, 2);
				                              }
				                              """);

		assertSuccessfullyTransformed("""
				                              void print() {
				                              }
				                              int max() {
				                                print()
				                                return 0
				                              }
				                              void main() {
				                                v0 := max()
				                              }
				                              """,
		                              Messages.warningStatementAfterReturn() + "\n" +
				                              Messages.warningStatementAfterReturn() + "\n" +
				                              Messages.warningUnusedVar(9, 6, "a") + "\n" +
				                              Messages.warningUnusedVar(12, 6, "a") + "\n",
		                              """
				                              void print() {
				                              }
				                              int max() {
				                                {
				                                  print();
				                                  return 0;
				                                  print();
				                                }
				                                var a = 0;
				                              }
				                              void main() {
				                                var a = max();
				                              }
				                              """);

		assertSuccessfullyTransformed("""
				                              int max() {
				                                return 0
				                              }
				                              void main() {
				                                v0 := max()
				                              }
				                              """,
		                              Messages.warningStatementAfterReturn() + "\n" +
				                              Messages.warningUnusedVar(3, 6, "a") + "\n" +
				                              Messages.warningUnusedVar(6, 6, "b") + "\n",
		                              """
				                              int max() {
				                                return 0;
				                                var a = 0;
				                              }
				                              void main() {
				                                var b = max();
				                              }
				                              """);

		assertTransformationFailedException(Messages.errorMissingReturnStatement("test"), NO_WARNING, """
				int test() {
				}
				""" + VOID_MAIN);
	}

	@Test
	public void testCall() {
		assertSuccessfullyTransformed("""
				                              int one() {
				                                return 1
				                              }
				                              int zero() {
				                                one()
				                                return 0
				                              }
				                              """ + VOID_MAIN,
		                              Messages.warningIgnoredReturnValue(3, 2, "one", BasicTypes.INT16) + "\n",
		                              """
				                              int one() return 1
				                              int zero() {
				                                one();
				                                return 0;
				                              }
				                              """ + VOID_MAIN);
	}

	@Test
	public void testConstants() {
		assertSuccessfullyTransformed("""
				                              void printChar(int p0) {
				                              }
				                              void main() {
				                                printChar(13)
				                              }
				                              """,
		                              Messages.warningUnusedParameter(2, 19, "c") + "\n",
		                              """
				                              const CR = 0x0D
				                              void printChar(int c) {
				                              }
				                              void main() {
				                                printChar(CR)
				                              }""");
		assertTransformationFailedException(Messages.errorUndeclaredVariable(1, 11, "CR"),
		                                    "",
		                                    """
		    				                              const NL = CR + 1
		    				                              const CR = 0x0D
		    				                              void printChar(int c) {
		    				                              }
		    				                              void main() {
		    				                                printChar(CR)
		    				                              }""");
	}

	@Test
	public void testValidDuplicateDeclarations() {
		assertSuccessfullyTransformed("""
				                              int twice(int p0, int p1) {
				                                return p0 * 2
				                              }
				                              int zero() {
				                                v0 := 0
				                                return v0
				                              }
				                              void main() {
				                              }
				                              """,
		                              Messages.warningUnusedParameter(1, 21, "b") + "\n",
		                              """
				                              int twice(int a, int b) return a * 2
				                              int zero() {
				                              var a = 0;
				                              return a;
				                              }
				                              """ + VOID_MAIN);

		assertSuccessfullyTransformed("""
				                              int twice(int p0, int p1) {
				                                return p0 * 2
				                              }
				                              int zero() {
				                                v0 := 1000
				                                v1 := 1
				                                v0 = twice(v1, v1)
				                                v2 := 0
				                                return v2
				                              }
				                              void main() {
				                              }
				                              """,
		                              Messages.warningUnusedParameter(1, 21, "b") + "\n",
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
				                              """ +
				                              VOID_MAIN);
	}

	@Test
	public void testBreak() {
		assertSuccessfullyTransformed("""
				                              void main() {
				                                v0 := 0
				                                while 1
				                                {
				                                  v0 = v0 + 1
				                                  if v0 == 10
				                                  {
				                                    break
				                                  }
				                                }
				                              }
				                              """,
		                              Messages.warningStatementAfterBreak() + "\n",
		                              """
				                              void main() {
				                                int i = 0;
				                                while (true) {
				                                  i = i + 1;
				                                  if (i == 10) {
				                                    break;
				                                    i = 0;
				                                  }
				                                }
				                              }""");

		assertTransformationFailedException(Messages.errorBreakStatementNotInWhile(2, 2), NO_WARNING, """
				void main() {
				  break;
				}""");

		assertTransformationFailedException(Messages.errorBreakStatementNotInWhile(3, 4), NO_WARNING, """
				void main() {
				  if (true) {
				    break;
				  }
				}""");

		assertTransformationFailedException(Messages.errorBreakStatementNotInWhile(5, 4), NO_WARNING, """
				void main() {
				  if (false) {
				  }
				  else {
				    break;
				  }
				}""");
	}

	@Test
	public void testDuplicateDeclaration() {
		assertTransformationFailedException(Messages.errorVarAlreadyDeclared(3, 6, "a"),
		                                    NO_WARNING,
		                                    """
				                                    void main() {
				                                      var a = 1;
				                                      var a = 2;
				                                    }""");

		assertTransformationFailedException(Messages.errorVarAlreadyDeclaredAsParameter(2, 6, "a"),
		                                    NO_WARNING,
		                                    """
				                                    int func(int a) {
				                                      var a = 0;
				                                    }
				                                    """ + VOID_MAIN);

		assertTransformationFailedException(Messages.errorVarAlreadyDeclaredAsParameter(4, 7, "a"),
		                                    NO_WARNING,
		                                    """
				                                    int func(int a) {
				                                      var b = 0;
				                                      {
				                                    	  var a = 1;
				                                      }
				                                    }
				                                    """ + VOID_MAIN);

		assertTransformationFailedException(Messages.errorParameterAlreadyDeclared(1, 20, "a"),
		                                    NO_WARNING,
		                                    """
				                                    int func(int a, int a) {
				                                    }
				                                    """ + VOID_MAIN);

		assertTransformationFailedException(Messages.errorFunctionAlreadyDeclared(3, 5, "main"),
		                                    NO_WARNING,
		                                    """
				                                    void main() {
				                                    }
				                                    void main() {
				                                    }
				                                    """);
	}

	// Utils ==================================================================

	private void assertSuccessfullyTransformed(String expectedCode, String expectedWarnings, String code) {
		final DeclarationList root = AstFactory.parseString(code);
		final StringOutput out = new StringStringOutput();

		final DeclarationList newRoot = DetermineTypesTransformation.transform(root, out);

		assertEquals(expectedCode, CodePrinter.print(newRoot));
		assertEquals(expectedWarnings, out.toString());
	}

	private void assertTransformationFailedException(String expectedExceptionMsg, String expectedWarnings, String code) {
		final StringOutput out = new StringStringOutput();
		try {
			DetermineTypesTransformation.transform(AstFactory.parseString(code), out);
			fail();
		}
		catch (TransformationFailedException ex) {
			assertEquals(expectedExceptionMsg, ex.getMessage());
		}
		assertEquals(expectedWarnings, out.toString());
	}
}
