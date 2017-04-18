package edu.bsu.pvgestwicki.etschallenge.core.model;

import java.util.List;

import react.SignalView;

import com.google.common.collect.Lists;

import edu.bsu.pvgestwicki.etschallenge.core.GameConfiguration;
import edu.bsu.pvgestwicki.etschallenge.core.model.TileArea.Cell;
import edu.bsu.pvgestwicki.etschallenge.core.model.TileModel.DigitTileModel;
import edu.bsu.pvgestwicki.etschallenge.core.model.TileModel.EqualsTileModel;
import edu.bsu.pvgestwicki.etschallenge.core.model.TileModel.OperatorTileModel;
import edu.bsu.pvgestwicki.etschallenge.core.model.TileModel.VariableTileModel;
import edu.bsu.pvgestwicki.etschallenge.core.model.TileModel.Visitor;

public final class TrayModel implements ReadOnlyTrayModel {

	private static final TileModel.Visitor<Boolean> IS_A_DIGIT_OR_VARIABLE = new TileModel.Visitor<Boolean>() {

		@Override
		public Boolean visit(VariableTileModel variableTile, Object... args) {
			return true;
		}

		@Override
		public Boolean visit(DigitTileModel digitTile, Object... args) {
			return true;
		}

		@Override
		public Boolean visit(OperatorTileModel operatorTile, Object... args) {
			return false;
		}

		@Override
		public Boolean visit(EqualsTileModel equalsTile, Object... args) {
			return false;
		}
	};

	private static final TileModel.Visitor<Boolean> IS_AN_OPERATOR = new TileModel.Visitor<Boolean>() {

		@Override
		public Boolean visit(VariableTileModel variableTile, Object... args) {
			return false;
		}

		@Override
		public Boolean visit(DigitTileModel digitTile, Object... args) {
			return false;
		}

		@Override
		public Boolean visit(OperatorTileModel operatorTile, Object... args) {
			return true;
		}

		@Override
		public Boolean visit(EqualsTileModel equalsTile, Object... args) {
			return false;
		}
	};

	public static TrayModel create() {
		int tilesInTray = GameConfiguration.instance().tilesAllowedInTray();
		return new TrayModel(tilesInTray);
	}

	private TileArea tileArea;

	public TrayModel(int tilesInTray) {
		tileArea = TileArea.create(tilesInTray, 1);
	}

	@Override
	public boolean isFull() {
		return tileArea.isFull();
	}

	@Override
	public TileArea.Cell at(int i) {
		return tileArea.at(i, 0);
	}

	public int add(TileModel tile) {
		for (int i = 0; i < tileArea.width(); i++) {
			Cell cell = at(i);
			if (!cell.isOccupied()) {
				cell.place(tile);
				return i;
			}
		}
		throw new TrayFullException();
	}

	@Override
	public boolean isEmpty() {
		return tileArea.isEmpty();
	}

	public SignalView<TileModel> onTileDiscarded() {
		return tileArea.onTileDiscarded();
	}

	@Override
	public int size() {
		return tileArea.width();
	}

	@Override
	public String asString() {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < size(); i++) {
			result.append(stringVersionOfPosition(i));
		}
		return result.toString();
	}

	private String stringVersionOfPosition(int i) {
		if (at(i).isOccupied())
			return at(i).tile().value();
		else
			return " ";
	}

	public void clear() {
		tileArea.clear();
	}

	public int countDigitsAndVariables() {
		return countIf(IS_A_DIGIT_OR_VARIABLE);
	}

	public int countOperators() {
		return countIf(IS_AN_OPERATOR);
	}

	private int countIf(Visitor<Boolean> isA) {
		int count = 0;
		for (TileModel t : tiles()) {
			if (t.accept(isA))
				count++;
		}
		return count;
	}

	private List<TileModel> tiles() {
		List<TileModel> tiles = Lists.newArrayList();
		for (int i = 0; i < size(); i++) {
			if (at(i).isOccupied())
				tiles.add(at(i).tile());
		}
		return tiles;
	}

	@Override
	public String toString() {
		return "Tray[" + asString() + "]";
	}

}
