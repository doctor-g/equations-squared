package edu.bsu.pvgestwicki.etschallenge.core.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.bsu.pvgestwicki.etschallenge.core.math.Factor;
import edu.bsu.pvgestwicki.etschallenge.core.math.FactorBuilder;
import edu.bsu.pvgestwicki.etschallenge.core.math.Variable;
import edu.bsu.pvgestwicki.etschallenge.core.math.VariableBuilder;

public class FactorBuilderTest extends EnvironmentTest{

	private static final int TEN = 10;

	@Test
	public void testBuildIntegerAndValue() {
		Factor f = FactorBuilder.of(TEN).build();
		assertEquals(TEN, f.value(env));
	}

	@Test
	public void testBuildVariable() {
		Variable v = VariableBuilder.create("a");
		Factor f = FactorBuilder.of(v).build();
		env.bind(v, 5);
		assertEquals(5, f.value(env));
	}
}
