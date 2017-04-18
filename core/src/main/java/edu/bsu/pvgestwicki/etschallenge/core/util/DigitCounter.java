package edu.bsu.pvgestwicki.etschallenge.core.util;

public final class DigitCounter {

	private static final DigitCounter SINGLETON = new DigitCounter();

	public static DigitCounter instance() {
		return SINGLETON;
	}

	private DigitCounter() {
	}

	public int countDigitsIn(int i) {
		if (i == 0)
			return 1;
		return (int) Math.ceil(Math.log10(Math.abs(i) + 1));
	}
}
