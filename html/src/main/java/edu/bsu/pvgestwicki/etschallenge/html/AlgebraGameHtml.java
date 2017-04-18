package edu.bsu.pvgestwicki.etschallenge.html;

import playn.core.PlayN;
import playn.html.HtmlGame;
import playn.html.HtmlPlatform;
import edu.bsu.pvgestwicki.etschallenge.core.AlgebraGame;
import edu.bsu.pvgestwicki.etschallenge.core.util.FontLoader;
import edu.bsu.pvgestwicki.etschallenge.core.view.GameFontName;

public final class AlgebraGameHtml extends HtmlGame {

	public static native String getUserAgent() /*-{
		return navigator.userAgent;
	}-*/;

	private FontLoader[] fontLoaders;
	private AlgebraGame game;

	@Override
	public void start() {
		configureHTMLPlatform();
		makeFontLoaders();
		startGame();
	}

	private void startGame() {
		AlgebraGame.Builder builder = new AlgebraGame.Builder()//
				.withFontLoaders(fontLoaders)//
				.withUserAgentString(getUserAgent());
		AlgebraGame game = builder.build();
		PlayN.run(game);
	}

	private void configureHTMLPlatform() {
		HtmlPlatform platform = HtmlPlatform.register();
		platform.assets().setPathPrefix("algebra/");
	}

	private void makeFontLoaders() {
		GameFontName[] gameFontNames = GameFontName.values();
		fontLoaders = new FontLoader[gameFontNames.length];
		for (int i = 0; i < fontLoaders.length; i++) {
			fontLoaders[i] = WebFontLoader.forFont(gameFontNames[i].toString());
		}
	}
}
