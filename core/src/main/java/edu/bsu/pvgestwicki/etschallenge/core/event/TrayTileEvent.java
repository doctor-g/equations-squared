package edu.bsu.pvgestwicki.etschallenge.core.event;

import static com.google.common.base.Preconditions.checkNotNull;
import edu.bsu.pvgestwicki.etschallenge.core.model.TileModel;

public final class TrayTileEvent {
	
	private final TileModel tile;
	private final int pos;
	
	public TrayTileEvent(TileModel tile, int pos) {
		this.tile=checkNotNull(tile);
		this.pos=pos;
	}

	public TileModel tile() { return tile; }
	
	public int position() { return pos; }
}
