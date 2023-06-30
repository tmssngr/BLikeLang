package de.regnis.bril;

/**
 * When calling a subroutine, we provide the first parameter in rr0 and push the other parameters to the stack.
 * Same could be applied for multiple return values: the first goes to the rr0 register, others on the stack
 * reusing the same locations as the parameter; if more output than input parameters exist, appropriate space
 * needs to be reserved before calling using pushes.
 * Example with just one return value
 *   ld rr0, #1234      // first parameter
 *   push rr4           // second parameter
 *   push rr2           // third parameter
 *   call foo           // returns result in rr0
 *   pop rr2            // clean up stack globbering rr2
 *   pop rr2            // -"-
 * The subroutine receives the parameters on the stack. The stack pointer (SP, register pair FE+FF on Z8) points
 * to the previously pushed byte.
 *
 * PCH PCL  High(3rd parameter) Low(3rd parameter)  High(2nd parameter) Low(2nd parameter)
 *  ^
 *   \_ top of stack
 *
 * For local variables in the subroutine, we might reserve space for spilled local variables at the beginning (prolog) and clear it up at the end (epilog):
 *   foo:
 *     push rr0 (the used register is irrelevant, it is just for modifying the stack pointer)
 *     push rr0
 *     push rr4 (save preserved local registers)
 *     push rr6
 *     push rr8
 *     ...      (body)
 *     pop rr8 (prestore preserved local registers)
 *     pop rr6
 *     pop rr4
 *     pop  rr0 (cleanup local variable space)
 *     pop  rr0
 *     ret
 * In the body of the subroutine the stack might look like:
 *
 * High(localVar1) Low(localVar1)  High(localVar2) Low(localVar2)  PCH PCL  High(3rd parameter) Low(3rd parameter)  High(2nd parameter) Low(2nd parameter)
 *  ^
 *   \_ top of stack
 *
 * To access parameters or spilled local variables we use rr14 as address pointer
 *   ldw  rr14, SP
 *   addw rr14, #2
 *   lde  r0, rr14    (read localVar2 into rr0)
 *   incw rr14
 *   lde  r1, rr14
 *
 * @author Thomas Singer
 */
final class BrilAsmCallParameterManagement {
}
