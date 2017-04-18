package edu.bsu.pvgestwicki.etschallenge.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static playn.core.PlayN.graphics;

import java.util.Map;
import java.util.Random;

import playn.core.CanvasImage;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.Pointer;
import playn.core.Pointer.Event;
import playn.core.Sound;
import playn.core.util.Callback;
import playn.core.util.Clock;
import pythagoras.f.Dimension;
import pythagoras.f.IRectangle;
import pythagoras.f.Point;
import pythagoras.f.Rectangle;
import pythagoras.i.IPoint;
import react.SignalView;
import react.Slot;
import react.UnitSignal;
import react.UnitSlot;
import tripleplay.anim.Animation;
import tripleplay.anim.Animator;
import tripleplay.ui.Button;
import tripleplay.ui.Interface;
import tripleplay.util.Layers;
import tripleplay.util.PointerInput;

import com.google.common.collect.Maps;

import edu.bsu.pvgestwicki.etschallenge.core.AlgebraGame;
import edu.bsu.pvgestwicki.etschallenge.core.GameConfiguration;
import edu.bsu.pvgestwicki.etschallenge.core.achieve.Achievement;
import edu.bsu.pvgestwicki.etschallenge.core.achieve.AchievementTracker;
import edu.bsu.pvgestwicki.etschallenge.core.achieve.Badge;
import edu.bsu.pvgestwicki.etschallenge.core.achieve.Demerit;
import edu.bsu.pvgestwicki.etschallenge.core.event.EventQueue;
import edu.bsu.pvgestwicki.etschallenge.core.event.EventQueuePostingSlot;
import edu.bsu.pvgestwicki.etschallenge.core.math.Variable;
import edu.bsu.pvgestwicki.etschallenge.core.model.BindingRequestHandler;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.IllegalMove;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.Success;
import edu.bsu.pvgestwicki.etschallenge.core.model.CommitResult.UnboundVariable;
import edu.bsu.pvgestwicki.etschallenge.core.model.EqualsTilePredicate;
import edu.bsu.pvgestwicki.etschallenge.core.model.GameModel;
import edu.bsu.pvgestwicki.etschallenge.core.model.Move;
import edu.bsu.pvgestwicki.etschallenge.core.model.Place.Board;
import edu.bsu.pvgestwicki.etschallenge.core.model.Place.Equals;
import edu.bsu.pvgestwicki.etschallenge.core.model.Place.Tray;
import edu.bsu.pvgestwicki.etschallenge.core.model.Place.Visitor;
import edu.bsu.pvgestwicki.etschallenge.core.model.ReadOnlyTileArea.ReadOnlyCell;
import edu.bsu.pvgestwicki.etschallenge.core.model.TileArea.Cell;
import edu.bsu.pvgestwicki.etschallenge.core.model.TileModel;
import edu.bsu.pvgestwicki.etschallenge.core.util.HeightValue;
import edu.bsu.pvgestwicki.etschallenge.core.util.LayerEliminator;
import edu.bsu.pvgestwicki.etschallenge.core.util.UnfailableCallback;
import edu.bsu.pvgestwicki.etschallenge.core.util.WidthValue;

public class GameView {

	private static final float TILE_PADDING = 6;
	private static final float BOARD_BORDER = 20;

	private static final pythagoras.f.IPoint SIDEBAR_POS = new Point(600,
			BOARD_BORDER + 70);
	private static final pythagoras.f.IPoint SIDEBAR_OFFSCREEN_POS = new Point(
			graphics().width(), SIDEBAR_POS.y());

	private static final Dimension BOARD_TILE_SIZE = new Dimension(45, 45);
	private static final Dimension SIDEBAR_TILE_SIZE = new Dimension(55, 55);

	private static final int MAX_NUMBER_ENTRY_DIGITS = 5;

	private static final pythagoras.f.IPoint GAME_SUMMARY_POS = new Point(100,
			80);

	private GroupLayer gameLayer;
	private final GameModel game;
	private final PointerInput input;
	private Map<TileModel, TileView> tileMap = Maps.newHashMap();
	private final SingleTileSelectionModel selection = new SingleTileSelectionModel();

	private final Interface iface = new Interface();
	private final PopupQueue popupQueue;

	private TraySidebar sidebarTileTray;
	private TileHolderFactory tileHolderFactory;
	private Sidebar currentSidebar;

	private UnitSignal onRestart = new UnitSignal();

	private final Achievement.Visitor<Void> demeritPopupFactory = new Achievement.Visitor<Void>() {
		@Override
		public Void visit(Badge badge) {
			return null;
		}

		@Override
		public Void visit(Demerit demerit) {
			popup(demerit.friendlyName(), GameSound.DEMERIT.sound);
			return null;
		}
	};

	public GameView(GameModel game, PointerInput input) {
		this.game = checkNotNull(game);
		this.input = checkNotNull(input);
		gameLayer = graphics().createGroupLayer();
		popupQueue = PopupQueue.createFor(gameLayer);

		initBoard();
		initGameMoveWatcher();
		initGameCommitSuccessWatcher();
		initIllegalMoveWatcher();
		initVariableBinder();
		initGameOverWatcher();

		currentSidebar = sidebarTileTray = TraySidebar.builder()//
				.tileSize(SIDEBAR_TILE_SIZE)//
				.pointerInput(input)//
				.tileRegistry(tileMap)//
				.game(game)//
				.tripleplayInterface(iface)//
				.tileClickedCallback(new UnfailableCallback<Integer>() {
					@Override
					public void onSuccess(Integer tileIndex) {
						traySpaceClicked(tileIndex);
					}
				})//
				.equalsClickedCallback(new UnfailableCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						equalsPoolClicked();
					}
				})//
				.build();
		Layer trayLayer = sidebarTileTray.layer();
		trayLayer.setTranslation(SIDEBAR_POS.x(), SIDEBAR_POS.y());
		gameLayer.add(sidebarTileTray.layer());
		sidebarTileTray.hookup(game);

		ScoreBox scorebox = ScoreBox.createWith(iface).andInput(input);
		gameLayer.add(scorebox.layer());
		scorebox.layer().setTranslation(SIDEBAR_POS.x(), 0);
		scorebox.attachTo(game);
		scorebox.onClick().connect(new EventQueuePostingSlot<ScoreBox>() {
			@Override
			public void handle() {
				GameSound.CLICK.sound.play();
				state.scoreBoxClicked();
			}

			@Override
			public String toString() {
				return "Scorebox clicked";
			}
		});

		game.start();
		initScorePopup();
		initDemeritPopup();
	}

	/**
	 * This is used for testing the game summary view. It's too cool to get rid
	 * of.
	 */
	@SuppressWarnings("unused")
	private void debugGameSummaryView() {
		Random random = new Random();
		AchievementTracker.DebuggingTrackerBuilder builder = AchievementTracker
				.debug();
		for (Badge badge : Badge.values()) {
			builder.add(badge, random.nextInt(9) + 1);
		}
		for (Demerit demerit : Demerit.values()) {
			builder.add(demerit, random.nextInt(9) + 1);
		}
		AchievementTracker tracker = builder.create();

		GameSummaryView view = GameSummaryView.builder()
				.withAchievements(tracker).withInterface(iface).build();
		gameLayer.add(view.layer());
		view.layer().setTranslation(GAME_SUMMARY_POS.x(), GAME_SUMMARY_POS.y());
	}

	public Layer layer() {
		return gameLayer;
	}

	private void initBoard() {
		makeBackground();

		tileHolderFactory = TileHolderFactory.forSize(BOARD_TILE_SIZE);
		tileHolderFactory.setTileSize(BOARD_TILE_SIZE);

		for (int i = 0; i < game.board().width(); i++) {
			for (int j = 0; j < game.board().height(); j++) {
				initBoardTileHolder(i, j);
			}
		}
	}

	private void makeBackground() {
		CanvasImage image = graphics().createImage(graphics().width(),
				graphics().height());
		image.canvas().setFillColor(Color.MED_GREY.intValue());
		image.canvas().fillRect(0, 0, image.width(), image.height());
		image.canvas().setFillColor(Color.DARK_GREY.intValue());

		int offset = 8;
		int length = 596 - offset * 2;
		image.canvas().fillRect(offset, offset, length, length);
		ImageLayer layer = graphics().createImageLayer(image);
		gameLayer.add(layer);
	}

	private void initBoardTileHolder(int i, int j) {
		ImageLayer layer = tileHolderFactory.createImageLayer();
		ImageLayerBounds.set(layer).to(boardTileBounds(i, j));
		gameLayer.add(layer);
		registerBoardPointerListener(layer, i, j);
	}

	private IRectangle boardTileBounds(int i, int j) {
		float x = BOARD_BORDER + i * (BOARD_TILE_SIZE.width + TILE_PADDING);
		float y = BOARD_BORDER + j * (BOARD_TILE_SIZE.height + TILE_PADDING);
		return new Rectangle(x, y, BOARD_TILE_SIZE.width,
				BOARD_TILE_SIZE.height);
	}

	private void registerBoardPointerListener(ImageLayer layer, final int i,
			final int j) {
		input.register(layer, new Pointer.Adapter() {
			@Override
			public void onPointerStart(Event event) {
				AlgebraGame.eventQueue().add(new EventQueue.Event() {
					@Override
					public void handle() {
						boardSpaceClicked(i, j);
					}

					@Override
					public String toString() {
						return "Board space clicked [" + i + "," + j
								+ "], where content is "
								+ game.board().at(i, j).value();
					}
				});
			}
		});
	}

	private void initGameMoveWatcher() {
		game.onTileMoved().connect(new Slot<Move>() {
			@Override
			public void onEmit(Move move) {
				TileView tileView = lookupViewFor(move.tile());
				final AnimationStateBuilder b = new AnimationStateBuilder(
						tileView);
				move.destination().accept(new Visitor<Void>() {
					@Override
					public Void visit(Equals eq, Object... args) {
						b.toEqualsPool();
						return null;
					}

					@Override
					public Void visit(Board board, Object... args) {
						b.toBoardPosition(board.point());
						return null;
					}

					@Override
					public Void visit(Tray tray, Object... args) {
						b.toTraySlot(tray.position());
						return null;
					}

				}, b);
				b.enterItAndThen(new WaitingForClickState());
			}
		});
	}

	private void initGameCommitSuccessWatcher() {
		game.onCommit().connect(new Slot<CommitResult>() {
			@Override
			public void onEmit(CommitResult event) {
				event.accept(new CommitResult.Visitor.Adapter<Void>(null) {
					@Override
					public Void visit(Success success, Object... args) {
						freezeCurrentTurnTiles();
						return null;
					}
				});
			}
		});
	}

	private void initIllegalMoveWatcher() {
		game.onCommit().connect(new Slot<CommitResult>() {
			@Override
			public void onEmit(CommitResult event) {
				event.accept(new CommitResult.Visitor.Adapter<Void>(null) {
					@Override
					public Void visit(IllegalMove illegalMove, Object... args) {
						popup("Illegal move!", GameSound.DEMERIT.sound);
						return null;
					}
				});
			}
		});
	}

	private void initVariableBinder() {
		game.onCommit().connect(new Slot<CommitResult>() {
			@Override
			public void onEmit(CommitResult result) {
				result.accept(new CommitResult.Visitor.Adapter<Void>(null) {
					@Override
					public Void visit(final UnboundVariable unboundVariable,
							Object... args) {
						enterState(new VariableBindingState(unboundVariable));
						return null;
					}
				});
			}
		});
	}

	private void freezeCurrentTurnTiles() {
		for (TileModel tile : game.turn().placements().values()) {
			TileView tileView = lookupViewFor(tile);
			tileView.freeze();
		}
	}

	private void initScorePopup() {
		game.score()
				.onChange()
				.connect(
						new Slot<edu.bsu.pvgestwicki.etschallenge.core.model.Score.Event>() {
							@Override
							public void onEmit(
									edu.bsu.pvgestwicki.etschallenge.core.model.Score.Event event) {
								popup(event.message(), event.message()
										.startsWith("+") ? GameSound.BLIP.sound
										: GameSound.BADGE.sound);
							}
						});
	}

	private void initDemeritPopup() {
		game.achievementTracker().achievementAdded()
				.connect(new Slot<Achievement>() {
					@Override
					public void onEmit(Achievement event) {
						event.accept(demeritPopupFactory);
					}
				});
	}

	private void popup(String mesg, Sound sound) {
		final PopupText popup = PopupText.builder()//
				.withText(mesg)//
				.onInterface(iface)//
				.withSound(sound)//
				.build();
		popupQueue.enqueue(popup);
	}

	private void playSidebarSwooshSound() {
		GameSound.WHOOSH.sound.play();
	}

	private void initGameOverWatcher() {
		game.onGameOver().connect(new UnitSlot() {
			@Override
			public void onEmit() {
				final GameOverSidebar gameoverSidebar = GameOverSidebar
						.createOn(iface);
				configureOffscreenSidebar(gameoverSidebar);
				gameoverSidebar.playAgainClicked().connect(
						new EventQueuePostingSlot<Button>() {
							@Override
							public void handle() {
								LayerEliminator//
										.layer(gameoverSidebar.layer())//
										.fromParent(gameLayer)//
										.run();
								onRestart.emit();
							}

							@Override
							public String toString() {
								return "Play Again clicked";
							}

						});
				gameoverSidebar.viewSummaryClicked().connect(
						new EventQueuePostingSlot<Button>() {
							@Override
							public void handle() {
								gameoverSidebar.disableButtons();
								enterState(new GameSummaryState(state) {
									@Override
									public void onExit() {
										gameoverSidebar.enableButtons();
									}
								});
							}

							@Override
							public String toString() {
								return "View Summary clicked";
							}

						});

				slideOut(sidebarTileTray);
				slideIn(gameoverSidebar);

				enterState(new GameOverState());
			}
		});
	}

	private GameSummaryView popupGameSummary() {
		playSidebarSwooshSound();
		final GameSummaryView summary = GameSummaryView.builder()//
				.withInterface(iface)//
				.withAchievements(game.achievementTracker())//
				.build();
		gameLayer.add(summary.layer());

		GameConfiguration.instance().animator()//
				.tweenTranslation(summary.layer())//
				.from(graphics().width(), GAME_SUMMARY_POS.y())//
				.to(GAME_SUMMARY_POS)//
				.easeIn()//
				.in(400);

		return summary;
	}

	private void slideOutGameSummary(GameSummaryView summary) {
		GameConfiguration
				.instance()
				.animator()
				.tweenTranslation(summary.layer())
				.to(graphics().width(), GAME_SUMMARY_POS.y())
				.easeIn()
				.in(400)
				.then()
				.action(LayerEliminator.layer(summary.layer()).fromParent(
						gameLayer));
	}

	private void configureOffscreenSidebar(Sidebar sidebar) {
		gameLayer.add(sidebar.layer());
		sidebar.layer().setTranslation(SIDEBAR_OFFSCREEN_POS.x(),
				SIDEBAR_OFFSCREEN_POS.y());
	}

	public void update(int deltaMS) {
		popupQueue.update(deltaMS);
		iface.update(deltaMS);
	}

	public void paint(Clock clock) {
		iface.paint(clock);
	}

	public SignalView<Void> onRestart() {
		return onRestart;
	}

	private TileView lookupViewFor(TileModel model) {
		return tileMap.get(model);
	}

	private interface State {
		void onExit();

		void boardSpaceClicked(int i, int j);

		void traySpaceClicked(int i);

		void equalsPoolClicked();

		void scoreBoxClicked();
	}

	private State state = new WaitingForClickState();

	private void enterState(State state) {
		if (this.state != null)
			this.state.onExit();
		this.state = state;
	}

	void boardSpaceClicked(int i, int j) {
		state.boardSpaceClicked(i, j);
	}

	void traySpaceClicked(int i) {
		state.traySpaceClicked(i);
	}

	void equalsPoolClicked() {
		state.equalsPoolClicked();
	}

	private abstract class AbstractState implements State {
		@Override
		public void onExit() {
		}

		protected void selectTrayTile(int i) {
			TileModel tile = game.tray().at(i).tile();
			TileView tileView = lookupViewFor(tile);
			selection.select(tileView);
			enterState(new TrayTileSelectedState(i));
		}

		protected void selectBoardTile(int i, int j) {
			TileModel tile = game.board().at(i, j).tile();
			TileView tileView = lookupViewFor(tile);
			selection.select(tileView);
			enterState(new BoardTileSelectedState(i, j));
		}

		protected void selectEqualsTile() {
			TileModel tile = game.equalsPool().top();
			TileView view = lookupViewFor(tile);
			selection.select(view);
			enterState(new EqualsTileInPoolSelectedState());
		}

		protected boolean selectedTileIsEqualsTile() {
			return EqualsTilePredicate.instance().apply(
					selection.selection().model());
		}

		protected boolean occupiedAndSelectable(ReadOnlyCell cell) {
			return cell.isOccupied()
					&& lookupViewFor(cell.tile()).movable().get();
		}

		@Override
		public void scoreBoxClicked() {
			enterState(new GameSummaryState(this));
		}
	}

	private abstract class IgnoreClicksState implements State {

		@Override
		public void onExit() {
		}

		@Override
		public void boardSpaceClicked(int i, int j) {
		}

		@Override
		public void traySpaceClicked(int i) {
		}

		@Override
		public void equalsPoolClicked() {
		}

		@Override
		public void scoreBoxClicked() {
		}
	}

	private final class AnimationStateBuilder {
		private final float dur = 150;
		private final ImageLayer layer;
		private final Animator anim = GameConfiguration.instance().animator();
		private IRectangle bounds;
		private GroupLayer newParent;
		private Runnable post;

		private AnimationStateBuilder(TileView tileView) {
			checkNotNull(tileView);
			this.layer = tileView.layer();
		}

		public AnimationStateBuilder toTraySlot(int i) {
			this.bounds = sidebarTileTray.boundsOfTile(i);
			this.newParent = sidebarTileTray.layer();
			return this;
		}

		public AnimationStateBuilder toBoardPosition(int i, int j) {
			this.bounds = boardTileBounds(i, j);
			this.newParent = gameLayer;
			return this;
		}

		public final AnimationStateBuilder toBoardPosition(IPoint point) {
			return toBoardPosition(point.x(), point.y());
		}

		public AnimationStateBuilder toEqualsPool() {
			this.bounds = sidebarTileTray.equalsPoolBounds();
			this.newParent = sidebarTileTray.layer();
			// I'm not completely happy with this, but the problem is that the
			// equals pool just behaves differently. So, for now, after
			// returning the equal sign, we'll just kill it, making it look like
			// it's still on top even though it's not. This will only be a
			// problem if the equals tiles start looking different (e.g. with
			// images).
			this.post = LayerEliminator.layer(layer).fromParent(newParent);
			return this;
		}

		public void enterItAndThen(final State nextState) {
			checkState(bounds != null, "Bounds must be set.");
			GameSound.PLACE.sound.play();
			enterState(new IgnoreClicksState() {
				{
					Layers.reparent(layer, newParent);
					anim.tweenTranslation(layer)//
							.to(bounds.location())//
							.in(dur)//
							.easeIn();
					anim.tween(new WidthValue(layer))//
							.to(bounds.width())//
							.in(dur)//
							.easeIn();
					Animation animation = anim.tween(new HeightValue(layer))//
							.to(bounds.height())//
							.in(dur)//
							.easeIn();
					if (post != null)
						animation.then().action(post);
					animation.then()//
							.action(new Runnable() {
								@Override
								public void run() {
									selection.clear();
									enterState(nextState);
								}
							});
				}
			});
		}
	}

	private final class WaitingForClickState extends AbstractState {

		@Override
		public void boardSpaceClicked(int i, int j) {
			Cell cell = game.board().at(i, j);
			if (occupiedAndSelectable(cell))
				selectBoardTile(i, j);
		}

		@Override
		public void traySpaceClicked(int i) {
			ReadOnlyCell cell = game.tray().at(i);
			if (occupiedAndSelectable(cell)) {
				selectTrayTile(i);
			}
		}

		@Override
		public void equalsPoolClicked() {
			selectEqualsTile();
		}
	};

	private final class TrayTileSelectedState extends AbstractState {
		private final int position;

		public TrayTileSelectedState(int position) {
			this.position = position;
		}

		@Override
		public void boardSpaceClicked(final int i, final int j) {
			ReadOnlyCell cell = game.board().at(i, j);
			if (occupiedAndSelectable(cell)) {
				selectBoardTile(i, j);
			} else {
				game.moveTileFromTraySlot(position).toBoardPosition(i, j);
			}
		}

		@Override
		public void traySpaceClicked(int clickedPosition) {
			ReadOnlyCell cell = game.tray().at(clickedPosition);
			if (occupiedAndSelectable(cell)) {
				selectTrayTile(clickedPosition);
			} else {
				game.moveTileFromTraySlot(position).toTray(clickedPosition);
			}
		}

		@Override
		public void equalsPoolClicked() {
			selectEqualsTile();
		}
	}

	private final class BoardTileSelectedState extends AbstractState {
		private final int i, j;

		public BoardTileSelectedState(int i, int j) {
			this.i = i;
			this.j = j;
		}

		@Override
		public void boardSpaceClicked(int clickedI, int clickedJ) {
			ReadOnlyCell cell = game.board().at(clickedI, clickedJ);
			if (occupiedAndSelectable(cell)) {
				selectBoardTile(clickedI, clickedJ);
			} else {
				game.moveTileFromBoard(i, j).toBoard(clickedI, clickedJ);
			}
		}

		@Override
		public void traySpaceClicked(int traySpace) {
			if (!attemptToMoveEqualsTileToEmptySpace(traySpace))
				processSelectionForClickOn(traySpace);
		}

		private boolean attemptToMoveEqualsTileToEmptySpace(int traySpace) {
			return selectedTileIsEqualsTile()
					&& !game.tray().at(traySpace).isOccupied();
		}

		private void processSelectionForClickOn(int traySpace) {
			ReadOnlyCell cell = game.tray().at(traySpace);
			if (!cell.isOccupied()) {
				game.moveTileFromBoard(i, j).toTray(traySpace);
			} else {
				selectTrayTile(traySpace);
			}
		}

		@Override
		public void equalsPoolClicked() {
			if (selectedTileIsEqualsTile()) {
				game.moveTileFromBoard(i, j).toEqualsPool();
			} else
				selectEqualsTile();
		}
	}

	private final class EqualsTileInPoolSelectedState extends AbstractState {

		@Override
		public void boardSpaceClicked(int i, int j) {
			Cell cell = game.board().at(i, j);
			if (cell.isOccupied()) {
				selectBoardTile(i, j);
			} else
				game.moveTileFromEqualsPoolToBoard(i, j);
		}

		@Override
		public void traySpaceClicked(int i) {
			ReadOnlyCell cell = game.tray().at(i);
			if (cell.isOccupied())
				selectTrayTile(i);
		}

		@Override
		public void equalsPoolClicked() {
			// Do nothing
		}

	}

	private final class GameOverState extends AbstractState {

		@Override
		public void boardSpaceClicked(int i, int j) {
		}

		@Override
		public void traySpaceClicked(int i) {
		}

		@Override
		public void equalsPoolClicked() {
		}

	}

	private class GameSummaryState extends IgnoreClicksState {

		GameSummaryView summary;

		GameSummaryState(final State next) {
			slideOut(currentSidebar);
			summary = popupGameSummary();
			summary.closeRequested().connect(new UnitSlot() {
				@Override
				public void onEmit() {
					slideIn(currentSidebar);
					slideOutGameSummary(summary);
					enterState(next);
				}
			});
		}

		@Override
		public void scoreBoxClicked() {
			summary.requestClose();
		}
	}

	private final class VariableBindingState extends AbstractState {

		public VariableBindingState(
				final CommitResult.UnboundVariable unboundVariableResult) {
			game.setBindingRequestHandler(new BindingRequestHandler() {
				NumberEntryWidget widget;

				@Override
				public void request(Variable v, final Callback<Integer> binder) {
					slideOut(sidebarTileTray);
					widget = new NumberEntryWidget(unboundVariableResult
							.variable().name(), MAX_NUMBER_ENTRY_DIGITS, iface);
					configureOffscreenSidebar(widget);
					slideIn(widget);

					widget.okClicked().connect(new UnitSlot() {
						@Override
						public void onEmit() {
							slideIn(sidebarTileTray);
							slideOut(widget);
							enterState(new WaitingForClickState());
							int theValue = widget.intValue();
							binder.onSuccess(theValue);
						}

					});
				}

			});

		}

		@Override
		public void boardSpaceClicked(int i, int j) {
		}

		@Override
		public void traySpaceClicked(int i) {
		}

		@Override
		public void equalsPoolClicked() {
		}

	}

	private void slideIn(Sidebar sidebar) {
		playSidebarSwooshSound();
		currentSidebar = sidebar;
		GameConfiguration.instance().animator()//
				.tweenTranslation(sidebar.layer())//
				.to(SIDEBAR_POS)//
				.in(400)//
				.easeIn();
	}

	private void slideOut(Sidebar sidebar) {
		GameConfiguration.instance().animator()//
				.tweenTranslation(sidebar.layer())//
				.to(SIDEBAR_OFFSCREEN_POS)//
				.in(400)//
				.easeIn();
	}

}
