package edu.bsu.pvgestwicki.etschallenge.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.graphics;
import playn.core.Font;
import react.ValueView.Listener;
import tripleplay.game.ScreenStack;
import tripleplay.game.UIAnimScreen;
import tripleplay.ui.Background;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.SimpleStyles;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AbsoluteLayout;
import edu.bsu.pvgestwicki.etschallenge.core.view.Color;

public class LoadingScreen extends UIAnimScreen {

	private static final String[] texts = { "Loading..\u00A0", "Loading ..",
			"Loading. ." };

	private final ScreenStack screenStack;
	private boolean transitionStarted = false;
	private final AssetLoadingTracker assetLoader;

	public LoadingScreen(ScreenStack screenStack,
			AssetLoadingTracker assetLoader) {
		this.assetLoader = checkNotNull(assetLoader);
		this.screenStack = checkNotNull(screenStack);

		assetLoader.assetsRemaining().connect(new Listener<Integer>() {
			@Override
			public void onChange(Integer value, Integer oldValue) {
				synchronized (LoadingScreen.this) {
					if (value == 0 && !transitionStarted) {
						flip();
					}
				}
			}
		});

		Font font = graphics().createFont("sans", Font.Style.PLAIN, 48);
		final Label loadingLabel = new Label("Loading...")//
				.addStyles(Style.FONT.is(font),
						Style.COLOR.is(Color.LIGHT_GREY.intValue()),
						Style.HALIGN.center);

		Root root = iface.createRoot(new AbsoluteLayout(),
				SimpleStyles.newSheet(), layer)//
				.setStyles(
						Style.BACKGROUND.is(Background.solid(Color.MED_GREY
								.intValue())));
		root.setSize(graphics().width(), graphics().height());
		root.add(AbsoluteLayout.at(loadingLabel, 170, 320));

		anim.repeat(layer).delay(500f).then().action(new Runnable() {
			int index = -1;

			@Override
			public void run() {
				index = (index + 1) % texts.length;
				loadingLabel.text.update(texts[index]);
			}
		});
	}

	private void flip() {
		transitionStarted = true;
		// TODO: put transition back in
		screenStack.push(new PlayingScreen(screenStack), screenStack.slide());
	}

	@Override
	public void update(int deltaMS) {
		super.update(deltaMS);
		flipInCaseAllTheAssetsLoadedBeforeThisClassWasInitialized();
	}

	private synchronized void flipInCaseAllTheAssetsLoadedBeforeThisClassWasInitialized() {
		if (!transitionStarted && assetLoader.assetsRemaining().get() == 0)
			flip();
	}

	@Override
	public void wasHidden() {
		screenStack.remove(this);
	}
}
