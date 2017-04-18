package edu.bsu.pvgestwicki.etschallenge.core.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EqualsTilePredicateTest {

	EqualsTilePredicate predicate = new EqualsTilePredicate();
	
	@Test
	public void testEqualsPasses() {
		TileModel tile = TileModelFactory.instance().forSymbol('=');
		assertTrue(predicate.apply(tile));
	}
	
	@Test
	public void testDigitFails() {
		TileModel tile = TileModelFactory.instance().forSymbol('1');
		assertFalse(predicate.apply(tile));
	}
}
