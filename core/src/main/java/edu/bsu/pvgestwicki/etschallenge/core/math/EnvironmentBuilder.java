package edu.bsu.pvgestwicki.etschallenge.core.math;

import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import com.google.common.collect.Maps;

public final class EnvironmentBuilder {

	public static Environment create() {
		return new EnvironmentImpl();
	}

	private static final class EnvironmentImpl implements Environment {
		private Map<Variable, Integer> bindings = Maps.newHashMap();

		private EnvironmentImpl() {
		}

		@Override
		public Environment bind(Variable v, int i) {
			bindings.put(v, i);
			return this;
		}

		@Override
		public int valueOf(Variable v) {
			if (!hasBindingFor(v))
				throw new UnboundVariableException(v, this);
			else
				return bindings.get(v);
		}

		@Override
		public boolean hasBindingFor(Variable v) {
			return bindings.containsKey(v);
		}

		@Override
		public void unbind(Variable variable) {
			checkState(hasBindingFor(variable));
			bindings.remove(variable);
		}
	}
}
