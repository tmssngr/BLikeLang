import de.regnis.b.*;
import de.regnis.b.node.DeclarationList;
import de.regnis.b.out.CodePrinter;
import de.regnis.b.out.StringOutput;
import de.regnis.b.out.TreePrinter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Thomas Singer
 */
public final class Main {

	// Static =================================================================

	public static void main(String[] args) throws IOException {
		final Path file = Paths.get("examples/test.b");

		DeclarationList rootAst = AstFactory.parseFile(file);

		rootAst = DetermineTypesTransformation.transform(rootAst, StringOutput.out);

		final TreePrinter printer = new TreePrinter();
		printer.print(rootAst, StringOutput.out);

		rootAst = SplitExpressionsTransformation.transform(rootAst);
		new CodePrinter().print(rootAst, StringOutput.out);

		rootAst = ConstantFoldingTransformation.transform(rootAst);
		new CodePrinter().print(rootAst, StringOutput.out);
	}
}
