package edu.bsu.pvgestwicki.etschallenge.core.model;

import react.Signal;
import react.SignalView;
import react.Value;
import react.ValueView;

public final class Score {

	public static final class Event {

		private final String mesg;

		private Event(String mesg) {
			this.mesg = mesg;
		}

		public String message() {
			return mesg;
		}

		@Override
		public String toString() {
			return mesg;
		}
	}

	public static Score create() {
		return new Score();
	}

	private Value<Integer> value = Value.create(0);
	private Signal<Event> onChange = Signal.create();

	private Score() {
	}

	public int intValue() {
		return value.get();
	}

	public void add(int points) {
		add(points, stringify(points));
	}

	private String stringify(int points) {
		return ((points > 0) ? "+" : "-") + points;
	}

	public ValueView<Integer> value() {
		return value;
	}

	public SignalView<Event> onChange() {
		return onChange;
	}

	public void add(int points, String message) {
		int oldValue = value.get();
		value.update(oldValue + points);
		onChange.emit(new Event(message));
	}

}
