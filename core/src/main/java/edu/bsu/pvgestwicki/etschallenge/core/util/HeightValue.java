package edu.bsu.pvgestwicki.etschallenge.core.util;

import static com.google.common.base.Preconditions.checkNotNull;
import playn.core.ImageLayer;
import tripleplay.anim.Animation.Value;

public final class HeightValue implements Value {
	private final ImageLayer layer;

	public HeightValue(ImageLayer layer) {
		this.layer = checkNotNull(layer);
	}

	@Override
	public float initial() {
		return layer.height();
	}

	@Override
	public void set(float value) {
		layer.setHeight(value);
	}
}