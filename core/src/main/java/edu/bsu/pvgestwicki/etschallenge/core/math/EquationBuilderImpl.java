package edu.bsu.pvgestwicki.etschallenge.core.math;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.collect.Lists;

public final class EquationBuilderImpl implements EquationBuilder {

	private List<Expression> expressions = Lists.newArrayList();

	@Override
	public EquationBuilder expression(Expression e) {
		expressions.add(checkNotNull(e));
		return this;
	}

	@Override
	public Equation build() {
		if (expressions.size()<2)
			throw new NotAnEquationException();
		
		return new Equation(expressions.get(0), expressions.get(1), expressionArrayFrom(2));
	}
	
	private Expression[] expressionArrayFrom(int index) {
		List<Expression> list = expressions.subList(index, expressions.size());
		return list.toArray(new Expression[] {});
	}

}
