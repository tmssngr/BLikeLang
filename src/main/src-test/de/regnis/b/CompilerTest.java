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
				     main:
				         push r4
				         push r5
				         ld r0, #%00
				         ld r1, #%01
				         push r0
				         push r1
				         ld r0, #%00
				         ld r1, #%02
				         push r0
				         push r1
				         call max
				         pop r1
				         pop r0
				         pop r1
				         pop r0
				         ld r4, r0
				         ld r5, r1
				         push r0
				         push r1
				         call print
				         pop r1
				         pop r0
				         pop r5
				         pop r4
				         ret

				     print:
				         ret

				     max:
				         ld r14, %FE
				         ld r15, %FF
				         add r15, #%02
				         adc r14, #%00
				         lde r0, @rr14
				         incw r14
				         lde r1, @rr14
				         ld r14, %FE
				         ld r15, %FF
				         add r15, #%04
				         adc r14, #%00
				         lde r2, @rr14
				         incw r14
				         lde r3, @rr14
				         cp r0, r2
				         .jp gt, max_then_1
				         .jp nz, max_exit
				         cp r1, r3
				         .jp ule, max_exit
				     max_then_1:
				         ld r14, %FE
				         ld r15, %FF
				         add r15, #%02
				         adc r14, #%00
				         lde r0, @rr14
				         incw r14
				         lde r1, @rr14
				         ld r14, %FE
				         ld r15, %FF
				         add r15, #%04
				         adc r14, #%00
				         lde @rr14, r0
				         incw r14
				         lde @rr14, r1
				     max_exit:
				         ret
				     """);
	}

	@Test
	public void test2() {
		test("""
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
		     "",
		     """
				     main:
				         push r4
				         push r5
				         push r6
				         push r7
				         push r8
				         push r9
				         ld r0, #%40
				         ld r1, #%00
				         ld r6, r0
				         ld r7, r1
				         ld r0, #%00
				         ld r1, #%40
				         ld r4, r0
				         ld r5, r1
				     main_while_1:
				         ld r0, r4
				         ld r1, r5
				         cp r0, #%00
				         .jp gt, main_do_1
				         .jp nz, main_exit
				         cp r1, #%00
				         .jp ule, main_exit
				     main_do_1:
				         ld r0, r6
				         ld r1, r7
				         lde r1, @rr0
				         ld r0, #%00
				         ld r8, r0
				         ld r9, r1
				         com r9
				         ld r0, r6
				         ld r1, r7
				         ld r2, r8
				         ld r3, r9
				         lde @rr0, r3
				         incw r6
				         decw r4
				         .jp main_while_1
				     main_exit:
				         pop r9
				         pop r8
				         pop r7
				         pop r6
				         pop r5
				         pop r4
				         ret
				     """);
	}

	@Test
	public void test3() {
		test("""
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
		     "",
		     """
				     main:
				         push r4
				         push r5
				         push r0
				         push r1
				         call getInput
				         pop r1
				         pop r0
				         ld r4, r0
				         ld r5, r1
				         push r0
				         push r1
				         call printHex16
				         pop r1
				         pop r0
				         pop r5
				         pop r4
				         ret

				     getInput:
				         ld r0, #%00
				         ld r1, #%C0
				         ld r14, %FE
				         ld r15, %FF
				         add r15, #%02
				         adc r14, #%00
				         lde @rr14, r0
				         incw r14
				         lde @rr14, r1
				         ret

				     printHex4:
				         ld r14, %FE
				         ld r15, %FF
				         add r15, #%02
				         adc r14, #%00
				         lde r0, @rr14
				         incw r14
				         lde r1, @rr14
				         and r1, #%0F
				         ld r0, #%00
				         ld r14, %FE
				         ld r15, %FF
				         add r15, #%02
				         adc r14, #%00
				         lde @rr14, r0
				         incw r14
				         lde @rr14, r1
				         ld r14, %FE
				         ld r15, %FF
				         add r15, #%02
				         adc r14, #%00
				         lde r0, @rr14
				         incw r14
				         lde r1, @rr14
				         cp r0, #%00
				         .jp lt, printHex4_then_1
				         .jp nz, printHex4_else_1
				         cp r1, #%0A
				         .jp uge, printHex4_else_1
				     printHex4_then_1:
				         ld r14, %FE
				         ld r15, %FF
				         add r15, #%02
				         adc r14, #%00
				         lde r0, @rr14
				         incw r14
				         lde r1, @rr14
				         add r1, #%30
				         adc r0, #%00
				         ld r14, %FE
				         ld r15, %FF
				         add r15, #%02
				         adc r14, #%00
				         lde @rr14, r0
				         incw r14
				         lde @rr14, r1
				         .jp printHex4_after_if_1
				     printHex4_else_1:
				         ld r14, %FE
				         ld r15, %FF
				         add r15, #%02
				         adc r14, #%00
				         lde r0, @rr14
				         incw r14
				         lde r1, @rr14
				         add r1, #%37
				         adc r0, #%00
				         ld r14, %FE
				         ld r15, %FF
				         add r15, #%02
				         adc r14, #%00
				         lde @rr14, r0
				         incw r14
				         lde @rr14, r1
				     printHex4_after_if_1:
				         ld r14, %FE
				         ld r15, %FF
				         add r15, #%02
				         adc r14, #%00
				         lde r0, @rr14
				         incw r14
				         lde r1, @rr14
				         ld %15, r1
				         call %0818
				         ret

				     printHex8:
				         push r4
				         push r5
				         ld r14, %FE
				         ld r15, %FF
				         add r15, #%04
				         adc r14, #%00
				         lde r0, @rr14
				         incw r14
				         lde r1, @rr14
				         ld r4, r0
				         ld r5, r1
				         sra r0
				         rrc r1
				         sra r0
				         rrc r1
				         sra r0
				         rrc r1
				         sra r0
				         rrc r1
				         ld r4, r0
				         ld r5, r1
				         push r0
				         push r1
				         call printHex4
				         pop r1
				         pop r0
				         ld r14, %FE
				         ld r15, %FF
				         add r15, #%04
				         adc r14, #%00
				         lde r0, @rr14
				         incw r14
				         lde r1, @rr14
				         push r0
				         push r1
				         call printHex4
				         pop r1
				         pop r0
				         pop r5
				         pop r4
				         ret

				     printHex16:
				         push r4
				         push r5
				         ld r14, %FE
				         ld r15, %FF
				         add r15, #%04
				         adc r14, #%00
				         lde r0, @rr14
				         incw r14
				         lde r1, @rr14
				         ld r4, r0
				         ld r5, r1
				         ld r1, r0
				         ld r0, #%00
				         ld r4, r0
				         ld r5, r1
				         push r0
				         push r1
				         call printHex8
				         pop r1
				         pop r0
				         ld r14, %FE
				         ld r15, %FF
				         add r15, #%04
				         adc r14, #%00
				         lde r0, @rr14
				         incw r14
				         lde r1, @rr14
				         push r0
				         push r1
				         call printHex8
				         pop r1
				         pop r0
				         pop r5
				         pop r4
				         ret
				     """);
	}

	@Test
	public void testAsciiListing() {
		test("""
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
		     "",
		     """
				     main:
				         push r4
				         push r5
				         push r6
				         push r7
				         ld r0, #%00
				         ld r1, #%20
				         ld r4, r0
				         ld r5, r1
				     main_while_1:
				         ld r0, r4
				         ld r1, r5
				         cp r0, #%00
				         .jp lt, main_do_1
				         .jp nz, main_exit
				         cp r1, #%80
				         .jp uge, main_exit
				     main_do_1:
				         ld r0, r4
				         ld r1, r5
				         ld %15, r1
				         call %0818
				         ld r0, r4
				         ld r1, r5
				         ld r6, r0
				         ld r7, r1
				         incw r6
				         ld r0, r6
				         ld r1, r7
				         ld r4, r0
				         ld r5, r1
				         and r5, #%0F
				         ld r4, #%00
				         ld r0, r4
				         ld r1, r5
				         cp r0, #%00
				         .jp nz, main_after_if_2
				         cp r1, #%00
				         .jp nz, main_after_if_2
				         ld r0, #%00
				         ld r1, #%0D
				         ld %15, r1
				         call %0818
				     main_after_if_2:
				         ld r0, r6
				         ld r1, r7
				         ld r4, r0
				         ld r5, r1
				         .jp main_while_1
				     main_exit:
				         pop r7
				         pop r6
				         pop r5
				         pop r4
				         ret
				     """);
	}

	@Test
	public void testAverage() {
		test("""
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
		     "",
		     """
				     main:
				         push r4
				         push r5
				         push r6
				         push r7
				         push r8
				         push r9
				         ld r0, #%00
				         ld r1, #%00
				         ld r6, r0
				         ld r7, r1
				         ld r0, #%00
				         ld r1, #%00
				         ld r4, r0
				         ld r5, r1
				     main_do_1:
				         ld r0, #%00
				         ld r1, #%57
				         ld %15, r1
				         call %0818
				         ld r0, #%00
				         ld r1, #%65
				         ld %15, r1
				         call %0818
				         ld r0, #%00
				         ld r1, #%72
				         ld %15, r1
				         call %0818
				         ld r0, #%00
				         ld r1, #%74
				         ld %15, r1
				         call %0818
				         ld r0, #%00
				         ld r1, #%3A
				         ld %15, r1
				         call %0818
				         push %FD
				         srp #%10
				         call %02E4
				         pop %FD
				         ld r0, %12
				         ld r1, %13
				         ld r8, r0
				         ld r9, r1
				         cp r0, #%00
				         .jp lt, main_exit
				         incw r6
				         ld r0, r4
				         ld r1, r5
				         ld r2, r8
				         ld r3, r9
				         add r1, r3
				         adc r0, r2
				         ld r4, r0
				         ld r5, r1
				         ld r0, #%00
				         ld r1, #%53
				         ld %15, r1
				         call %0818
				         ld r0, #%00
				         ld r1, #%75
				         ld %15, r1
				         call %0818
				         ld r0, #%00
				         ld r1, #%6D
				         ld %15, r1
				         call %0818
				         ld r0, #%00
				         ld r1, #%6D
				         ld %15, r1
				         call %0818
				         ld r0, #%00
				         ld r1, #%65
				         ld %15, r1
				         call %0818
				         ld r0, #%00
				         ld r1, #%3D
				         ld %15, r1
				         call %0818
				         ld r0, r4
				         ld r1, r5
				         ld %12, r0
				         ld %13, r1
				         push %FD
				         srp #%10
				         call %06e5
				         pop %FD
				         ld r0, #%00
				         ld r1, #%0D
				         ld %15, r1
				         call %0818
				         ld r0, #%00
				         ld r1, #%4D
				         ld %15, r1
				         call %0818
				         ld r0, #%00
				         ld r1, #%69
				         ld %15, r1
				         call %0818
				         ld r0, #%00
				         ld r1, #%74
				         ld %15, r1
				         call %0818
				         ld r0, #%00
				         ld r1, #%74
				         ld %15, r1
				         call %0818
				         ld r0, #%00
				         ld r1, #%65
				         ld %15, r1
				         call %0818
				         ld r0, #%00
				         ld r1, #%6C
				         ld %15, r1
				         call %0818
				         ld r0, #%00
				         ld r1, #%3D
				         ld %15, r1
				         call %0818
				         ld r0, r4
				         ld r1, r5
				         ld r8, r0
				         ld r9, r1
				         ld r2, r6
				         ld r3, r7
				         ld %12, r0
				         ld %13, r1
				         ld %14, r2
				         ld %15, r3
				         push %FD
				         srp #%10
				         call %00E0
				         pop %FD
				         ld r0, %12
				         ld r1, %13
				         ld r8, r0
				         ld r9, r1
				         ld %12, r0
				         ld %13, r1
				         push %FD
				         srp #%10
				         call %06e5
				         pop %FD
				         ld r0, #%00
				         ld r1, #%0D
				         ld %15, r1
				         call %0818
				         .jp main_do_1
				     main_exit:
				         pop r9
				         pop r8
				         pop r7
				         pop r6
				         pop r5
				         pop r4
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
