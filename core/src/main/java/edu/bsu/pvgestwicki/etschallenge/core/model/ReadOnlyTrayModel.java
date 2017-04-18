package edu.bsu.pvgestwicki.etschallenge.core.model;

import react.SignalView;

public interface ReadOnlyTrayModel {

	boolean isFull();

	TileArea.ReadOnlyCell at(int i);

	boolean isEmpty();

	int size();

	String asString();
	
	SignalView<TileModel> onTileDiscarded();
}
