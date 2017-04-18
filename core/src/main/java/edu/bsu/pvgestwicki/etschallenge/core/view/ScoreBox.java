package edu.bsu.pvgestwicki.etschallenge.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.graphics;
import playn.core.CanvasImage;
import playn.core.Font;
import playn.core.Gradient;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.Pointer;
import playn.core.Pointer.Event;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.Rectangle;
import react.Signal;
import react.SignalView;
import react.ValueView;
import tripleplay.ui.Constraints;
import tripleplay.ui.Group;
import tripleplay.ui.Interface;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.SimpleStyles;
import tripleplay.ui.Style;
import tripleplay.ui.Style.Binding;
import tripleplay.ui.layout.FlowLayout;
import tripleplay.ui.layout.TableLayout;
import tripleplay.util.PointerInput;
import edu.bsu.pvgestwicki.etschallenge.core.achieve.AchievementTracker;
import edu.bsu.pvgestwicki.etschallenge.core.model.GameModel;

public class ScoreBox {

	private static final IDimension FIXED_SIZE = new Dimension(180, 80);
	private static final int[] GRADIENT_COLORS = {
			Color.SIDEBAR_GREY.intValue(),//
			Color.SIDEBAR_HIGHLIGHT.intValue(),//
			Color.SIDEBAR_GREY.intValue() };

	private static final float[] GRADIENT_POSITIONS = { 0.0f, 0.1f, 0.3f };

	private static final Binding<?>[] SCORE_STYLES = {
			Style.COLOR.is(Color.LIGHT_GREY.intValue()),
			Style.TEXT_EFFECT.vectorOutline,
			Style.HIGHLIGHT.is(Color.COMPLEMENTARY_BLUE.intValue()) };

	private static final Font LABEL_FONT = GameFontName.MONO.createFont(
			Font.Style.PLAIN, 16);

	public static Builder createWith(Interface iface) {
		return new Builder(iface);
	}

	public static final class Builder {
		private final ScoreBox product;

		private Builder(Interface iface) {
			this.product = new ScoreBox(iface);
		}

		public ScoreBox andInput(PointerInput input) {
			product.register(input);
			return product;
		}
	}

	private final GroupLayer layer;

	private final Interface iface;
	private Label scoreLabel;
	private Label digitsRemainingLabel;
	private Signal<ScoreBox> onClick = Signal.create();

	private ScoreBox(Interface iface) {
		this.iface = checkNotNull(iface);
		layer = createLayer();
		initUI();
	}

	private GroupLayer createLayer() {
		Layer background = createGradientBackgroundLayer();
		GroupLayer groupLayer = graphics().createGroupLayer();
		groupLayer.add(background);
		return groupLayer;
	}

	private ImageLayer createGradientBackgroundLayer() {
		CanvasImage canvasImage = graphics().createImage(FIXED_SIZE.width(),
				FIXED_SIZE.height());
		Gradient gradient = graphics().createLinearGradient(0, 0, 0,
				FIXED_SIZE.height(), GRADIENT_COLORS, GRADIENT_POSITIONS);
		canvasImage.canvas().setFillGradient(gradient);
		canvasImage.canvas().fillRect(0, 0, FIXED_SIZE.width(),
				FIXED_SIZE.height());

		return graphics().createImageLayer(canvasImage);
	}

	private void register(PointerInput input) {
		input.register(layer, new Rectangle(0, 0, FIXED_SIZE.width(),
				FIXED_SIZE.height()), new Pointer.Listener() {
			@Override
			public void onPointerStart(Event event) {
				onClick.emit(ScoreBox.this);
			}

			@Override
			public void onPointerEnd(Event event) {
			}

			@Override
			public void onPointerDrag(Event event) {
			}

			@Override
			public void onPointerCancel(Event event) {
			}
			
		});
	}

	private void initUI() {
		createWidgets();
		layoutWidgets();
	}

	private void createWidgets() {
		scoreLabel = new Label("0")//
				.addStyles(
						Style.HALIGN.right,
						Style.FONT.is(GameFontName.MONO.createFont(
								Font.Style.BOLD, 24)))//
				.addStyles(SCORE_STYLES);
		digitsRemainingLabel = createStyledZeroLabel();
	}

	private static Label createStyledZeroLabel() {
		return new Label("0")//
				.addStyles(Style.FONT.is(LABEL_FONT));
	}

	private void layoutWidgets() {
		Gradient gradient = graphics().createLinearGradient(0, 0, 0,
				FIXED_SIZE.height(), GRADIENT_COLORS, GRADIENT_POSITIONS);
		Root root = iface
				.createRoot(new FlowLayout(), SimpleStyles.newSheet(), layer)
				.setSize(FIXED_SIZE.width(), FIXED_SIZE.height())
				.addStyles(
						Style.BACKGROUND.is(BorderedGradientBackground
								.withGradient(gradient)));
		Group mainGroup = makeMainGroup();
		root.add(mainGroup);
		layer.add(root.layer);
	}

	private Group makeMainGroup() {
		return new Group(new TableLayout(TableLayout.COL, //
				TableLayout.COL.fixed().alignRight())//
				.gaps(6, 0))//
				.add(createScoreLabel(),//
						scoreLabel//
								.setConstraint(Constraints
										.fixedWidth(FIXED_SIZE.width() / 3)),
						createRemainingDigitsLabel(),//
						digitsRemainingLabel);
	}

	public void attachTo(GameModel game) {
		game.score().value().connect(new FieldUpdater(scoreLabel));
		game.filler().digitsRemaining()
				.connect(new FieldUpdater(digitsRemainingLabel));
	}

	public void attachTo(AchievementTracker tracker) {

	}

	private Label createScoreLabel() {
		return new Label("Score:")//
				.addStyles(
						Style.FONT.is(GameFontName.SANS.createFont(
								Font.Style.BOLD, 24)))//
				.addStyles(SCORE_STYLES);
	}

	private Label createRemainingDigitsLabel() {
		return new Label("Digits left:")//
				.addStyles(Style.FONT.is(GameFontName.SANS.createFont(
						Font.Style.PLAIN, 16)));
	}

	public GroupLayer layer() {
		return layer;
	}

	public SignalView<ScoreBox> onClick() {
		return onClick;
	}

	private static final class FieldUpdater extends ValueView.Listener<Integer> {
		private final Label label;

		public FieldUpdater(Label label) {
			this.label = checkNotNull(label);
		}

		@Override
		public void onChange(Integer value, Integer oldValue) {
			label.text.update(String.valueOf(value));
		}
	}

}
