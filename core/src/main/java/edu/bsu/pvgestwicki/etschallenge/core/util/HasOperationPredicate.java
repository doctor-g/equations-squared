package edu.bsu.pvgestwicki.etschallenge.core.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Predicate;

import edu.bsu.pvgestwicki.etschallenge.core.math.Expression;
import edu.bsu.pvgestwicki.etschallenge.core.math.Operation;

public final class HasOperationPredicate implements Predicate<Expression> {

	private static final HasOperationPredicate SINGLETON = new HasOperationPredicate();

	public static HasOperationPredicate instance() {
		return SINGLETON;
	}

	public static boolean on(Expression input) {
		return SINGLETON.apply(input);
	}

	private HasOperationPredicate() {
	}

	@Override
	public boolean apply(Expression input) {
		checkNotNull(input);
		Visitor v = new Visitor();
		input.accept(v);
		return v.found;
	}

	private static final class Visitor extends Expression.Visitor.Adapter {

		boolean found = false;

		@Override
		public void visit(Operation operator) {
			found = true;
		}

	}

}
