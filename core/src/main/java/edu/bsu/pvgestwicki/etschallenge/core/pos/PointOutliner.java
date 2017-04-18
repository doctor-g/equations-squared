package edu.bsu.pvgestwicki.etschallenge.core.pos;

import java.util.Set;

import pythagoras.i.IPoint;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

public final class PointOutliner implements Function<Set<IPoint>, Set<IPoint>> {

	public static PointOutliner withSize(int width, int height) {
		return new PointOutliner(width, height);
	}

	private final int width;
	private final int height;

	private PointOutliner(int w, int h) {
		this.width = w;
		this.height = h;
	}

	@Override
	public Set<IPoint> apply(Set<IPoint> input) {
		Set<IPoint> result = Sets.newHashSet();
		for (IPoint p : input) {
			result.addAll(DirectionFunction.applyAll(p));
		}
		result = removeOutOfBoundsPointsFrom(result);
		return Sets.difference(result, input);
	}

	private Set<IPoint> removeOutOfBoundsPointsFrom(Set<IPoint> set) {
		Set<IPoint> result = Sets.newHashSet();
		for (IPoint p : set) {
			if (inBounds(p))
				result.add(p);
		}
		return result;
	}

	private boolean inBounds(IPoint p) {
		return p.x() >= 0 && p.x() < width && p.y() >= 0 && p.y() < height;
	}

}
