package edu.bsu.pvgestwicki.etschallenge.core.math;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class OperationTest {

	@Test
	public void testAddIsNotHigherPriorityThanSubtract() {
		assertFalse(Operation.ADD.isHigherPriorityThan(Operation.SUBTRACT));
	}
	
	@Test
	public void testSubtractIsNotHigherPriorityThanAdd() {
		assertFalse(Operation.SUBTRACT.isHigherPriorityThan(Operation.ADD));
	}
	
}
