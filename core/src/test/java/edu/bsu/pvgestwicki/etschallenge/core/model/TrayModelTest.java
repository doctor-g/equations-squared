package edu.bsu.pvgestwicki.etschallenge.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.bsu.pvgestwicki.etschallenge.core.GameConfiguration;

public class TrayModelTest {

	TrayModel trayModel = TrayModel.create();

	@Test
	public void testCountDigitsWhenOnlyDigitsAreThere() {
		int NUM = 4;
		for (int i = 0; i < NUM; i++)
			trayModel.add(makeDigitTile());
		assertEquals(NUM, trayModel.countDigitsAndVariables());
	}

	@Test
	public void testCountOperatorsWhenOnlyOperatorsAreThere() {
		trayModel.add(TileModelFactory.instance().forSymbol('+'));
		assertEquals(1, trayModel.countOperators());
	}

	@Test
	public void testCountDigitsWhenThereAreVariables() {
		trayModel.add(makeDigitTile());
		trayModel.add(makeVariableTile());
		assertEquals(2, trayModel.countDigitsAndVariables());
	}

	private TileModel makeDigitTile() {
		TileModel tile = TileModelFactory.instance().forSymbol('5');
		return tile;
	}

	private TileModel makeVariableTile() {
		TileModel tile = TileModelFactory.instance().forSymbol('A');
		return tile;
	}

	@Test
	public void testIsFullWhenFull() {
		int max = GameConfiguration.instance().tilesAllowedInTray();
		for (int i = 0; i < max; i++)
			trayModel.add(makeDigitTile());
		assertTrue(trayModel.isFull());
	}
}
