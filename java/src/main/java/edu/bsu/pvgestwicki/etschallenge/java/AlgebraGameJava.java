package edu.bsu.pvgestwicki.etschallenge.java;

import playn.core.PlayN;
import playn.core.util.Callback;
import playn.java.JavaPlatform;
import edu.bsu.pvgestwicki.etschallenge.core.AlgebraGame;
import edu.bsu.pvgestwicki.etschallenge.core.util.FontLoader;

public final class AlgebraGameJava {

	private static final String[][] FONTS = {
			{ "Droid Sans", "text/DroidSans.ttf" },
			{ "Droid Sans Mono", "text/DroidSansMono.ttf" } };

	private static JavaPlatform platform;

	public static void main(String[] args) {
		JavaPlatform.Config config = new JavaPlatform.Config();
		config.width = 800;
		config.height = 600;
		platform = JavaPlatform.register(config);
		registerFonts();
		startGame();
	}

	private static void startGame() {
		FontLoader[] fontLoaders = makeFontLoaders();
		AlgebraGame game = new AlgebraGame.Builder()//
				.withFontLoaders(fontLoaders)//
				.build();
		PlayN.run(game);
	}

	private static FontLoader[] makeFontLoaders() {
		FontLoader[] loaders = new FontLoader[FONTS.length];
		for (int i = 0; i < FONTS.length; i++) {
			final int index = i;
			loaders[i] = new FontLoader() {
				@Override
				public void addCallback(Callback<? super String> callback) {
					callback.onSuccess(FONTS[index][0]);
				}
			};
		}
		return loaders;
	}

	private static void registerFonts() {
		for (String[] pair : FONTS) {
			platform.graphics().registerFont(pair[0], pair[1]);
		}
	}
}
