package edu.bsu.pvgestwicki.etschallenge.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.graphics;
import playn.core.CanvasImage;
import playn.core.Gradient;
import playn.core.ImageLayer;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import tripleplay.ui.Background;

public final class BorderedGradientBackground extends Background {

	public static BorderedGradientBackground withDefaultsFor(IDimension size) {
		Gradient gradient = graphics().createLinearGradient(
				0,
				0,
				0,
				size.height(),
				new int[] { Color.SIDEBAR_GREY.intValue(),
						Color.SIDEBAR_HIGHLIGHT.intValue(),
						Color.SIDEBAR_GREY.intValue() },
				new float[] { 0.05f, 0.15f, 0.4f });

		return withGradient(gradient);
	}

	public static BorderedGradientBackground withGradient(Gradient gradient) {
		BorderedGradientPainter painter = BorderedGradientPainter//
				.withGradient(gradient)//
				.andBorderColor(Color.DARK_GREY.intValue())//
				.andThickness(2f)//
				.andRadius(2f);

		return withPainter(painter);
	}
	
	public static BorderedGradientBackground withPainter(BorderedGradientPainter painter) {
		return new BorderedGradientBackground(painter);
	}

	private BorderedGradientPainter painter;

	private BorderedGradientBackground(BorderedGradientPainter painter) {
		super();
		this.painter = checkNotNull(painter);
	}

	@Override
	protected Instance instantiate(IDimension size) {
		CanvasImage canvasImage = graphics().createImage(size.width(),
				size.height());
		painter.paint(canvasImage.canvas());
		ImageLayer imageLayer = graphics().createImageLayer(canvasImage);
		return new LayerInstance(new Dimension(size.width(), size.height()), imageLayer);
	}

}
