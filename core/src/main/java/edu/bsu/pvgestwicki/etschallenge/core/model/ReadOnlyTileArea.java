package edu.bsu.pvgestwicki.etschallenge.core.model;

import java.util.Set;

import pythagoras.i.IDimension;
import pythagoras.i.IPoint;
import react.ValueView;
import edu.bsu.pvgestwicki.etschallenge.core.model.TileArea.Cell;

public interface ReadOnlyTileArea {

	public interface ReadOnlyCell {

		boolean isOccupied();

		TileModel tile();

		ValueView<TileModel> value();

	}

	IDimension size();

	Cell at(int i, int j);

	Cell at(IPoint p);

	boolean isFull();

	int width();

	int height();

	boolean isEmpty();

	boolean contains(int i, int j);

	boolean contains(IPoint p);
	
	public Set<IPoint> occupiedPoints();
}