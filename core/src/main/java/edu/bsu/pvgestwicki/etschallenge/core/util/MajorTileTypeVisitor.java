package edu.bsu.pvgestwicki.etschallenge.core.util;

import edu.bsu.pvgestwicki.etschallenge.core.model.TileModel;
import edu.bsu.pvgestwicki.etschallenge.core.model.TileModel.DigitTileModel;
import edu.bsu.pvgestwicki.etschallenge.core.model.TileModel.OperatorTileModel;
import edu.bsu.pvgestwicki.etschallenge.core.model.TileModel.VariableTileModel;

public abstract class MajorTileTypeVisitor<T> implements TileModel.Visitor<T>{

	@Override
	public final T visit(OperatorTileModel operatorTile, Object... args) {
		return visitNonEqualsTile(operatorTile, args);
	}

	@Override
	public T visit(DigitTileModel digitTile, Object... args) {
		return visitNonEqualsTile(digitTile, args);
	}

	@Override
	public T visit(VariableTileModel variableTile, Object... args) {
		return visitNonEqualsTile(variableTile, args);
	}
	
	protected abstract T visitNonEqualsTile(TileModel aNonEqualsTileModel, Object... args);
	

}
