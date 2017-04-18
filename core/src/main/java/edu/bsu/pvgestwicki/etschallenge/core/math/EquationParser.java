package edu.bsu.pvgestwicki.etschallenge.core.math;

import java.util.List;

public final class EquationParser {

	private final EquationBuilder builder = new EquationBuilderImpl();
	private final Lexer lexer = Lexer.create();

	public Equation parse(final String string) {
		checkLeadingAndTrailingEquals(string);
		final String[] tokens = string.split("=");
		for (String tok : tokens) {
			Expression expression = parseExpression(tok);
			builder.expression(expression);
		}
		return builder.build();
	}

	private void checkLeadingAndTrailingEquals(String string) {
		if (string.startsWith("="))
			throw new ParseException("Leading equals");
		if (string.endsWith("="))
			throw new ParseException("Trailing equals");
	}

	private Expression parseExpression(String expressionString) {
		List<Token> tokens = lexer.analyze(expressionString);
		Expression e = ExpressionParser.parse(tokens);
		return e;
	}

}
