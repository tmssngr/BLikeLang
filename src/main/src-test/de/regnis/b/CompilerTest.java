package de.regnis.b;

import de.regnis.b.ir.ControlFlowGraph;
import de.regnis.b.ir.ControlFlowGraphPrinter;
import de.regnis.b.out.PathStringOutput;
import de.regnis.b.out.StringOutput;
import de.regnis.b.out.StringStringOutput;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Thomas Singer
 */
public class CompilerTest {

	// Accessing ==============================================================

	@Test
	public void test1() throws IOException {
		test("max", """
				     void print(int v) {
				     }
				     int max(int a, int b) {
				       if (a > b) {
				         return a;
				       }
				       return b;
				     }
				     void main() {
				       print(max(1, 2));
				     }""",
		     Messages.warningUnusedParameter(1, 15, "v") + "\n"
		);
	}

	@Test
	public void test2() throws IOException {
		test("fillMem", """
				     void main() {
				       int address = 0x4000
				       int count = 8 * 8
				       while (count > 0) {
				         int value = getMem(address)
				         setMem(address, value ^ 0xFF)
				         address += 1
				         count -= 1
				       }
				     }
				     """,
		     ""
		);
	}

	@Test
	public void test3() throws IOException {
		test("printHex16", """
				     int getInput() {
				       return 192
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
				       printChar(chr);
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
				     }""",
		     ""
		);
	}

	@Test
	public void testAsciiListing() throws IOException {
		test("printAsciiListing", """
				     void main() {
				     	var chr = ' '
				     	while (chr < 0x80) {
				     		printChar(chr)
				     		chr += 1
				     		var tmp = chr & 0x0F
				     		if (tmp == 0) {
				     			printChar(0x0D)
				     		}
				     	}
				     }
				     """,
		     ""
		);
	}

	@Test
	public void testAverage() throws IOException {
		test("average", """
				     void main() {
				         var i = 0
				         var s = 0
				         while (true) {
				             printChar('W')
				             printChar('e')
				             printChar('r')
				             printChar('t')
				             printChar(':')
				             var a = readInt()
				             if a < 0 {
				                 break
				             }
				             i += 1
				             s += a
				             printChar('S')
				             printChar('u')
				             printChar('m')
				             printChar('m')
				             printChar('e')
				             printChar('=')
				             printInt(s)
				             printChar(0x0d)
				             printChar('M')
				             printChar('i')
				             printChar('t')
				             printChar('t')
				             printChar('e')
				             printChar('l')
				             printChar('=')
				             printInt(s / i)
				             printChar(0x0d)
				         }
				     }
				     """,
		     ""
		);
	}

	@Test
	public void testLocalVars() throws IOException {
		test("localVars",
		     """
				     void main() {
				        foo(1, 2)
				     }

				     void foo(int a, int c) {
				        if (a > 0) {
				          var b = 2 + c
				          printInt(b)
				        }
				        else {
				          var b = '-' + c
				          printChar(b)
				        }
				     }
				     """,
		     "");
	}

	// Utils ==================================================================

	private void test(String testName, String input, String expectedWarnings) throws IOException {
		final var path = "src/main/resources-test/" + getClass().getName().replace('.', '/') + '-' + testName;
		final var testOutDir = ".test";
		final var asmSuffix = ".txt";
		final Path testPath = Paths.get(testOutDir, path + asmSuffix);
		Files.createDirectories(testPath.getParent());
		Files.deleteIfExists(testPath);

		final Path expectedPath = Paths.get(path + asmSuffix);

		final StringOutput warnings = new StringStringOutput();
		final Compiler compiler = new Compiler(warnings) {
			@Override
			protected void preCommandFactory(ControlFlowGraph cfg, String methodName) {
				try {
					final String precommandSuffix = ".precommand-" + methodName;
					final var testPath = Paths.get(testOutDir, path + precommandSuffix);
					final var expectedPath = Paths.get(path + precommandSuffix);
					try(PathStringOutput precommandOutput = new PathStringOutput(testPath)) {
						ControlFlowGraphPrinter.print(cfg, precommandOutput);
					}

					assertEquals(expectedPath, testPath);
				}
				catch (IOException e) {
					throw new AssertionError(e);
				}
			}
		};

		try (PathStringOutput asmOutput = new PathStringOutput(testPath)) {
			compiler.compile(input, asmOutput);
		}

		Assert.assertEquals(expectedWarnings, warnings.toString());
		assertEquals(expectedPath, testPath);
	}

	private void assertEquals(Path expected, Path output) throws IOException {
		try (BufferedReader expectedReader = Files.newBufferedReader(expected)) {
			try (BufferedReader outputReader = Files.newBufferedReader(output)) {
				for (int lineNo = 1; ; lineNo++) {
					final var expectedLine = expectedReader.readLine();
					final var outputLine = outputReader.readLine();
					Assert.assertEquals("line " + lineNo + " differs", expectedLine, outputLine);
					if (expectedLine == null) {
						break;
					}
				}
			}
		}
	}
}
