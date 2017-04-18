package edu.bsu.pvgestwicki.etschallenge.core.math;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

public final class TermBuilder {

	public static TermBuilder of(Factor f) {
		return new TermBuilder(f);
	}

	public static Term makeIntegerTerm(int value) {
		Factor f = FactorBuilder.of(value).build();
		Term t = TermBuilder.of(f).build();
		return t;
	}

	public static Term makeVariableTerm(Variable v) {
		Factor f = FactorBuilder.of(v).build();
		return TermBuilder.of(f).build();
	}

	private Factor f;

	private TermBuilder(Factor f) {
		this.f = checkNotNull(f);
	}

	public Term build() {
		return new FactorTerm(f);
	}

	private static final class FactorTerm implements Term {

		private final Factor f;

		FactorTerm(Factor f) {
			this.f = f;
		}

		@Override
		public int value(Environment env) {
			return f.value(env);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof FactorTerm) {
				FactorTerm other = (FactorTerm) obj;
				return Objects.equal(f, other.f);
			} else
				return false;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(f);
		}

		@Override
		public String toString() {
			return f.toString();
		}
	}

}
