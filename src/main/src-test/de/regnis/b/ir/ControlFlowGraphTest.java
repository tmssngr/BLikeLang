package de.regnis.b.ir;

import de.regnis.b.ast.AstFactory;
import de.regnis.b.ast.DeclarationList;
import de.regnis.b.ast.FuncDeclaration;
import de.regnis.b.ast.transformation.DetermineTypesTransformation;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringStringOutput;
import de.regnis.utils.Utils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Thomas Singer
 */
public class ControlFlowGraphTest {

	// Accessing ==============================================================

	@Test
	public void testIf() {
		final DeclarationList root = DetermineTypesTransformation.transform(AstFactory.parseString(
				"""
						void print(int chr) {
						}

						void printHex4(int i) {
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

						void printHex8(int i) {
						  printHex4(i >> 4);
						  printHex4(i);
						}

						void printHex16(int i) {
						  printHex8(i >> 8);
						  printHex8(i);
						}

						void main() {
						  printHex16(192);
						}"""
		), new StringStringOutput());

		final FuncDeclaration printHex4 = Utils.notNull(root.getFunction("printHex4"));
		final ControlFlowGraph graph = new ControlFlowGraph(printHex4);

		assertEquals("""
				             printHex4_start:
				                 p0 = p0 & 15
				                 v0 := 0
				             printHex4_if_1:
				                 if p0 < 10
				                 if ! goto printHex4_else_1
				             printHex4_then_1:
				                 v0 = p0 + 48
				                 goto printHex4_after_if_1

				             printHex4_else_1:
				                 v0 = (p0 - 10) + 65
				             printHex4_after_if_1:
				                 print(v0)
				             printHex4_exit:
				                 return
				             """, ControlFlowGraphPrinter.print(graph, new StringStringOutput()).toString());

		SplitExpressionsTransformation.transform(graph);

		assertEquals("""
				             printHex4_start:
				                 p0 = p0 & 15
				                 v0 := 0
				             printHex4_if_1:
				                 if p0 < 10
				                 if ! goto printHex4_else_1
				             printHex4_then_1:
				                 v0 = p0 + 48
				                 goto printHex4_after_if_1

				             printHex4_else_1:
				                 $1 := p0 - 10
				                 v0 = $1 + 65
				             printHex4_after_if_1:
				                 print(v0)
				             printHex4_exit:
				                 return
				             """, ControlFlowGraphPrinter.print(graph, new StringStringOutput()).toString());

		assertEquals("""
				             printHex4_start:
				                 // [p0]
				                 p0 = p0 & 15
				                 // [p0]
				                 v0 := 0
				                 // [p0]
				             printHex4_if_1:
				                 // [p0]
				                 if p0 < 10
				                 if ! goto printHex4_else_1
				             printHex4_then_1:
				                 // [p0]
				                 v0 = p0 + 48
				                 // [v0]
				                 goto printHex4_after_if_1

				             printHex4_else_1:
				                 // [p0]
				                 $1 := p0 - 10
				                 // [$1]
				                 v0 = $1 + 65
				                 // [v0]
				             printHex4_after_if_1:
				                 // [v0]
				                 print(v0)
				                 // []
				             printHex4_exit:
				                 return
				             """,
		             ControlFlowGraphVarUsageDetector
				             .detectVarUsages(graph)
				             .createPrinter(new StringStringOutput())
				             .print()
				             .toString());
	}

	@Test
	public void testWhile() {
		final DeclarationList root = DetermineTypesTransformation.transform(AstFactory.parseString(
				"""
						int getNumber() {
						  return 10;
						}

						int rnd() {
						  return 5;
						}

						void print(int char) {
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
		), new StringStringOutput());

		assertEquals("""
				             int getNumber() {
				               return 10
				             }
				             int rnd() {
				               return 5
				             }
				             void print(int p0) {
				             }
				             void main() {
				               v0 := rnd()
				               while 1
				               {
				                 v1 := getNumber()
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
		SplitExpressionsTransformation.transform(graph);
		assertEquals("""
				             main_start:
				                 // []
				                 v0 := rnd()
				                 // [v0]
				             main_while_1:
				                 // [v0]
				                 while 1
				             main_do_1:
				                 // [v0]
				                 v1 := getNumber()
				                 // [v0, v1]
				             main_if_2:
				                 // [v0, v1]
				                 if v1 == v0
				                 if ! goto main_else_2
				             main_then_2:
				                 // []
				                 goto main_after_while_1

				             main_else_2:
				                 // [v0, v1]
				             main_after_if_2:
				                 // [v0, v1]
				             main_if_3:
				                 // [v0, v1]
				                 if v1 < v0
				                 if ! goto main_else_3
				             main_then_3:
				                 // [v0]
				                 print(60)
				                 // [v0]
				                 goto main_after_if_3

				             main_else_3:
				                 // [v0]
				                 print(62)
				                 // [v0]
				             main_after_if_3:
				                 // [v0]
				                 goto main_while_1

				             main_after_while_1:
				                 // []
				             main_exit:
				                 return
				             """,
		             ControlFlowGraphVarUsageDetector
				             .detectVarUsages(graph)
				             .createPrinter(new StringStringOutput())
				             .print()
				             .toString());
	}

	@Test
	public void testAverage() {
		final DeclarationList root = DetermineTypesTransformation.transform(AstFactory.parseString(
				"""
						int getInput() {
						  return 10;
						}

						void print(int value) {
						}

						void main() {
						  var count = 0;
						  var sum = 0;
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
		), new StringStringOutput());
		final FuncDeclaration main = Utils.notNull(root.getFunction("main"));
		final ControlFlowGraph graph = new ControlFlowGraph(main);
		SplitExpressionsTransformation.transform(graph);
		assertEquals("""
				             main_start:
				                 // []
				                 v0 := 0
				                 // [v0]
				                 v1 := 0
				                 // [v0, v1]
				             main_while_1:
				                 // [v0, v1]
				                 while 1
				             main_do_1:
				                 // [v0, v1]
				                 v2 := getInput()
				                 // [v0, v1, v2]
				             main_if_2:
				                 // [v0, v1, v2]
				                 if v2 == 0
				                 if ! goto main_else_2
				             main_then_2:
				                 // [v0, v1]
				                 goto main_after_while_1

				             main_else_2:
				                 // [v0, v1, v2]
				             main_after_if_2:
				                 // [v0, v1, v2]
				                 v0 = v0 + 1
				                 // [v0, v1, v2]
				                 v1 = v1 + v2
				                 // [v0, v1]
				                 goto main_while_1

				             main_after_while_1:
				                 // [v0, v1]
				             main_if_3:
				                 // [v0, v1]
				                 if v0 > 0
				                 if ! goto main_else_3
				             main_then_3:
				                 // [v0, v1]
				                 $1 := v1 / v0
				                 // [$1]
				                 print($1)
				                 // []
				                 goto main_after_if_3

				             main_else_3:
				                 // []
				             main_after_if_3:
				                 // []
				             main_exit:
				                 return
				             """,
		             ControlFlowGraphVarUsageDetector
				             .detectVarUsages(graph)
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
		final DeclarationList root = DetermineTypesTransformation.transform(AstFactory.parseString(
				"""
						int calculate(int day, int month, int year) {
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
						  return ((d + day + z + a + 4) % 7);
						}

						void print(int value) {
						}

						void main() {
						  print(calculate(9, 1, 2001));
						}"""
		), new StringStringOutput());

		final FuncDeclaration function = Utils.notNull(root.getFunction("calculate"));
		final ControlFlowGraph graph = new ControlFlowGraph(function);
		SplitExpressionsTransformation.transform(graph);
		assertEquals("""
				             calculate_start:
				                 // [p0, p1, p2]
				                 v0 := p2 / 100
				                 // [p0, p1, p2, v0]
				                 v1 := p2 / 400
				                 // [p0, p1, p2, v0, v1]
				                 $1 := p2 / 4
				                 // [$1, p0, p1, p2, v0, v1]
				                 $2 := p2 + $1
				                 // [$2, p0, p1, p2, v0, v1]
				                 $3 := $2 - v0
				                 // [$3, p0, p1, p2, v1]
				                 $4 := $3 + v1
				                 // [$4, p0, p1, p2]
				                 $5 := $4 + 1
				                 // [$5, p0, p1, p2]
				                 v2 := $5 % 7
				                 // [p0, p1, p2, v2]
				                 v3 := p2 % 4
				                 // [p0, p1, p2, v2, v3]
				                 v0 = p2 % 100
				                 // [p0, p1, p2, v0, v2, v3]
				                 v1 = p2 % 400
				                 // [p0, p1, v0, v1, v2, v3]
				             calculate_if_1:
				                 // [p0, p1, v0, v1, v2, v3]
				                 if v0 == 0
				                 if ! goto calculate_else_1
				             calculate_then_1:
				                 // [p0, p1, v1, v2, v3]
				                 v3 = v3 + 1
				                 // [p0, p1, v1, v2, v3]
				                 goto calculate_after_if_1

				             calculate_else_1:
				                 // [p0, p1, v1, v2, v3]
				             calculate_after_if_1:
				                 // [p0, p1, v1, v2, v3]
				             calculate_if_2:
				                 // [p0, p1, v1, v2, v3]
				                 if v1 == 0
				                 if ! goto calculate_else_2
				             calculate_then_2:
				                 // [p0, p1, v2, v3]
				                 v3 = v3 - 1
				                 // [p0, p1, v2, v3]
				                 goto calculate_after_if_2

				             calculate_else_2:
				                 // [p0, p1, v2, v3]
				             calculate_after_if_2:
				                 // [p0, p1, v2, v3]
				                 v4 := -30
				                 // [p0, p1, v2, v3, v4]
				                 v5 := 1
				                 // [p0, p1, v2, v3, v4, v5]
				                 v6 := 0
				                 // [p0, p1, v2, v3, v4, v5, v6]
				             calculate_if_3:
				                 // [p0, p1, v2, v3, v4, v5, v6]
				                 if v3 > 0
				                 if ! goto calculate_else_3
				             calculate_then_3:
				                 // [p0, p1, v2, v4, v5]
				                 v6 = 1
				                 // [p0, p1, v2, v4, v5, v6]
				                 goto calculate_after_if_3

				             calculate_else_3:
				                 // [p0, p1, v2, v4, v5, v6]
				             calculate_after_if_3:
				                 // [p0, p1, v2, v4, v5, v6]
				             calculate_if_4:
				                 // [p0, p1, v2, v4, v5, v6]
				                 if p1 > 2
				                 if ! goto calculate_else_4
				             calculate_then_4:
				                 // [p0, p1, v2, v4, v5, v6]
				                 $6 := v4 - 1
				                 // [$6, p0, p1, v2, v5, v6]
				                 v4 = $6 - v6
				                 // [p0, p1, v2, v4, v5, v6]
				                 goto calculate_after_if_4

				             calculate_else_4:
				                 // [p0, p1, v2, v4, v5, v6]
				             calculate_after_if_4:
				                 // [p0, p1, v2, v4, v5, v6]
				             calculate_if_5:
				                 // [p0, p1, v2, v4, v5, v6]
				                 if p1 > 8
				                 if ! goto calculate_else_5
				             calculate_then_5:
				                 // [p0, p1, v2, v4, v6]
				                 v5 = 2
				                 // [p0, p1, v2, v4, v5, v6]
				                 goto calculate_after_if_5

				             calculate_else_5:
				                 // [p0, p1, v2, v4, v5, v6]
				             calculate_after_if_5:
				                 // [p0, p1, v2, v4, v5, v6]
				             calculate_while_6:
				                 // [p0, p1, v2, v4, v5, v6]
				                 while 1
				             calculate_do_6:
				                 // [p0, p1, v2, v4, v5, v6]
				                 $7 := v4 + 30
				                 // [$7, p0, p1, v2, v5, v6]
				                 $8 := $7 + p1
				                 // [$8, p0, p1, v2, v5, v6]
				                 $9 := v5 % 2
				                 // [$8, $9, p0, p1, v2, v5, v6]
				                 v4 = $8 + $9
				                 // [p0, p1, v2, v4, v5, v6]
				                 p1 = p1 - 1
				                 // [p0, p1, v2, v4, v5, v6]
				             calculate_if_7:
				                 // [p0, p1, v2, v4, v5, v6]
				                 if p1 <= 0
				                 if ! goto calculate_else_7
				             calculate_then_7:
				                 // [p0, v2, v4, v6]
				                 goto calculate_after_while_6

				             calculate_else_7:
				                 // [p0, p1, v2, v4, v5, v6]
				             calculate_after_if_7:
				                 // [p0, p1, v2, v4, v5, v6]
				                 goto calculate_while_6

				             calculate_after_while_6:
				                 // [p0, v2, v4, v6]
				                 $10 := v4 + p0
				                 // [$10, v2, v6]
				                 $11 := $10 + v2
				                 // [$11, v6]
				                 $12 := $11 + v6
				                 // [$12]
				                 $13 := $12 + 4
				                 // [$13]
				                 result = $13 % 7
				                 // [result]
				             calculate_exit:
				                 return
				             """,
		             ControlFlowGraphVarUsageDetector
				             .detectVarUsages(graph)
				             .createPrinter(new StringStringOutput())
				             .print()
				             .toString());

		final RegisterAllocation.Result varToRegister = RegisterAllocation.run(graph);
		if (false) {
			Utils.print(varToRegister.debugToMap(), "expected.put(\"", "\", ", ");\n");
		}

		assertEquals(3, varToRegister.parameterCount);
		assertEquals(1, varToRegister.returnVarCount);
		assertEquals(4, varToRegister.localVarRegisterCount);

		final Map<String, Integer> expected = new HashMap<>();
		expected.put("p0", 0);
		expected.put("p1", 1);
		expected.put("p2", 2);
		expected.put("result", 0);
		expected.put("v0", 6);
		expected.put("v1", 4);
		expected.put("v2", 3);
		expected.put("v3", 5);
		expected.put("v4", 6);
		expected.put("v5", 4);
		expected.put("v6", 2);
		expected.put("$1", 3);
		expected.put("$10", 0);
		expected.put("$11", 0);
		expected.put("$12", 0);
		expected.put("$13", 0);
		expected.put("$2", 3);
		expected.put("$3", 3);
		expected.put("$4", 3);
		expected.put("$5", 3);
		expected.put("$6", 5);
		expected.put("$7", 5);
		expected.put("$8", 5);
		expected.put("$9", 6);
		assertEquals(expected, varToRegister.debugToMap());
	}
}
