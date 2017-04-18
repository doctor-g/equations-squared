package edu.bsu.pvgestwicki.etschallenge.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.bsu.pvgestwicki.etschallenge.core.GameConfiguration;

public class FixedQuantityTrayFillerTest {

	private static final GameConfiguration conf = GameConfiguration.instance();

	private TrayModel tray = TrayModel.create();
	private FixedQuantityTrayFiller filler = FixedQuantityTrayFiller.create();

	@Test
	public void testFillTrayResultsInTheTrayBeingFull() {
		filler.fill(tray);
		assertTrue("Should be full: " + tray.asString(), tray.isFull());
	}

	@Test
	public void testFillTrayReducesDigitsRemainingByNumberOfDigitsInTray() {
		int digitsRemaining = filler.digitsRemaining().get();
		filler.fill(tray);
		assertEquals(digitsRemaining - conf.digitsInTray(), filler
				.digitsRemaining().get().intValue());
	}
	
}
