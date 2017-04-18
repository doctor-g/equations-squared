package edu.bsu.pvgestwicki.etschallenge.core.pos;

import static edu.bsu.pvgestwicki.etschallenge.core.pos.DirectionFunction.ABOVE;
import static edu.bsu.pvgestwicki.etschallenge.core.pos.DirectionFunction.BELOW;
import static edu.bsu.pvgestwicki.etschallenge.core.pos.DirectionFunction.LEFT;
import static edu.bsu.pvgestwicki.etschallenge.core.pos.DirectionFunction.RIGHT;
import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;

import pythagoras.i.IPoint;
import pythagoras.i.Point;

import com.google.common.collect.ImmutableSet;

import edu.bsu.pvgestwicki.etschallenge.core.model.BorderFinder;

public class BorderFinderTest {

	private static final IPoint CENTER = new Point(0, 0);
	private static final IPoint RIGHT_OF_CENTER = RIGHT.apply(CENTER);
	private BorderFinder borderFinder = BorderFinder.create();

	@Test
	public void testBorderOfSingleIPointIsOrthogonalNeighbors() {
		Set<IPoint> input = ImmutableSet.of(CENTER);
		Set<IPoint> actual = borderFinder.findBorder(input);

		Set<IPoint> expected = ImmutableSet.of(//
				ABOVE.apply(CENTER),//
				BELOW.apply(CENTER),//
				RIGHT.apply(CENTER),//
				LEFT.apply(CENTER));
		assertEquals(expected, actual);
	}

	@Test
	public void testBorderOfAPairOfIPoints() {
		Set<IPoint> input = ImmutableSet.of(CENTER, RIGHT.apply(CENTER));
		Set<IPoint> actual = borderFinder.findBorder(input);

		Set<IPoint> expected = ImmutableSet.of(//
				ABOVE.apply(CENTER),//
				ABOVE.apply(RIGHT_OF_CENTER),//
				RIGHT.apply(RIGHT_OF_CENTER),//
				BELOW.apply(RIGHT_OF_CENTER),//
				BELOW.apply(CENTER),//
				LEFT.apply(CENTER));
		assertEquals(expected, actual);
	}
}
