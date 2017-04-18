package edu.bsu.pvgestwicki.etschallenge.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import pythagoras.i.IPoint;
import pythagoras.i.Point;
import edu.bsu.pvgestwicki.etschallenge.core.model.Place.Board;
import edu.bsu.pvgestwicki.etschallenge.core.model.Place.Equals;
import edu.bsu.pvgestwicki.etschallenge.core.model.Place.Tray;
import edu.bsu.pvgestwicki.etschallenge.core.model.Place.Visitor;

public class TurnTest {

	private GameModel game = GameModel.create();
	
	private void startGame() {
		game.start();
	}
	
	@Test
	public void testTurnStartsEmpty() {
		Turn t = Turn.create();
		assertTrue(t.isEmpty());
	}
	
	@Test
	public void testTurnContainsFirstMove() {
		startGame();
		game.moveTileFromTraySlot(0).toBoardPosition(0, 0);
		Move firstMove = game.turn().moves().get(0);
		assertTrue(isTrayPosition(firstMove.source(), 0) && isBoardPosition(firstMove.destination(), 0, 0));
	}

	private boolean isTrayPosition(Place place, final int trayPos) {
		return place.accept(new Visitor<Boolean>() {
			@Override
			public Boolean visit(Equals eq, Object... args) {
				return false;
			}

			@Override
			public Boolean visit(Board board, Object... args) {
				return false;
			}

			@Override
			public Boolean visit(Tray tray, Object... args) {
				return tray.position() == trayPos;
			}
		});
	}
	
	private boolean isBoardPosition(Place place, int i, int j) {
		final Point point = new Point(i,j);
		return place.accept(new Visitor<Boolean>() {
			@Override
			public Boolean visit(Equals eq, Object... args) {
				return false;
			}

			@Override
			public Boolean visit(Board board, Object... args) {
				return board.point().equals(point);
			}

			@Override
			public Boolean visit(Tray tray, Object... args) {
				return false;
			}
		});
	}
	
	@Test
	public void testTilePlacementsListIsEmptyAtStart() {
		startGame();
		assertTrue(game.turn().placements().isEmpty());
	}
	
	@Test
	public void testTilePlacementContainsOneEntryWhenOneMoveFromTrayIsMade() {
		startGameAndPutATileAtZeroZero();
		assertEquals(1, game.turn().placements().size());
	}
	
	@Test
	public void testTilePlacementContainsOneEntryWhenOneMoveFromEqualsPoolIsMade() {
		startGameAndPutEqualsTileAtZeroZero();
		assertEquals(1, game.turn().placements().size());
	}
	
	private void startGameAndPutEqualsTileAtZeroZero() {
		startGame();
		game.moveTileFromEqualsPoolToBoard(0, 0);
	}

	private void startGameAndPutATileAtZeroZero() {
		startGame();
		game.moveTileFromTraySlot(0).toBoardPosition(0, 0);
	}

	@Test
	public void testTilePlacementContainsCorrectMoveWhenOneTrayMoveIsMade() {
		startGameAndPutATileAtZeroZero();
		Map<IPoint,TileModel> placements = game.turn().placements();
		assertEquals(game.board().at(0,0).tile(), placements.get(new Point(0,0)));
	}
	
	@Test
	public void testTilePlacementContainsCorrectMoveWhenOneEqualsMoveIsMade() {
		startGameAndPutEqualsTileAtZeroZero();
		Map<IPoint,TileModel> placements = game.turn().placements();
		assertEquals(game.board().at(0,0).tile(), placements.get(new Point(0,0)));
	}
	
	@Test
	public void testTilePlacementContainsOneEntryWhenATrayTileIsPlacedOnBoardAndThenMoved() {
		startGameAndPutATileAtZeroZeroAndMoveItToOneOne();
		assertEquals(1, game.turn().placements().size());
	}
	
	private void startGameAndPutATileAtZeroZeroAndMoveItToOneOne() {
		startGameAndPutATileAtZeroZero();
		game.moveTileFromBoard(0, 0).toBoard(1, 1);
	}
	
	@Test
	public void testTilePlacementContainsOneEntryWhenEqualsTileIsPlacedOnBoardAndThenMoved() {
		startGameAndPutEqualsTileAtZeroZeroAndMoveItToOneOne();
		assertEquals(1, game.turn().placements().size());
	}
	

	private void startGameAndPutEqualsTileAtZeroZeroAndMoveItToOneOne() {
		startGameAndPutEqualsTileAtZeroZero();
		game.moveTileFromBoard(0, 0).toBoard(1, 1);
	}

	@Test
	public void testTilePlacementContainsCorrectMoveWhenATrayTileIsPlacedOnBoardAndThenMoved() {
		startGameAndPutATileAtZeroZeroAndMoveItToOneOne();
		Map<IPoint,TileModel> placements = game.turn().placements();
		assertEquals(game.board().at(1,1).tile(), placements.get(new Point(1,1)));
	}
	
	@Test
	public void testTilePlacementContainsCorrectMoveWhenEqualsIsPlacedOnBoardAndThenMoved() {
		startGameAndPutEqualsTileAtZeroZeroAndMoveItToOneOne();
		Map<IPoint,TileModel> placements = game.turn().placements();
		assertEquals(game.board().at(1,1).tile(), placements.get(new Point(1,1)));
	}
	
	@Test
	public void testTilePlacementIsEmptyIfATrayTileIsAddedToBoardAndThenRemoved() {
		startGameAndPutATileAtZeroZero();
		game.moveTileFromBoard(0, 0).toTray(0);
		assertEquals(0, game.turn().placements().size());
	}
	
	@Test
	public void testTilePlacementIsEmptyIfEqualsTileIsAddedToBoardAndThenRemoved() {
		startGameAndPutEqualsTileAtZeroZero();
		game.moveTileFromBoard(0, 0).toEqualsPool();
		assertEquals(0, game.turn().placements().size());
	}
}
