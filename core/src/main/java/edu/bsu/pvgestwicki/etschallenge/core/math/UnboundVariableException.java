package edu.bsu.pvgestwicki.etschallenge.core.math;

import static com.google.common.base.Preconditions.checkNotNull;

public class UnboundVariableException extends RuntimeException {

	private static final long serialVersionUID = -421249775662363200L;

	private final Variable variable;
	private final Environment environment;

	public UnboundVariableException(Variable variable, Environment environment) {
		super();
		this.variable = checkNotNull(variable);
		this.environment = checkNotNull(environment);
	}

	public Variable variable() {
		return variable;
	}

	public Environment environment() {
		return environment;
	}
}
