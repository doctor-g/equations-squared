package edu.bsu.pvgestwicki.etschallenge.core.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import edu.bsu.pvgestwicki.etschallenge.core.util.Publisher.Distributor;

public class PublisherTest {

	interface Listener {
		public void onEvent();
	}

	Publisher<Listener> publisher = Publisher.create();
	Listener mockListener = mock(Listener.class);

	/**
	 * Tests that the publisher can be created using type inference as above
	 * with {@link Publisher#create()}. However, I don't think theres any way to
	 * get rid of the explicit <code>Distributor&lt;Listener&gt;</code> below
	 * since we're actually creating an anonymous inner class to do the
	 * distribution. An alternative would be to send a guava function object to
	 * the distributor.
	 */
	@Test
	public void testTypeInference() {
		publisher.subscribe(mockListener);
		publisher.publish(new Distributor<Listener>() {
			@Override
			public void deliverTo(Listener subscriber) {
				subscriber.onEvent();
			}
		});
		verify(mockListener).onEvent();
	}

}
