package edu.bsu.pvgestwicki.etschallenge.core.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Objects;

public final class Move {

	public static Builder tile(TileModel tile) {
		return new Builder(tile);
	}

	public static final class Builder {
		private final TileModel tile;
		private Place source;

		private Builder(TileModel tile) {
			this.tile = checkNotNull(tile);
		}

		public Builder from(Place source) {
			this.source = checkNotNull(source);
			return this;
		}

		public Move to(Place destination) {
			checkState(source != null, "Source must be specified first.");
			return new Move(tile, source, destination);
		}
	}

	private final TileModel tile;
	private final Place source;
	private final Place destination;

	private Move(TileModel tile, Place source, Place dest) {
		this.tile = tile;
		this.source = checkNotNull(source);
		this.destination = checkNotNull(dest);
	}

	public TileModel tile() {
		return tile;
	}

	public Place source() {
		return source;
	}

	public Place destination() {
		return destination;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("source", source)
				.add("destination", destination).toString();
	}

}
