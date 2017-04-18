package edu.bsu.pvgestwicki.etschallenge.core.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.collect.Lists;

import edu.bsu.pvgestwicki.etschallenge.core.math.Environment;
import edu.bsu.pvgestwicki.etschallenge.core.math.Variable;

public final class ProvisionalVariableBinder {

	public static ProvisionalVariableBinder forEnvironment(Environment e) {
		return new ProvisionalVariableBinder(e);
	}
	
	private final Environment env;
	
	private List<Variable> provisionallyBoundVariables = Lists.newArrayList();
	
	private ProvisionalVariableBinder(Environment e) {
		this.env=checkNotNull(e);
	}
	
	public void provisionallyBind(Variable v, int value) {
		env.bind(v, value);
		provisionallyBoundVariables.add(v);
	}
	
	public void finalizeBindings() {
		provisionallyBoundVariables.clear();
	}
	
	public void unbind() {
		for (Variable v : provisionallyBoundVariables)
			env.unbind(v);
		provisionallyBoundVariables.clear();
	}
	
	public boolean isEmpty() {
		return provisionallyBoundVariables.isEmpty();
	}
}
