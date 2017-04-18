package edu.bsu.pvgestwicki.etschallenge.core.math;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

public final class FactorBuilder {
	private int value;
	private Variable var;

	public static FactorBuilder of(int value) {
		FactorBuilder fb = new FactorBuilder();
		fb.value = value;
		return fb;
	}

	public static FactorBuilder of(Variable v) {
		FactorBuilder fb = new FactorBuilder();
		fb.var = checkNotNull(v);
		return fb;
	}

	private FactorBuilder() {

	}

	public Factor build() {
		if (variableHasBeenSet()) {
			return new VariableFactor(var);
		} else

			return new IntegerFactor(value);
	}

	private boolean variableHasBeenSet() {
		return var != null;
	}

	private static final class VariableFactor implements Factor {
		private final Variable v;

		VariableFactor(Variable v) {
			this.v = checkNotNull(v);
		}

		@Override
		public int value(Environment en) {
			return en.valueOf(v);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof VariableFactor) {
				VariableFactor other = (VariableFactor) obj;
				return Objects.equal(v, other.v);
			} else
				return false;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(v);
		}

		@Override
		public String toString() {
			return v.toString();
		}

	}

	private static final class IntegerFactor implements Factor {

		private final int value;

		public IntegerFactor(int value) {
			this.value = value;
		}

		@Override
		public int value(Environment en) {
			return value;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof IntegerFactor) {
				IntegerFactor other = (IntegerFactor) obj;
				return value == other.value;
			} else
				return false;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(value);
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}

	}

}
