package edu.bsu.pvgestwicki.etschallenge.core.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import edu.bsu.pvgestwicki.etschallenge.core.math.Equation;
import edu.bsu.pvgestwicki.etschallenge.core.math.Variable;

public interface CommitResult {

	public final class Success implements CommitResult {

		private final List<Equation> equations = Lists.newArrayList();

		public Success(Equation eq) {
			this.equations.add(eq);
		}

		@Override
		public <T> T accept(Visitor<T> v, Object... args) {
			return v.visit(this, args);
		}

		public Success append(List<Equation> equations) {
			this.equations.addAll(equations);
			return this;
		}

		public List<Equation> equations() {
			return ImmutableList.copyOf(equations);
		}

	}

	public final class UnbalancedEquation implements CommitResult {

		private final Equation eq;

		public UnbalancedEquation(Equation eq) {
			this.eq = checkNotNull(eq);
		}

		@Override
		public <T> T accept(Visitor<T> v, Object... args) {
			return v.visit(this, args);
		}

		public Equation equation() {
			return eq;
		}

	}

	public final class NonintersectingMove implements CommitResult {

		@Override
		public <T> T accept(Visitor<T> v, Object... args) {
			return v.visit(this, args);
		}
	}

	public final class MalformedEquation implements CommitResult {
		@Override
		public <T> T accept(Visitor<T> v, Object... args) {
			return v.visit(this, args);
		}

	}

	public final class NotAnEquation implements CommitResult {

		@Override
		public <T> T accept(Visitor<T> v, Object... args) {
			return v.visit(this, args);
		}

	}

	public final class DivideByZero implements CommitResult {

		@Override
		public <T> T accept(Visitor<T> v, Object... args) {
			return v.visit(this, args);
		}

	}

	public final class UnboundVariable implements CommitResult {

		private final Variable variable;

		public UnboundVariable(Variable variable) {
			this.variable = checkNotNull(variable);
		}

		public Variable variable() {
			return variable;
		}

		@Override
		public <T> T accept(Visitor<T> v, Object... args) {
			return v.visit(this, args);
		}

	}

	public class IllegalMove implements CommitResult {

		@Override
		public <T> T accept(Visitor<T> v, Object... args) {
			return v.visit(this, args);
		}

	}

	public interface Visitor<T> {
		public abstract class Adapter<T> implements Visitor<T> {
			private final T defaultValue;

			public Adapter(T defaultValue) {
				this.defaultValue = defaultValue;
			}

			@Override
			public T visit(Success success, Object... args) {
				return defaultValue;
			}

			@Override
			public T visit(UnbalancedEquation unbalanced, Object... args) {
				return defaultValue;
			}

			@Override
			public T visit(MalformedEquation malformed, Object... args) {
				return defaultValue;
			}

			@Override
			public T visit(NotAnEquation notAnEquation, Object... args) {
				return defaultValue;
			}

			@Override
			public T visit(DivideByZero divideByZero, Object... args) {
				return defaultValue;
			}

			@Override
			public T visit(UnboundVariable unboundVariable, Object... args) {
				return defaultValue;
			}

			@Override
			public T visit(IllegalMove illegalMove, Object... args) {
				return defaultValue;
			}

			@Override
			public T visit(NonintersectingMove nonintersectingMove,
					Object... args) {
				return defaultValue;
			}
		}

		public T visit(Success success, Object... args);

		public T visit(UnbalancedEquation unbalanced, Object... args);

		public T visit(MalformedEquation malformed, Object... args);

		public T visit(NotAnEquation notAnEquation, Object... args);

		public T visit(DivideByZero divideByZero, Object... args);

		public T visit(UnboundVariable unboundVariable, Object... args);

		public T visit(IllegalMove illegalMove, Object... args);

		public T visit(NonintersectingMove nonintersectingMove, Object... args);
	}

	public <T> T accept(Visitor<T> v, Object... args);
}
