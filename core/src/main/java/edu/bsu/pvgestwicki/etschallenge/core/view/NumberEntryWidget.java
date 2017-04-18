package edu.bsu.pvgestwicki.etschallenge.core.view;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static playn.core.PlayN.graphics;

import java.util.Map;

import playn.core.Font;
import playn.core.GroupLayer;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import react.SignalView;
import react.Slot;
import react.UnitSignal;
import react.Value;
import tripleplay.ui.Background;
import tripleplay.ui.Button;
import tripleplay.ui.Group;
import tripleplay.ui.Interface;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.SimpleStyles;
import tripleplay.ui.Style;
import tripleplay.ui.Styles;
import tripleplay.ui.layout.AbsoluteLayout;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.layout.TableLayout;

import com.google.common.collect.DiscreteDomains;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ranges;

import edu.bsu.pvgestwicki.etschallenge.core.event.EventQueuePostingSlot;
import edu.bsu.pvgestwicki.etschallenge.core.util.DigitCounter;
import edu.bsu.pvgestwicki.etschallenge.core.util.SpecialChar;

public final class NumberEntryWidget implements Sidebar {

	private static final IDimension FIXED_SIZE = new Dimension(180, 486);

	private static final Font TOP_LABEL_FONT = GameFontName.SANS.createFont(
			Font.Style.PLAIN, 24);
	private static final Font BUTTON_FONT = GameFontName.MONO.createFont(
			Font.Style.PLAIN, 32);
	private static final Font OK_BUTTON_FONT = GameFontName.SANS.createFont(
			Font.Style.PLAIN, 24);
	private static final Font VALUE_FONT = BUTTON_FONT;

	private static final Styles TOP_LABEL_STYLE = Styles.make(//
			Style.FONT.is(TOP_LABEL_FONT));
	private static final Styles BUTTON_STYLE = Styles.make(
			Style.FONT.is(BUTTON_FONT),
			Style.ACTION_SOUND.is(GameSound.CLICK.sound));
	private static final Styles OK_BUTTON_STYLE = Styles.make(
			Style.FONT.is(OK_BUTTON_FONT),
			Style.ACTION_SOUND.is(GameSound.CLICK.sound));
	private static final Styles VALUE_STYLE = Styles.make(//
			Style.FONT.is(VALUE_FONT),//
			Style.HALIGN.right,//
			Style.BACKGROUND.is(Background.bordered(
					edu.bsu.pvgestwicki.etschallenge.core.view.Color.MED_GREY
							.intValue(),
					edu.bsu.pvgestwicki.etschallenge.core.view.Color.DARK_GREY
							.intValue(), 2).inset(5f)));

	private final int maxDigits;
	private final String variableName;
	private Value<Integer> value = Value.create(0);
	private Group group;
	private Map<Integer, Button> digitButtons;
	private UnitSignal okClicked = new UnitSignal();

	private GroupLayer layer;

	public NumberEntryWidget(String variableName, int maxDigits, Interface iface) {
		this.variableName = checkNotNull(variableName);
		checkArgument(maxDigits > 0);
		this.maxDigits = maxDigits;
		createValue();
		initLayout();

		layer = graphics().createGroupLayer();
		Root root = iface.createRoot(new AbsoluteLayout(),
				SimpleStyles.newSheet(), layer)//
				.setSize(FIXED_SIZE.width(), FIXED_SIZE.height());
		root.add(AbsoluteLayout.at(group, 0, 0, FIXED_SIZE.width(),
				FIXED_SIZE.height()));
	}

	private void createValue() {
		value = Value.create(0);
		value.connect(new Slot<Integer>() {
			@Override
			public void onEmit(Integer event) {
				updateDigitButtonStatesBasedOnDigitsInValue();
			}
		});
	}

	private void initLayout() {
		digitButtons = createDigitButtonMap();
		Button plusMinusButton = createPlusMinusButton();
		Button clearButton = createClearButton();

		Group buttonTable = new Group(new TableLayout(
				TableLayout.COL.stretch(), TableLayout.COL.stretch())//
				.gaps(6, 6)).add(digitButtons.get(1))//
				.add(digitButtons.get(6))//
				.add(digitButtons.get(2))//
				.add(digitButtons.get(7))//
				.add(digitButtons.get(3))//
				.add(digitButtons.get(8))//
				.add(digitButtons.get(4))//
				.add(digitButtons.get(9))//
				.add(digitButtons.get(5))//
				.add(digitButtons.get(0))//
				.add(plusMinusButton)//
				.add(clearButton);

		this.group = new Group(AxisLayout.vertical().offEqualize())
				.setStyles(
						Style.BACKGROUND.is(BorderedGradientBackground
								.withDefaultsFor(FIXED_SIZE)))
				.add(new Label("Value of " + variableName)
						.setStyles(TOP_LABEL_STYLE))//
				.add(makeValueLabel())//
				.add(buttonTable)//
				.add(createOKButton());
	}

	private Map<Integer, Button> createDigitButtonMap() {
		ImmutableMap.Builder<Integer, Button> builder = ImmutableMap.builder();
		for (int i : allDigits()) {
			Button b = makeDigitButton(i);
			builder.put(i, b);
		}
		return builder.build();
	}

	private Iterable<Integer> allDigits() {
		return Ranges.closed(0, 9).asSet(DiscreteDomains.integers());
	}

	private Button makeDigitButton(final int i) {
		Button b = makeStyledButton("" + i);
		b.clicked().connect(new EventQueuePostingSlot<Button>() {
			@Override
			public void handle() {
				int oldValue = value.get();
				int newValue = oldValue * 10 + i;
				value.update(newValue);
			}

			@Override
			public String toString() {
				return i + " clicked";
			}
		});
		return b;
	}

	private Button makeStyledButton(String label) {
		return new Button(label).addStyles(BUTTON_STYLE);
	}

	private void updateDigitButtonStatesBasedOnDigitsInValue() {
		if (valueDigitsIsMaxDigits()) {
			disableButtons();
		} else
			enableButtons();
	}

	private boolean valueDigitsIsMaxDigits() {
		int numDigits = DigitCounter.instance().countDigitsIn(value.get());
		return maxDigits == numDigits;
	}

	private void disableButtons() {
		for (Button b : digitButtons.values())
			b.setEnabled(false);
	}

	private void enableButtons() {
		for (Button b : digitButtons.values()) {
			b.setEnabled(true);
		}
	}

	private Label makeValueLabel() {
		final Label label = new Label(padToMaxDigits(0)).setStyles(VALUE_STYLE);
		value.connect(new Slot<Integer>() {
			@Override
			public void onEmit(Integer newValue) {
				label.text.update(padToMaxDigits(newValue));
			}
		});
		return label;
	}

	private String padToMaxDigits(int v) {
		String s = String.valueOf(v);
		while (s.length() != maxCharsAccountingForNegationSign()) {
			// Tripleplay trims the label strings!
			s = '\u00A0' + s;
		}
		return s;
	}

	private final int maxCharsAccountingForNegationSign() {
		return maxDigits + 1;
	}

	private Button createPlusMinusButton() {
		Button b = makeStyledButton("" + SpecialChar.PLUSMINUS.asCharacter());
		b.clicked().connect(new EventQueuePostingSlot<Button>() {
			@Override
			public void handle() {
				value.update(-value.get());
			}

			@Override
			public String toString() {
				return "+/- clicked";
			}

		});
		return b;
	}

	private Button createClearButton() {
		Button b = makeStyledButton("C");
		b.clicked().connect(new EventQueuePostingSlot<Button>() {
			@Override
			public void handle() {
				value.update(0);
			}

			@Override
			public String toString() {
				return "Clear number entry widget clicked";
			}
		});
		return b;
	}

	private Button createOKButton() {
		final Button b = new Button("OK").addStyles(OK_BUTTON_STYLE);
		b.clicked().connect(new EventQueuePostingSlot<Button>() {
			@Override
			public void handle() {
				b.setEnabled(false);
				okClicked.emit();
			}

			@Override
			public String toString() {
				return "OK clicked";
			}
		});
		return b;
	}

	public SignalView<Void> okClicked() {
		return okClicked;
	}

	public int intValue() {
		return value.get();
	}

	public GroupLayer layer() {
		return layer;
	}
}
