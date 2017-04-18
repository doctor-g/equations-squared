package edu.bsu.pvgestwicki.etschallenge.core.pos;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import pythagoras.i.IPoint;
import pythagoras.i.Point;

import com.google.common.collect.ImmutableSet;

import edu.bsu.pvgestwicki.etschallenge.core.pos.DirectionFunction;
import edu.bsu.pvgestwicki.etschallenge.core.pos.HasThreeFreedomsPredicate;

public class HasAtThreeFreedomsPredicateTest {

	private static final IPoint CENTER = new Point(0, 0);
	private static final IPoint RIGHT_OF_CENTER = DirectionFunction.RIGHT
			.apply(CENTER);

	@Test
	public void testIPointWithoutHorizAndVertNeighborsPassesFilter() {
		Set<IPoint> sourceData = ImmutableSet.of(CENTER);
		HasThreeFreedomsPredicate predicate = HasThreeFreedomsPredicate
				.createWithSourceData(sourceData);
		assertTrue(predicate.apply(RIGHT_OF_CENTER));
	}

	@Test
	public void testIPointWithHorizAndVertNeighborsIsExcluded() {
		Set<IPoint> sourceData = ImmutableSet.of(//
				CENTER, RIGHT_OF_CENTER, DirectionFunction.BELOW.apply(CENTER));
		IPoint aPointWithHAndVNeighbors = DirectionFunction.BELOW
				.apply(RIGHT_OF_CENTER);
		HasThreeFreedomsPredicate predicate = HasThreeFreedomsPredicate
				.createWithSourceData(sourceData);
		assertFalse(predicate.apply(aPointWithHAndVNeighbors));
	}

}
