package edu.bsu.pvgestwicki.etschallenge.core.model;

import edu.bsu.pvgestwicki.etschallenge.core.model.TileModel.DigitTileModel;
import edu.bsu.pvgestwicki.etschallenge.core.model.TileModel.EqualsTileModel;
import edu.bsu.pvgestwicki.etschallenge.core.model.TileModel.OperatorTileModel;
import edu.bsu.pvgestwicki.etschallenge.core.model.TileModel.VariableTileModel;
import edu.bsu.pvgestwicki.etschallenge.core.util.SpecialChar;

public final class TileModelFactory {

	private static final TileModelFactory INSTANCE = new TileModelFactory();

	public static TileModelFactory instance() {
		return INSTANCE;
	}

	private TileModelFactory() {
	}

	public TileModel forSymbol(char c) {
		if (Character.isDigit(c))
			return new DigitTileImpl(c);
		else if (Character.isLetter(c))
			return new VariableTileImpl(c);
		else
			return arithmetic(c);
	}

	private TileModel arithmetic(char c) {
		switch (c) {
		case '=':
			return new EqualsTileImpl();
		case '+':
		case '-':
			return new OperatorTileImpl(c);
		case '*':
			return new OperatorTileImpl(SpecialChar.MULTIPLICATION_SIGN.asCharacter());
		case '/':
			return new OperatorTileImpl(SpecialChar.OBELUS.asCharacter());
		default:
			throw new IllegalArgumentException("Unrecognized character: " + c);
		}
	}

	private static abstract class AbstractTileModel implements TileModel {
		protected final String value;

		public AbstractTileModel(char value) {
			this.value = "" + value;
		}

		@Override
		public String value() {
			return value;
		}
	}

	private static final class DigitTileImpl extends AbstractTileModel
			implements DigitTileModel {
		DigitTileImpl(char digit) {
			super(digit);
		}

		@Override
		public <T> T accept(Visitor<T> visitor, Object... args) {
			return visitor.visit(this,args);
		}
	}

	private static final class VariableTileImpl extends AbstractTileModel
			implements VariableTileModel {
		VariableTileImpl(char var) {
			super(var);
		}

		@Override
		public <T> T accept(Visitor<T> visitor, Object... args) {
			return visitor.visit(this,args);
		}
	}

	private static final class EqualsTileImpl extends AbstractTileModel
			implements EqualsTileModel {
		EqualsTileImpl() {
			super('=');
		}
		
		@Override
		public <T> T accept(Visitor<T> visitor, Object... args) {
			return visitor.visit(this,args);
		}
	}

	private static final class OperatorTileImpl extends AbstractTileModel
			implements OperatorTileModel {
		public OperatorTileImpl(char operator) {
			super(operator);
		}
		
		@Override
		public <T> T accept(Visitor<T> visitor, Object... args) {
			return visitor.visit(this,args);
		}
	}

}
