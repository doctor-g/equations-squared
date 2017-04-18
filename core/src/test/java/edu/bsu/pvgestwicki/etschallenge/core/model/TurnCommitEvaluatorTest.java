package edu.bsu.pvgestwicki.etschallenge.core.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import pythagoras.i.Point;

import com.google.common.collect.Lists;

import edu.bsu.pvgestwicki.etschallenge.core.math.Environment;
import edu.bsu.pvgestwicki.etschallenge.core.math.EnvironmentBuilder;
import edu.bsu.pvgestwicki.etschallenge.core.math.Variable;
import edu.bsu.pvgestwicki.etschallenge.core.math.VariableBuilder;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.DivideByZero;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.IllegalMove;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.MalformedEquation;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.NonintersectingMove;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.NotAnEquation;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.Success;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.UnbalancedEquation;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.UnboundVariable;

public class TurnCommitEvaluatorTest {

	private TurnCommitEvaluator evaluator;
	private CommitResult result;
	private TileArea board;
	private Environment env;

	@Before
	public void setUp() {
		env = EnvironmentBuilder.create();
		board = TileArea.create(11, 11);
		evaluator = TurnCommitEvaluator.on(board).in(env);
	}

	@Test
	public void testUnparseableExpressionResult() {
		playTurnAcrossTopOfBoard("11+=");
		assertResultIs(MalformedEquation.class);
	}

	private void playTurnAcrossTopOfBoard(String tileString) {
		playTurnAcross(tileString, 0, 0);
	}
	
	private void playTurnAcross(String tileString, int row, int col) {
		List<Move> moves = Lists.newArrayList();
		for (int i = 0; i < tileString.length(); i++) {
			TileModel tile = makeTile(tileString.charAt(i));
			Move move = createMoveAndUpdateBoard(tile, col+i, row);
			moves.add(move);
		}
		Turn t = Turn.createFromTestData(moves);
		result = evaluator.evaluateCommit(t);		
	}
	
	private void playTurnDownward(String tileString, int row, int col) {
		List<Move> moves = Lists.newArrayList();
		for (int i = 0; i < tileString.length(); i++) {
			TileModel tile = makeTile(tileString.charAt(i));
			Move move = createMoveAndUpdateBoard(tile, col, row+i);
			moves.add(move);
		}
		Turn t = Turn.createFromTestData(moves);
		result = evaluator.evaluateCommit(t);		
	}

	private Move createMoveAndUpdateBoard(TileModel tile, int i, int j) {
		Move m = Move//
				.tile(tile)//
				.from(new Place.Tray(i))//
				.to(new Place.Board(i, j));
		board.at(i, j).place(tile);
		return m;
	}

	private TileModel makeTile(char c) {
		return TileModelFactory.instance().forSymbol(c);
	}

	private void assertResultIs(Class<? extends CommitResult> expectedType) {
		assertEquals(expectedType, result.getClass());
	}

	@Test
	public void testPlayExpressionWithoutEqualsFiresFailure() {
		playTurnAcrossTopOfBoard("1+1");
		assertResultIs(NotAnEquation.class);
	}

	@Test
	public void testPlayUnbalancedEquationFiresEvent() {
		playTurnAcrossTopOfBoard("1+1=1");
		assertResultIs(UnbalancedEquation.class);
	}

	@Test
	public void testIntersectingEquationIsSuccess() {
		setupAcrossTopOfBoard("9-4=5");
		Turn t = makeTurn("-9=0").from(0, 1).vertically();
		result = evaluator.evaluateCommit(t);
		assertResultIs(Success.class);
	}

	private void setupAcrossTopOfBoard(String string) {
		for (int i = 0; i < string.length(); i++) {
			TileModel tile = makeTile(string.charAt(i));
			board.at(i, 0).place(tile);
		}
	}

	private TurnBuilder makeTurn(String tileString) {
		return new TurnBuilder(tileString);
	}

	private final class TurnBuilder {
		private final String tileString;
		private final Point from = new Point(0, 0);

		public TurnBuilder(String tileString) {
			this.tileString = checkNotNull(tileString);
		}

		public TurnBuilder from(int x, int y) {
			from.x = x;
			from.y = y;
			return this;
		}

		public Turn vertically() {
			List<Move> moves = Lists.newArrayList();
			for (int i = 0; i < tileString.length(); i++) {
				TileModel tile = makeTile(tileString.charAt(i));
				Move m = createMoveAndUpdateBoard(tile, from.x, from.y + i);
				moves.add(m);
			}
			return Turn.createFromTestData(moves);
		}

		public Turn diagonally() {
			List<Move> moves = Lists.newArrayList();
			for (int i = 0; i < tileString.length(); i++) {
				TileModel tile = makeTile(tileString.charAt(i));
				Move m = createMoveAndUpdateBoard(tile, from.x+i, from.y + i);
				moves.add(m);
			}
			return Turn.createFromTestData(moves);
		}
	}

	@Test
	public void testDivideByZeroResult() {
		playTurnAcrossTopOfBoard("6/0=3");
		assertResultIs(DivideByZero.class);
	}
	
	@Test
	public void testDivideByZeroOnRHS() {
		playTurnAcrossTopOfBoard("3=6/0");
		assertResultIs(DivideByZero.class);
	}

	@Test
	public void testUnboundVariableResult() {
		playTurnAcrossTopOfBoard("A+1=3");
		assertResultIs(UnboundVariable.class);
	}
	
	@Test
	public void testNegatedUnboundVariableResult() {
		playTurnAcrossTopOfBoard("-A=2");
		assertResultIs(UnboundVariable.class);
	}

	@Test
	public void testInvalidVariableBindingIsUnbalancedEquation() {
		Variable v = VariableBuilder.create("A");
		env.bind(v, 10);
		playTurnAcrossTopOfBoard("A+1=5");
		assertResultIs(UnbalancedEquation.class);
	}

	@Test
	public void testPlayDiagonalTilesResultsInIllegalMove() {
		playDiagonalSequence();
		assertResultIs(IllegalMove.class);
	}
	
	private void playDiagonalSequence() {
		Turn t = makeTurn("12").from(0, 0).diagonally();
		result = evaluator.evaluateCommit(t);
	}

	@Test
	public void testPlayASingleTileIsAnIllegalMove() {
		playTurnAcrossTopOfBoard("1");
	}
	
	@Test
	public void testNonintersectingPlayResult() {
		playTurnAcrossTopOfBoard("1+1=2");
		playTurnAcross("1+1=2", 3, 0);
		assertResultIs(NonintersectingMove.class);
	}
	
	@Test
	public void testUnbalancedVariableAndDivisionByZeroResultsInDivisionByZero() {
		Variable v = VariableBuilder.create("A");
		env.bind(v, 2);
		playTurnAcrossTopOfBoard("A=1/0");
		assertResultIs(DivideByZero.class);
	
	}
	
	@Test
	public void testUnboundVariableAndDivisionByZeroResultsInUnboundVariable() {
		playTurnAcrossTopOfBoard("A=1/0");
		assertResultIs(UnboundVariable.class);
	}
	
	@Test
	public void testUnboundVariableAndDivisionByZeroResultsInUnboundVariableWithDivisionOnLeft() {
		playTurnAcrossTopOfBoard("1/0=A");
		assertResultIs(UnboundVariable.class);
	}


	@Test
	public void testPlayingAnEquationAlongSideAnotherSoThatBothChangeAndAreTrue() {
		playTurnAcross("1+1=2", 0, 1);
		playTurnDownward("0+0=0", 0, 0);
	}
}
