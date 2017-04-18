package edu.bsu.pvgestwicki.etschallenge.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.pointer;
import playn.core.util.Clock;
import react.UnitSlot;
import tripleplay.game.Screen;
import tripleplay.game.ScreenStack;
import tripleplay.util.PointerInput;
import edu.bsu.pvgestwicki.etschallenge.core.model.GameModel;
import edu.bsu.pvgestwicki.etschallenge.core.view.GameView;

public class PlayingScreen extends Screen {

	private final PointerInput pointerInput = new PointerInput();
	private GameView gameView;
	private final ScreenStack screenStack;

	public PlayingScreen(final ScreenStack screenStack) {
		this.screenStack = checkNotNull(screenStack);

		pointer().setListener(pointerInput.plistener);
		startGame();
		configureRestarting();
	}

	private void configureRestarting() {
		gameView.onRestart().connect(new UnitSlot() {
			@Override
			public void onEmit() {
				screenStack.push(new PlayingScreen(screenStack),
						screenStack.slide());
			}
		});
	}

	private void startGame() {
		GameModel game = GameModel.create();
		gameView = new GameView(game, pointerInput);
		layer.add(gameView.layer());
	}

	@Override
	public void update(int deltaMS) {
		gameView.update(deltaMS);
	}

	@Override
	public void paint(Clock clock) {
		gameView.paint(clock);
	}

	@Override
	public void wasHidden() {
		screenStack.remove(this);
	}

}
