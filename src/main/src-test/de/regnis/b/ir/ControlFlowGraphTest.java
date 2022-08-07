package de.regnis.b.ir;

import de.regnis.b.AssertTypes;
import de.regnis.b.AstFactory;
import de.regnis.b.DetermineTypesTransformation;
import de.regnis.b.SplitExpressionsTransformation;
import de.regnis.b.ast.DeclarationList;
import de.regnis.b.ast.FuncDeclaration;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringStringOutput;
import de.regnis.utils.Utils;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
						  printHex8((u8)(i >> 8));
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
				               $3 : u16 = p0 >> 8
				               $4 : u8 = (u8) $3
				               printHex8($4)
				               $5 : u8 = (u8) p0
				               printHex8($5)
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

		AssertTypes.assertAllExpressionsHaveType(root);

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

	@Test
	public void testAverage() {
		final DeclarationList root = SplitExpressionsTransformation.transform(DetermineTypesTransformation.transform(AstFactory.parseString(
				"""
						int getInput() {
						  return 10;
						}

						void print(int value) {
						}

						void main() {
						  var count = 0;
						  i16 sum = 0;
						  while (true) {
						    var input = getInput();
						    if (input == 0) {
						      break;
						    }
						    
						    count = count + 1;
						    sum = sum + input;
						  }
						  
						  if (count > 0) {
						    print(sum / count);
						  }
						}"""
		), new StringStringOutput()));
		final FuncDeclaration main = Utils.notNull(root.getFunction("main"));
		final ControlFlowGraph graph = new ControlFlowGraph(main);
		graph.compact();

		assertEquals("""
				             start:
				                 // []
				                 v0 : u8 = 0
				                 // [v0]
				                 v1 : i16 = 0
				                 // [v0, v1]
				             while_1:
				                 // [v0, v1]
				                 while true
				             do_1:
				                 // [v0, v1]
				                 v2 : i16 = getInput()
				                 // [v0, v1, v2]
				             if_2:
				                 // [v0, v1, v2]
				                 if v2 == 0
				                 if ! goto after_if_2
				             if_3:
				                 // [v0, v1]
				                 if v0 > 0
				                 if ! goto exit
				             then_3:
				                 // [v0, v1]
				                 $1 : i16 = (i16) v0
				                 // [$1, v1]
				                 $2 : i16 = v1 / $1
				                 // [$2]
				                 print($2)
				                 // []
				                 goto exit

				             after_if_2:
				                 // [v0, v1, v2]
				                 v0 = v0 + 1
				                 // [v0, v1, v2]
				                 v1 = v1 + v2
				                 // [v0, v1]
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

	@Test
	public void testWeekday() {
		/*
		10 PROC PTC[12]
		20 PRINT"WOCHENTAGS-"
		30 PRINT "BERECHNUNG"
		40 INPUT "TAG:"T
		50 INPUT "MONAT:"M
		60 INPUT "JAHR:"J
		70 LET H=J/100, V=J/400
		80 LET Z=J+(J/4)-H+V+1$M7
		90 LET I=J$M4
		100 LET H=J$M100,V=J$M400
		110 IF H=0
		THEN LET I=I+1
		120 IF V=0
		THEN LET I=I-1
		130 LET D=-30,B=1,A=0
		135 IF I>0
		THEN LET A=1
		140 IF M>2
		THEN LET D=D-1-A
		158 IF M>8
		THEN LET B=2
		160 LET D=D+30+(M+B$M2)
		170 LET M=M-1
		180 IF M>0
		THEN GOTO 160
		190 LET C=D+T+Z+A+4$M7
		200 GOTO C+210
		210 PRINT "-SONNTAG-";
		GOTO 230
		211 PRINT "-MONTAG-";
		GOTO 230
		212 PRINT "-DIENSTAG":
		GOTO 230
		213 PRINT "-MITTWOCH-";
		GOTO 230
		214 PRINT "-DONNERSTAG";
		GOTO 230
		215 PRINT "-FREITAG-";
		GOTO 230
		216 PRINT"-SAMSTAG-"
		230 WAIT 800; GOTO 10
		 */
		final DeclarationList root = SplitExpressionsTransformation.transform(DetermineTypesTransformation.transform(AstFactory.parseString(
				"""
						u8 calculate(u8 day, u8 month, u16 year) {
						  var h = year / 100;
						  var v = year / 400;
						  var z = (year + year/4 - h + v + 1) % 7;
						  var i = year % 4;
						  h = year % 100;
						  v = year % 400;
						  if (h == 0) {
						    i = i + 1;
						  }
						  if (v == 0) {
						    i = i - 1;
						  }
						  var d = -30;
						  var b = 1;
						  var a = 0;
						  if (i > 0) {
						    a = 1;
						  }
						  if (month > 2) {
						    d = d - 1 - a;
						  }
						  if (month > 8) {
						    b = 2;
						  }
						  while (true) {
						    d = d + 30 + month + b % 2;
						    month = month - 1;
						    if (month <= 0) {
						      break;
						    }
						  }
						  return (u8) ((d + day + z + a + 4) % 7);
						}

						void print(u8 value) {
						}

						void main() {
						  print(calculate(9, 1, (u16) 2001));
						}"""
		), new StringStringOutput()));

		assertEquals("""
				             u8 calculate(u8 p0, u8 p1, u16 p2) {
				               v0 : u16 = p2 / 100
				               $1 : i16 = (i16) p2
				               v1 : i16 = $1 / 400
				               $2 : u16 = p2 / 4
				               $3 : u16 = p2 + $2
				               $4 : u16 = $3 - v0
				               $5 : i16 = (i16) $4
				               $6 : i16 = $5 + v1
				               $7 : i16 = $6 + 1
				               v2 : i16 = $7 % 7
				               v3 : u16 = p2 % 4
				               v0 = p2 % 100
				               $8 : i16 = (i16) p2
				               v1 = $8 % 400
				               if v0 == 0
				               {
				                 v3 = v3 + 1
				               }
				               if v1 == 0
				               {
				                 v3 = v3 - 1
				               }
				               v4 : i8 = -30
				               v5 : u8 = 1
				               v6 : u8 = 0
				               if v3 > 0
				               {
				                 v6 = 1
				               }
				               if p1 > 2
				               {
				                 $9 : i8 = v4 - 1
				                 $10 : i8 = (i8) v6
				                 v4 = $9 - $10
				               }
				               if p1 > 8
				               {
				                 v5 = 2
				               }
				               while true
				               {
				                 $11 : i8 = v4 + 30
				                 $12 : i8 = (i8) p1
				                 $13 : i8 = $11 + $12
				                 $14 : u8 = v5 % 2
				                 $15 : i8 = (i8) $14
				                 v4 = $13 + $15
				                 p1 = p1 - 1
				                 if p1 <= 0
				                 {
				                   break
				                 }
				               }
				               $16 : i8 = (i8) p0
				               $17 : i8 = v4 + $16
				               $18 : i16 = (i16) $17
				               $19 : i16 = $18 + v2
				               $20 : i16 = (i16) v6
				               $21 : i16 = $19 + $20
				               $22 : i16 = $21 + 4
				               $23 : i16 = $22 % 7
				               return (u8) $23
				             }
				             void print(u8 p0) {
				             }
				             void main() {
				               $24 : u16 = (u16) 2001
				               $25 : u8 = calculate(9, 1, $24)
				               print($25)
				             }
				             """, CodePrinter.print(root));

		final FuncDeclaration function = Utils.notNull(root.getFunction("calculate"));
		final ControlFlowGraph graph = new ControlFlowGraph(function);
		graph.compact();

		assertEquals("""
				             start:
				                 // [p0, p1, p2]
				                 v0 : u16 = p2 / 100
				                 // [p0, p1, p2, v0]
				                 $1 : i16 = (i16) p2
				                 // [$1, p0, p1, p2, v0]
				                 v1 : i16 = $1 / 400
				                 // [p0, p1, p2, v0, v1]
				                 $2 : u16 = p2 / 4
				                 // [$2, p0, p1, p2, v0, v1]
				                 $3 : u16 = p2 + $2
				                 // [$3, p0, p1, p2, v0, v1]
				                 $4 : u16 = $3 - v0
				                 // [$4, p0, p1, p2, v1]
				                 $5 : i16 = (i16) $4
				                 // [$5, p0, p1, p2, v1]
				                 $6 : i16 = $5 + v1
				                 // [$6, p0, p1, p2]
				                 $7 : i16 = $6 + 1
				                 // [$7, p0, p1, p2]
				                 v2 : i16 = $7 % 7
				                 // [p0, p1, p2, v2]
				                 v3 : u16 = p2 % 4
				                 // [p0, p1, p2, v2, v3]
				                 v0 = p2 % 100
				                 // [p0, p1, p2, v0, v2, v3]
				                 $8 : i16 = (i16) p2
				                 // [$8, p0, p1, v0, v2, v3]
				                 v1 = $8 % 400
				                 // [p0, p1, v0, v1, v2, v3]
				             if_1:
				                 // [p0, p1, v0, v1, v2, v3]
				                 if v0 == 0
				                 if ! goto if_2
				             then_1:
				                 // [p0, p1, v1, v2, v3]
				                 v3 = v3 + 1
				                 // [p0, p1, v1, v2, v3]
				             if_2:
				                 // [p0, p1, v1, v2, v3]
				                 if v1 == 0
				                 if ! goto after_if_2
				             then_2:
				                 // [p0, p1, v2, v3]
				                 v3 = v3 - 1
				                 // [p0, p1, v2, v3]
				             after_if_2:
				                 // [p0, p1, v2, v3]
				                 v4 : i8 = -30
				                 // [p0, p1, v2, v3, v4]
				                 v5 : u8 = 1
				                 // [p0, p1, v2, v3, v4, v5]
				                 v6 : u8 = 0
				                 // [p0, p1, v2, v3, v4, v5, v6]
				             if_3:
				                 // [p0, p1, v2, v3, v4, v5, v6]
				                 if v3 > 0
				                 if ! goto if_4
				             then_3:
				                 // [p0, p1, v2, v4, v5]
				                 v6 = 1
				                 // [p0, p1, v2, v4, v5, v6]
				             if_4:
				                 // [p0, p1, v2, v4, v5, v6]
				                 if p1 > 2
				                 if ! goto if_5
				             then_4:
				                 // [p0, p1, v2, v4, v5, v6]
				                 $9 : i8 = v4 - 1
				                 // [$9, p0, p1, v2, v5, v6]
				                 $10 : i8 = (i8) v6
				                 // [$10, $9, p0, p1, v2, v5, v6]
				                 v4 = $9 - $10
				                 // [p0, p1, v2, v4, v5, v6]
				             if_5:
				                 // [p0, p1, v2, v4, v5, v6]
				                 if p1 > 8
				                 if ! goto while_6
				             then_5:
				                 // [p0, p1, v2, v4, v6]
				                 v5 = 2
				                 // [p0, p1, v2, v4, v5, v6]
				             while_6:
				                 // [p0, p1, v2, v4, v5, v6]
				                 while true
				             do_6:
				                 // [p0, p1, v2, v4, v5, v6]
				                 $11 : i8 = v4 + 30
				                 // [$11, p0, p1, v2, v5, v6]
				                 $12 : i8 = (i8) p1
				                 // [$11, $12, p0, p1, v2, v5, v6]
				                 $13 : i8 = $11 + $12
				                 // [$13, p0, p1, v2, v5, v6]
				                 $14 : u8 = v5 % 2
				                 // [$13, $14, p0, p1, v2, v5, v6]
				                 $15 : i8 = (i8) $14
				                 // [$13, $15, p0, p1, v2, v5, v6]
				                 v4 = $13 + $15
				                 // [p0, p1, v2, v4, v5, v6]
				                 p1 = p1 - 1
				                 // [p0, p1, v2, v4, v5, v6]
				             if_7:
				                 // [p0, p1, v2, v4, v5, v6]
				                 if p1 <= 0
				                 if ! goto while_6
				             after_while_6:
				                 // [p0, v2, v4, v6]
				                 $16 : i8 = (i8) p0
				                 // [$16, v2, v4, v6]
				                 $17 : i8 = v4 + $16
				                 // [$17, v2, v6]
				                 $18 : i16 = (i16) $17
				                 // [$18, v2, v6]
				                 $19 : i16 = $18 + v2
				                 // [$19, v6]
				                 $20 : i16 = (i16) v6
				                 // [$19, $20]
				                 $21 : i16 = $19 + $20
				                 // [$21]
				                 $22 : i16 = $21 + 4
				                 // [$22]
				                 $23 : i16 = $22 % 7
				                 // [$23]
				                 result = (u8) $23
				                 // []
				             exit:
				                 return
				             """,
		             ControlFlowGraphVarUsageDetector
				             .detectVarUsage(graph)
				             .createPrinter(new StringStringOutput())
				             .print()
				             .toString());

		final RegisterAllocation registerAllocation = new RegisterAllocation(graph);
		registerAllocation.initializeParameters(function);
		final Map<String, Integer> varToRegister = registerAllocation.run();
		if (false) {
			Utils.print(varToRegister, "expected.put(\"", "\", ", ");\n");
		}

		assertEquals(12, registerAllocation.getMaxRegisterCount());

		final Map<String, Integer> expected = new HashMap<>();
		expected.put("$1", 4);
		expected.put("$10", 6);
		expected.put("$11", 6);
		expected.put("$12", 7);
		expected.put("$13", 6);
		expected.put("$14", 7);
		expected.put("$15", 7);
		expected.put("$16", 0);
		expected.put("$17", 0);
		expected.put("$18", 0);
		expected.put("$19", 0);
		expected.put("$2", 4);
		expected.put("$20", 2);
		expected.put("$21", 0);
		expected.put("$22", 0);
		expected.put("$23", 0);
		expected.put("$3", 4);
		expected.put("$4", 4);
		expected.put("$5", 4);
		expected.put("$6", 4);
		expected.put("$7", 4);
		expected.put("$8", 2);
		expected.put("$9", 7);
		expected.put("p0", 0);
		expected.put("p1", 1);
		expected.put("p2", 2);
		expected.put("v0", 6);
		expected.put("v1", 8);
		expected.put("v2", 4);
		expected.put("v3", 10);
		expected.put("v4", 6);
		expected.put("v5", 3);
		expected.put("v6", 2);
		assertEquals(expected, varToRegister);
	}
}
