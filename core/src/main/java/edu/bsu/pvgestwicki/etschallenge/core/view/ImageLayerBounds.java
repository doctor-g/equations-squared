package edu.bsu.pvgestwicki.etschallenge.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import playn.core.ImageLayer;
import pythagoras.f.IRectangle;

public final class ImageLayerBounds {

	public static final class Placer {
		private final ImageLayer layer;

		private Placer(ImageLayer layer) {
			this.layer = checkNotNull(layer);
		}

		public void to(float x, float y, float w, float h) {
			layer.setTranslation(x, y);
			layer.setSize(w, h);
		}

		public final void to(IRectangle tileBounds) {
			to(tileBounds.x(), tileBounds.y(), tileBounds.width(), tileBounds.height());
		}
	}

	public static Placer set(ImageLayer image) {
		return new Placer(image);
	}

}
