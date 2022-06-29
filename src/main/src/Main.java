import de.regnis.b.AstFactory;
import de.regnis.b.ConstantFoldingTransformation;
import de.regnis.b.ParseFailedException;
import de.regnis.b.SplitExpressionsTransformation;
import de.regnis.b.node.*;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringOutput;
import de.regnis.b.out.TreePrinter;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Thomas Singer
 */
public final class Main {

	// Static =================================================================

	public static void main(String[] args) throws IOException {
		final Path file = Paths.get("examples/test.b");

		DeclarationList rootAst = AstFactory.parseFile(file);

		checkVariables(rootAst);
		final TreePrinter printer = new TreePrinter();
		printer.print(rootAst, StringOutput.out);

		rootAst = SplitExpressionsTransformation.transform(rootAst);
		new CodePrinter().print(rootAst, StringOutput.out);

		rootAst = ConstantFoldingTransformation.transform(rootAst);
		new CodePrinter().print(rootAst, StringOutput.out);
	}

	private static void checkVariables(DeclarationList root) {
		final NodeVisitor<?> visitor = new NodeVisitor<>() {
			private final Set<String> definedVariables = new HashSet<>();

			@Nullable
			@Override
			public Object visitAssignment(Assignment node) {
				super.visitAssignment(node);

				final String var = node.var;
				if (!definedVariables.contains(var)) {
					throw new ParseFailedException("Var " + var + " undeclared", node.line, node.column);
				}
				return null;
			}

			@Nullable
			@Override
			public Object visitLocalVarDeclaration(VarDeclaration node) {
				super.visitLocalVarDeclaration(node);

				final String var = node.var;
				if (definedVariables.contains(var)) {
					throw new ParseFailedException("Var " + var + " already defined", node.line, node.column);
				}
				definedVariables.add(var);
				return null;
			}

			@Nullable
			@Override
			public Object visitVarRead(VarRead node) {
				final String var = node.var;
				if (!definedVariables.contains(var)) {
					throw new ParseFailedException("Var " + var + " undeclared", node.line, node.column);
				}
				return null;
			}
		};
		visitor.visitDeclarationList(root);
	}
}
