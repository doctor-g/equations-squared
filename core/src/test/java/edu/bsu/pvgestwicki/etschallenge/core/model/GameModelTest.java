package edu.bsu.pvgestwicki.etschallenge.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import playn.core.util.Callback;
import react.Slot;
import react.UnitSlot;
import edu.bsu.pvgestwicki.etschallenge.core.GameConfiguration;
import edu.bsu.pvgestwicki.etschallenge.core.math.Variable;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.DivideByZero;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.UnboundVariable;

public class GameModelTest {

	private GameModel game = GameModel.create();
	private BindingRequestHandler localBindingRequestHandler = null;
	private Slot<CommitResult> localCommitSlot = null;

	@SuppressWarnings("unchecked")
	private Slot<Move> moveSlot = (Slot<Move>) mock(Slot.class);
	private UnitSlot gameOverSlot = mock(UnitSlot.class);

	@Test
	public void testTrayIsEmptyAtStart() {
		assertTrue(game.tray().isEmpty());
	}

	@Test
	public void testFillTrayFillsTray() {
		startGame();
		assertTrue(game.tray().isFull());
	}

	private void startGame() {
		game.onGameOver().connect(gameOverSlot);
		game.start();
	}

	@Test
	public void testBoardIsEmptyToStart() {
		assertTrue(game.board().isEmpty());
	}

	@Test
	public void testMoveTileFromTrayPutsTileInRequestedSpot() {
		startGame();
		TileModel tile = game.tray().at(0).tile();
		game.moveTileFromTraySlot(0).toBoardPosition(0, 0);
		assertEquals(tile, game.board().at(0, 0).tile());
	}

	@Test
	public void testMoveTileFromTrayResultsInThatTraySlotBeingEmpty() {
		startGame();
		game.moveTileFromTraySlot(0).toBoardPosition(0, 0);
		assertFalse(game.tray().at(0).isOccupied());
	}

	@Test
	public void testMoveEqualsTileToBoardPutsItThere() {
		startGame();
		game.moveTileFromEqualsPoolToBoard(0, 0);
		assertIsEqualsTileAtBoardPosition(0, 0);
	}

	private void assertIsEqualsTileAtBoardPosition(int i, int j) {
		TileModel tile = game.board().at(i, j).tile();
		assertTrue(new EqualsTilePredicate().apply(tile));
	}

	@Test
	public void testMoveEqualsTileBackToPoolLeavesAnEmptySpaceBehind() {
		startGame();
		game.moveTileFromEqualsPoolToBoard(0, 0);
		game.moveTileFromBoard(0, 0).toEqualsPool();
		assertFalse(game.board().at(0, 0).isOccupied());
	}

	@Test(expected = IllegalStateException.class)
	public void testAttemptToMoveNonEqualsTileToEqualsPoolThrowsException() {
		startGame();
		game.moveTileFromTraySlot(0).toBoardPosition(0, 0);
		game.moveTileFromBoard(0, 0).toEqualsPool();
	}

	@Test
	public void testPlayingTileFromTrayToBoardFiresEvent() {
		startGameAndSetupMoveSlot();
		game.moveTileFromTraySlot(0).toBoardPosition(0, 0);
		verify(moveSlot).onEmit(any(Move.class));
	}

	@Test
	public void testPlayingEqualsTileFiresEvent() {
		startGameAndSetupMoveSlot();
		game.moveTileFromEqualsPoolToBoard(0, 0);
		verify(moveSlot).onEmit(any(Move.class));
	}

	private void startGameAndSetupMoveSlot() {
		startGame();
		game.onTileMoved().connect(moveSlot);
	}

	@Test(expected = IllegalStateException.class)
	public void testPlayEqualsTileIntoTrayThrowsException() {
		startGame();
		game.moveTileFromTraySlot(0).toBoardPosition(1, 0);
		game.moveTileFromEqualsPoolToBoard(0, 0);
		game.moveTileFromBoard(0, 0).toTray(0);
	}

	@Test(expected = IllegalStateException.class)
	public void testAccessingTurnBeforeGameStartsThrowsException() {
		game.turn();
	}

	@Test
	public void testTurnIsEmptyWhenTheGameStarts() {
		startGame();
		assertTrue(game.turn().isEmpty());
	}

	@Test
	public void testResetTurnResultsInEmptyBoard() {
		startGameAndMoveTileToBoardAndResetTurn();
		assertTrue(game.board().isEmpty());
	}

	private void startGameAndMoveTileToBoardAndResetTurn() {
		startGame();
		game.moveTileFromTraySlot(0).toBoardPosition(0, 0);
		game.resetTurn();
	}

	@Test
	public void testResetTurnPutsTileBack() {
		startGameAndMoveTileToBoardAndResetTurn();
		assertTrue(game.tray().at(0).isOccupied());
	}

	@Test
	public void testPuntChangesTrayContents() {
		startGame();
		String traySignatureBefore = game.tray().asString();
		game.punt();
		String traySignatureAfter = game.tray().asString();
		assertFalse(traySignatureAfter.equals(traySignatureBefore));
	}

	@Test
	public void testCommitIsDisabledBeforeStartOfGame() {
		assertFalse(game.commitAction().enabled().get());
	}

	@Test
	public void testCommitIsEnabledAfterPlayingOneTile() {
		startGame();
		game.moveTileFromTraySlot(0).toBoardPosition(0, 0);
		assertTrue(game.commitAction().enabled().get());
	}

	@Test
	public void testCommitIsDisabledAfterPlayingAndResetting() {
		startGameAndMoveTileToBoardAndResetTurn();
		assertFalse(game.commitAction().enabled().get());
	}

	@Test
	public void testTrayIsRefilledAfterSuccessfulPlay() {
		starrGameAndPlaySuccessfulTurn();
		assertEquals(GameConfiguration.instance().tilesAllowedInTray(), game
				.tray().size());
	}

	private void starrGameAndPlaySuccessfulTurn() {
		startGameAndPlayXOpXEqX("1+12");
	}

	private void startGameAndPlayXOpXEqX(String tiles) {
		startGameAndPlayXOpXEqX(tiles, 0);
	}
	
	private void startGameAndPlayXOpXEqX(String tiles, int fromColumn) {
		setupTrayAndStartGame(tiles);

		for (int i = 0; i < 3; i++) {
			game.moveTileFromTraySlot(i).toBoardPosition(i+fromColumn, 0);
		}
		game.moveTileFromEqualsPoolToBoard(3+fromColumn, 0);
		game.moveTileFromTraySlot(3).toBoardPosition(4+fromColumn, 0);
		game.commitTurn();
	}
	

	private void setupTrayAndStartGame(String tiles) {
		TrayModel tray = TrayModel.create();
		for (int i = 0; i < tiles.length(); i++) {
			TileModel tile = TileModelFactory.instance().forSymbol(
					tiles.charAt(i));
			tray.at(i).place(tile);
		}

		game = GameModel.withTray(tray).create();

		if (localBindingRequestHandler != null)
			game.setBindingRequestHandler(localBindingRequestHandler);

		if (localCommitSlot != null)
			game.onCommit().connect(localCommitSlot);
		startGame();
	}

	private void startGameAndPlayXEqXOpX(String tiles) {
		setupTrayAndStartGame(tiles);

		game.moveTileFromTraySlot(0).toBoardPosition(0, 0);
		game.moveTileFromEqualsPoolToBoard(1, 0);
		for (int i = 2; i < 5; i++) {
			game.moveTileFromTraySlot(i - 1).toBoardPosition(i, 0);
		}
		game.commitTurn();
	}

	@Test
	public void testCommitIsDisabledAfterSuccessfulPlay() {
		starrGameAndPlaySuccessfulTurn();
		assertFalse(game.commitAction().enabled().get());
	}

	@Test
	public void testGameOverIsFiredWhenThereAreNotEnoughDigitTilesToRefillTray() {
		startGameAndPuntUntilGameOver();
		verify(gameOverSlot).onEmit();
	}

	private void startGameAndPuntUntilGameOver() {
		startGame();
		puntUntilThereAreNotEnoughDigitsLeftToFillTray();
		game.punt();
	}

	private void puntUntilThereAreNotEnoughDigitsLeftToFillTray() {
		while (game.filler().digitsRemaining().get() >= GameConfiguration
				.instance().digitsInTray())
			game.punt();
	}

	@Test
	public void testScoreStartsAtZero() {
		startGame();
		assertEquals(0, score());
	}

	private int score() {
		return game.score().intValue();
	}

	@Test
	public void testScoreChangesOnSuccessfulCommit() {
		starrGameAndPlaySuccessfulTurn();
		assertTrue(score() > 0);
	}

	@Test
	public void testClearIsDisabledAfterSuccessfulPlay() {
		starrGameAndPlaySuccessfulTurn();
		assertFalse(game.resetTurnAction().enabled().get());
	}

	@Test
	public void testClearIsEnabledAfterFirstMoveOfSecondPlay() {
		starrGameAndPlaySuccessfulTurn();
		game.moveTileFromTraySlot(0).toBoardPosition(2, 2);
		assertTrue(game.resetTurnAction().enabled().get());
	}

	@Test
	public void testPuntDisabledAtGameover() {
		startGameAndPuntUntilGameOver();
		assertFalse(game.puntAction().enabled().get());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUnboundVariableEqDivByZeroResultsInUnboundThenDivByZero() {
		localCommitSlot = mock(Slot.class);
		localBindingRequestHandler = new BindingRequestHandler() {
			@Override
			public void request(Variable v, Callback<Integer> binder) {
				binder.onSuccess(0);
			}
		};
		startGameAndPlayXOpXEqX("1/0A");// 1/0=A
		assertResultIsUnboundThenDivideByZero();
	}

	private void assertResultIsUnboundThenDivideByZero() {
		ArgumentCaptor<CommitResult> captor = ArgumentCaptor
				.forClass(CommitResult.class);
		verify(localCommitSlot, times(2)).onEmit(captor.capture());
		List<CommitResult> results = captor.getAllValues();
		assertEquals(UnboundVariable.class, results.get(0).getClass());
		assertEquals(DivideByZero.class, results.get(1).getClass());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDivByZeroEqUnboundVariableIsUnboundThenDivByZero() {
		localCommitSlot = mock(Slot.class);
		localBindingRequestHandler = new BindingRequestHandler() {
			@Override
			public void request(Variable v, Callback<Integer> binder) {
				binder.onSuccess(0);
			}
		};
		startGameAndPlayXEqXOpX("A1/0");// A=1/0
		assertResultIsUnboundThenDivideByZero();
	}

	@Test
	public void testCommitIsDisabledAfterPlayingThenUnplacingATile() {
		startGame();
		game.moveTileFromTraySlot(0).toBoardPosition(0, 0);
		game.moveTileFromBoard(0, 0).toTray(0);
		assertFalse(game.commitAction().enabled().get());
	}
}