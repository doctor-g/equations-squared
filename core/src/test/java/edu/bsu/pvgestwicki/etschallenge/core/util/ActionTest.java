package edu.bsu.pvgestwicki.etschallenge.core.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import react.Slot;

public class ActionTest {

	@Test
	public void testDisableActionFiresNotification() {
		Action action = new DoNothingAction();
		@SuppressWarnings("unchecked")
		Slot<Boolean> slot = (Slot<Boolean>) mock(Slot.class);
		action.enabled().connect(slot);
		action.disable();
		verify(slot).onChange(false, true);
	}

	private class DoNothingAction extends Action {

		public DoNothingAction() {
			super("Do Nothing");
		}

		@Override
		public void run() {
			// Do nothing.
		}

	}
}
