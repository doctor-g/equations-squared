package edu.bsu.pvgestwicki.etschallenge.core.model;

public class IllegalPlayException extends RuntimeException {

	private static final long serialVersionUID = -8243264594747696111L;

	public IllegalPlayException(String msg) {
		super(msg);
	}
}
