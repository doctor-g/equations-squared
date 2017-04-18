package edu.bsu.pvgestwicki.etschallenge.core.math;

public final class ParseException extends RuntimeException {
	private static final long serialVersionUID = -2154122493293711869L;

	public ParseException(Throwable t) {
		super(t);
	}
	
	public ParseException(String mesg) {
		super(mesg);
	}
}
