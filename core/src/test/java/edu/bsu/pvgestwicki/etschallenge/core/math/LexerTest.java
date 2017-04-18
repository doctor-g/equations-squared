package edu.bsu.pvgestwicki.etschallenge.core.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import edu.bsu.pvgestwicki.etschallenge.core.math.IntegerToken;
import edu.bsu.pvgestwicki.etschallenge.core.math.Lexer;
import edu.bsu.pvgestwicki.etschallenge.core.math.SymbolToken;
import edu.bsu.pvgestwicki.etschallenge.core.math.Token;
import edu.bsu.pvgestwicki.etschallenge.core.math.TokenVisitor;
import edu.bsu.pvgestwicki.etschallenge.core.math.VariableToken;

public class LexerTest {

	private final Lexer lexer = Lexer.create();
	private List<Token> tokens;
	private boolean processedVisitor = false;

	@Test
	public void testSingleIntegerExpressions() {
		checkSingleInteger("123");
		checkSingleInteger("345");
	}

	private void checkSingleInteger(String input) {
		tokens = lexer.analyze(input);
		assertEquals(1, tokens.size());

		checkTokenValueIs(tokens.get(0), Integer.parseInt(input));
	}

	private void checkTokenValueIs(Token token, final int value) {
		token.accept(new TokenVisitor.Abstract<Void>() {
			@Override
			public Void visit(IntegerToken integerToken) {
				assertEquals(value, integerToken.value());
				processedVisitor = true;
				return null;
			}
		});
		assertTrue(processedVisitor);
		processedVisitor = false;
	}

	@Test
	public void testAddition() {
		Object[] expected = new Object[] { 123, SymbolToken.PLUS, 4 };
		tokens = lexer.analyze("123+4");
		assertTokenListMatchesExpected(expected);
	}

	private void assertTokenListMatchesExpected(Object[] expectedValues) {
		for (int i = 0; i < expectedValues.length; i++) {
			Object expected = expectedValues[i];
			Token token = tokens.get(i);
			if (expected instanceof Integer) {
				assertIntegerMatch(expected, token);
			} else if (expected instanceof SymbolToken) {
				assertEquals(expected, token);
			} else if (expected instanceof Character) {
				assertCharacterMatch(expected, token);
			} else {
				throw new IllegalStateException("Unexpected expected value: "
						+ expected);
			}
		}
	}

	private void assertIntegerMatch(Object expected, Token token) {
		int value = (Integer) expected;
		assertTrue(token instanceof IntegerToken);
		int actual = ((IntegerToken) token).value();
		assertEquals(value, actual);
	}

	private void assertCharacterMatch(Object expected, Token token) {
		char value = (Character) expected;
		assertTrue(token instanceof VariableToken);
		String actual = ((VariableToken) token).name();
		assertEquals(""+value, actual);
	}

	@Test
	public void testSubtraction() {
		Object[] expected = new Object[] { 10, SymbolToken.MINUS, 7 };
		tokens = lexer.analyze("10-7");
		assertTokenListMatchesExpected(expected);
	}

	@Test
	public void testAdditionSubstractionExpression() {
		Object[] expected = new Object[] { 123, SymbolToken.PLUS, 45,
				SymbolToken.MINUS, 67 };
		tokens = lexer.analyze("123+45-67");
		assertTokenListMatchesExpected(expected);
	}

	@Test
	public void testVariableExpression() {
		Object[] expected = new Object[] { 123, SymbolToken.PLUS, 'A' };
		tokens = lexer.analyze("123+A");
		assertTokenListMatchesExpected(expected);
	}

}
