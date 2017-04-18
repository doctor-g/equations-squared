package edu.bsu.pvgestwicki.etschallenge.core.pos;

import pythagoras.i.IPoint;

import com.google.common.base.Predicate;

public final class CommonVerticalAxisPredicate implements
		Predicate<Iterable<? extends IPoint>> {

	private static final CommonVerticalAxisPredicate INSTANCE = new CommonVerticalAxisPredicate();

	public static CommonVerticalAxisPredicate instance() {
		return INSTANCE;
	}

	private CommonVerticalAxisPredicate() {
	}

	@Override
	public boolean apply(Iterable<? extends IPoint> input) {
		final int x = input.iterator().next().x();
		for (IPoint p : input) {
			if (p.x() != x)
				return false;
		}
		return true;
	}

}
