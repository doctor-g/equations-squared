package edu.bsu.pvgestwicki.etschallenge.core.view;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static playn.core.PlayN.graphics;
import playn.core.Font;
import playn.core.GroupLayer;
import playn.core.Sound;
import tripleplay.ui.Interface;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.SimpleStyles;
import tripleplay.ui.Style;
import tripleplay.ui.Styles;
import tripleplay.ui.layout.FlowLayout;

public final class PopupText {

	public static final class Builder {
		private String mesg;
		private Interface iface;
		private Sound sound;

		public Builder withText(String mesg) {
			this.mesg = checkNotNull(mesg);
			return this;
		}

		public Builder onInterface(Interface iface) {
			this.iface = checkNotNull(iface);
			return this;
		}

		public Builder withSound(Sound sound) {
			this.sound = checkNotNull(sound);
			return this;
		}

		public PopupText build() {
			checkState(mesg != null);
			checkState(iface != null);
			return new PopupText(this);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	private static final Styles STYLE = Styles.make(
			Style.FONT.is(GameFontName.SANS.createFont(Font.Style.PLAIN, 32)),//
			Style.COLOR.is(Color.LIGHT_GREY.intValue()),//
			Style.HALIGN.center,//
			Style.TEXT_EFFECT.pixelOutline,
			Style.HIGHLIGHT.is(Color.COMPLEMENTARY_BLUE.intValue()),
			Style.OUTLINE_WIDTH.is(2f));

	private GroupLayer layer;
	private final Interface iface;
	private final String mesg;
	private final Sound sound;

	private PopupText(Builder builder) {
		this.iface = builder.iface;
		this.mesg = builder.mesg;
		this.sound = builder.sound;
		init();
	}

	private void init() {
		layer = graphics().createGroupLayer();
		Root root = iface.createRoot(new FlowLayout(), SimpleStyles.newSheet(),
				layer);
		Label label = new Label(mesg).addStyles(STYLE);
		root.add(label);
	}

	public GroupLayer layer() {
		return layer;
	}

	public void playSound() {
		if (sound != null)
			sound.play();
	}
}
