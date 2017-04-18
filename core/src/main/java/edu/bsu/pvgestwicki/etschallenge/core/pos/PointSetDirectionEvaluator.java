package edu.bsu.pvgestwicki.etschallenge.core.pos;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pythagoras.i.IPoint;

import com.google.common.collect.Lists;

import edu.bsu.pvgestwicki.etschallenge.core.util.TopologicalComparator;

public final class PointSetDirectionEvaluator {

	public static PointSetDirectionEvaluator create() {
		return new PointSetDirectionEvaluator();
	}

	private PointSetDirectionEvaluator() {
	}

	public Direction evaluate(Collection<? extends IPoint> points) {
		List<IPoint> list = sortedCopyOf(points);
		Direction direction = guessDirection(list);
		if (direction == Direction.INDETERMINATE)
			return Direction.INDETERMINATE;
		else
			return verifyDirection(list, direction);
	}

	private List<IPoint> sortedCopyOf(Iterable<? extends IPoint> points) {
		List<IPoint> list = Lists.newArrayList(points);
		Collections.sort(list, TopologicalComparator.instance());
		return list;
	}

	private Direction guessDirection(List<IPoint> list) {
		if (list.size() < 2)
			return Direction.INDETERMINATE;
		IPoint p1 = list.get(0);
		IPoint p2 = list.get(1);
		return guessDirection(p1, p2);
	}

	private Direction guessDirection(IPoint p1, IPoint p2) {
		if (p2.x() - 1 == p1.x())
			return Direction.HORIZONTAL;
		else if (p2.y() - 1 == p1.y())
			return Direction.VERTICAL;
		else
			return Direction.INDETERMINATE;
	}

	private Direction verifyDirection(List<IPoint> list, Direction direction) {
		for (int i = 0; i < list.size() - 1; i++) {
			IPoint p = list.get(i);
			IPoint p2 = list.get(i + 1);

			if (!direction.increasingFunction().apply(p).equals(p2)) {
				return Direction.INDETERMINATE;
			}
		}
		return direction;
	}

}
