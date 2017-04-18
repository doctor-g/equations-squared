package edu.bsu.pvgestwicki.etschallenge.core.view;

import static playn.core.PlayN.assets;
import playn.core.Image;

public enum GameImage {

	TILES("images/tile_images.png");
	
	public final Image image;
	
	private GameImage(String path) {
		this.image = assets().getImage(path);
	}
	
}
