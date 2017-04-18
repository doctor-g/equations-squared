package edu.bsu.pvgestwicki.etschallenge.core.pos;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import pythagoras.i.IPoint;
import pythagoras.i.Point;

import com.google.common.collect.ImmutableSet;

public class PointOutlinerTest {

	@Test
	public void testSimpleBoundary() {
		PointOutliner outliner = PointOutliner.withSize(5, 5);
		Set<IPoint> set = ImmutableSet.of((IPoint) new Point(1, 1));
		Set<IPoint> result = outliner.apply(set);
		Set<IPoint> expected = ImmutableSet.of((IPoint) new Point(1, 0),
				new Point(0, 1), new Point(2, 1), new Point(1, 2));
		assertTrue(result.equals(expected));
	}

	@Test
	public void testOffscreenEliminatedWhenBoundariesAreSet() {
		PointOutliner outliner = PointOutliner.withSize(5, 5);
		Set<IPoint> set = ImmutableSet.of((IPoint) new Point(0, 0));
		Set<IPoint> result = outliner.apply(set);
		Set<IPoint> expected = ImmutableSet.of((IPoint) new Point(1, 0),
				new Point(0, 1));
		assertTrue(result.equals(expected));
	}
}
