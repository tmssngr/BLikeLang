package de.regnis.bril;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Thomas Singer
 */
public class BrilVarsTest {

	// Accessing ==============================================================

	@Test
	public void testIfLeftRight() {
		final BrilVars brilVars = new BrilVars("v.", "r.", "s.", 8);
		brilVars.assignArguments(List.of("r.0", "r.1"));
		brilVars.assignReturnValue(BrilInstructions.VOID);
		brilVars.assignLocalVariables(Set.of("v.0"));
		Assert.assertEquals(0, brilVars.getByteCountForSpilledLocalVars());
		final Map<String, BrilVars.VarLocation> varToLocationMapping = brilVars.getVarToLocationMapping();
		Assert.assertEquals(new BrilVars.VarLocation(true, 0), varToLocationMapping.get("r.0"));
		Assert.assertEquals(new BrilVars.VarLocation(true, 2), varToLocationMapping.get("r.1"));
		Assert.assertEquals(new BrilVars.VarLocation(true, 4), varToLocationMapping.get("v.0"));
	}
}
