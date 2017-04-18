package edu.bsu.pvgestwicki.etschallenge.core.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.bsu.pvgestwicki.etschallenge.core.math.Equation;

public class AtMostOneExpressionHasOneTermPredicateTest extends EquationParsingTest {

	private AtMostOneExpressionHasOneTermPredicate predicate = AtMostOneExpressionHasOneTermPredicate.instance();
	
	@Test
	public void testAtMostOneExpressionHasOneTerm() {
		Equation eq = parse("1+1=2");
		assertTrue(predicate.apply(eq));
	}
	
	@Test
	public void testAtMostOneExpressionHasOneTermWhenUntrue() {
		Equation eq = parse("1=1");
		assertFalse(predicate.apply(eq));
	}
	
	@Test
	public void testAtMostOneExpressionHasOneTermOnMultipleEqualsExpression() {
		Equation eq = parse("1+1=0+2=2");
		assertTrue(predicate.apply(eq));
	}
	
	@Test
	public void testAtMostOneExpressionHasOneTermOnMultipleEqualsExpressionWhenFalse() {
		Equation eq = parse("1+1=2=2");
		assertFalse(predicate.apply(eq));
	}
	
	@Test
	public void testAtMostOneExpressionHasOneTermWhenNoExpressionHasOneTerm() {
		Equation eq = parse("1+1=2+0");
		assertTrue(predicate.apply(eq));
	}
}
