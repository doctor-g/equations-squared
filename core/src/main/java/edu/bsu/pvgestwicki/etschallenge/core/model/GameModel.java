package edu.bsu.pvgestwicki.etschallenge.core.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import playn.core.util.Callback;
import pythagoras.i.IPoint;
import react.Signal;
import react.SignalView;
import react.Slot;
import react.UnitSignal;
import edu.bsu.pvgestwicki.etschallenge.core.GameConfiguration;
import edu.bsu.pvgestwicki.etschallenge.core.achieve.AchievementTracker;
import edu.bsu.pvgestwicki.etschallenge.core.event.TrayTileEvent;
import edu.bsu.pvgestwicki.etschallenge.core.math.Environment;
import edu.bsu.pvgestwicki.etschallenge.core.math.EnvironmentBuilder;
import edu.bsu.pvgestwicki.etschallenge.core.math.Variable;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.DivideByZero;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.IllegalMove;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.MalformedEquation;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.NonintersectingMove;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.NotAnEquation;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.Success;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.UnbalancedEquation;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.UnboundVariable;
import edu.bsu.pvgestwicki.etschallenge.core.model.TileModel.EqualsTileModel;
import edu.bsu.pvgestwicki.etschallenge.core.util.Action;
import edu.bsu.pvgestwicki.etschallenge.core.util.MajorTileTypeVisitor;

public final class GameModel {

	public static final class Builder {
		private TileArea board;
		private TrayModel tray;

		public Builder() {
		}

		public Builder andTray(TrayModel tray) {
			this.tray = checkNotNull(tray);
			return this;
		}

		public GameModel create() {
			GameModel game = new GameModel();
			initValuesOf(game);
			return game;
		}

		private void initValuesOf(GameModel game) {
			if (this.board != null)
				game.board = this.board;
			if (this.tray != null)
				game.tray = this.tray;
		}
	}

	public static Builder withTray(TrayModel tray) {
		Builder b = new Builder();
		b.tray = tray;
		return b;
	}

	public static Builder withBoard(TileArea board) {
		Builder b = new Builder();
		b.board = board;
		return b;
	}

	public static GameModel create() {
		return new GameModel();
	}

	private FixedQuantityTrayFiller filler = createTrayFiller();
	private TrayModel tray = TrayModel.create();
	private TileArea board = TileArea.create(11, 11);
	private EqualsPoolModel eqPool = EqualsPoolModel.create();
	private Turn turn;
	private Environment env = EnvironmentBuilder.create();
	private final ProvisionalVariableBinder provisionalBinder = ProvisionalVariableBinder
			.forEnvironment(env);
	private final AchievementTracker achievementTracker = AchievementTracker
			.create();

	private final Score score = Score.create();

	private final Signal<Move> onTileMoved = Signal.create();
	private final Signal<TrayTileEvent> onTileAddedFromBag = Signal.create();
	private final Signal<CommitResult> onCommit = Signal.create();
	private final UnitSignal onGameOver = new UnitSignal();
	private final Action resetTurnAction = new Action("Clear") {
		{
			disable();
		}

		@Override
		public void run() {
			resetTurn();
		}
	};
	private final Action puntAction = new Action("Punt") {
		@Override
		public void run() {
			punt();
		}
	};

	private final Action commitAction = new Action("Commit") {
		{
			disable();
		}

		@Override
		public void run() {
			commitTurn();
		}
	};

	private BindingRequestHandler bindingRequestHandler;

	private GameModel() {
	}

	public ReadOnlyTrayModel tray() {
		return tray;
	}

	public ReadOnlyTileArea board() {
		return board;
	}

	public ReadOnlyEqualsPoolModel equalsPool() {
		return eqPool;
	}

	public FixedQuantityTrayFiller filler() {
		return filler;
	}

	public Score score() {
		return score;
	}

	public AchievementTracker achievementTracker() {
		return achievementTracker;
	}

	public void start() {
		createTrayFiller();
		filler.fill(tray);
		eqPool.start();
		initNewTurn();
		configureScoring();
		achievementTracker.track(this);
	}

	private void initNewTurn() {
		turn = Turn.create();
		turn.registerWith(this);
		commitAction.disable();
	}

	private FixedQuantityTrayFiller createTrayFiller() {
		FixedQuantityTrayFiller f = FixedQuantityTrayFiller.create();
		f.onTileCreated().connect(new Slot<TrayTileEvent>() {
			@Override
			public void onEmit(TrayTileEvent event) {
				onTileAddedFromBag.emit(event);
			}
		});
		return f;
	}

	private void configureScoring() {
		OperatorAndAchievementScoringStrategy.forGame(this);
	}

	public void setBindingRequestHandler(BindingRequestHandler handler) {
		this.bindingRequestHandler = checkNotNull(handler);
	}

	public FromTrayPlacer moveTileFromTraySlot(int traySlot) {
		return new FromTrayPlacer(traySlot);
	}

	public Turn turn() {
		checkState(turn != null);
		return turn;
	}

	private abstract class AbstractPlacer<T extends Place> {
		protected final T source;
		protected final TileModel tile;

		protected AbstractPlacer(T source, TileModel tile) {
			this.source = checkNotNull(source);
			this.tile = checkNotNull(tile);
		}

		protected T source() {
			return source;
		}

		protected void fireMoveTo(Place dest) {
			Move move = Move.tile(tile).from(source).to(dest);
			onTileMoved.emit(move);
			handleActionStates();
		}

		private void handleActionStates() {
			boolean commitShouldBeEnabled = turn.placements().size() != 0;
			if (commitShouldBeEnabled)
				enableCommitAndReset();
			else
				disableCommitAndReset();
		}

		private void enableCommitAndReset() {
			commitAction.enable();
			resetTurnAction.enable();
		}

		private void disableCommitAndReset() {
			commitAction.disable();
			resetTurnAction.disable();
		}
	}

	public final class FromTrayPlacer extends AbstractPlacer<Place.Tray> {

		private FromTrayPlacer(int traySlot) {
			super(new Place.Tray(traySlot), tray.at(traySlot).removeTile());
		}

		public void toBoardPosition(int i, int j) {
			board.at(i, j).place(tile);
			fireMoveTo(new Place.Board(i, j));
		}

		public void toTray(int newSlot) {
			tray.at(newSlot).place(tile);
			fireMoveTo(new Place.Tray(newSlot));
		}
	}

	public FromBoardPlacer moveTileFromBoard(int i, int j) {
		return new FromBoardPlacer(i, j);
	}

	public final class FromBoardPlacer extends AbstractPlacer<Place.Board> {

		private FromBoardPlacer(int i, int j) {
			super(new Place.Board(i, j), board.at(i, j).removeTile());
		}

		public void toTray(int traySpace) {
			checkState(!EqualsTilePredicate.instance().apply(tile),
					"Cannot put equals there");
			tray.at(traySpace).place(tile);
			fireMoveTo(new Place.Tray(traySpace));
		}

		public void toBoard(int newI, int newJ) {
			board.at(newI, newJ).place(tile);
			fireMoveTo(new Place.Board(newI, newJ));
		}

		public void toEqualsPool() {
			checkState(EqualsTilePredicate.instance().apply(tile),
					"Not an equals tile!");
			board.at(source().point()).clear();
			fireMoveTo(new Place.Equals());
		}

		public void toNextOpenSpaceInTray() {
			int trayPos = tray.add(tile);
			fireMoveTo(new Place.Tray(trayPos));
		}
	}

	public void moveTileFromEqualsPoolToBoard(int i, int j) {
		new FromEqualsPoolPlacer().toBoard(i, j);
	}

	private final class FromEqualsPoolPlacer extends
			AbstractPlacer<Place.Equals> {
		private FromEqualsPoolPlacer() {
			super(new Place.Equals(), eqPool.take());
		}

		public void toBoard(int i, int j) {
			board.at(i, j).place(tile);
			fireMoveTo(new Place.Board(i, j));
		}
	}

	public SignalView<Move> onTileMoved() {
		return onTileMoved;
	}

	public SignalView<TrayTileEvent> onTileAddedFromBag() {
		return onTileAddedFromBag;
	}

	public SignalView<CommitResult> onCommit() {
		return onCommit;
	}

	public SignalView<Void> onGameOver() {
		return onGameOver;
	}

	public Action puntAction() {
		return puntAction;
	}

	public Action resetTurnAction() {
		return resetTurnAction;
	}

	public Action commitAction() {
		return commitAction;
	}

	public void resetTurn() {
		returnTurnTilesBackToTheirSources();
		commitAction.disable();
		resetTurnAction.disable();
	}

	private void returnTurnTilesBackToTheirSources() {
		for (IPoint p : turn.placements().keySet()) {
			returnTurnTileToItsSource(p);
		}
	}

	private void returnTurnTileToItsSource(final IPoint p) {
		final TileModel tileModel = turn.placements().get(p);
		tileModel.accept(new MajorTileTypeVisitor<Void>() {
			@Override
			public Void visit(EqualsTileModel equalsTile, Object... args) {
				moveTileFromBoard(p.x(), p.y()).toEqualsPool();
				return null;
			}

			@Override
			protected Void visitNonEqualsTile(TileModel aNonEqualsTileModel,
					Object... args) {
				moveTileFromBoard(p.x(), p.y()).toNextOpenSpaceInTray();
				return null;
			}
		});
	}

	public void punt() {
		resetTurn();
		tray.clear();
		fillTrayOrEndGame();
	}

	private void fillTrayOrEndGame() {
		if (areThereEnoughDigitsLeftToFillTray()) {
			filler.fill(tray);
		} else {
			endGame();
		}
	}

	private boolean areThereEnoughDigitsLeftToFillTray() {
		return filler.digitsRemaining().get() >= GameConfiguration.instance()
				.digitsInTray();
	}

	private void endGame() {
		puntAction.disable();
		onGameOver.emit();
	}

	public void commitTurn() {
		TurnCommitEvaluator eval = TurnCommitEvaluator.on(board).in(env);
		CommitResult result = eval.evaluateCommit(turn);
		result.accept(new CommitResult.Visitor<Void>() {
			@Override
			public Void visit(Success success, Object... args) {
				handleSuccessfulCommit(success);
				return null;
			}

			@Override
			public Void visit(UnbalancedEquation unbalanced, Object... args) {
				unbindProvisionalBindingsAndFire(unbalanced);
				return null;
			}

			@Override
			public Void visit(MalformedEquation malformed, Object... args) {
				unbindProvisionalBindingsAndFire(malformed);
				return null;
			}

			@Override
			public Void visit(NotAnEquation notAnEquation, Object... args) {
				unbindProvisionalBindingsAndFire(notAnEquation);
				return null;
			}

			@Override
			public Void visit(DivideByZero divideByZero, Object... args) {
				unbindProvisionalBindingsAndFire(divideByZero);
				return null;
			}

			@Override
			public Void visit(UnboundVariable unboundVariableResult,
					Object... args) {
				fireCommitResult(unboundVariableResult);
				final Variable theUnboundVariable = unboundVariableResult
						.variable();
				bindingRequestHandler.request(theUnboundVariable,
						new Callback<Integer>() {
							@Override
							public void onSuccess(Integer result) {
								provisionalBinder.provisionallyBind(
										theUnboundVariable, result);
								commitTurn();
							}

							@Override
							public void onFailure(Throwable cause) {
								throw new IllegalStateException();
							}
						});

				return null;
			}

			public Void visit(IllegalMove illegalMove, Object... args) {
				unbindProvisionalBindingsAndFire(illegalMove);
				return null;
			}

			@Override
			public Void visit(NonintersectingMove nonintersectingMove,
					Object... args) {
				unbindProvisionalBindingsAndFire(nonintersectingMove);
				return null;
			}

			private void unbindProvisionalBindingsAndFire(CommitResult result) {
				provisionalBinder.unbind();
				fireCommitResult(result);
			}
		});
	}

	private void handleSuccessfulCommit(Success success) {
		commitAction.disable();
		resetTurnAction.disable();

		provisionalBinder.finalizeBindings();
		fireCommitResult(success);
		initNewTurn();
		fillTrayOrEndGame();
	}

	private void fireCommitResult(CommitResult result) {
		onCommit.emit(result);
	}

	TrayModel writableTrayForTesting() {
		return tray;
	}

	Environment environmentExposedForTesting() {
		return env;
	}

}