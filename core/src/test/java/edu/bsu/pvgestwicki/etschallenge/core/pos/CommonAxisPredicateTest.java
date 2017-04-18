package edu.bsu.pvgestwicki.etschallenge.core.pos;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import pythagoras.i.Point;

import com.google.common.collect.ImmutableSet;

import edu.bsu.pvgestwicki.etschallenge.core.pos.CommonAxisPredicate;

public class CommonAxisPredicateTest {

	private CommonAxisPredicate pred = CommonAxisPredicate.instance();
	
	@Test
	public void testVertical() {
		Set<Point> vertical = ImmutableSet.of(
				new Point(0,0),
				new Point(0,1),
				new Point(0,2));
		assertTrue(pred.apply(vertical));
	}
	
	@Test
	public void testHorizontal() {
		Set<Point> horizontal = ImmutableSet.of(
				new Point(0,0),
				new Point(1,0),
				new Point(2,0));
		assertTrue(pred.apply(horizontal));
	}
	
	@Test
	public void testDiagonal() {
		Set <Point> diagonal = ImmutableSet.of(
				new Point(0,0),
				new Point(1,1));
		assertFalse(pred.apply(diagonal));
	}
	
}
