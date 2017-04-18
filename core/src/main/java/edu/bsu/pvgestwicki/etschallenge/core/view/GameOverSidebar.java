package edu.bsu.pvgestwicki.etschallenge.core.view;

import static playn.core.PlayN.graphics;
import playn.core.GroupLayer;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import react.SignalView;
import tripleplay.ui.Button;
import tripleplay.ui.Interface;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.Shim;
import tripleplay.ui.SimpleStyles;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AxisLayout;

public final class GameOverSidebar implements Sidebar {

	private static final IDimension FIXED_SIZE = new Dimension(180, 486);

	public static GameOverSidebar createOn(Interface iface) {
		return new GameOverSidebar(iface);
	}

	private final GroupLayer layer;

	private Button playAgainButton;
	private Button viewSummaryButton;

	private GameOverSidebar(Interface iface) {
		layer = graphics().createGroupLayer();

		Root root = iface
				.createRoot(AxisLayout.vertical(), SimpleStyles.newSheet(),
						layer)//
				.setSize(FIXED_SIZE.width(), FIXED_SIZE.height())
				.addStyles(
						Style.BACKGROUND.is(BorderedGradientBackground
								.withDefaultsFor(FIXED_SIZE)));

		root.add(new Label("Game Over"), //
				new Shim(0, 12), //
				viewSummaryButton = new Button("View Summary"), //
				playAgainButton = new Button("Play Again"));
	}

	public GroupLayer layer() {
		return layer;
	}

	public SignalView<Button> playAgainClicked() {
		return playAgainButton.clicked();
	}

	public SignalView<Button> viewSummaryClicked() {
		return viewSummaryButton.clicked();
	}

	public void disableButtons() {
		playAgainButton.setEnabled(false);
		viewSummaryButton.setEnabled(false);
	}

	public void enableButtons() {
		playAgainButton.setEnabled(true);
		viewSummaryButton.setEnabled(true);
	}
}
