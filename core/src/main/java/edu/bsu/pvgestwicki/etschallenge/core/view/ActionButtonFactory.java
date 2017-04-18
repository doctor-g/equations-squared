package edu.bsu.pvgestwicki.etschallenge.core.view;

import react.Slot;
import tripleplay.ui.Button;
import tripleplay.ui.Styles;
import edu.bsu.pvgestwicki.etschallenge.core.AlgebraGame;
import edu.bsu.pvgestwicki.etschallenge.core.event.EventQueue.Event;
import edu.bsu.pvgestwicki.etschallenge.core.event.EventQueuePostingSlot;
import edu.bsu.pvgestwicki.etschallenge.core.util.Action;

public final class ActionButtonFactory {

	private static final ActionButtonFactory SINGLETON = new ActionButtonFactory();

	public static ActionButtonFactory instance() {
		return SINGLETON;
	}

	private Styles styles = Styles.none();

	public Button createActionButton(final Action action) {
		final Button b = new Button(action.name()).addStyles(styles);
		b.setEnabled(action.enabled().get());
		action.enabled().connect(new Slot<Boolean>() {
			@Override
			public void onEmit(final Boolean enabled) {
				AlgebraGame.eventQueue().add(new Event() {
					@Override
					public void handle() {
						b.setEnabled(enabled);
					}
					@Override
					public String toString() {
						return action.name() + (enabled ? "enabled" : "disabled");
					}
				});
			}
		});
		b.clicked().connect(new EventQueuePostingSlot<Button>() {
			@Override
			public void handle() {
				action.run();
			}
			@Override
			public String toString() {
				return "Button clicked for action " + action.name();
			}
		});
		return b;
	}

	public ActionButtonFactory withStyles(Styles buttonStyles) {
		this.styles = buttonStyles;
		return this;
	}

}
