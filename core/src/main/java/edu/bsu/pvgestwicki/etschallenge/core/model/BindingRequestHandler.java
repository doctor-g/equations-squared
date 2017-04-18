package edu.bsu.pvgestwicki.etschallenge.core.model;

import playn.core.util.Callback;
import edu.bsu.pvgestwicki.etschallenge.core.math.Variable;

public interface BindingRequestHandler {

	public void request(Variable v, Callback<Integer> binder);
}
