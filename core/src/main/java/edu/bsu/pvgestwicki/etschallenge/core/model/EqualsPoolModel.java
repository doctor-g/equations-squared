package edu.bsu.pvgestwicki.etschallenge.core.model;

import static com.google.common.base.Preconditions.checkState;
import react.Signal;
import react.SignalView;

public final class EqualsPoolModel implements ReadOnlyEqualsPoolModel {

	public static EqualsPoolModel create() {
		return new EqualsPoolModel();
	}
	
	private final Signal<TileModel> onNewTopTile = Signal.create();

	private TileModel top;
	
	private EqualsPoolModel() {}

	public TileModel take() {
		checkState(top!=null, "Must initialize equals pool first");
		TileModel oldTop = top;
		makeTopTile();
		return oldTop;
	}

	public void start() {
		checkState(top==null);
		makeTopTile();
	}
	
	private void makeTopTile() {
		top = TileModelFactory.instance().forSymbol('=');
		onNewTopTile.emit(top);
	}
	
	@Override
	public SignalView<TileModel> onNewTopTile() {
		return onNewTopTile;
	}

	@Override
	public TileModel top() {
		return top;
	}
}
