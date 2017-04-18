package edu.bsu.pvgestwicki.etschallenge.core.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import edu.bsu.pvgestwicki.etschallenge.core.event.EventQueue;

import react.Slot;

public class EventQueueTest {

	private EventQueue queue = EventQueue.create();

	@Test
	public void testQueueStartsEmpty() {
		assertTrue(queue.isEmpty());
	}

	@Test
	public void testAfterAddingAnElementQueueIsNotEmpty() {
		queue.add(mock(EventQueue.Event.class));
		assertFalse(queue.isEmpty());
	}

	@Test
	public void testAddAndRemove() {
		EventQueue.Event event = mock(EventQueue.Event.class);
		queue.add(event);
		assertEquals(event, queue.dequeue());
	}

	@Test
	public void testAddFiresEvent() {
		@SuppressWarnings("unchecked")
		Slot<EventQueue.Event> slot = mock(Slot.class);
		EventQueue.Event event = mock(EventQueue.Event.class);
		queue.added().connect(slot);
		queue.add(event);
		verify(slot).onEmit(event);
	}

}
