package edu.bsu.pvgestwicki.etschallenge.java.image;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import edu.bsu.pvgestwicki.etschallenge.core.util.TileImageSheetConstants;
import edu.bsu.pvgestwicki.etschallenge.core.view.Color;
import edu.bsu.pvgestwicki.etschallenge.core.view.RGB;

public final class TileImageGenerator {

	private static final int TILE_WIDTH = 50;
	private static final int TILE_HEIGHT = 50;
	private static final float FONT_SIZE = 42;
	private static final float SHADOW_FONT_SIZE = 46;
	private static final String FONT_PATH = "src/main/java/edu/bsu/pvgestwicki/etschallenge/resources/text/DroidSansMono.ttf";
	private static final int ARC_RADIUS = 6;
	private static final String OUTPUT_PATH = "../core/src/main/java/edu/bsu/pvgestwicki/etschallenge/resources/images/tile_images.png";

	private final Font shadowFont;
	private final Font mainFont;

	private TileImageGenerator() throws Exception {
		Font rawFont = Font.createFont(Font.TRUETYPE_FONT, new File(FONT_PATH));
		mainFont = rawFont.deriveFont(FONT_SIZE);
		shadowFont = rawFont.deriveFont(SHADOW_FONT_SIZE);
	}

	private void createImage() throws Exception {
		final int numCharacters = TileImageSheetConstants.CHARACTERS.size();
		BufferedImage image = new BufferedImage(TILE_WIDTH * numCharacters,
				TILE_HEIGHT * 3, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		for (int i = 0; i < numCharacters; i++) {
			final char c = TileImageSheetConstants.CHARACTERS.get(i);

			AffineTransform transform = g.getTransform();
			g.translate(TILE_WIDTH * i, 0);
			makeRegularTile(g, c);
			g.setTransform(transform);

			g.translate(TILE_WIDTH * i, TILE_HEIGHT);
			makeSelectedTile(g, c);
			g.setTransform(transform);

			g.translate(TILE_WIDTH * i, TILE_HEIGHT * 2);
			makeFrozenTile(g, c);
			g.setTransform(transform);
		}

		ImageIO.write(image, "png", new File(OUTPUT_PATH));
	}

	private void makeRegularTile(Graphics2D g, char c) {
		fillGreyRectangle(g);
		
		final int inset = 2;
		
		java.awt.Color color1 = toAWTColor(Color.COMPLEMENTARY_BLUE);
		java.awt.Color color2 = new java.awt.Color(RGB.lighter(Color.COMPLEMENTARY_BLUE.intValue()));
		Paint p = new LinearGradientPaint(0, 0, TILE_WIDTH * 2 / 3,
				TILE_HEIGHT, new float[] { 0f, 0.2f, 0.8f, 1.0f },
				new java.awt.Color[] { color1, color2, color2, color1 });
		g.setPaint(p);
		g.fillRoundRect(inset, inset, TILE_WIDTH-2*inset, TILE_HEIGHT-2*inset, ARC_RADIUS, ARC_RADIUS);

		g.setColor(toAWTColor(Color.COMPLEMENTARY_BLUE).brighter());
		drawCentered(c, shadowFont, g);
		g.setColor(toAWTColor(Color.LIGHT_GREY));
		drawCentered(c, mainFont, g);
	}
	
	private void fillGreyRectangle(Graphics2D g) {
		g.setColor(toAWTColor(Color.DARK_GREY));
		g.fillRect(0,0,TILE_WIDTH, TILE_HEIGHT);
	}

	private void makeSelectedTile(Graphics2D g, char c) {
		fillGreyRectangle(g);
		
		final int inset = 2;
		
		java.awt.Color color1 = toAWTColor(Color.LIGHT_GREY);
		java.awt.Color color2 = new java.awt.Color(RGB.lighter(Color.LIGHT_GREY.intValue()));
		Paint p = new LinearGradientPaint(0, 0, TILE_WIDTH * 2 / 3,
				TILE_HEIGHT, new float[] { 0f, 0.2f, 0.8f, 1.0f },
				new java.awt.Color[] { color1, color2, color2, color1 });
		g.setPaint(p);
		g.fillRoundRect(inset, inset, TILE_WIDTH-inset*2, TILE_HEIGHT-inset*2, ARC_RADIUS, ARC_RADIUS);

		g.setColor(toAWTColor(Color.LIGHT_GREY).darker());
		drawCentered(c, shadowFont, g);
		g.setColor(toAWTColor(Color.COMPLEMENTARY_BLUE));
		drawCentered(c, mainFont, g);
	}

	private void makeFrozenTile(Graphics2D g, char c) {
		fillGreyRectangle(g);

		g.setColor(toAWTColor(Color.DARK_GREY).brighter());
		drawCentered(c, shadowFont, g);
		g.setColor(toAWTColor(Color.LIGHT_GREY));
		drawCentered(c, mainFont, g);
	}

	private void drawCentered(char c, Font font, Graphics2D g) {
		TextLayout layout = new TextLayout(String.valueOf(c), font,
				g.getFontRenderContext());
		layout.draw(g, (float) (TILE_WIDTH - layout.getVisibleAdvance()) / 2,
				(TILE_HEIGHT - layout.getDescent() + layout.getAscent()) / 2);
	}

	private java.awt.Color toAWTColor(Color complementaryBlue) {
		return new java.awt.Color(complementaryBlue.intValue());
	}

	public static void main(String[] args) throws Exception {
		new TileImageGenerator().createImage();
	}
}
