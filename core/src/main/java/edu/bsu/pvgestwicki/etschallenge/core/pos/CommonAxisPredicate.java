package edu.bsu.pvgestwicki.etschallenge.core.pos;

import pythagoras.i.IPoint;

import com.google.common.base.Predicate;

public final class CommonAxisPredicate implements Predicate<Iterable<? extends IPoint>> {

	private static final CommonHorizontalAxisPredicate horizontal = CommonHorizontalAxisPredicate.instance();
	private static final CommonVerticalAxisPredicate vertical = CommonVerticalAxisPredicate.instance();
	
	private static final CommonAxisPredicate INSTANCE = new CommonAxisPredicate();
	
	public static CommonAxisPredicate instance() { return INSTANCE; }
	
	private CommonAxisPredicate() {}

	@Override
	public boolean apply(Iterable<? extends IPoint> input) {
		return horizontal.apply(input) || vertical.apply(input);
	}

}
