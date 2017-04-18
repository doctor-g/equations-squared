package edu.bsu.pvgestwicki.etschallenge.core.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

public class EquationTest extends EnvironmentTest {

	@Test
	public void testOneEqualsOne() {
		Equation equation = parse("1=1");
		assertTrue(equation.isTrue(env));
	}

	@Test
	public void testOneNotEqualsZero() {
		Equation equation = parse("1=0");
		assertFalse(equation.isTrue(env));
	}

	private static Equation parse(String eq) {
		return new EquationParser().parse(eq);
	}

	@Test
	public void testEqualsOnOneEqualsOne() {
		Equation equation1 = parse("1=1");
		Equation equation2 = parse("1=1");
		assertTrue(equation1.equals(equation2));
	}

	@Test
	public void testEqualityWithBoundVariable() {
		Equation equation = parse("1=x");
		Variable v = VariableBuilder.create("x");
		env.bind(v, 1);
		assertTrue(equation.isTrue(env));
	}
	
	@Test
	public void testEqualityWithBoundVariableAndComplexExpression() {
		Equation equation = parse("12+A-4=30");
		Variable v = VariableBuilder.create("A");
		env.bind(v, 22);
		assertTrue(equation.isTrue(env));
	}
	
	@Test
	public void testEqualityFailsWithBoundVariable() {
		Equation equation = parse("12+A=30");
		Variable v = VariableBuilder.create("A");
		env.bind(v, 0);
		assertFalse(equation.isTrue(env));
	}
	
	@Test
	public void testEqualityWithTwoVariables() {
		Equation equation = parse("A+2=B");
		Variable a = VariableBuilder.create("A");
		Variable b = VariableBuilder.create("B");
		env.bind(a, 0);
		env.bind(b, 2);
		assertTrue(equation.isTrue(env));
	}
	
	@Test(expected=ParseException.class)
	public void testParseFailsOnInvalidExpression() {
		parse("2+=35");
		fail();
	}
	
	@Test
	public void testIntegerValueOfExpression() {
		Equation equation = parse("1+1=2");
		assertEquals(2, equation.value(env));
	}
	
	@Test(expected=UnboundVariableException.class)
	public void testAttemptToGetValueWhenTheresAnUnboundVariableThrowsException() {
		Equation eq = parse("A=1");
		eq.value(env);
	}
	
	@Test
	public void testEqualityOverMultiplication() {
		Equation eq = parse("5*2=10");
		boolean isTrue = eq.isTrue(env);
		assertTrue(isTrue);
	}
	
	@Test
	public void testEqualityOverDivision() {
		Equation eq = parse("10/2=5");
		boolean isTrue = eq.isTrue(env);
		assertTrue(isTrue);
	}
	
	@Test
	public void testMultipleEquals() {
		Equation eq = parse("1=1=1");
		assertTrue(eq.isTrue(env));
	}
	
	@Test
	public void testMultipleEqualsForUnequalCaseOneEqualsTwoEqualsOne() {
		Equation eq = parse("1=2=1");
		assertFalse(eq.isTrue(env));
	}
	
	@Test
	public void testMultipleEqualsForUnequalCaseTwoEqualsOneEqualsOne() {
		Equation eq = parse("2=1=1");
		assertFalse(eq.isTrue(env));
	}
	
	@Test
	public void testMultipleEqualsForUnequalCaseOneEqualsOneEqualsTwo() {
		Equation eq = parse("1=1=2");
		assertFalse(eq.isTrue(env));
	}
	
	@Test
	public void testGetExpressionsSizeIsCorrect() {
		Equation eq = parse("1=1");
		List<Expression> expressions = eq.expressions();
		assertEquals(2, expressions.size());
	}
	
	@Test
	public void testMultiplicationByJuxtapositionWithTwoVariables() {
		Equation eq = parse("AB=10");
		env.bind(VariableBuilder.create("A"), 5);
		env.bind(VariableBuilder.create("B"), 2);
		assertTrue(eq.isTrue(env));
	}
	
	@Test(expected=ParseException.class)
	public void testTrailingEqualsFailsToParse() {
		parse("6=6=");
	}
	
	@Test
	public void testMultiplicationByJuxtapositionWithAVariableSurroundedByIntegers() {
		// This expression comes directly from Ben Dean, and it should be true.
		Equation eq = parse("7-4=5-2Y3+4");
		env.bind(VariableBuilder.create("Y"), 1);
		assertTrue(eq.isTrue(env));
	}
}
