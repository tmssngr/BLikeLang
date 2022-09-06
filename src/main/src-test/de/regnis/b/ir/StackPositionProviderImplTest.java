package de.regnis.b.ir;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Thomas Singer
 */
public class StackPositionProviderImplTest {

	// Accessing ==============================================================

	@Test
	public void testStack2Param1Local() {
		final RegisterAllocation.Result result = new RegisterAllocation.Result(2, 1, 1, Map.of("p0", 0,
		                                                                                       "p1", 1,
		                                                                                       "lv", 2));
		final StackPositionProvider provider = new StackPositionProviderImpl(result, 0, 0);
		assertEqualsExpectStack(0, "lv", provider);
		// 2 = return method
		assertEqualsExpectStack(4, "p1", provider);
		assertEqualsExpectStack(6, "p0", provider);

		final StackPositionProvider.RegistersToPush registersToPush = provider.getRegistersToPush();
		assertEquals(0, registersToPush.startRegister());
		assertEquals(0, registersToPush.count());
		assertEquals(1, registersToPush.localVarsStoredOnStack());
	}

	@Test
	public void testStack2Param0Local() {
		final RegisterAllocation.Result result = new RegisterAllocation.Result(2, 1, 0, Map.of("p0", 0,
		                                                                                       "p1", 1));
		final StackPositionProvider provider = new StackPositionProviderImpl(result, 0, 0);
		// 0 = return method
		assertEqualsExpectStack(2, "p1", provider);
		assertEqualsExpectStack(4, "p0", provider);

		final StackPositionProvider.RegistersToPush registersToPush = provider.getRegistersToPush();
		assertEquals(0, registersToPush.startRegister());
		assertEquals(0, registersToPush.count());
		assertEquals(0, registersToPush.localVarsStoredOnStack());
	}

	@Test
	public void test1Reg2Param1Local() {
		final RegisterAllocation.Result result = new RegisterAllocation.Result(2, 1, 1, Map.of("p0", 0,
		                                                                                       "p1", 1,
		                                                                                       "lv", 2));
		final StackPositionProvider provider = new StackPositionProviderImpl(result, 1, 4);
		assertEqualsExpectRegister(4, "lv", provider);
		// 2 = return method
		assertEqualsExpectStack(4, "p1", provider);
		assertEqualsExpectStack(6, "p0", provider);

		final StackPositionProvider.RegistersToPush registersToPush = provider.getRegistersToPush();
		assertEquals(4, registersToPush.startRegister());
		assertEquals(1, registersToPush.count());
		assertEquals(0, registersToPush.localVarsStoredOnStack());
	}

	@Test
	public void test1Reg2Param0Local() {
		final RegisterAllocation.Result result = new RegisterAllocation.Result(2, 1, 0, Map.of("p0", 0,
		                                                                                       "p1", 1));
		final StackPositionProvider provider = new StackPositionProviderImpl(result, 1, 4);
		// 0 = return method
		assertEqualsExpectStack(2, "p1", provider);
		assertEqualsExpectStack(4, "p0", provider);

		final StackPositionProvider.RegistersToPush registersToPush = provider.getRegistersToPush();
		assertEquals(4, registersToPush.startRegister());
		assertEquals(0, registersToPush.count());
		assertEquals(0, registersToPush.localVarsStoredOnStack());
	}

	@Test
	public void test2Reg2Param1Local() {
		final RegisterAllocation.Result result = new RegisterAllocation.Result(2, 1, 2, Map.of("p0", 0,
		                                                                                       "p1", 1,
		                                                                                       "lv0", 2,
		                                                                                       "lv1", 3));
		final StackPositionProvider provider = new StackPositionProviderImpl(result, 2, 10);
		assertEqualsExpectRegister(10, "lv0", provider);
		assertEqualsExpectRegister(12, "lv1", provider);
		// 2 = return method
		assertEqualsExpectStack(6, "p1", provider);
		assertEqualsExpectStack(8, "p0", provider);

		final StackPositionProvider.RegistersToPush registersToPush = provider.getRegistersToPush();
		assertEquals(10, registersToPush.startRegister());
		assertEquals(2, registersToPush.count());
		assertEquals(0, registersToPush.localVarsStoredOnStack());
	}

	// Utils ==================================================================

	private void assertEqualsExpectStack(int expectedStack, String varName, StackPositionProvider provider) {
		assertEquals(expectedStack, provider.getStackPosition(varName));
		assertEquals(-1, provider.getRegister(varName));
	}

	private void assertEqualsExpectRegister(int expectedRegister, String varName, StackPositionProvider provider) {
		assertEquals(-1, provider.getStackPosition(varName));
		assertEquals(expectedRegister, provider.getRegister(varName));
	}
}
