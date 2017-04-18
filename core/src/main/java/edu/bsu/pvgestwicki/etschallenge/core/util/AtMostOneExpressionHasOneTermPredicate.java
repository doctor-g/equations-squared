package edu.bsu.pvgestwicki.etschallenge.core.util;

import com.google.common.base.Predicate;

import edu.bsu.pvgestwicki.etschallenge.core.math.Equation;
import edu.bsu.pvgestwicki.etschallenge.core.math.Expression;

public final class AtMostOneExpressionHasOneTermPredicate implements Predicate<Equation> {

	private static final AtMostOneExpressionHasOneTermPredicate SINGLETON = new AtMostOneExpressionHasOneTermPredicate();

	private AtMostOneExpressionHasOneTermPredicate() {}
	
	@Override
	public boolean apply(Equation input) {
		int expressionsWithOneTerm = 0;
		for (Expression e : input.expressions()) {
			int terms = TermCounter.count(e);
			if (terms==1) expressionsWithOneTerm++;
		}
		return expressionsWithOneTerm <= 1;
	}

	public static AtMostOneExpressionHasOneTermPredicate instance() {
		return SINGLETON;
	}

	
	
}
