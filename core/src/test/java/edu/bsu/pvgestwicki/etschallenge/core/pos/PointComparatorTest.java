package edu.bsu.pvgestwicki.etschallenge.core.pos;

import static edu.bsu.pvgestwicki.etschallenge.core.pos.DirectionFunction.ABOVE;
import static edu.bsu.pvgestwicki.etschallenge.core.pos.DirectionFunction.BELOW;
import static edu.bsu.pvgestwicki.etschallenge.core.pos.DirectionFunction.LEFT;
import static edu.bsu.pvgestwicki.etschallenge.core.pos.DirectionFunction.RIGHT;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.bsu.pvgestwicki.etschallenge.core.pos.PointComparator;

import pythagoras.i.IPoint;
import pythagoras.i.Point;

public class PointComparatorTest {

	private static final IPoint CENTER = new Point(0, 0);

	@Test
	public void testLeftmost() {
		int result = PointComparator.LEFTWARD.compare(CENTER,
				LEFT.apply(CENTER));
		assertTrue(result < 0);
	}

	@Test
	public void testRightmost() {
		int result = PointComparator.RIGHTWARD.compare(CENTER,
				RIGHT.apply(CENTER));
		assertTrue(result < 0);
	}

	@Test
	public void testTopmost() {
		int result = PointComparator.UPWARD
				.compare(CENTER, ABOVE.apply(CENTER));
		assertTrue(result < 0);
	}

	@Test
	public void testBottommost() {
		int result = PointComparator.DOWNWARD.compare(CENTER,
				BELOW.apply(CENTER));
		assertTrue(result < 0);
	}

}
