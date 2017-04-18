package edu.bsu.pvgestwicki.etschallenge.core.math;

public interface Token {

	<T> T accept(TokenVisitor<T> visitor);

}
