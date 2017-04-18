package edu.bsu.pvgestwicki.etschallenge.core.pos;

import pythagoras.i.IPoint;

import com.google.common.base.Predicate;

public final class CommonHorizontalAxisPredicate implements
		Predicate<Iterable<? extends IPoint>> {

	private static final CommonHorizontalAxisPredicate INSTANCE = new CommonHorizontalAxisPredicate();

	public static CommonHorizontalAxisPredicate instance() {
		return INSTANCE;
	}

	private CommonHorizontalAxisPredicate() {
	}

	@Override
	public boolean apply(Iterable<? extends IPoint> input) {
		final int y = input.iterator().next().y();
		for (IPoint p : input) {
			if (p.y() != y)
				return false;
		}
		return true;
	}

}
