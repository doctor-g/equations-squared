package edu.bsu.pvgestwicki.etschallenge.core.util;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import edu.bsu.pvgestwicki.etschallenge.core.math.Expression;
import edu.bsu.pvgestwicki.etschallenge.core.math.ExpressionParser;
import edu.bsu.pvgestwicki.etschallenge.core.math.IntegerToken;
import edu.bsu.pvgestwicki.etschallenge.core.math.SymbolToken;
import edu.bsu.pvgestwicki.etschallenge.core.math.Token;
import edu.bsu.pvgestwicki.etschallenge.core.math.VariableToken;

public class TermCounterTest {

	private TermCounter counter = new TermCounter();

	@Test
	public void testSingleTermHasOneTerm() {
		parseAndCount(IntegerToken.create(5));
		assertCountIs(1);
	}

	@Test
	public void testTwoTermsHasTwo() {
		parseAndCount(IntegerToken.create(5), SymbolToken.TIMES,
				IntegerToken.create(2));
		assertCountIs(2);
	}

	@Test
	public void testVariableTermIsCounted() {
		parseAndCount(VariableToken.create("Y"), SymbolToken.TIMES,
				IntegerToken.create(9));
		assertCountIs(2);
	}

	@Test
	public void testVariableAndIntegerTermsAreCountedWhenOneIsNegated() {
		parseAndCount(VariableToken.create("Y"), SymbolToken.TIMES,
				SymbolToken.MINUS, IntegerToken.create(9));
		assertCountIs(2);
	}

	private void parseAndCount(Token... tokens) {
		List<Token> tokenList = ImmutableList.copyOf(tokens);
		Expression e = ExpressionParser.parse(tokenList);
		e.accept(counter);
	}

	private void assertCountIs(int n) {
		assertEquals(n, counter.count());
	}
}
