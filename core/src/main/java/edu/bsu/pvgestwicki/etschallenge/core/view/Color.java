package edu.bsu.pvgestwicki.etschallenge.core.view;

public enum Color {

	BROWN(161,114,80),
	SIDEBAR_GREY(150,150,150),
	SIDEBAR_HIGHLIGHT(180,180,180),
	
	DULL_ORANGE(230,138,0),
	LIGHT_GREY(255,244,227),
	DARK_GREY(33,32,30),
	MED_GREY(110,105,98),
	COMPLEMENTARY_BLUE(0,92,230);
	
	private final int theColor;
	
	private Color(int r, int g, int b) {
		this.theColor=playn.core.Color.rgb(r, g, b);
	}
	
	public int intValue() { return theColor; }
}
