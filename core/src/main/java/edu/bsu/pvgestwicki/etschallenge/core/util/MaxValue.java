package edu.bsu.pvgestwicki.etschallenge.core.util;

import static com.google.common.base.Preconditions.checkNotNull;
import edu.bsu.pvgestwicki.etschallenge.core.math.Environment;
import edu.bsu.pvgestwicki.etschallenge.core.math.Equation;
import edu.bsu.pvgestwicki.etschallenge.core.math.Expression;
import edu.bsu.pvgestwicki.etschallenge.core.math.Variable;

public final class MaxValue {

	private final Environment env;

	private MaxValue(Environment env) {
		this.env = checkNotNull(env);
	}

	public int of(Equation input) {
		int max = Integer.MIN_VALUE;
		for (Expression e : input.expressions()) {
			max = Math.max(max, of(e));
		}
		return max;
	}

	public int of(Expression expression) {
		Visitor v = new Visitor();
		expression.accept(v);
		return v.max;
	}

	public static MaxValue in(Environment env) {
		return new MaxValue(env);
	}

	private final class Visitor extends Expression.Visitor.Adapter {

		int max = Integer.MIN_VALUE;

		@Override
		public void visit(int number) {
			max = Math.max(max, number);
		}

		@Override
		public void visit(Variable variable) {
			max = Math.max(max, env.valueOf(variable));
		}

	}

}
