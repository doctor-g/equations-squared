package edu.bsu.pvgestwicki.etschallenge.core.model;

import react.SignalView;

public interface ReadOnlyEqualsPoolModel {

	SignalView<TileModel> onNewTopTile();

	TileModel top();

}