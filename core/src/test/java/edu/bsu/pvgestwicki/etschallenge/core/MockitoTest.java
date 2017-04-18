package edu.bsu.pvgestwicki.etschallenge.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class MockitoTest {

	@Test
	public void testInOrderOnMockWithDifferentSpecificArgumentTypes() {
		C c = mock(C.class);
		c.bar(new J());
		c.bar(new J());
		c.bar(new K());
		ArgumentCaptor<I> argumentCaptor = ArgumentCaptor.forClass(I.class);
		verify(c, times(3)).bar(argumentCaptor.capture());
		assertEquals(3, argumentCaptor.getAllValues().size());
		assertTrue(argumentCaptor.getAllValues().get(0) instanceof J);
		assertTrue(argumentCaptor.getAllValues().get(1) instanceof J);
		assertTrue(argumentCaptor.getAllValues().get(2) instanceof K);
	}
	
	class C {
		void bar(I i) {}
	}
	
	class I {
		void foo() {}
	}
	
	class J extends I {}
	class K extends I {}
	
}

