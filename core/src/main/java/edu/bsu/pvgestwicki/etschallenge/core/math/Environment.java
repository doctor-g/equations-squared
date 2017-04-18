package edu.bsu.pvgestwicki.etschallenge.core.math;


public interface Environment {

	Environment bind(Variable variable, int value);

	int valueOf(Variable v);

	boolean hasBindingFor(Variable v);

	void unbind(Variable variable);

}
