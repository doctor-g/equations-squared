package edu.bsu.pvgestwicki.etschallenge.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Set;

import org.junit.Test;

import pythagoras.i.Dimension;
import pythagoras.i.IDimension;
import pythagoras.i.IPoint;
import pythagoras.i.Point;
import react.UnitSlot;

import com.google.common.collect.Sets;

public class TileAreaTest {

	private static final int WIDTH = 11, HEIGHT = 7;

	TileArea tileArea = TileArea.create(WIDTH, HEIGHT);
	TileModel tile = mock(TileModel.class);

	@Test
	public void testTileAreaHasSize() {
		Dimension expected = new Dimension(WIDTH, HEIGHT);
		IDimension actual = tileArea.size();
		assertEquals(expected, actual);
	}

	@Test
	public void testNoSpaceIsOccupiedOnStart() {
		for (int i = 0; i < WIDTH; i++)
			for (int j = 0; j < HEIGHT; j++)
				assertFalse(tileArea.at(0, 0).isOccupied());
	}

	@Test
	public void testPutTileInSpaceMakesItOccupied() {
		tileArea.at(0, 0).place(tile);
		assertTrue(tileArea.at(0, 0).isOccupied());
	}

	@Test
	public void testCanReadTileFromSpaceAfterPlacingIt() {
		tileArea.at(0, 0).place(tile);
		TileModel actual = tileArea.at(0, 0).tile();
		assertTrue("Retrieved tile is identical to the tile sent.",
				tile == actual);
	}

	@Test
	public void testCellPostsNotificationWhenTileIsPlacedWithin() {
		UnitSlot slot = mock(UnitSlot.class);
		tileArea.at(0, 0).value().connect(slot);
		tileArea.at(0, 0).place(tile);
		verify(slot).onChange(tile, null);
	}

	@Test(expected = TileAlreadyPresentException.class)
	public void testPlacingTileAtopAnotherThrowsException() {
		for (int i = 0; i < 2; i++) {
			tileArea.at(0, 0).place(tile);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSendingNullAsTileThrowsException() {
		tileArea.at(0, 0).place(null);
	}

	@Test
	public void testCallingClearRemovesATile() {
		tileArea.at(0, 0).place(tile);
		tileArea.at(0, 0).clear();
		assertFalse(tileArea.at(0, 0).isOccupied());
	}

	@Test
	public void testIsFullFailsUntilItIsFull() {
		final int max = WIDTH * HEIGHT;
		int count = 0;
		for (IPoint p : legalKeys()) {
			tileArea.at(p).place(tile);
			count++;
			if (count < max)
				assertFalse(tileArea.isFull());
			else
				assertTrue(tileArea.isFull());
		}
	}
	
	private Set<IPoint> legalKeys() {
		Set<IPoint> result = Sets.newHashSet();
		for (int i=0; i<WIDTH; i++)
			for (int j=0; j<HEIGHT; j++) 
				result.add(new Point(i,j));
		return result;
	}
	
	@Test
	public void testContainsReturnsFalseWhenQueryingPointOffBoard() {
		assertFalse(tileArea.contains(-1,-1));
	}
}
