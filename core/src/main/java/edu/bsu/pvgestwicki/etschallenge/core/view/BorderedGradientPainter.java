package edu.bsu.pvgestwicki.etschallenge.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import playn.core.Canvas;
import playn.core.Gradient;

public final class BorderedGradientPainter {

	public static Builder withGradient(Gradient gradient) {
		Builder builder = new Builder();
		builder.product.gradient = checkNotNull(gradient);
		return builder;
	}

	public static final class Builder {
		private BorderedGradientPainter product = new BorderedGradientPainter();

		private Builder() {
		}

		public Builder andBorderColor(int color) {
			product.borderColor = color;
			return this;
		}

		public Builder andThickness(float thickness) {
			product.thickness = thickness;
			return this;
		}

		public BorderedGradientPainter andRadius(float radius) {
			product.radius = radius;
			return product;
		}
	}

	private Gradient gradient;
	private int borderColor;
	private float thickness;
	private float radius;

	public void paint(Canvas canvas) {
		paintBorderAround(canvas);
		paintGradientInside(canvas);
	}

	private void paintBorderAround(Canvas canvas) {
		canvas.setFillColor(borderColor);
		fill(canvas);
	}

	private void fill(Canvas canvas) {
		canvas.fillRoundRect(0, 0, canvas.width(), canvas.height(), radius);
	}

	private void paintGradientInside(Canvas canvas) {
		canvas.setFillGradient(gradient);
		fillInsideToLeaveBorder(canvas);
	}

	private void fillInsideToLeaveBorder(Canvas canvas) {
		float twiceThickness = thickness * 2f;
		canvas.fillRoundRect(thickness, thickness, //
				canvas.width() - twiceThickness, //
				canvas.height() - twiceThickness, //
				radius);
	}
}
