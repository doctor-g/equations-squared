package edu.bsu.pvgestwicki.etschallenge.core.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import edu.bsu.pvgestwicki.etschallenge.core.math.Expression;
import edu.bsu.pvgestwicki.etschallenge.core.math.ExpressionParser;
import edu.bsu.pvgestwicki.etschallenge.core.math.IntegerToken;
import edu.bsu.pvgestwicki.etschallenge.core.math.SymbolToken;
import edu.bsu.pvgestwicki.etschallenge.core.math.Token;

public class HasOperatorPredicateTest {

	private HasOperationPredicate predicate = HasOperationPredicate.instance();

	@Test
	public void testHasOperatorWhenThereIsntOne() {
		Expression e = makeExpression(IntegerToken.create(1));
		assertFalse(predicate.apply(e));
	}

	private Expression makeExpression(IntegerToken intToken) {
		return ExpressionParser.parse(ImmutableList.of((Token) intToken));
	}

	@Test
	public void testHasOperatorWhenThereIsOne() {
		Expression e = makeFivePlusTwoExpression();
		assertTrue(predicate.apply(e));
	}

	private Expression makeFivePlusTwoExpression() {
		List<Token> list = ImmutableList.of(IntegerToken.create(5),
				SymbolToken.PLUS, IntegerToken.create(2));
		return ExpressionParser.parse(list);
	}

}
