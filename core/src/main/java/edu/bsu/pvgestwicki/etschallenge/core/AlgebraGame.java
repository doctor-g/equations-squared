package edu.bsu.pvgestwicki.etschallenge.core;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import playn.core.Game;
import playn.core.PlayN;
import playn.core.util.Clock;
import react.UnitSlot;
import tripleplay.anim.Animator;
import tripleplay.game.ScreenStack;
import tripleplay.util.Logger;
import edu.bsu.pvgestwicki.etschallenge.core.event.EventQueue;
import edu.bsu.pvgestwicki.etschallenge.core.util.FontLoader;
import edu.bsu.pvgestwicki.etschallenge.core.view.GameImage;

public final class AlgebraGame extends Game.Default {

	private static final int UPDATE_RATE = 30;
	private static final EventQueue eventQueue = EventQueue.create();

	public static EventQueue eventQueue() {
		return eventQueue;
	}

	public static class Builder {
		private FontLoader[] fontLoaders;
		private String userAgentString;

		public Builder withFontLoaders(FontLoader... fontLoaders) {
			this.fontLoaders = checkNotNull(fontLoaders);
			return this;
		}

		public Builder withUserAgentString(String userAgentString) {
			this.userAgentString = checkNotNull(userAgentString);
			return this;
		}

		public AlgebraGame build() {
			return new AlgebraGame(userAgentString, fontLoaders);
		}
	}

	private final Clock.Source clock = new Clock.Source(UPDATE_RATE);
	private Animator animator = GameConfiguration.instance().animator();
	private LoadingScreen loadingScreen;
	private final AssetLoadingTracker assetLoader;
	private @Nullable final String userAgentString;

	private final ScreenStack screenStack = new ScreenStack() {
		@Override
		protected void handleError(RuntimeException error) {
			PlayN.log().warn("Screen failure", error);
		}

	};

	private AlgebraGame(@Nullable String userAgentString,
			FontLoader... fontLoaders) {
		super(UPDATE_RATE);
		Logger.setImpl(new Logger.PlayNImpl());
		assetLoader = AssetLoadingTracker.builder()//
				.withFontLoader(fontLoaders)//
				.withImages(GameImage.values())//
				.build();
		this.userAgentString = userAgentString;
	}

	@Override
	public void init() {
		if (needsAWarning()) {
			goToWarningScreen();
		} else {
			goToLoadingScreen();
		}
	}

	private void goToWarningScreen() {
		final WarningScreen warningScreen = new WarningScreen(screenStack);
		warningScreen.dismissed().connect(new UnitSlot() {
			@Override
			public void onEmit() {
				goToLoadingScreenWithTransition();
			}
		});
		screenStack.push(warningScreen);
	}

	private void goToLoadingScreen() {
		loadingScreen = new LoadingScreen(screenStack, assetLoader);
		screenStack.push(loadingScreen);
	}

	private void goToLoadingScreenWithTransition() {
		loadingScreen = new LoadingScreen(screenStack, assetLoader);
		screenStack.push(loadingScreen, screenStack.slide());
	}

	private boolean needsAWarning() {
		return userAgentString != null
				&& userAgentString.toLowerCase().contains("msie");
	}

	@Override
	public void update(int deltaMS) {
		clock.update(deltaMS);
		while (!eventQueue.isEmpty())
			eventQueue.dequeue().handle();

		screenStack.update(deltaMS);
	}


	@Override
	public void paint(float alpha) {
		clock.paint(alpha);
		screenStack.paint(clock);
		animator.paint(clock);
	}

}
