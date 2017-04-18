package edu.bsu.pvgestwicki.etschallenge.core.math;

import edu.bsu.pvgestwicki.etschallenge.core.util.SpecialChar;

public enum SymbolToken implements Token {
	PLUS('+'), MINUS('-'), TIMES('*'), DIVIDED_BY('/');

	public static SymbolToken parse(char c) {
		for (SymbolToken candidate : SymbolToken.values()) {
			if (candidate.c == c)
				return candidate;
		}
		if (c == SpecialChar.MULTIPLICATION_SIGN.asCharacter())
			return TIMES;
		if (c == SpecialChar.OBELUS.asCharacter())
			return DIVIDED_BY;

		throw new NoSuchSymbolException(c);
	}

	private final char c;

	private SymbolToken(char c) {
		this.c = c;
	}

	@Override
	public <T> T accept(TokenVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public char asCharacter() {
		return c;
	}

	public boolean isHigherPriorityThan(SymbolToken token) {
		switch (this) {
		case PLUS:
		case MINUS:
			return false;
		case TIMES:
		case DIVIDED_BY:
			return isPlusOrMinus(token);
		default:
			throw new IllegalStateException();
		}
	}

	private boolean isPlusOrMinus(SymbolToken token) {
		return token.equals(PLUS) || token.equals(MINUS);
	}

}
