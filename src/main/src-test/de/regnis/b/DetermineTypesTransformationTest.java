package de.regnis.b;

import de.regnis.b.node.BasicTypes;
import de.regnis.b.node.DeclarationList;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringOutput;
import de.regnis.b.out.StringStringOutput;
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

		assertTransformationFailedException(DetermineTypesTransformation.errorUndeclaredFunction(2, 2, "init"), NO_WARNING, """
				                          void main() {
				                            init();
				                          }""");
		assertTransformationFailedException(DetermineTypesTransformation.errorUndeclaredFunction(2, 12, "init"), NO_WARNING, """
				                          void main() {
				                            var foo = init();
				                          }""");
	}

	@Test
	public void testGlobalVars() {
		assertSuccessfullyTransformed("""
				                              g0 : u8 = 1
				                              g1 : i8 = -1
				                              g2 : i8 = g0 + g1
				                              g3 : boolean = false
				                              """ + VOID_MAIN,
		                              DetermineTypesTransformation.warningUnusedVar(3, 4, "b") + "\n" +
				                              DetermineTypesTransformation.warningUnusedVar(4, 4, "booF") + "\n",
		                              """
				                              var a = 1;
				                              var A = -1;
				                              var b=a+A;
				                              var booF = false;
				                              """ + VOID_MAIN);
	}

	@Test
	public void testLocalVars() {
		assertSuccessfullyTransformed("""
				                              i16 add(i16 p0, i16 p1) {
				                                v0 : i16 = p0 + p1
				                                return v0
				                              }
				                              boolean getFalse() {
				                                v0 : boolean = false
				                                return v0
				                              }
				                              """ + VOID_MAIN,
		                              DetermineTypesTransformation.warningUnusedFunction(1, 4, "add") + "\n" +
				                              DetermineTypesTransformation.warningUnusedFunction(5, 8, "getFalse") + "\n",
		                              """
				                              int add(int a, int b) {
				                                var sum = a + b;
				                                return sum;
				                              }
				                              boolean getFalse() {
				                                boolean v = false;
				                                return v;
				                              }
				                              """ + VOID_MAIN);
	}

	@Test
	public void testReturn() {
		assertSuccessfullyTransformed("""
				                              boolean isEqual(i16 p0, i16 p1) {
				                                return p0 == p1
				                              }
				                              """ + VOID_MAIN,
		                              DetermineTypesTransformation.warningUnusedFunction(1, 8, "isEqual") + "\n",
		                              """
				                              boolean isEqual(int a, int b) {
				                                return a == b;
				                              }
				                              """ + VOID_MAIN);

		assertSuccessfullyTransformed("""
				                              boolean isSingleDigit(u16 p0) {
				                                return p0 <= 10
				                              }
				                              """ + VOID_MAIN,
		                              DetermineTypesTransformation.warningUnusedFunction(1, 8, "isSingleDigit") + "\n",
		                              """
				                              boolean isSingleDigit(u16 a) {
				                                return a <= 10;
				                              }
				                              """ + VOID_MAIN);

		assertSuccessfullyTransformed("""
				                              i16 print(i16 p0) {
				                                return 0
				                              }
				                              void anotherPrint(i16 p0) {
				                                v0 : i16 = print(p0)
				                                return
				                              }
				                              """ + VOID_MAIN,
		                              DetermineTypesTransformation.warningUnusedParameter(1, 14, "a") + "\n" +
				                              DetermineTypesTransformation.warningUnusedVar(5, 6, "ignored") + "\n" +
				                              DetermineTypesTransformation.warningUnusedFunction(4, 5, "anotherPrint") + "\n",
		                              """
				                              int print(int a) {
				                                return 0;
				                              }
				                              void anotherPrint(int a) {
				                                var ignored = print(a);
				                                return;
				                              }
				                              """ + VOID_MAIN);

		assertSuccessfullyTransformed("""
				                              i16 max(i16 p0, i16 p1) {
				                                if p0 > p1
				                                {
				                                  return p0
				                                }
				                                else
				                                {
				                                  return p1
				                                }
				                              }
				                              """ + VOID_MAIN,
		                              DetermineTypesTransformation.warningUnusedFunction(1, 4, "max") + "\n",
		                              """
				                              int max(int a, int b) {
				                                if (a > b) {
				                                  return a;
				                                } else {
				                                  return b;
				                                }
				                              }
				                              """ + VOID_MAIN);

		assertSuccessfullyTransformed("""
				                              void print() {
				                              }
				                              i16 max() {
				                                print()
				                                return 0
				                              }
				                              """ + VOID_MAIN,
		                              DetermineTypesTransformation.warningStatementAfterReturn() + "\n" +
				                              DetermineTypesTransformation.warningStatementAfterReturn() + "\n" +
				                              DetermineTypesTransformation.warningUnusedVar(9, 6, "a") + "\n" +
				                              DetermineTypesTransformation.warningUnusedFunction(3, 4, "max") + "\n",
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
				                              """ + VOID_MAIN);

		assertSuccessfullyTransformed("""
				                              i16 max() {
				                                return 0
				                              }
				                              """ + VOID_MAIN,
		                              DetermineTypesTransformation.warningStatementAfterReturn() + "\n" +
				                              DetermineTypesTransformation.warningUnusedVar(3, 6, "a") + "\n" +
				                              DetermineTypesTransformation.warningUnusedFunction(1, 4, "max") + "\n",
		                              """
				                              int max() {
				                                return 0;
				                                var a = 0;
				                              }
				                              """ +
				                              VOID_MAIN);

		assertTransformationFailedException(DetermineTypesTransformation.errorMissingReturnStatement("test"), NO_WARNING, """
				                           int test() {
				                           }
				                           """ + VOID_MAIN);
	}

	@Test
	public void testCall() {
		assertSuccessfullyTransformed("""
				                              i16 one() {
				                                return 1
				                              }
				                              i16 zero() {
				                                one()
				                                return 0
				                              }
				                              """ + VOID_MAIN,
		                              DetermineTypesTransformation.warningIgnoredReturnValue(3, 2, "one", BasicTypes.INT16) + "\n" +
				                              DetermineTypesTransformation.warningUnusedFunction(2, 4, "zero") + "\n",
		                              """
				                              int one() return 1;
				                              int zero() {
				                                one();
				                                return 0;
				                              }
				                              """ + VOID_MAIN);
	}

	@Test
	public void testValidDuplicateDeclarations() {
		assertSuccessfullyTransformed("""
				                              g0 : u8 = 1
				                              i16 twice(i16 p0, i16 p1) {
				                                return p0 * 2
				                              }
				                              i16 zero() {
				                                v0 : u8 = 0
				                                return v0
				                              }
				                              """ + VOID_MAIN,
		                              DetermineTypesTransformation.warningUnusedParameter(2, 21, "b") + "\n" +
				                              DetermineTypesTransformation.warningUnusedVar(1, 4, "a") + "\n" +
				                              DetermineTypesTransformation.warningUnusedFunction(2, 4, "twice") + "\n" +
				                              DetermineTypesTransformation.warningUnusedFunction(3, 4, "zero") + "\n",
		                              """
				                              var a = 1;
				                              int twice(int a, int b) return a * 2;
				                              int zero() {
				                              var a = 0;
				                              return a;
				                              }
				                              """ + VOID_MAIN);

		assertSuccessfullyTransformed("""
				                              g0 : u8 = 1
				                              i16 twice(i16 p0, i16 p1) {
				                                return p0 * 2
				                              }
				                              i16 zero() {
				                                v0 : i16 = 1000
				                                v1 : u8 = 1
				                                v0 = twice(v1, v1)
				                                v2 : u8 = 0
				                                return v2
				                              }
				                              """ + VOID_MAIN,
		                              DetermineTypesTransformation.warningUnusedParameter(2, 21, "b") + "\n" +
				                              DetermineTypesTransformation.warningUnusedVar(1, 4, "a") + "\n" +
				                              DetermineTypesTransformation.warningUnusedFunction(3, 4, "zero") + "\n",
		                              """
				                              var a = 1;
				                              int twice(int a, int b) return a * 2;
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
	public void testInvalidTypes() {
		assertTransformationFailedException("foobar", NO_WARNING, "foobar a = 0;\n" + VOID_MAIN);

		assertTransformationFailedException(DetermineTypesTransformation.errorCantAssignType(1, 3, "a", BasicTypes.INT8, BasicTypes.UINT8), NO_WARNING, "u8 a = -1;\n" + VOID_MAIN);

		assertTransformationFailedException(DetermineTypesTransformation.errorCantAssignType(2, 5, "a", BasicTypes.INT16, BasicTypes.UINT8), NO_WARNING, """
				                           int test() {
				                             u8 a = 256;
				                           }
				                           """ + VOID_MAIN);

		assertTransformationFailedException(DetermineTypesTransformation.errorCantAssignType(4, 2, "a", BasicTypes.INT16, BasicTypes.UINT8), NO_WARNING, """
				                           int test() {
				                             u8 a = 0;
				                             int b = 0;
				                             a = a + b;
				                             return 0;
				                           }
				                           """ + VOID_MAIN);

		assertTransformationFailedException(DetermineTypesTransformation.errorCantAssignReturnType(2, 9, BasicTypes.UINT8, BasicTypes.INT8), NO_WARNING, """
				                           i8 test() {
				                             return 128;
				                           }
				                           """ + VOID_MAIN);

		assertTransformationFailedException(DetermineTypesTransformation.errorNoReturnExpressionExpectedForVoid(2, 9), NO_WARNING, """
				                           void test() {
				                             return 128;
				                           }
				                           """ + VOID_MAIN);

		assertTransformationFailedException(DetermineTypesTransformation.errorReturnExpressionExpected(2, 2, BasicTypes.INT16), NO_WARNING, """
				                           int test() {
				                             return;
				                           }
				                           """ + VOID_MAIN);

		assertTransformationFailedException(DetermineTypesTransformation.errorFunctionDoesNotReturnAValue(5, 9, "nothing"), DetermineTypesTransformation.warningUnusedParameter(1, 17, "a") + "\n", """
				                           void nothing(int a) {
				                             return;
				                           }
				                           int test() {
				                             return nothing(1) + 2;
				                           }
				                           """ + VOID_MAIN);

		assertTransformationFailedException(DetermineTypesTransformation.errorBooleanExpected(2, 5, BasicTypes.UINT8), NO_WARNING, """
				                           int max(int a, int b) {
				                             if (1) {
				                               return a;
				                             } else {
				                               return b;
				                             }
				                           }
				                           """ + VOID_MAIN);

		assertTransformationFailedException(DetermineTypesTransformation.errorBooleanExpected(2, 8, BasicTypes.UINT8), NO_WARNING, """
				                           int max(int a, int b) {
				                             while (1) {
				                             }
				                           }
				                           """ + VOID_MAIN);
	}

	@Test
	public void testTypeCast() {
		assertSuccessfullyTransformed("g0 : u8 = (u8) -1\n" + VOID_MAIN,
		                              DetermineTypesTransformation.warningUnusedVar(1, 4, "a") + "\n",
		                              "var a = (u8)-1;\n" + VOID_MAIN);

		assertSuccessfullyTransformed("g0 : u8 = (u8) 0\n" + VOID_MAIN,
		                              DetermineTypesTransformation.warningUnnecessaryCastTo(1, 8, BasicTypes.UINT8) + "\n" +
				                              DetermineTypesTransformation.warningUnusedVar(1, 3, "a") + "\n",
		                              "u8 a = (u8)0;\n" + VOID_MAIN);

		assertTransformationFailedException("Foo", NO_WARNING, "var a = (Foo)0;\n" + VOID_MAIN);
	}

	@Test
	public void testDuplicateDeclaration() {
		assertTransformationFailedException(DetermineTypesTransformation.errorVarAlreadyDeclared(2, 4, "a"),
		                                    NO_WARNING,
		                                    """
				                              var a = 1;
				                              var a = 2;
				                              """ + VOID_MAIN);

		assertTransformationFailedException(DetermineTypesTransformation.errorVarAlreadyDeclared(3, 6, "a"),
		                                    NO_WARNING,
		                                    """
                                            void main() {
				                              var a = 1;
				                              var a = 2;
				                            }""");

		assertTransformationFailedException(DetermineTypesTransformation.errorVarAlreadyDeclaredAsParameter(2, 6, "a"),
		                                    NO_WARNING,
		                                    """
				                              int func(int a) {
				                                var a = 0;
				                              }
				                              """ + VOID_MAIN);

		assertTransformationFailedException(DetermineTypesTransformation.errorVarAlreadyDeclaredAsParameter(4, 7, "a"),
		                                    NO_WARNING,
		                                    """
				                              int func(int a) {
				                                var b = 0;
				                                {
				                              	  var a = 1;
				                                }
				                              }
				                              """ + VOID_MAIN);

		assertTransformationFailedException(DetermineTypesTransformation.errorParameterAlreadyDeclared(1, 20, "a"),
		                                    NO_WARNING,
		                                    """
				                              int func(int a, int a) {
				                              }
				                              """ + VOID_MAIN);

		assertTransformationFailedException(DetermineTypesTransformation.errorFunctionAlreadyDeclared(3, 5, "main"),
		                                    NO_WARNING,
		                                    """
				                              void main() {
				                              }
				                              void main() {
				                              }
				                              """);
	}

	@Test
	public void testMain() {
		assertSuccessfullyTransformed(VOID_MAIN,
		                              NO_WARNING,
		                              VOID_MAIN);
		assertTransformationFailedException(DetermineTypesTransformation.errorMissingMain(), NO_WARNING, "");
		assertTransformationFailedException(DetermineTypesTransformation.errorMissingMain(), NO_WARNING, "var global = 1;");
		assertTransformationFailedException(DetermineTypesTransformation.errorMissingMain(), NO_WARNING, """
				                           void foo() {
				                           }""");
		assertTransformationFailedException(DetermineTypesTransformation.errorMissingMain(), NO_WARNING, """
				                           int main() {
				                           }""");
		assertTransformationFailedException(DetermineTypesTransformation.errorMissingMain(), NO_WARNING, """
				                           void main(int a) {
				                           }""");
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
