package edu.bsu.pvgestwicki.etschallenge.core.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.bsu.pvgestwicki.etschallenge.core.math.Factor;
import edu.bsu.pvgestwicki.etschallenge.core.math.FactorBuilder;
import edu.bsu.pvgestwicki.etschallenge.core.math.Term;
import edu.bsu.pvgestwicki.etschallenge.core.math.TermBuilder;

public class TermBuilderTest extends EnvironmentTest {

	private static final int TEN = 10;

	@Test
	public void testBuildAndEvaluateFactorTerm() {
		Factor f = FactorBuilder.of(TEN).build();
		Term t = TermBuilder.of(f).build();
		assertEquals(TEN, t.value(env));
	}
}
