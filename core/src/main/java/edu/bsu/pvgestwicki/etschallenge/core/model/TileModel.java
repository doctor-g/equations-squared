package edu.bsu.pvgestwicki.etschallenge.core.model;

public interface TileModel {

	String value();

	<T> T accept(Visitor<T> visitor, Object... args);

	interface EqualsTileModel extends TileModel {
	}

	interface OperatorTileModel extends TileModel {
	}

	interface DigitTileModel extends TileModel {
	}

	interface VariableTileModel extends TileModel {
	}

	interface Visitor<T> {
		T visit(EqualsTileModel equalsTile, Object... args);

		T visit(OperatorTileModel operatorTile, Object... args);

		T visit(DigitTileModel digitTile, Object... args);

		T visit(VariableTileModel variableTile, Object... args);
		
		public static abstract class Abstract<T> implements Visitor<T> {

			@Override
			public T visit(EqualsTileModel equalsTile, Object... args) {
				return null;
			}

			@Override
			public T visit(OperatorTileModel operatorTile, Object... args) {
				return null;
			}

			@Override
			public T visit(DigitTileModel digitTile, Object... args) {
				return null;
			}

			@Override
			public T visit(VariableTileModel variableTile, Object... args) {
				return null;
			}
			
		}
	}
}
