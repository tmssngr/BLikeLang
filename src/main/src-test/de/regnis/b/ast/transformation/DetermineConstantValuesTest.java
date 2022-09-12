package de.regnis.b.ast.transformation;

import de.regnis.b.Messages;
import de.regnis.b.ast.AstFactory;
import de.regnis.b.ast.DeclarationList;
import de.regnis.b.ast.NumberLiteral;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Thomas Singer
 */
public class DetermineConstantValuesTest {

	// Accessing ==============================================================

	@Test
	public void testSuccess() {
		final DeclarationList root = AstFactory.parseString("""
				                                                    const CR = 0x0D
				                                                    void printChar(int c) {
				                                                    }
				                                                    void main() {
				                                                      printChar(CR)
				                                                    }""");
		final Map<String, NumberLiteral> constants = DetermineConstantValues.determineConstants(root);
		assertEquals(Map.of("CR", new NumberLiteral(13)), constants);
	}

	@Test
	public void testFailure() {
		final DeclarationList root = AstFactory.parseString("""
				                                                    const NL = CR + 1
				                                                    const CR = 0x0D
				                                                    void printChar(int c) {
				                                                    }
				                                                    void main() {
				                                                      printChar(CR)
				                                                    }""");
		try {
			DetermineConstantValues.determineConstants(root);
			fail();
		}
		catch (TransformationFailedException ex) {
			assertEquals(Messages.errorUndeclaredVariable(1, 11, "CR"), ex.getMessage());
		}
	}
}
