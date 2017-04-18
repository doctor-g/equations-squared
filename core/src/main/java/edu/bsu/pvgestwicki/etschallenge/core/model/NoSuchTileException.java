package edu.bsu.pvgestwicki.etschallenge.core.model;

import java.util.NoSuchElementException;

public final class NoSuchTileException extends NoSuchElementException {
	private static final long serialVersionUID = 7868071434937322123L;

	public NoSuchTileException(int slotNumber) {
		super("" + slotNumber);
	}

}
