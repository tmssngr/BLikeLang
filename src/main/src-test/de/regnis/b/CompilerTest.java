package de.regnis.b;

import de.regnis.b.out.StringOutput;
import de.regnis.b.out.StringStringOutput;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Thomas Singer
 */
public class CompilerTest {

	// Accessing ==============================================================

	@Test
	public void test1() {
		test("""
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
		     Messages.warningUnusedParameter(1, 15, "v") + "\n",
		     """
				     print_exit:
				         ret
				              
				     max_if_1:
				         cmp r1.0, r0.0
				         jp gt, max_then_1
				         jp nz, max_after_if_1
				         cmp r1.1, r0.1
				         jp ule, max_after_if_1
				     max_then_1:
				         ld result.1, r1.1
				         ld result.0, r1.0
				         jp max_exit
				     max_after_if_1:
				         ld result.1, r0.1
				         ld result.0, r0.0
				         jp max_exit
				     max_exit:
				         ret
				              
				     main_start:
				         ld cp0.0, #0
				         ld cp1.1, #1
				         ld cp2.0, #0
				         ld cp3.1, #2
				         call max
				         ld r0.0, rv.0
				         ld r0.1, rv.1
				         ld cp0.0, r0.0
				         ld cp1.1, r0.1
				         call print
				         jp main_exit
				     main_exit:
				         ret
				     """);
	}

	@Test
	public void test2() {
		test("""
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
				     }""",
		     Messages.warningUnusedParameter(5, 15, "chr") + "\n",
		     """
				     getInput_start:
				         ld result.1, #192
				         ld result.0, #0
				         jp getInput_exit
				     getInput_exit:
				         ret
				              
				     print_exit:
				         ret
				              
				     printHex4_start:
				         ld r2.1, r2.1
				         ld r2.0, r2.0
				         and r2.1, #15
				         and r2.0, #0
				         jp printHex4_if_1
				     printHex4_if_1:
				         cmp r2.0, #0
				         jp lt, printHex4_then_1
				         jp nz, printHex4_else_1
				         cmp r2.1, #10
				         jp uge, printHex4_else_1
				     printHex4_then_1:
				         ld r0.1, r2.1
				         ld r0.0, r2.0
				         add r0.1, #48
				         adc r0.0, #0
				         jp printHex4_after_if_1
				     printHex4_after_if_1:
				         ld cp0.0, r0.0
				         ld cp1.1, r0.1
				         ld cp2.0, r1.0
				         ld cp3.1, r1.1
				         call phi\s
				         ld r0.0, rv.0
				         ld r0.1, rv.1
				         ld cp0.0, r0.0
				         ld cp1.1, r0.1
				         call print
				         jp printHex4_exit
				     printHex4_else_1:
				         ld r1.1, r2.1
				         ld r1.0, r2.0
				         add r1.1, #55
				         adc r1.0, #0
				         jp printHex4_after_if_1
				     printHex4_exit:
				         ret
				              
				     printHex8_start:
				         ld r1.1, r0.1
				         ld r1.0, r0.0
				         ccf
				         rrc r1.0
				         rrc r1.1
				         ccf
				         rrc r1.0
				         rrc r1.1
				         ccf
				         rrc r1.0
				         rrc r1.1
				         ccf
				         rrc r1.0
				         rrc r1.1
				         ld cp0.0, r1.0
				         ld cp1.1, r1.1
				         call printHex4
				         ld cp0.0, r0.0
				         ld cp1.1, r0.1
				         call printHex4
				         jp printHex8_exit
				     printHex8_exit:
				         ret
				              
				     printHex16_start:
				         ld r1.1, r0.1
				         ld r1.0, r0.0
				         ccf
				         rrc r1.0
				         rrc r1.1
				         ccf
				         rrc r1.0
				         rrc r1.1
				         ccf
				         rrc r1.0
				         rrc r1.1
				         ccf
				         rrc r1.0
				         rrc r1.1
				         ccf
				         rrc r1.0
				         rrc r1.1
				         ccf
				         rrc r1.0
				         rrc r1.1
				         ccf
				         rrc r1.0
				         rrc r1.1
				         ccf
				         rrc r1.0
				         rrc r1.1
				         ld cp0.0, r1.0
				         ld cp1.1, r1.1
				         call printHex8
				         ld cp0.0, r0.0
				         ld cp1.1, r0.1
				         call printHex8
				         jp printHex16_exit
				     printHex16_exit:
				         ret
				              
				     main_start:
				         call getInput
				         ld r0.0, rv.0
				         ld r0.1, rv.1
				         ld cp0.0, r0.0
				         ld cp1.1, r0.1
				         call printHex16
				         jp main_exit
				     main_exit:
				         ret
				     """);
	}

	// Utils ==================================================================

	private void test(String input, String expectedWarnings, String expectedOutput) {
		final StringOutput warnings = new StringStringOutput();
		final Compiler compiler = new Compiler(warnings);
		final StringOutput asmOutput = new StringStringOutput();
		compiler.compile(input, asmOutput);
		Assert.assertEquals(expectedWarnings, warnings.toString());
		Assert.assertEquals(expectedOutput, asmOutput.toString());
	}
}
