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
		final BrilVars brilVars = new BrilVars();
		brilVars.assign(List.of("a", "b"),
		                Set.of("cond"),
		                Map.of("a", 0,
		                       "b", 1,
		                       "cond", 0)::get);
		Assert.assertEquals(0, brilVars.getByteCountForSpilledLocalVars());
		Assert.assertEquals(new BrilVars.VarLocation(true, BrilVarMapping.ARG0_REGISTER), brilVars.getLocation("a"));
		Assert.assertEquals(new BrilVars.VarLocation(true, BrilVarMapping.ARG1_REGISTER), brilVars.getLocation("b"));
		Assert.assertEquals(new BrilVars.VarLocation(true, BrilVarMapping.ARG0_REGISTER), brilVars.getLocation("cond"));
	}

	@Test
	public void testSimple() {
		final BrilVars brilVars = new BrilVars();
		brilVars.assign(List.of("a", "b"),
		                Set.of("sum"),
		                Map.of("a", 0,
		                       "b", 1,
		                       "sum", 0)::get);
		Assert.assertEquals(0, brilVars.getByteCountForSpilledLocalVars());
		Assert.assertEquals(new BrilVars.VarLocation(true, BrilVarMapping.ARG0_REGISTER), brilVars.getLocation("a"));
		Assert.assertEquals(new BrilVars.VarLocation(true, BrilVarMapping.ARG1_REGISTER), brilVars.getLocation("b"));
		Assert.assertEquals(new BrilVars.VarLocation(true, BrilVarMapping.ARG0_REGISTER), brilVars.getLocation("sum"));
	}
}
