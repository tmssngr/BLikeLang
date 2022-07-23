package de.regnis.b;

import de.regnis.b.type.BasicTypes;
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
	public void testGlobalVars() {
		assertSuccessfullyTransformed("""
				                              g0 : u8 = 1
				                              g1 : i8 = -1
				                              g2 : i8 = g0 + g1
				                              g3 : boolean = false
				                              """ + VOID_MAIN,
		                              Messages.warningUnusedVar(3, 4, "b") + "\n" +
				                              Messages.warningUnusedVar(4, 4, "booF") + "\n",
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
				                              void main() {
				                                v0 : i16 = add(1, 2)
				                                v1 : boolean = getFalse()
				                              }
				                              """,
		                              Messages.warningUnusedVar(10, 6, "a") + "\n" +
				                              Messages.warningUnusedVar(11, 6, "b") + "\n",
		                              """
				                              int add(int a, int b) {
				                                var sum = a + b;
				                                return sum;
				                              }
				                              boolean getFalse() {
				                                boolean v = false;
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
				                              boolean isEqual(i16 p0, i16 p1) {
				                                return p0 == p1
				                              }
				                              void main() {
				                                isEqual(1, 2)
				                              }
				                              """,
		                              Messages.warningIgnoredReturnValue(5, 2, "isEqual", BasicTypes.BOOLEAN) + "\n",
		                              """
				                              boolean isEqual(int a, int b) {
				                                return a == b;
				                              }
				                              void main() {
				                                isEqual(1, 2);
				                              }
				                              """);

		assertSuccessfullyTransformed("""
				                              boolean isSingleDigit(u16 p0) {
				                                return p0 <= 10
				                              }
				                              void main() {
				                                isSingleDigit(9)
				                              }
				                              """,
		                              Messages.warningIgnoredReturnValue(5, 2, "isSingleDigit", BasicTypes.BOOLEAN) + "\n",
		                              """
				                              boolean isSingleDigit(u16 a) {
				                                return a <= 10;
				                              }
				                              void main() {
				                                isSingleDigit(9);
				                              }
				                              """);

		assertSuccessfullyTransformed("""
				                              i16 print(i16 p0) {
				                                return 0
				                              }
				                              void anotherPrint(i16 p0) {
				                                v0 : i16 = print(p0)
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
				                              i16 max() {
				                                print()
				                                return 0
				                              }
				                              void main() {
				                                v0 : i16 = max()
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
				                              i16 max() {
				                                return 0
				                              }
				                              void main() {
				                                v0 : i16 = max()
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
				                              i16 one() {
				                                return 1
				                              }
				                              """ + VOID_MAIN,
		                              Messages.warningIgnoredReturnValue(3, 2, "one", BasicTypes.INT16) + "\n" +
				                              Messages.warningUnusedFunction(2, 4, "zero") + "\n",
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
				                              """ + VOID_MAIN,
		                              Messages.warningUnusedParameter(2, 21, "b") + "\n" +
				                              Messages.warningUnusedVar(1, 4, "a") + "\n" +
				                              Messages.warningUnusedFunction(2, 4, "twice") + "\n" +
				                              Messages.warningUnusedFunction(3, 4, "zero") + "\n",
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
				                              """ + VOID_MAIN,
		                              Messages.warningUnusedParameter(2, 21, "b") + "\n" +
				                              Messages.warningUnusedVar(1, 4, "a") + "\n" +
				                              Messages.warningUnusedFunction(3, 4, "zero") + "\n",
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

		assertTransformationFailedException(Messages.errorCantAssignType(1, 3, "a", BasicTypes.INT8, BasicTypes.UINT8), NO_WARNING, "u8 a = -1;\n" + VOID_MAIN);

		assertTransformationFailedException(Messages.errorCantAssignType(2, 5, "a", BasicTypes.INT16, BasicTypes.UINT8), NO_WARNING, """
				int test() {
				  u8 a = 256;
				}
				""" + VOID_MAIN);

		assertTransformationFailedException(Messages.errorCantAssignType(4, 2, "a", BasicTypes.INT16, BasicTypes.UINT8), NO_WARNING, """
				int test() {
				  u8 a = 0;
				  int b = 0;
				  a = a + b;
				  return 0;
				}
				""" + VOID_MAIN);

		assertTransformationFailedException(Messages.errorCantAssignReturnType(2, 9, BasicTypes.UINT8, BasicTypes.INT8), NO_WARNING, """
				i8 test() {
				  return 128;
				}
				""" + VOID_MAIN);

		assertTransformationFailedException(Messages.errorNoReturnExpressionExpectedForVoid(2, 9), NO_WARNING, """
				void test() {
				  return 128;
				}
				""" + VOID_MAIN);

		assertTransformationFailedException(Messages.errorReturnExpressionExpected(2, 2, BasicTypes.INT16), NO_WARNING, """
				int test() {
				  return;
				}
				""" + VOID_MAIN);

		assertTransformationFailedException(Messages.errorFunctionDoesNotReturnAValue(5, 9, "nothing"), Messages.warningUnusedParameter(1, 17, "a") + "\n", """
				void nothing(int a) {
				  return;
				}
				int test() {
				  return nothing(1) + 2;
				}
				""" + VOID_MAIN);

		assertTransformationFailedException(Messages.errorBooleanExpected(2, 5, BasicTypes.UINT8), NO_WARNING, """
				int max(int a, int b) {
				  if (1) {
				    return a;
				  } else {
				    return b;
				  }
				}
				""" + VOID_MAIN);

		assertTransformationFailedException(Messages.errorBooleanExpected(2, 8, BasicTypes.UINT8), NO_WARNING, """
				int max(int a, int b) {
				  while (1) {
				  }
				}
				""" + VOID_MAIN);
	}

	@Test
	public void testBreak() {
		assertSuccessfullyTransformed("""
				                              void main() {
				                                v0 : i16 = 0
				                                while true
				                                {
				                                  v0 = v0 + 1
				                                  if v0 == 10
				                                  {
				                                    break
				                                  }
				                                  else
				                                  {
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
	public void testTypeCast() {
		assertSuccessfullyTransformed("g0 : u8 = (u8) -1\n" + VOID_MAIN,
		                              Messages.warningUnusedVar(1, 4, "a") + "\n",
		                              "var a = (u8)-1;\n" + VOID_MAIN);

		assertSuccessfullyTransformed("g0 : u8 = 0\n" + VOID_MAIN,
		                              Messages.warningUnnecessaryCastTo(1, 8, BasicTypes.UINT8) + "\n" +
				                              Messages.warningUnusedVar(1, 3, "a") + "\n",
		                              "u8 a = (u8)0;\n" + VOID_MAIN);

		assertSuccessfullyTransformed("""
				                              void main() {
				                                v0 : u8 = 10
				                                v1 : u8 = v0
				                              }
				                              """,
		                              Messages.warningUnnecessaryCastTo(3, 11, BasicTypes.UINT8) + "\n" +
				                              Messages.warningUnusedVar(3, 6, "b") + "\n",
		                              """
				                              void main() {
				                                u8  a = 10;
				                                var b = (u8) a;
				                              }
				                              """);

		assertSuccessfullyTransformed("""
				                              void print(i16 p0) {
				                              }
				                              void main() {
				                                v0 : u8 = 10
				                                print((i16) v0)
				                              }
				                              """,
		                              Messages.warningUnusedParameter(1, 27, "value") + "\n",
		                              """
                                              void print(i16 value) {
                                              }
				                              void main() {
				                                u8  a = 10;
				                                print(a);
				                              }
				                              """);

		assertSuccessfullyTransformed("""
				                              void print(i16 p0) {
				                              }
				                              void main() {
				                                v0 : u8 = 10
				                                print((i16) v0)
				                              }
				                              """,
		                              Messages.warningUnusedParameter(1, 27, "value") + "\n",
		                              """
                                              void print(i16 value) {
                                              }
				                              void main() {
				                                u8  a = 10;
				                                print((i8) a);
				                              }
				                              """);

		assertTransformationFailedException("Foo", NO_WARNING, "var a = (Foo)0;\n" + VOID_MAIN);
	}

	@Test
	public void testDuplicateDeclaration() {
		assertTransformationFailedException(Messages.errorVarAlreadyDeclared(2, 4, "a"),
		                                    NO_WARNING,
		                                    """
				                                    var a = 1;
				                                    var a = 2;
				                                    """ + VOID_MAIN);

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

	@Test
	public void memAccess() {
		assertSuccessfullyTransformed("""
				                              void main() {
				                                v0 : u16 = 32768
				                                v1 : u8 = v0[]
				                                v0[] = 0
				                              }
				                              """,
		                              Messages.warningUnusedVar(3, 6, "byte") + "\n",
		                              """
				                              void main() {
				                                var address = 32768;
				                                var byte = address[];
				                                address[] = 0;
				                              }""");

		assertTransformationFailedException(Messages.errorMemAccessNeedsU16(3, 13, "addr", BasicTypes.UINT8),
		                                    NO_WARNING, """
				                                    void main() {
				                                      u8 addr = 0;
				                                      var byte = addr[];
				                                    }""");

		assertTransformationFailedException(Messages.errorMemWriteNeedsU8(3, 2, BasicTypes.INT16),
		                                    NO_WARNING, """
				                                    void main() {
				                                      u16 addr = 32768;
				                                      addr[] = 256;
				                                    }""");
	}

	@Test
	public void testMain() {
		assertSuccessfullyTransformed(VOID_MAIN,
		                              NO_WARNING,
		                              VOID_MAIN);
		assertTransformationFailedException(Messages.errorMissingMain(), NO_WARNING, "");
		assertTransformationFailedException(Messages.errorMissingMain(), NO_WARNING, "var global = 1;");
		assertTransformationFailedException(Messages.errorMissingMain(), NO_WARNING, """
				void foo() {
				}""");
		assertTransformationFailedException(Messages.errorMissingMain(), NO_WARNING, """
				int main() {
				}""");
		assertTransformationFailedException(Messages.errorMissingMain(), NO_WARNING, """
				void main(int a) {
				}""");
	}

	@Test
	public void testGlobalVarsBeforeFunctions() {
		assertSuccessfullyTransformed("""
				                              g0 : u8 = 24
				                              g1 : u8 = 40
				                              g2 : u8 = g0 * g1
				                              void main() {
				                                test()
				                                test2()
				                              }
				                              void test() {
				                              }
				                              void test2() {
				                              }
				                              """,
		                              Messages.warningUnusedVar(11, 4, "charsPerScreen") + "\n",
		                              """
				                              void main() {
				                                test();
				                                test2();
				                              }
				                              var lines = 24;
				                              void test() {
				                              }
				                              var columns = 40;
				                              void test2() {
				                              }
				                              var charsPerScreen = lines * columns;
				                              				                              
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
