package edu.bsu.pvgestwicki.etschallenge.core.view;

import static com.google.common.base.Preconditions.checkNotNull;

public final class SingleTileSelectionModel {

	private TileView selection;
	
	public void select(TileView tileView) {
		if (selection!=null)
			selection.unselect();
		this.selection = checkNotNull(tileView);
		this.selection.select();
	}
	
	public TileView selection() { 
		return selection;
	}

	public void clear() {
		if (selection!=null)
			selection.unselect();
		selection=null;
	}
	
}
