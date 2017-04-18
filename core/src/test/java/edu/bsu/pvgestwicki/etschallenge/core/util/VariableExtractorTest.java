package edu.bsu.pvgestwicki.etschallenge.core.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.bsu.pvgestwicki.etschallenge.core.math.Equation;
import edu.bsu.pvgestwicki.etschallenge.core.math.VariableBuilder;

public class VariableExtractorTest extends EquationParsingTest {

	@Test
	public void testCountVariablesWithNone() {
		Equation eq = parse("1=1");
		assertEquals(0, VariableExtractor.from(eq).size());
	}
	
	@Test
	public void testCountVariablesWithOne() {
		Equation eq = parse("A=1");
		assertEquals(1, VariableExtractor.from(eq).size());
	}

	@Test
	public void testGetVariableWhenThereIsOne() {
		Equation eq = parse("A=1");
		assertEquals(VariableBuilder.create("A"), VariableExtractor.from(eq).get(0));
	}
	
	@Test
	public void testGetVariablesWhenThereAreTwo() {
		Equation eq = parse("A+1=B-2");
		assertEquals(2, VariableExtractor.from(eq).size());
		assertEquals(VariableBuilder.create("A"), VariableExtractor.from(eq).get(0));
		assertEquals(VariableBuilder.create("B"), VariableExtractor.from(eq).get(1));
	}
}
