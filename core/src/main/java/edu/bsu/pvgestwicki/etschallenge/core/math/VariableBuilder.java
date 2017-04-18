package edu.bsu.pvgestwicki.etschallenge.core.math;

import com.google.common.base.Objects;

public final class VariableBuilder {

	public static Variable create(String name) {
		return new VariableImpl(name);
	}

	private static final class VariableImpl implements Variable {
		private final String name;

		VariableImpl(String name) {
			this.name = name;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof VariableImpl) {
				VariableImpl other = (VariableImpl) obj;
				return this.name.equals(other.name);
			} else
				return false;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(name);
		}

		@Override
		public String toString() {
			return name;
		}

		@Override
		public String name() {
			return name;
		}

	}

}
