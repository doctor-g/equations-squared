package edu.bsu.pvgestwicki.etschallenge.core.event;

import java.util.List;

import react.Signal;
import react.SignalView;

import com.google.common.collect.Lists;

public final class EventQueue {

	public interface Event {
		public void handle();
	}

	public static EventQueue create() {
		return new EventQueue();
	}

	private final List<Event> queue = Lists.newLinkedList();
	private final Signal<Event> added = Signal.create();

	public EventQueue add(Event e) {
		queue.add(e);
		added.emit(e);
		return this;
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public Event dequeue() {
		return queue.remove(0);
	}

	public SignalView<EventQueue.Event> added() {
		return added;
	}

}