package edu.bsu.pvgestwicki.etschallenge.core.util;

import edu.bsu.pvgestwicki.etschallenge.core.math.Expression;
import edu.bsu.pvgestwicki.etschallenge.core.math.Variable;

public final class TermCounter extends Expression.Visitor.Adapter {

	private int count;

	@Override
	public void visit(Variable variable) {
		count++;
	}

	@Override
	public void visit(int number) {
		count++;
	}
	
	public int count() {
		return count;
	}

	public static int count(Expression expression) {
		TermCounter counter = new TermCounter();
		expression.accept(counter);
		return counter.count();
	}
}
