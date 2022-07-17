package de.regnis.ir;

import de.regnis.b.AstFactory;
import de.regnis.b.DetermineTypesTransformation;
import de.regnis.b.SplitExpressionsTransformation;
import de.regnis.b.ast.DeclarationList;
import de.regnis.b.ast.FuncDeclaration;
import de.regnis.b.ir.*;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringStringOutput;
import de.regnis.utils.Utils;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Thomas Singer
 */
public class ControlFlowGraphTest {

	// Accessing ==============================================================

	@Test
	public void testIf() {
		final DeclarationList root = SplitExpressionsTransformation.transform(DetermineTypesTransformation.transform(AstFactory.parseString(
				"""
						void print(u8 chr) {
						}
												
						void printHex4(u8 i) {
						  i = i & 15;
						  var chr = 0;
						  if (i < 10) {
						    chr = i + 48;
						  }
						  else {
						    chr = i - 10 + 65;
						  }
						  print(chr);
						}
												
						void printHex8(u8 i) {
						  printHex4(i >> 4);
						  printHex4(i);
						}

						void printHex16(u16 i) {
						  printHex8((u8)i >> 8);
						  printHex8((u8)i);
						}
												
						void main() {
						  printHex16(192);
						}"""
		), new StringStringOutput()));

		assertEquals("""
				             void print(u8 p0) {
				             }
				             void printHex4(u8 p0) {
				               p0 = p0 & 15
				               v0 : u8 = 0
				               if p0 < 10
				               {
				                 v0 = p0 + 48
				               }
				               else
				               {
				                 $1 : u8 = p0 - 10
				                 v0 = $1 + 65
				               }
				               print(v0)
				             }
				             void printHex8(u8 p0) {
				               $2 : u8 = p0 >> 4
				               printHex4($2)
				               printHex4(p0)
				             }
				             void printHex16(u16 p0) {
				               printHex8((u8) p0 >> 8)
				               printHex8((u8) p0)
				             }
				             void main() {
				               printHex16(192)
				             }
				             """, CodePrinter.print(root));

		final FuncDeclaration printHex4 = Utils.notNull(root.getFunction("printHex4"));
		final ControlFlowGraph graph = new ControlFlowGraph(printHex4);

		final String expectedOutput = """
				start:
				    p0 = p0 & 15
				    v0 : u8 = 0
				if_1:
				    if p0 < 10
				    if ! goto else_1
				then_1:
				    v0 = p0 + 48
				after_if_1:
				    print(v0)
				    goto exit

				else_1:
				    $1 : u8 = p0 - 10
				    v0 = $1 + 65
				    goto after_if_1

				exit:
				    return
				""";
		assertEquals(expectedOutput, ControlFlowGraphPrinter.print(graph, new StringStringOutput()).toString());
		graph.compact();
		assertEquals(expectedOutput, ControlFlowGraphPrinter.print(graph, new StringStringOutput()).toString());

		final BasicBlock firstBlock = (BasicBlock) graph.getFirstBlock();

		assertEquals("""
				             p0 = p0 & 15
				             v0 : u8 = 0
				             """, firstBlock.print(new StringStringOutput()).toString());
		assertTrue(firstBlock.getPrev().isEmpty());

		final IfBlock ifBlock = (IfBlock) firstBlock.getSingleNext();

		assertEquals(List.of(firstBlock), ifBlock.getPrev());

		final List<AbstractBlock> trueFalseBlocks = ifBlock.getNext();

		assertEquals(2, trueFalseBlocks.size());

		final BasicBlock trueBlock = (BasicBlock) trueFalseBlocks.get(0);
		assertEquals("""
				             v0 = p0 + 48
				             """,
		             trueBlock.print(new StringStringOutput()).toString());
		assertEquals(List.of(ifBlock), trueBlock.getPrev());

		final List<AbstractBlock> trueNext = trueBlock.getNext();

		assertEquals(1, trueNext.size());

		final BasicBlock postIfBlock = (BasicBlock) trueNext.get(0);

		final BasicBlock falseBlock = (BasicBlock) trueFalseBlocks.get(1);
		assertEquals("""
				             $1 : u8 = p0 - 10
				             v0 = $1 + 65
				             """, falseBlock.print(new StringStringOutput()).toString());
		assertEquals(List.of(ifBlock), falseBlock.getPrev());

		final List<AbstractBlock> falseNext = falseBlock.getNext();

		assertEquals(trueNext, falseNext);
		assertEquals("print(v0)\n", postIfBlock.print(new StringStringOutput()).toString());
		assertEquals(List.of(trueBlock, falseBlock), postIfBlock.getPrev());

		final AbstractBlock exitBlock = postIfBlock.getSingleNext();

		assertEquals(List.of(), exitBlock.getNext());

		assertEquals("""
				             start:
				                 // [p0]
				                 p0 = p0 & 15
				                 // [p0]
				                 v0 : u8 = 0
				                 // [p0]
				             if_1:
				                 // [p0]
				                 if p0 < 10
				                 if ! goto else_1
				             then_1:
				                 // [p0]
				                 v0 = p0 + 48
				                 // [v0]
				             after_if_1:
				                 // [v0]
				                 print(v0)
				                 // []
				                 goto exit

				             else_1:
				                 // [p0]
				                 $1 : u8 = p0 - 10
				                 // [$1]
				                 v0 = $1 + 65
				                 // [v0]
				                 goto after_if_1

				             exit:
				                 return
				             """,
		             ControlFlowGraphVarUsageDetector
				             .detectVarUsage(graph)
				             .createPrinter(new StringStringOutput())
				             .print()
				             .toString());
	}

	@Test
	public void testWhile() {
		final DeclarationList root = SplitExpressionsTransformation.transform(DetermineTypesTransformation.transform(AstFactory.parseString(
				"""
						int getNumber() {
						  return 10;
						}
												
						int rnd() {
						  return 5;
						}
												
						void print(u8 char) {
						}
												
						void main() {
						  var secret = rnd();
						  while (true) {
						    var input = getNumber();
						    if (input == secret) {
						      break;
						    }
						    
						    if (input < secret) {
						      print('<');
						    }
						    else {
						      print('>');
						    }
						  }
						}"""
		), new StringStringOutput()));

		assertEquals("""
				             i16 getNumber() {
				               return 10
				             }
				             i16 rnd() {
				               return 5
				             }
				             void print(u8 p0) {
				             }
				             void main() {
				               v0 : i16 = rnd()
				               while true
				               {
				                 v1 : i16 = getNumber()
				                 if v1 == v0
				                 {
				                   break
				                 }
				                 else
				                 {
				                 }
				                 if v1 < v0
				                 {
				                   print(60)
				                 }
				                 else
				                 {
				                   print(62)
				                 }
				               }
				             }
				             """, CodePrinter.print(root));

		final FuncDeclaration main = Utils.notNull(root.getFunction("main"));
		final ControlFlowGraph graph = new ControlFlowGraph(main);
		assertEquals("""
				             start:
				                 v0 : i16 = rnd()
				             while_1:
				                 while true
				             do_1:
				                 v1 : i16 = getNumber()
				             if_2:
				                 if v1 == v0
				                 if ! goto else_2
				             then_2:
				             after_while_1:
				                 goto exit

				             else_2:
				             after_if_2:
				             if_3:
				                 if v1 < v0
				                 if ! goto else_3
				             then_3:
				                 print(60)
				             after_if_3:
				                 goto while_1

				             else_3:
				                 print(62)
				                 goto after_if_3

				             exit:
				                 return
				             """, ControlFlowGraphPrinter.print(graph, new StringStringOutput()).toString());
		graph.compact();

		assertEquals("""
				             start:
				                 // []
				                 v0 : i16 = rnd()
				                 // [v0]
				             while_1:
				                 // [v0]
				                 while true
				             do_1:
				                 // [v0]
				                 v1 : i16 = getNumber()
				                 // [v0, v1]
				             if_2:
				                 // [v0, v1]
				                 if v1 == v0
				                 if ! goto if_3
				                 goto exit

				             if_3:
				                 // [v0, v1]
				                 if v1 < v0
				                 if ! goto else_3
				             then_3:
				                 // [v0]
				                 print(60)
				                 // [v0]
				                 goto while_1

				             else_3:
				                 // [v0]
				                 print(62)
				                 // [v0]
				                 goto while_1

				             exit:
				                 return
				             """,
		             ControlFlowGraphVarUsageDetector
				             .detectVarUsage(graph)
				             .createPrinter(new StringStringOutput())
				             .print()
				             .toString());
	}
}
