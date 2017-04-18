package edu.bsu.pvgestwicki.etschallenge.core.view;

import playn.core.Color;

public final class RGB {

	private static int r, g, b;

	public static int lighter(int color) {
		return disassembleScaleAndReassemble(color, 1.2f);
	}

	public static int darker(int color) {
		return disassembleScaleAndReassemble(color, 0.2f);
	}

	private static int disassembleScaleAndReassemble(int color, float factor) {
		extractComponents(color);
		multiplyComponentsBy(factor);
		return reassemble();
	}

	private static int reassemble() {
		return Color.rgb(r,g,b);
	}

	private static void multiplyComponentsBy(float f) {
		r = Math.min((int) (r * f), 0xff);
		g = Math.min((int) (g * f), 0xff);
		b = Math.min((int) (b * f), 0xff);
	}

	private static void extractComponents(int color) {
		r = (color >> 16) & 0xff;
		g = (color >> 8) & 0xff;
		b = color & 0xff;
	}
}
