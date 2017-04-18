package edu.bsu.pvgestwicki.etschallenge.core.math;

public final class NoSuchSymbolException extends RuntimeException {

	private static final long serialVersionUID = 59563029648854823L;

	private final char c;
	
	public NoSuchSymbolException(char c) {
		this.c=c;
	}

	@Override
	public String getMessage() {
		return "Symbol: " + c;
	}
}
