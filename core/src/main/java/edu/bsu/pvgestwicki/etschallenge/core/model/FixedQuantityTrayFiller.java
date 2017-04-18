package edu.bsu.pvgestwicki.etschallenge.core.model;

import java.util.List;

import react.Signal;
import react.SignalView;
import react.Value;
import react.ValueView;

import com.google.common.collect.Lists;

import edu.bsu.pvgestwicki.etschallenge.core.GameConfiguration;
import edu.bsu.pvgestwicki.etschallenge.core.event.TrayTileEvent;
import edu.bsu.pvgestwicki.etschallenge.core.util.FischerYatesShuffler;

public final class FixedQuantityTrayFiller {

	private static final GameConfiguration conf = GameConfiguration.instance();
	private static final int TIMES_TO_INCLUDE_EACH_DIGIT = 3;
	private static final String DIGITS = "1234567890";
	private static final String DIGITS_AND_VARS = times(DIGITS,
			TIMES_TO_INCLUDE_EACH_DIGIT) + "ABYZ";
	private static final String OPERATOR_FREQ = "+++++----**//";

	private static String times(String s, int times) {
		if (times == 0)
			return "";
		else
			return s + times(s, times - 1);
	}

	public static FixedQuantityTrayFiller create() {
		return new FixedQuantityTrayFiller();
	}

	private final Signal<TrayTileEvent> onTileCreated = Signal.create();

	private final TileModelFactory factory = TileModelFactory.instance();
	private Value<Integer> digitsRemaining = Value.create(0);
	private List<Character> digitQueue = Lists.newArrayList();

	private FixedQuantityTrayFiller() {
		char[] digitsAndVars = DIGITS_AND_VARS.toCharArray();
		FischerYatesShuffler.shuffle(digitsAndVars);
		for (int i = 0; i < digitsAndVars.length; i++) {
			digitQueue.add(digitsAndVars[i]);
		}
		updateDigitsRemaining();
	}

	private void updateDigitsRemaining() {
		digitsRemaining.update(digitQueue.size());
	}

	public SignalView<TrayTileEvent> onTileCreated() {
		return onTileCreated;
	}

	public ValueView<Integer> digitsRemaining() {
		return digitsRemaining;
	}

	public void fill(TrayModel tray) {
		fillDigits(tray);
		fillOperators(tray);
	}

	private void fillDigits(TrayModel tray) {
		while (tray.countDigitsAndVariables() < conf.digitsInTray())
			addDigit(tray);
	}

	private void addDigit(TrayModel tray) {
		char symbol = digitQueue.remove(0);
		addTileAndFireNotification(tray, symbol);
		updateDigitsRemaining();
	}

	private void addTileAndFireNotification(TrayModel tray, char symbol) {
		TileModel tile = factory.forSymbol(symbol);
		int position = tray.add(tile);
		onTileCreated.emit(new TrayTileEvent(tile, position));
	}

	private void fillOperators(TrayModel tray) {
		while (tray.countOperators() < conf.operatorsInTray())
			addOperator(tray);
	}

	private void addOperator(TrayModel tray) {
		char symbol = randomOperator();
		addTileAndFireNotification(tray, symbol);
	}

	private char randomOperator() {
		int index = (int) (Math.random() * OPERATOR_FREQ.length());
		return OPERATOR_FREQ.charAt(index);
	}
}
