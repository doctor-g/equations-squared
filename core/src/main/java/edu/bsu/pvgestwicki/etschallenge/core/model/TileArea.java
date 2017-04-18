package edu.bsu.pvgestwicki.etschallenge.core.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;
import java.util.Set;

import pythagoras.i.Dimension;
import pythagoras.i.IDimension;
import pythagoras.i.IPoint;
import pythagoras.i.Point;
import react.Signal;
import react.SignalView;
import react.Value;
import react.ValueView;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public final class TileArea implements ReadOnlyTileArea {

	public static final class Cell implements ReadOnlyCell {

		private final Value<TileModel> tileValue = Value.create(null);

		public void place(TileModel tile) {
			checkArgument(tile != null, "Use clear() instead.");
			if (isOccupied())
				throw new TileAlreadyPresentException();
			tileValue.update(tile);
		}

		@Override
		public boolean isOccupied() {
			return tileValue.get() != null;
		}

		@Override
		public TileModel tile() {
			return tileValue.get();
		}

		@Override
		public ValueView<TileModel> value() {
			return tileValue;
		}

		public void clear() {
			if (isOccupied())
				tileValue.update(null);
		}

		public TileModel removeTile() {
			checkState(isOccupied());
			TileModel tile = tile();
			clear();
			return tile;
		}
	}

	public static TileArea create(int i, int j) {
		return new TileArea(i, j);
	}

	private final Dimension size;
	private Map<IPoint, Cell> map = Maps.newHashMap();
	private Signal<TileModel> onTileDiscarded = Signal.create();

	private TileArea(int w, int h) {
		size = new Dimension(w, h);
		initBoard();
	}

	private void initBoard() {
		for (int i = size.width() - 1; i >= 0; i--) {
			for (int j = size.height() - 1; j >= 0; j--) {
				initCell(i, j);
			}
		}
	}

	private void initCell(int x, int y) {
		Point p = new Point(x, y);
		Cell c = new Cell();
		map.put(p, c);
	}

	@Override
	public IDimension size() {
		return size;
	}

	@Override
	public Cell at(int i, int j) {
		return map.get(new Point(i, j));
	}

	@Override
	public final Cell at(IPoint p) {
		return at(p.x(), p.y());
	}

	@Override
	public boolean isFull() {
		for (IPoint p : map.keySet()) {
			if (!at(p).isOccupied())
				return false;
		}
		return true;
	}

	@Override
	public final int width() {
		return size.width;
	}

	@Override
	public final int height() {
		return size.height;
	}

	@Override
	public boolean isEmpty() {
		for (IPoint p : map.keySet()) {
			if (at(p).isOccupied())
				return false;
		}
		return true;
	}

	public void clear() {
		for (Cell cell : map.values()) {
			clearCell(cell);
		}
	}

	private void clearCell(Cell cell) {
		if (cell.isOccupied()) {
			clearOccupiedCell(cell);
		}
	}

	private void clearOccupiedCell(Cell cell) {
		TileModel tile = cell.tile();
		cell.clear();
		onTileDiscarded.emit(tile);
	}

	public SignalView<TileModel> onTileDiscarded() {
		return onTileDiscarded;
	}

	@Override
	public boolean contains(int i, int j) {
		return i >= 0 && j >= 0 && i < size.width() && j < size.height();
	}

	@Override
	public final boolean contains(IPoint p) {
		return contains(p.x(), p.y());
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("\n");
		for (int y = 0; y < height(); y++) {
			for (int x = 0; x < width(); x++) {
				Cell cell = at(x, y);
				if (cell.isOccupied())
					sb.append(cell.tile().value());
				else
					sb.append("\u25a1");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	@Override
	public Set<IPoint> occupiedPoints() {
		return Sets.filter(map.keySet(), occupied);
	}

	private final Predicate<IPoint> occupied = new Predicate<IPoint>() {
		@Override
		public boolean apply(IPoint input) {
			return at(input).isOccupied();
		}
	};

}
