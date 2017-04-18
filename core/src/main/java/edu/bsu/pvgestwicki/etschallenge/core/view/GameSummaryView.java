package edu.bsu.pvgestwicki.etschallenge.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.graphics;

import java.util.List;

import playn.core.Font;
import playn.core.Gradient;
import playn.core.GroupLayer;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import react.SignalView;
import react.UnitSignal;
import react.UnitSlot;
import tripleplay.ui.Background;
import tripleplay.ui.Button;
import tripleplay.ui.Constraints;
import tripleplay.ui.Element;
import tripleplay.ui.Group;
import tripleplay.ui.Interface;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.SimpleStyles;
import tripleplay.ui.Style;
import tripleplay.ui.Styles;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.layout.BorderLayout;
import tripleplay.ui.layout.FlowLayout;
import tripleplay.ui.layout.TableLayout;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;

import edu.bsu.pvgestwicki.etschallenge.core.achieve.Achievement;
import edu.bsu.pvgestwicki.etschallenge.core.achieve.AchievementTracker;
import edu.bsu.pvgestwicki.etschallenge.core.achieve.Badge;
import edu.bsu.pvgestwicki.etschallenge.core.achieve.Demerit;

public final class GameSummaryView {

	private static final IDimension FIXED_SIZE = new Dimension(650, 440);
	private static final float COLUMN_WIDTH = 275;

	public static Builder builder() {
		return new Builder();
	}

	public final static class Builder {
		private AchievementTracker tracker;
		private Interface iface;

		public Builder withAchievements(AchievementTracker tracker) {
			this.tracker = checkNotNull(tracker);
			return this;
		}

		public Builder withInterface(Interface iface) {
			this.iface = iface;
			return this;
		}

		public GameSummaryView build() {
			return new GameSummaryView(this);
		}
	}

	private GroupLayer layer;
	private UnitSignal closeRequested = new UnitSignal();

	private Gradient backgroundGradient = graphics().createLinearGradient(0, 0,
			0, FIXED_SIZE.height(), new int[] { Color.SIDEBAR_GREY.intValue(),//
					Color.SIDEBAR_HIGHLIGHT.intValue(), //
					Color.SIDEBAR_GREY.intValue(),//
					Color.SIDEBAR_GREY.intValue(),//
					RGB.lighter(Color.SIDEBAR_GREY.intValue()) },
			new float[] { 0f, 0.15f, 0.35f, 0.7f, 1f });

	private Gradient headerGradient = graphics().createLinearGradient(
			0,
			0,
			0,
			50,
			new int[] { RGB.lighter(Color.COMPLEMENTARY_BLUE.intValue()),
					Color.COMPLEMENTARY_BLUE.intValue(),
					Color.COMPLEMENTARY_BLUE.intValue(),
					RGB.lighter(Color.COMPLEMENTARY_BLUE.intValue()) },
			new float[] { 0f, 0.15f, 0.8f, 1f });

	private GameSummaryView(Builder builder) {
		layer = graphics().createGroupLayer();

		Root root = builder.iface
				.createRoot(new BorderLayout(),//
						SimpleStyles.newSheet(), layer)
				.setSize(FIXED_SIZE.width(), FIXED_SIZE.height())
				.addStyles(
						Style.BACKGROUND.is(BorderedGradientBackground
								.withGradient(backgroundGradient)));

		Group head = new Group(AxisLayout.vertical().offStretch())//
				.add(new Label("Game Summary")//
						.addStyles(Style.FONT.is(GameFontName.SANS.createFont(
								Font.Style.BOLD, 28)), //
								Style.BACKGROUND.is(BorderedGradientBackground
										.withGradient(headerGradient).inset(12,
												0, 4, 0)), Style.COLOR
										.is(Color.LIGHT_GREY.intValue())))//
				.setConstraint(BorderLayout.NORTH);
		root.add(head);

		root.add(new AchievementTable(builder.tracker)//
				.setConstraint(BorderLayout.CENTER));

		Button closeButton = new Button("Close")//
				.addStyles(
						Style.ACTION_SOUND.is(GameSound.CLICK.sound),
						Style.FONT.is(//
								GameFontName.SANS.createFont(Font.Style.PLAIN,
										16)));
		closeButton.clicked().connect(new UnitSlot() {
			@Override
			public void onEmit() {
				closeRequested.emit();
			}
		});

		root.add(wrapInAFlowLayoutToPreventStretching(closeButton)//
				.setConstraint(BorderLayout.SOUTH)//
				.addStyles(Style.BACKGROUND.is(//
						Background.blank().inset(0, 0, 11, 0))));
	}

	private Element<?> wrapInAFlowLayoutToPreventStretching(Element<?> element) {
		return new Group(new FlowLayout()).add(element);
	}

	public GroupLayer layer() {
		return layer;
	}

	public SignalView<Void> closeRequested() {
		return closeRequested;
	}

	private static final class AchievementTable extends Group {

		private static final GameFontName FONT_NAME = GameFontName.SANS;
		private static final Font HEADER_FONT = FONT_NAME.createFont(
				Font.Style.BOLD, 18);
		private static final Font ACHIEVEMENT_TITLE_FONT = FONT_NAME
				.createFont(Font.Style.BOLD, 14);
		private static final Font ACHIEVEMENT_DESCRIPTION_FONT = FONT_NAME
				.createFont(Font.Style.ITALIC, 12);

		private static final Styles HEADER_STYLES = Styles.make(Style.FONT
				.is(HEADER_FONT));
		private static final Styles ACHIEVEMENT_TITLE_STYLES = Styles.make(//
				Style.HALIGN.left,//
				Style.FONT.is(ACHIEVEMENT_TITLE_FONT));
		private static final Styles ACHIEVEMENT_DESCRIPTION_STYLES = Styles
				.make(//
				Style.HALIGN.left,//
						Style.FONT.is(ACHIEVEMENT_DESCRIPTION_FONT));

		private static final Gradient gradient = graphics()
				.createLinearGradient(
						0,
						0,
						0,
						100,
						new int[] { RGB.lighter(Color.DULL_ORANGE.intValue()),
								Color.DULL_ORANGE.intValue(),
								Color.DULL_ORANGE.intValue(),
								RGB.lighter(Color.DULL_ORANGE.intValue()) },
						new float[] { 0f, 0.15f, 0.85f, 1f });
		private static final BorderedGradientPainter painter = BorderedGradientPainter
				.withGradient(gradient)//
				.andThickness(0f)//
				.andRadius(0f);

		private List<Badge> badgeSequence;
		private List<Demerit> demeritSequence;

		public AchievementTable(AchievementTracker tracker) {
			super(new TableLayout(2).alignTop());

			initFieldsFrom(tracker);
			add(makeColumn("Badges", badgeSequence, tracker.badges()));
			add(makeColumn("Demerits", demeritSequence, tracker.demerits()));
		}

		private void initFieldsFrom(AchievementTracker tracker) {
			this.badgeSequence = ImmutableList.copyOf(tracker.badges()
					.elementSet());
			this.demeritSequence = ImmutableList.copyOf(tracker.demerits()
					.elementSet());
		}

		private Group makeColumn(String columnHeading,
				List<? extends Achievement> sequence,
				ImmutableMultiset<? extends Achievement> bag) {
			Group column = new Group(AxisLayout.vertical().offStretch())
					.setConstraint(Constraints.fixedWidth(COLUMN_WIDTH));

			column.add(new Label(columnHeading)//
					.setStyles(HEADER_STYLES));

			if (sequence.size() == 0)
				column.add(new Label("None")//
						.addStyles(ACHIEVEMENT_DESCRIPTION_STYLES)//
						.addStyles(Style.HALIGN.center));
			else {
				for (Achievement achievement : sequence) {
					column.add(createAchievementWidget(achievement,
							bag.count(achievement)));
				}
			}
			return column;
		}

		private Element<?> createAchievementWidget(Achievement achievement,
				int count) {
			return new Group(AxisLayout.vertical().offStretch()).add(
					new Label(createAchievementTitleText(achievement, count))//
							.setStyles(ACHIEVEMENT_TITLE_STYLES),//
					new Label(achievement.description())//
							.setStyles(ACHIEVEMENT_DESCRIPTION_STYLES))//
					.addStyles(
							Style.BACKGROUND.is(BorderedGradientBackground
									.withPainter(painter)));
		}

		private String createAchievementTitleText(Achievement achievement,
				int count) {
			String tmp = achievement.friendlyName();
			if (count > 1)
				return tmp + " (x" + count + ")";
			else
				return tmp;
		}

	}

	public void requestClose() {
		closeRequested.emit();
	}
}
