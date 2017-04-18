package edu.bsu.pvgestwicki.etschallenge.core.model;

import java.util.Collection;
import java.util.Set;

import pythagoras.i.IPoint;

import com.google.common.collect.Sets;

import edu.bsu.pvgestwicki.etschallenge.core.pos.DirectionFunction;

public final class BorderFinder {

	public static BorderFinder create() {
		return new BorderFinder();
	}

	private Set<IPoint> result;
	private Collection<IPoint> input;

	private BorderFinder() {
	}

	public Set<IPoint> findBorder(final Collection<IPoint> input) {
		this.input = input;
		result = Sets.newHashSet();
		for (IPoint point : input) {
			findBorderOfAPoint(point);
		}
		return result;
	}

	private void findBorderOfAPoint(final IPoint p) {
		for (DirectionFunction f : DirectionFunction.values()) {
			findBorderInGivenDirection(f, p);
		}
	}

	private void findBorderInGivenDirection(final DirectionFunction f, final IPoint p) {
		IPoint candidate = f.apply(p);
		if (!input.contains(candidate))
			result.add(candidate);
	}
}
