package edu.bsu.pvgestwicki.etschallenge.core.util;

public enum SpecialChar {

	/** Division symbol */
	OBELUS('\u00F7'),
	/** Multiplication symbol */
	MULTIPLICATION_SIGN('\u00D7'),
	PLUSMINUS('\u00B1');

	private final char c;

	private SpecialChar(char c) {
		this.c = c;
	}

	public char asCharacter() {
		return c;
	}
}
