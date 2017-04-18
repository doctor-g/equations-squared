package edu.bsu.pvgestwicki.etschallenge.core.util;

import java.util.List;

import com.google.common.collect.Lists;

import edu.bsu.pvgestwicki.etschallenge.core.math.Equation;
import edu.bsu.pvgestwicki.etschallenge.core.math.Expression;
import edu.bsu.pvgestwicki.etschallenge.core.math.Variable;

public final class VariableExtractor {

	private VariableExtractor() {
	}

	public static List<Variable> from(Equation eq) {
		Accumulator accumulator = new Accumulator();
		for (Expression e : eq.expressions()) {
			e.accept(accumulator);
		}
		return accumulator.list;
	}

	private static final class Accumulator extends Expression.Visitor.Adapter {

		List<Variable> list = Lists.newArrayList();

		@Override
		public void visit(Variable variable) {
			list.add(variable);
		}

	}

}
