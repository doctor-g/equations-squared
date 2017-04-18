package edu.bsu.pvgestwicki.etschallenge.core.math;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SymbolTokenTest {

	@Test
	public void testTimesHigherPriorityThenPlus() {
		assertTrue(SymbolToken.TIMES.isHigherPriorityThan(SymbolToken.PLUS));
	}
	
	@Test
	public void testPlusNotHigherPriorityThanTimes() {
		assertFalse(SymbolToken.PLUS.isHigherPriorityThan(SymbolToken.TIMES));
	}
}
