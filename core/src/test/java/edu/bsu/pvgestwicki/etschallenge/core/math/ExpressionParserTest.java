package edu.bsu.pvgestwicki.etschallenge.core.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import edu.bsu.pvgestwicki.etschallenge.core.util.TermCounter;

public class ExpressionParserTest extends EnvironmentTest {

	private List<Token> tokens;
	private final String variableName = "x";

	@Test
	public void testParseSingleIntegerExpression() {
		tokens = makeSingleIntegerToken(123);
		parseAndCheckValueIs(123);
	}

	private List<Token> makeSingleIntegerToken(int value) {
		return ImmutableList.of((Token) IntegerToken.create("" + value));
	}

	private void parseAndCheckValueIs(int value) {
		Expression e = parseTokens();
		assertEquals(value, e.value(env));
	}

	private Expression parseTokens() {
		return ExpressionParser.parse(tokens);
	}

	@Test
	public void testParseAddition() {
		tokens = makeAdditionTokens(123, 4);
		parseAndCheckValueIs(123 + 4);
	}

	private List<Token> makeAdditionTokens(int operand1, int operand2) {
		return ImmutableList.of(IntegerToken.create("" + operand1),//
				SymbolToken.PLUS,//
				IntegerToken.create("" + operand2));
	}

	@Test
	public void testParseSubtraction() {
		tokens = ImmutableList.of(IntegerToken.create("123"),//
				SymbolToken.MINUS,//
				IntegerToken.create("4"));
		parseAndCheckValueIs(123 - 4);
	}

	@Test
	public void testParseABoundVariable() {
		final String variableName = "x";
		createAndBind(variableName, 10);
		tokens = ImmutableList.of((Token) VariableToken.create(variableName));
		parseAndCheckValueIs(10);
	}

	private Variable createAndBind(String variableName, int value) {
		Variable v = VariableBuilder.create(variableName);
		env.bind(v, value);
		return v;
	}

	@Test
	public void testParseMultiplication() {
		tokens = ImmutableList.of(IntegerToken.create("2"), SymbolToken.TIMES,
				IntegerToken.create("4"));
		parseAndCheckValueIs(2 * 4);
	}

	@Test
	public void testParseDivision() {
		tokens = ImmutableList.of(IntegerToken.create("10"),
				SymbolToken.DIVIDED_BY, IntegerToken.create("2"));
		parseAndCheckValueIs(10 / 2);
	}

	@Test
	public void testSingleIntegerExpressionEquals() {
		tokens = ImmutableList.of((Token) IntegerToken.create("5"));
		Expression e1 = parseTokens();
		Expression e2 = parseTokens();
		assertTrue(e1.equals(e2));
	}

	@Test
	public void testOrderOfOperationsWithMultiplicationAtEnd() {
		tokens = ImmutableList.of(IntegerToken.create("1"), SymbolToken.PLUS,
				IntegerToken.create("3"), SymbolToken.TIMES,
				IntegerToken.create("4"));
		parseAndCheckValueIs(1 + 3 * 4);
	}

	@Test
	public void testOrderOfOperationsWithMultiplicationAtStart() {
		tokens = ImmutableList.of(IntegerToken.create("1"), //
				SymbolToken.TIMES,//
				IntegerToken.create("3"),//
				SymbolToken.PLUS, IntegerToken.create("4"));
		parseAndCheckValueIs(1 * 3 + 4);
	}

	@Test
	public void testCountTermsInSingleTermExpression() {
		tokens = makeSingleIntegerToken(10);
		assertParsedExpressionHasTerms(1);
	}

	private void assertParsedExpressionHasTerms(int numberOfTerms) {
		Expression exp = parseTokens();
		TermCounter counter = new TermCounter();
		exp.accept(counter);
		assertEquals(numberOfTerms, counter.count());
	}

	@Test
	public void testCountTermsInTwoTermExpression() {
		tokens = makeAdditionTokens(1, 2);
		assertParsedExpressionHasTerms(2);
	}

	@Test
	public void testCountTermsInThreeTermExpressionWithAVariable() {
		createAndBind(variableName, 10);
		tokens = ImmutableList.of(//
				IntegerToken.create(1),//
				SymbolToken.PLUS,//
				VariableToken.create(variableName),//
				SymbolToken.TIMES,//
				IntegerToken.create(7));
		assertParsedExpressionHasTerms(3);
	}

	@Test
	public void testMultiplicationByJuxtapositionInForm2X() {
		createAndBind(variableName, 10);
		tokens = ImmutableList.of(//
				IntegerToken.create(2),//
				VariableToken.create(variableName));
		parseAndCheckValueIs(2 * 10);
	}

	@Test
	public void testMultiplicationByJuxtapositionInFormX2() {
		createAndBind(variableName, 10);
		tokens = ImmutableList.of(//
				VariableToken.create(variableName), //
				IntegerToken.create(2));
		parseAndCheckValueIs(10 * 2);
	}

	@Test
	public void testMultiplicationByJuxtapositionInForm3X2() {
		createAndBind(variableName, 10);
		tokens = ImmutableList.of(//
				IntegerToken.create(3),//
				VariableToken.create(variableName), //
				IntegerToken.create(2));
		parseAndCheckValueIs(3 * 10 * 2);
	}

	@Test
	public void testMultiplicationByJuxtapositionInLargerExpression() {
		createAndBind(variableName, 10);
		tokens = ImmutableList.of(//
				IntegerToken.create(2),//
				VariableToken.create(variableName),//
				SymbolToken.PLUS,//
				IntegerToken.create(5));
		parseAndCheckValueIs(2 * 10 + 5);
	}

	@Test
	public void testVisitorHitsTheOneElement() {
		tokens = ImmutableList.of((Token) IntegerToken.create(1));
		parseAndCheckElementCountIs(1);
	}

	private void parseAndCheckElementCountIs(int n) {
		Expression e = parseTokens();
		ElementCounter counter = new ElementCounter();
		e.accept(counter);
		assertEquals(n, counter.count);
	}

	@Test
	public void testVisitorHitsThreeElement() {
		tokens = ImmutableList.of(IntegerToken.create(1), SymbolToken.PLUS,
				IntegerToken.create(45));
		parseAndCheckElementCountIs(3);
	}

	@Test
	public void testVisitorHitsNegationAndItsOperand() {
		tokens = ImmutableList.of(SymbolToken.MINUS, IntegerToken.create(1));
		Expression e = parseTokens();
		Expression.Visitor visitor = mock(Expression.Visitor.class);
		e.accept(visitor);
		verify(visitor).visit(UnaryOperation.NEGATE);
		verify(visitor).visit(1);
	}

	@Test
	public void testVisitorHitsNegationAndItsVariable() {
		tokens = ImmutableList.of(SymbolToken.MINUS, VariableToken.create("A"));
		Expression e = parseTokens();
		Expression.Visitor visitor = mock(Expression.Visitor.class);
		e.accept(visitor);
		verify(visitor).visit(UnaryOperation.NEGATE);
		verify(visitor).visit(VariableBuilder.create("A"));
	}

	@Test
	public void testParseNegativeInteger() {
		tokens = ImmutableList.of(SymbolToken.MINUS, IntegerToken.create(5));
		parseAndCheckValueIs(-5);
	}

	@Test
	public void testParseNegativeVariable() {
		tokens = ImmutableList.of(SymbolToken.MINUS, VariableToken.create("A"));
		env.bind(VariableBuilder.create("A"), 5);
		parseAndCheckValueIs(-5);
	}

	@Test
	public void testSumWhereFirstTermIsNegated() {
		tokens = ImmutableList.of(SymbolToken.MINUS, IntegerToken.create(5),
				SymbolToken.PLUS, IntegerToken.create(2));
		parseAndCheckValueIs(-3);
	}

	@Test
	public void testSumWhereSecondTermIsNegated() {
		tokens = ImmutableList.of(IntegerToken.create(5), SymbolToken.PLUS,
				SymbolToken.MINUS, IntegerToken.create(2));
		parseAndCheckValueIs(3);
	}

	@Test
	public void testProductWhereSecondTermIsNegated() {
		tokens = ImmutableList.of(IntegerToken.create(5), SymbolToken.TIMES,
				SymbolToken.MINUS, IntegerToken.create(2));
		parseAndCheckValueIs(-10);
	}

	@Test
	public void testCompoundExpressionWithNegationInMiddleWhereItHasToHaveHigherPriorityThanMultiplication() {
		tokens = ImmutableList.of(IntegerToken.create(5), SymbolToken.PLUS,
				SymbolToken.MINUS, IntegerToken.create(2), SymbolToken.TIMES,
				IntegerToken.create(3));

		parseAndCheckValueIs(-1);
	}

	@Test
	public void testCompoundExpressionWithNegationAtEnd() {
		tokens = ImmutableList.of(IntegerToken.create(5), SymbolToken.PLUS,
				IntegerToken.create(2), SymbolToken.TIMES, SymbolToken.MINUS,
				IntegerToken.create(3));

		parseAndCheckValueIs(-1);
	}

	@Test
	public void testCompoundExpressionWitMultiplicationFirstAndNegationInMiddle() {
		tokens = ImmutableList.of(IntegerToken.create(5), SymbolToken.TIMES,
				SymbolToken.MINUS, IntegerToken.create(2), SymbolToken.PLUS,
				IntegerToken.create(3));

		parseAndCheckValueIs(-7);
	}

	@Test
	public void testUnaryPositivizationInCompoundExpressionWhichShouldWorkIfUnaryDoes() {
		tokens = ImmutableList.of(IntegerToken.create(5), SymbolToken.TIMES,
				SymbolToken.PLUS, IntegerToken.create(2), SymbolToken.PLUS,
				IntegerToken.create(3));

		parseAndCheckValueIs(13);
	}

	@Test
	public void testAssociativityOfMinus() {
		tokens = ImmutableList.of(IntegerToken.create(1), SymbolToken.MINUS,
				IntegerToken.create(1), SymbolToken.MINUS,
				IntegerToken.create(1));
		parseAndCheckValueIs(1-1-1);
	}

	@Test
	public void testLeftToRightInMixedAddSubtract() {
		tokens = ImmutableList.of(IntegerToken.create(2), SymbolToken.PLUS,
				IntegerToken.create(3), SymbolToken.MINUS,
				IntegerToken.create(5));
		parseAndCheckValueIs(0);
	}

	@Test
	public void testParseWithNumbersSurroundingVariable() {
		tokens = ImmutableList.of(IntegerToken.create(2),
				VariableToken.create("A"), IntegerToken.create(3));
		env.bind(VariableBuilder.create("A"), 5);
		parseAndCheckValueIs(2 * 5 * 3);
	}

	@Test
	public void testSubtractSurroundedVariable() {
		tokens = ImmutableList.of(IntegerToken.create(5), SymbolToken.MINUS,
				IntegerToken.create(2), VariableToken.create("A"),
				IntegerToken.create(3));
		env.bind(VariableBuilder.create("A"), 5);
		parseAndCheckValueIs(5 - 2 * 5 * 3);
	}

	@Test
	public void testAddToSurroundedVariable() {
		tokens = ImmutableList.of(IntegerToken.create(2),
				VariableToken.create("A"), IntegerToken.create(3),
				SymbolToken.PLUS, IntegerToken.create(4));
		env.bind(VariableBuilder.create("A"), 5);
		parseAndCheckValueIs(2 * 5 * 3 + 4);
	}

	@Test
	public void testSubtractSurroundAdd() {
		tokens = ImmutableList.of(IntegerToken.create(5), SymbolToken.MINUS,
				IntegerToken.create(2), VariableToken.create("A"),
				IntegerToken.create(3), SymbolToken.PLUS,
				IntegerToken.create(4));
		env.bind(VariableBuilder.create("A"), 5);
		parseAndCheckValueIs(5 - 2 * 5 * 3 + 4);
	}
	
	private static final class ElementCounter implements Expression.Visitor {

		int count;

		@Override
		public void visit(Variable variable) {
			count++;
		}

		@Override
		public void visit(Operation operator) {
			count++;
		}

		@Override
		public void visit(int number) {
			count++;
		}

		@Override
		public void visit(UnaryOperation unaryOperation) {
			count++;
		}

	};

}
