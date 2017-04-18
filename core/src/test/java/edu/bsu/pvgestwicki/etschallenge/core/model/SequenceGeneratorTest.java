package edu.bsu.pvgestwicki.etschallenge.core.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SequenceGeneratorTest {

	private SequenceGenerator gen = SequenceGenerator.create();

	@Test
	public void testInitialSequenceIsEmpty() {
		assertEquals("", gen.generate());
	}

	@Test
	public void testNonrepeatingSequence() {
		String actual = gen.sequence("123").generate();
		assertEquals("123", actual);
	}

	@Test
	public void testTimesTwoSequence() {
		String actual = gen.sequence("123").times(2).generate();
		assertEquals("123123", actual);
	}

	@Test
	public void testMixedSequence() {
		String actual = gen.sequence("12").times(2).sequence("3").sequence("45").times(3).generate();
		assertEquals("12123454545", actual);
	}
}
