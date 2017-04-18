package edu.bsu.pvgestwicki.etschallenge.core.pos;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import pythagoras.i.IPoint;
import pythagoras.i.Point;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

public enum DirectionFunction implements Function<IPoint, IPoint> {

	ABOVE, BELOW, LEFT, RIGHT;

	public static Set<IPoint> applyAll(IPoint p) {
		checkNotNull(p);
		Set<IPoint> result = Sets.newHashSet();
		for (Function<IPoint, IPoint> f : values()) {
			result.add(f.apply(p));
		}
		return result;
	}

	@Override
	public IPoint apply(IPoint p) {
		switch (this) {
		case ABOVE:
			return new Point(p.x(), p.y() - 1);
		case BELOW:
			return new Point(p.x(), p.y() + 1);
		case LEFT:
			return new Point(p.x() - 1, p.y());
		case RIGHT:
			return new Point(p.x() + 1, p.y());
		default:
			throw new IllegalStateException();
		}
	}

}
