package edu.bsu.pvgestwicki.etschallenge.core.event;

import react.Slot;
import edu.bsu.pvgestwicki.etschallenge.core.AlgebraGame;
import edu.bsu.pvgestwicki.etschallenge.core.event.EventQueue.Event;

public abstract class EventQueuePostingSlot<T> extends Slot<T> {

	@Override
	public final void onEmit(T event) {
		AlgebraGame.eventQueue().add(new Event() {
			@Override
			public void handle() {
				EventQueuePostingSlot.this.handle();
			}

			@Override
			public String toString() {
				return EventQueuePostingSlot.this.toString();
			}
			
		});
	}
	
	public abstract void handle();
}
