package org.eclipse.vtp.framework.common.support;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TychoTestsTest {

	@Test
	void testAdd() {
		assertEquals(3, new TychoTests().add(1, 2), "Error!!!!");
	}

}
