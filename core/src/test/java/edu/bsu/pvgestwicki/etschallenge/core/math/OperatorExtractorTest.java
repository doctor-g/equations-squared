package edu.bsu.pvgestwicki.etschallenge.core.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class OperatorExtractorTest {

	private List<Operation> operations;

	private List<Token> tokens;

	@Test
	public void testExtractNoOperatorsFromSingleIntegerExpression() {
		tokens = ImmutableList.of((Token) IntegerToken.create(5));
		parseAndGetOperatorsList();
		assertTrue(operations.isEmpty());
	}

	private void parseAndGetOperatorsList() {
		Expression e = parseTokens();
		OperationExtractor extractor = new OperationExtractor();
		e.accept(extractor);
		operations = extractor.operations();
	}

	private Expression parseTokens() {
		return ExpressionParser.parse(tokens);
	}

	@Test
	public void testExtractTwoOperatorsFromAnExpression() {
		tokens = ImmutableList.of(IntegerToken.create(5), SymbolToken.PLUS,
				IntegerToken.create(10), SymbolToken.TIMES,
				IntegerToken.create(2));
		parseAndGetOperatorsList();
		assertEquals(2, operations.size());
	}

	@Test
	public void testOperatorExtractionOrder() {
		tokens = ImmutableList.of(IntegerToken.create(5), SymbolToken.PLUS,
				IntegerToken.create(10), SymbolToken.TIMES,
				IntegerToken.create(2));
		parseAndGetOperatorsList();
		assertEquals(Operation.ADD, operations.get(0));
		assertEquals(Operation.MULTIPLY, operations.get(1));
	}

}
