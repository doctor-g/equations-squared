package edu.bsu.pvgestwicki.etschallenge.core.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import edu.bsu.pvgestwicki.etschallenge.core.math.UnboundVariableException;
import edu.bsu.pvgestwicki.etschallenge.core.math.Variable;
import edu.bsu.pvgestwicki.etschallenge.core.math.VariableBuilder;

public class EnvironmentBuilderTest extends EnvironmentTest {

	private Variable variable = VariableBuilder.create("x");

	@Test
	public void testHasNoBinding() {
		assertFalse(env.hasBindingFor(variable));
	}

	@Test
	public void testHasBinding() {
		env.bind(variable, 123);
		assertTrue(env.hasBindingFor(variable));
	}

	@Test
	public void testBoundVariableCanBeRead() {
		env.bind(variable, 123);
		int readValue = env.valueOf(variable);
		assertEquals(123, readValue);
	}
	
	@Test(expected=UnboundVariableException.class)
	public void testAttemptToReadUnboundVariableThrowsException() {
		env.valueOf(variable);
		fail("An exception should have been thrown");
	}

}
