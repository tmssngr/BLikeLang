package de.regnis.ir;

import de.regnis.b.AstFactory;
import de.regnis.b.DetermineTypesTransformation;
import de.regnis.b.SplitExpressionsTransformation;
import de.regnis.b.ast.DeclarationList;
import de.regnis.b.ast.FuncDeclaration;
import de.regnis.b.ir.AbstractBlock;
import de.regnis.b.ir.BasicBlock;
import de.regnis.b.ir.ControlFlowGraphFactory;
import de.regnis.b.ir.IfBlock;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringStringOutput;
import de.regnis.utils.Utils;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Thomas Singer
 */
public class ControlFlowGraphTest {

	@Test
	public void testGraphBuilding() {
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
		final BasicBlock firstBlock = new ControlFlowGraphFactory().createGraph(printHex4);

		assertEquals("""
				             p0 = p0 & 15
				             v0 : u8 = 0
				             """, firstBlock.toString(new StringStringOutput()).toString());
		assertTrue(firstBlock.getPrev().isEmpty());
		assertEquals(Set.of("p0"), firstBlock.getInput());
		assertEquals(Set.of("p0"), firstBlock.getOutput());

		final List<AbstractBlock> next = firstBlock.getNext();

		assertEquals(1, next.size());

		final IfBlock ifBlock = (IfBlock) next.get(0);

		assertEquals(List.of(firstBlock), ifBlock.getPrev());

		final List<AbstractBlock> trueFalseBlocks = ifBlock.getNext();

		assertEquals(2, trueFalseBlocks.size());

		final BasicBlock trueBlock = (BasicBlock) trueFalseBlocks.get(0);
		assertEquals("""
				             v0 = p0 + 48
				             """,
		             trueBlock.toString(new StringStringOutput()).toString());
		assertEquals(List.of(ifBlock), trueBlock.getPrev());
		assertEquals(Set.of("p0"), trueBlock.getInput());
		assertEquals(Set.of("v0"), trueBlock.getOutput());

		final List<AbstractBlock> trueNext = trueBlock.getNext();

		assertEquals(1, trueNext.size());

		final BasicBlock postIfBlock = (BasicBlock) trueNext.get(0);

		final BasicBlock falseBlock = (BasicBlock) trueFalseBlocks.get(1);
		assertEquals("""
				             $1 : u8 = p0 - 10
				             v0 = $1 + 65
				             """, falseBlock.toString(new StringStringOutput()).toString());
		assertEquals(List.of(ifBlock), falseBlock.getPrev());
		assertEquals(Set.of("p0"), falseBlock.getInput());
		assertEquals(Set.of("v0"), falseBlock.getOutput());

		final List<AbstractBlock> falseNext = falseBlock.getNext();

		assertEquals(trueNext, falseNext);
		assertEquals("print(v0)\n", postIfBlock.toString(new StringStringOutput()).toString());
		assertEquals(List.of(trueBlock, falseBlock), postIfBlock.getPrev());
		assertEquals(List.of(), postIfBlock.getNext());
		assertEquals(Set.of("v0"), postIfBlock.getInput());
		assertEquals(Set.of(), postIfBlock.getOutput());
	}
}
