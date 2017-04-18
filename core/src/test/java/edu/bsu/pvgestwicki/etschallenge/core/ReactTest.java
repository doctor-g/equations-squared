package edu.bsu.pvgestwicki.etschallenge.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import react.Slot;
import react.Value;

/**
 * Tests for the <a href="https://github.com/threerings/react">React</a>
 * library.
 * 
 * @author <a href="http://www.cs.bsu.edu/~pvg">Paul Gestwicki</a>
 */
public class ReactTest {

	@Test
	public void testSlotAndSignal() {
		@SuppressWarnings("unchecked")
		Slot<Integer> slot = mock(Slot.class);
		Value<Integer> value = new Value<Integer>(10);
		value.connect(slot);
		value.update(15);
		verify(slot).onChange(15, 10);
	}

}
