package edu.bsu.pvgestwicki.etschallenge.core.view;

import static playn.core.PlayN.graphics;
import playn.core.Font;

public enum GameFontName {

	SANS("Droid Sans"), MONO("Droid Sans Mono");

	private final String name;

	private GameFontName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
	
	public Font createFont(Font.Style style, float size) {
		return graphics().createFont(name, style, size);
	}
}
