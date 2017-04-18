package edu.bsu.pvgestwicki.etschallenge.core.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import react.Slot;
import react.ValueView;

public class ScoreTest {

	private Score score = Score.create();

	@Test
	public void testScoreStartsAtZero() {
		assertEquals(0, score.intValue());
	}

	@Test
	public void testAddingToScoreIncreasesIt() {
		addToScore(10);
		assertEquals(10, score.intValue());
	}

	private void addToScore(int i) {
		score.add(10);
	}

	@Test
	public void testAddingToScoreFiresNotification() {
		@SuppressWarnings("unchecked")
		ValueView.Listener<Integer> listener = mock(ValueView.Listener.class);
		score.value().connect(listener);
		addToScore(10);
		verify(listener).onChange(10, 0);
	}

	@Test
	public void testAddingToScoreFiresScoreEvent() {
		@SuppressWarnings("unchecked")
		Slot<Score.Event> slot = mock(Slot.class);
		score.onChange().connect(slot);
		addToScore(10);
		verify(slot).onEmit(any(Score.Event.class));
	}

	@Test
	public void testAddingToScoreWithMessagePostsTheMessageInTheEvent() {
		@SuppressWarnings("unchecked")
		Slot<Score.Event> slot = mock(Slot.class);
		score.onChange().connect(slot);
		addToScore(10, "Message");
		ArgumentCaptor<Score.Event> captor = ArgumentCaptor
				.forClass(Score.Event.class);
		verify(slot).onEmit(captor.capture());
		assertEquals("Message", captor.getValue().message());
	}

	private void addToScore(int points, String message) {
		score.add(points, message);
	}
}
