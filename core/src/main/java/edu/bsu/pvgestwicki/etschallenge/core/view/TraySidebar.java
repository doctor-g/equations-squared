package edu.bsu.pvgestwicki.etschallenge.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static playn.core.PlayN.graphics;

import java.util.Map;

import playn.core.CanvasImage;
import playn.core.Font;
import playn.core.Gradient;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Pointer;
import playn.core.Pointer.Event;
import playn.core.util.Callback;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.IRectangle;
import pythagoras.f.Point;
import pythagoras.f.Rectangle;
import react.Slot;
import react.ValueView;
import react.ValueView.Listener;
import tripleplay.ui.Button;
import tripleplay.ui.Element;
import tripleplay.ui.Group;
import tripleplay.ui.Interface;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.SimpleStyles;
import tripleplay.ui.Style;
import tripleplay.ui.Styles;
import tripleplay.ui.layout.AbsoluteLayout;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.PointerInput;

import com.google.common.collect.Maps;

import edu.bsu.pvgestwicki.etschallenge.core.AlgebraGame;
import edu.bsu.pvgestwicki.etschallenge.core.event.EventQueue;
import edu.bsu.pvgestwicki.etschallenge.core.event.TrayTileEvent;
import edu.bsu.pvgestwicki.etschallenge.core.model.GameModel;
import edu.bsu.pvgestwicki.etschallenge.core.model.TileModel;

public final class TraySidebar implements Sidebar {

	private static final Font BUTTON_FONT = GameFontName.SANS.createFont(
			Font.Style.PLAIN, 24);

	public static class Builder {
		private Dimension tileSize;
		private PointerInput pointerInput;
		private Callback<Integer> tileClickedcallback;
		private Callback<Void> equalsClickedCallback;
		private Map<TileModel, TileView> registry;
		private GameModel game;
		private Interface iface;

		public Builder tileSize(IDimension size) {
			checkState(tileSize == null, "Size already specified");
			this.tileSize = new Dimension(size);
			return this;
		}

		public Builder pointerInput(PointerInput input) {
			this.pointerInput = checkNotNull(input);
			return this;
		}

		public Builder tileClickedCallback(Callback<Integer> callback) {
			this.tileClickedcallback = checkNotNull(callback);
			return this;
		}

		public Builder equalsClickedCallback(Callback<Void> callback) {
			this.equalsClickedCallback = checkNotNull(callback);
			return this;
		}

		public Builder tileRegistry(Map<TileModel, TileView> registry) {
			this.registry = registry;
			return this;
		}

		public Builder game(GameModel game) {
			this.game = checkNotNull(game);
			return this;
		}

		public Builder tripleplayInterface(Interface iface) {
			this.iface = checkNotNull(iface);
			return this;
		}

		public TraySidebar build() {
			return new TraySidebar(this);
		}
	}

	private static final Styles COMMAND_BUTTON_STYLES = Styles.make(//
			Style.FONT.is(BUTTON_FONT),//
			Style.HALIGN.center,//
			Style.ACTION_SOUND.is(GameSound.CLICK.sound));

	private static final IDimension FIXED_SIZE = new Dimension(180, 486);
	private static final float TOP_OF_CONTROL_BUTTONS = 345;
	private static final IPoint FIRST_TILE_POS = new Point(31, 23);
	private static final float PADDING = 8;
	private static final int COLUMNS = 2;
	private static final int NUM_TILES = 8;

	public static Builder builder() {
		return new Builder();
	}

	private GroupLayer layer;
	private final Builder builder;
	private TileHolderFactory tileHolderFactory;
	private Map<Integer, IRectangle> tileBoundsMap = Maps.newHashMap();
	private final IRectangle equalsPoolBounds;

	private TraySidebar(final Builder builder) {
		this.builder = checkNotNull(builder);
		layer = graphics().createGroupLayer();
		initTripleplayUI();

		equalsPoolBounds = new Rectangle(centerUnderTileGrid().x(),
				centerUnderTileGrid().y(), builder.tileSize.width,
				builder.tileSize.height);

		IDimension dimension = FIXED_SIZE;

		precomputeAllTileBounds();
		CanvasImage image = graphics().createImage(dimension.width(),
				dimension.height());
		layer.add(graphics().createImageLayer(image));

		tileHolderFactory = TileHolderFactory.forSize(new Dimension(55, 55))
				.outlineStyle();
		tileHolderFactory.setTileSize(builder.tileSize);

		for (int i = 0; i < NUM_TILES; i++) {
			final int tileIndex = i;
			ImageLayer tileHolderLayer = tileHolderFactory.createImageLayer();
			IRectangle bounds = boundsOfTile(i);
			tileHolderLayer.setTranslation(bounds.x(), bounds.y());
			layer.add(tileHolderLayer);

			builder.pointerInput.register(tileHolderLayer,
					new Pointer.Adapter() {
						@Override
						public void onPointerStart(Event event) {
							AlgebraGame.eventQueue().add(
									new EventQueue.Event() {
										@Override
										public void handle() {
											builder.tileClickedcallback
													.onSuccess(tileIndex);
										}
										@Override public String toString() {
											return "Tile " + tileIndex + " in tray clicked";
										}
									});
						}
					});
		}

		initEqualsPool();
	}

	private IPoint centerUnderTileGrid() {
		int rows = NUM_TILES / COLUMNS;
		float y = FIRST_TILE_POS.y() + rows
				* (builder.tileSize.height + PADDING);
		float x = FIRST_TILE_POS.x() + (builder.tileSize.width + PADDING) / 2;
		return new Point(x, y);
	}

	private void precomputeAllTileBounds() {
		for (int i = 0; i < NUM_TILES; i++)
			precomputeAndStoreTileBounds(i);
	}

	private void precomputeAndStoreTileBounds(int index) {
		int row = index / COLUMNS;
		int col = index % COLUMNS;

		float x = col * (builder.tileSize.width + PADDING) + FIRST_TILE_POS.x();
		float y = row * (builder.tileSize.height + PADDING)
				+ FIRST_TILE_POS.y();

		Rectangle bounds = new Rectangle(x, y,//
				builder.tileSize.width,//
				builder.tileSize.height);
		tileBoundsMap.put(index, bounds);
	}

	private void initEqualsPool() {
		ImageLayer imageLayer = tileHolderFactory.createImageLayer();
		ImageLayerBounds.set(imageLayer).to(equalsPoolBounds());
		layer.add(imageLayer);

		builder.pointerInput.register(imageLayer, new Pointer.Adapter() {
			@Override
			public void onPointerStart(Event event) {
				AlgebraGame.eventQueue().add(new EventQueue.Event() {
					@Override
					public void handle() {
						builder.equalsClickedCallback.onSuccess(null);
					}
					@Override
					public String toString() {
						return "Equals clicked";
					}
				});
			}
		});
	}

	private void initTripleplayUI() {
		Gradient gradient = graphics().createLinearGradient(
				0,
				0,
				0,
				FIXED_SIZE.height(),
				new int[] { Color.SIDEBAR_GREY.intValue(),
						Color.SIDEBAR_HIGHLIGHT.intValue(),
						Color.SIDEBAR_GREY.intValue() },
				new float[] { 0.05f, 0.2f, 0.4f });

		Root root = builder.iface
				.createRoot(new AbsoluteLayout(), SimpleStyles.newSheet(),
						layer)//
				.setSize(FIXED_SIZE.width(), FIXED_SIZE.height())
				//
				.addStyles(
						Style.BACKGROUND.is(BorderedGradientBackground
								.withGradient(gradient)));

		root.add(AbsoluteLayout.at(createControlButtonGroup(),//
				0, TOP_OF_CONTROL_BUTTONS, //
				FIXED_SIZE.width(), tallEnoughToContainButtons()));
	}

	private Group createControlButtonGroup() {
		final GameModel game = builder.game;
		ActionButtonFactory factory = ActionButtonFactory.instance()
				.withStyles(COMMAND_BUTTON_STYLES);
		Button clearButton = factory.createActionButton(game.resetTurnAction());
		Button puntButton = factory.createActionButton(game.puntAction());
		Button commitButton = factory.createActionButton(game.commitAction());

		Group group = new Group(AxisLayout.vertical().offEqualize().gap(6))//
				.addStyles(Style.HALIGN.center, Style.VALIGN.top)//
				.add(commitButton)//
				.add(clearButton)//
				.add(puntButton);

		return group;
	}

	private float tallEnoughToContainButtons() {
		return 200;
	}

	public IRectangle boundsOfTile(int index) {
		return tileBoundsMap.get(index);
	}

	public IRectangle equalsPoolBounds() {
		return equalsPoolBounds;
	}

	public GroupLayer layer() {
		return layer;
	}

	public void hookup(GameModel game) {
		game.onTileAddedFromBag().connect(new Slot<TrayTileEvent>() {
			@Override
			public void onEmit(TrayTileEvent event) {
				makeAndPlaceTile(event.tile(), boundsOfTile(event.position()));
			}
		});

		game.tray().onTileDiscarded().connect(new Slot<TileModel>() {
			@Override
			public void onEmit(TileModel tile) {
				TileView view = builder.registry.get(tile);
				view.layer().destroy();
				builder.registry.remove(tile);
			}
		});

		game.equalsPool().onNewTopTile().connect(new Slot<TileModel>() {
			@Override
			public void onEmit(TileModel tile) {
				makeAndPlaceTile(tile, equalsPoolBounds());
			}
		});
	}

	private void makeAndPlaceTile(TileModel tile, IRectangle bounds) {
		TileView tileView = TileView.ofTile(tile);
		ImageLayerBounds//
				.set(tileView.layer())//
				.to(bounds);
		layer.add(tileView.layer());
		builder.registry.put(tile, tileView);
	}
}

final class QuantityGroupBuilder {
	private static final Font LABEL_FONT = GameFontName.MONO.createFont(
			Font.Style.PLAIN, 16);

	public static QuantityGroupBuilder forValue(ValueView<Integer> value) {
		return new QuantityGroupBuilder(value);
	}

	private final ValueView<Integer> value;

	private QuantityGroupBuilder(ValueView<Integer> value) {
		this.value = checkNotNull(value);
	}

	public Group andLabel(String text) {
		Group group = new Group(AxisLayout.vertical())//
				.addStyles(Style.HALIGN.center)//
				.add(createLabelFor(text))//
				.add(createValueLabel());
		return group;
	}

	private Element<?> createLabelFor(String text) {
		return new Label(text).addStyles(Style.FONT.is(LABEL_FONT));
	}

	private Element<?> createValueLabel() {
		final Label label = new Label(value.get().toString())//
				.addStyles(Style.FONT.is(LABEL_FONT));
		value.connect(new Listener<Integer>() {
			@Override
			public void onChange(Integer value, Integer oldValue) {
				label.text.update(value.toString());
			}
		});
		return label;
	}
}
