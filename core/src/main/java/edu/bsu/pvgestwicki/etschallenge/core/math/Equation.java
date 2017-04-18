package edu.bsu.pvgestwicki.etschallenge.core.math;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public final class Equation implements Evaluatable {

	private List<Expression> expressions = Lists.newArrayList();

	Equation(Expression lhs, Expression rhs, Expression... others) {
		expressions.add(checkNotNull(lhs));
		expressions.add(checkNotNull(rhs));
		for (Expression e : others)
			expressions.add(e);
	}

	public boolean isTrue(Environment e) {
		int value = expressions.get(0).value(e);
		for (int i = 1; i < expressions.size(); i++) {
			int anotherValue = expressions.get(i).value(e);
			if (anotherValue != value)
				return false;
		}
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Equation) {
			final Equation other = (Equation) obj;
			return expressionListsAreEqual(expressions, other.expressions);
		} else {
			return false;
		}
	}

	public ImmutableList<Expression> expressions() {
		return ImmutableList.copyOf(expressions);
	}

	private static boolean expressionListsAreEqual(List<Expression> list1,
			List<Expression> list2) {
		if (list1.size() != list2.size())
			return false;
		else
			for (int i = 0; i < list1.size(); i++) {
				Expression e1 = list1.get(i);
				Expression e2 = list2.get(i);
				if (!e1.equals(e2))
					return false;
			}
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(expressions);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("expressions", expressions)
				.toString();
	}

	public int value(Environment env) {
		checkState(isTrue(env));
		return expressions.get(0).value(env);
	}

}
