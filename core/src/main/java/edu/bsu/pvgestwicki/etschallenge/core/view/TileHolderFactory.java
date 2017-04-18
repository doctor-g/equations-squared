package edu.bsu.pvgestwicki.etschallenge.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.graphics;
import playn.core.CanvasImage;
import playn.core.Gradient;
import playn.core.ImageLayer;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;

public class TileHolderFactory {

	public static TileHolderFactory forSize(IDimension size) {
		TileHolderFactory factory = new TileHolderFactory();
		factory.tileSize = new Dimension(size);
		return factory;
	}

	private Dimension tileSize;
	private CanvasImage cachedImage;
	private Style style = Style.FLAT;

	private TileHolderFactory() {
	}

	public void setTileSize(IDimension size) {
		checkNotNull(size);
		this.tileSize.setSize(size);
		createCachedImage();
	}

	private void createCachedImage() {
		cachedImage = style.createImage(tileSize);
	}

	public ImageLayer createImageLayer() {
		if (cachedImage == null)
			createCachedImage();
		return graphics().createImageLayer(cachedImage);
	}

	public TileHolderFactory outlineStyle() {
		this.style = Style.OUTLINE;
		return this;
	}

	private enum Style {
		FLAT {
			@Override
			public CanvasImage createImage(IDimension size) {
				CanvasImage image = graphics().createImage(size.width(),
						size.height());
				image.canvas().setFillColor(Color.DULL_ORANGE.intValue());
				image.canvas().fillRect(0, 0, image.width(), image.height());
				return image;
			}

		},
		OUTLINE {
			@Override
			public CanvasImage createImage(IDimension size) {
				CanvasImage image = graphics().createImage(size.width(),
						size.height());
				Gradient gradient = graphics().createLinearGradient(
						0,
						0,
						size.width() / 2,
						size.height(),
						new int[] { playn.core.Color.rgb(0, 0, 0),
								Color.DARK_GREY.intValue(),
								playn.core.Color.rgb(0, 0, 0)},
						new float[] { 0f, 0.3f, 0.7f });

				BorderedGradientPainter.withGradient(gradient)
						.andBorderColor(Color.MED_GREY.intValue())
						.andThickness(2f).//
						andRadius(2f).//
						paint(image.canvas());

				return image;
			}

		};

		public abstract CanvasImage createImage(IDimension size);
	}
}
