package edu.bsu.pvgestwicki.etschallenge.core.view;

import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import playn.core.Image;
import pythagoras.f.Dimension;

import com.google.common.collect.ImmutableMap;

import edu.bsu.pvgestwicki.etschallenge.core.util.TileImageSheetConstants;

public final class TileImageFactory {

	private static final TileImageFactory SINGLETON = new TileImageFactory();

	private static final Dimension TILE_SIZE = new Dimension(50, 50);
	private static final int PLAIN_ROW = 0;
	private static final int SELECTED_ROW = 1;
	private static final int FROZEN_ROW = 2;

	private final Map<Character, Integer> columnMap = createColumnMap();

	public static TileImageFactory instance() {
		return SINGLETON;
	}

	private final Image image = GameImage.TILES.image;

	private TileImageFactory() {
	}

	private static Map<Character, Integer> createColumnMap() {
		ImmutableMap.Builder<Character, Integer> builder = ImmutableMap
				.builder();
		for (int i = 0; i < TileImageSheetConstants.CHARACTERS.size(); i++) {
			Character c = TileImageSheetConstants.CHARACTERS.get(i);
			builder.put(c, i);
		}
		return builder.build();
	}

	public Image selectedImage(char c) {
		return tileSubimage(c, SELECTED_ROW);
	}

	public Image plainImage(char c) {
		return tileSubimage(c, PLAIN_ROW);
	}

	public Image frozenImage(char c) {
		return tileSubimage(c, FROZEN_ROW);
	}

	private Image tileSubimage(char c, int row) {
		int col = columnFor(c);
		return image.subImage(col * TILE_SIZE.width, row * TILE_SIZE.height,
				TILE_SIZE.width, TILE_SIZE.height);
	}

	private int columnFor(char c) {
		checkState(columnMap != null);
		return columnMap.get(c);
	}
}
