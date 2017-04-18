package edu.bsu.pvgestwicki.etschallenge.core.model;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import react.Slot;

public class EqualsPoolModelTest {

	EqualsPoolModel pool = EqualsPoolModel.create();
	
	@Before
	public void setUp() {
		pool.start();
	}
	
	@Test
	public void testTakeTileReturnsOne() {
		TileModel tile = pool.take();
		assertTrue(EqualsTilePredicate.instance().apply(tile));
	}
	
	@Test
	public void testEventFiredWhenNewTileIsMadeAfterOneIsTaken() {
		@SuppressWarnings("unchecked")
		Slot<TileModel> slot = (Slot<TileModel>)mock(Slot.class);
		pool.onNewTopTile().connect(slot);
		pool.take();
		verify(slot).onEmit(any(TileModel.class));
	}
	
}
