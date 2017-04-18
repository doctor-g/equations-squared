package edu.bsu.pvgestwicki.etschallenge.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.graphics;
import react.SignalView;
import react.UnitSignal;
import react.UnitSlot;
import tripleplay.game.ScreenStack;
import tripleplay.game.UIScreen;
import tripleplay.ui.Background;
import tripleplay.ui.Button;
import tripleplay.ui.Element;
import tripleplay.ui.Group;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.Shim;
import tripleplay.ui.SimpleStyles;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AxisLayout;
import edu.bsu.pvgestwicki.etschallenge.core.view.Color;

public final class WarningScreen extends UIScreen {

	private static final String THE_MESSAGE = "It looks like you're not using a fully-supported browser. The game may work fine, or it may have some graphical glitches, or it may not work at all. It is recommended that you run the game on Google Chrome for best results.";

	private UnitSignal dismissed = new UnitSignal();
	private final ScreenStack screenStack;

	public WarningScreen(ScreenStack screenStack) {
		this.screenStack = checkNotNull(screenStack);
		createInterface();
	}

	private void createInterface() {
		Root root = iface
				.createRoot(AxisLayout.horizontal(), SimpleStyles.newSheet(),
						layer)//
				.setSize(graphics().width(), graphics().height())
				.addStyles(
						Style.BACKGROUND.is(Background.solid(Color.MED_GREY
								.intValue())));
		float shimSize = graphics().width() / 4;
		root.add(new Shim(shimSize, shimSize), //
				makeGroup(), //
				new Shim(shimSize, shimSize));
	}

	private Group makeGroup() {
		return new Group(AxisLayout.vertical())
				//
				.addStyles(
						Style.BACKGROUND.is(Background.bordered(
								Color.SIDEBAR_GREY.intValue(),
								Color.DARK_GREY.intValue(), 2f).inset(20f, 20f)))//
				.add(AxisLayout.stretch(new Label(THE_MESSAGE)//
						.addStyles(Style.TEXT_WRAP.is(true), //
								Style.HALIGN.left)), //
						new Shim(0, 50f), //
						makeOKButton());
	}

	private Element<?> makeOKButton() {
		Button b = new Button("OK");
		b.clicked().connect(new UnitSlot() {
			@Override
			public void onEmit() {
				dismissed.emit();
			}
		});
		return b;
	}

	public SignalView<Void> dismissed() {
		return dismissed;
	}

	@Override
	public void wasHidden() {
		screenStack.remove(this);
	}
}
