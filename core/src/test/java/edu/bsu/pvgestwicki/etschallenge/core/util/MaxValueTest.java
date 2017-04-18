package edu.bsu.pvgestwicki.etschallenge.core.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import edu.bsu.pvgestwicki.etschallenge.core.math.Equation;
import edu.bsu.pvgestwicki.etschallenge.core.math.Expression;
import edu.bsu.pvgestwicki.etschallenge.core.math.ExpressionParser;
import edu.bsu.pvgestwicki.etschallenge.core.math.IntegerToken;
import edu.bsu.pvgestwicki.etschallenge.core.math.SymbolToken;
import edu.bsu.pvgestwicki.etschallenge.core.math.Token;

public class MaxValueTest extends EquationParsingTest {

	private MaxValue maxValue = MaxValue.in(env);
	private Equation eq;
	private Expression expression;

	@Test
	public void testMaxValueInSumEquation() {
		eq = parse("1+2=3");
		assertIsMaxValue(3);
	}

	private void assertIsMaxValue(int i) {
		if (eq != null)
			assertEquals(i, maxValue.of(eq));
		else
			assertEquals(i, maxValue.of(expression));
	}

	@Test
	public void testMaxValueWithMaxOnRight() {
		expression = makeExpression(IntegerToken.create(1), SymbolToken.PLUS,
				IntegerToken.create(2));
		assertIsMaxValue(2);
	}

	private Expression makeExpression(Token... tokens) {
		ImmutableList<Token> tokenList = ImmutableList.copyOf(tokens);
		return ExpressionParser.parse(tokenList);
	}

	@Test
	public void testMaxValueWithMaxOnLeft() {
		expression = makeFivePlusTwoExpression();
		assertIsMaxValue(5);
	}

	private Expression makeFivePlusTwoExpression() {
		return makeExpression(IntegerToken.create(5), SymbolToken.PLUS,
				IntegerToken.create(2));
	}

}
