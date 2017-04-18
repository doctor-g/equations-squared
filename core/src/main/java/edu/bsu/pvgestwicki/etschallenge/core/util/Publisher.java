package edu.bsu.pvgestwicki.etschallenge.core.util;

/**
 * Provides an efficient, thread-safe means of notifying listeners of an event.
 * This technique has been taken from Allen Holub's book <b>Holub on Patterns:
 * Learning Design Patterns by Looking at Code</b> (Apress, 2004). It has been
 * extended to use generics.
 * 
 * @see <a href="http://www.holub.com/goodies/patterns/index.html"> Holub on
 *      Patterns: Learning Design Patterns by Looking at Code</a>
 * 
 * @author Allen Holub (in <a
 *         href="http://www.holub.com/goodies/patterns/index.html"> Holub on
 *         Patterns</a>)
 * @author Paul Gestwicki (generic version, now with type inference)
 * @param <T>
 *            the type to which notification will be sent
 */
public final class Publisher<T> {

	public static <T> Publisher<T> create() {
		return new Publisher<T>();
	}

	/** The list of subscribers */
	private volatile Node subscribers = null;

	private Publisher() {
	}

	/**
	 * Publish an event using a delivery agent.
	 * 
	 * @param deliveryAgent
	 *            the agent that delivers the event
	 */
	public void publish(Distributor<T> deliveryAgent) {
		for (Node cursor = subscribers; cursor != null; cursor = cursor.next)
			cursor.accept(deliveryAgent);
	}

	/**
	 * Add a new subscriber to this publisher.
	 * 
	 * @param subscriber
	 *            the subcriber to add
	 */
	public void subscribe(T subscriber) {
		if (subscriber == null)
			throw new IllegalArgumentException();
		subscribers = new Node(subscriber, subscribers);
	}

	/**
	 * Cancel a subscription
	 * 
	 * @param subscriber
	 *            the subscriber to remove
	 */
	public void unsubscribe(T subscriber) {
		if (subscriber == null)
			throw new IllegalArgumentException();
		subscribers = subscribers.remove(subscriber);
	}

	/**
	 * Specifies an object that delivers messages to subcribers.
	 * 
	 * @author Allen Holub (in <a
	 *         href="http://www.holub.com/goodies/patterns/index.html"> Holub on
	 *         Patterns</a>)
	 * @author Paul Gestwicki
	 * @param <S>
	 *            subscriber type
	 */
	public interface Distributor<S> {
		/**
		 * Deliver a message to the given subscriber. This is equivalent to the
		 * <tt>visit</tt> method in the Visitor pattern.
		 * 
		 * @param subscriber
		 *            the subscriber
		 */
		void deliverTo(S subscriber);
		
	}

	/**
	 * An immutable linked list node. By having the list immutable,
	 * synchronization is not required. New nodes are added to the front of the
	 * structure, and so it is possible to modify the list while an event is
	 * being delivered on another thread. When an element is removed, the list
	 * is shallow copied so that any existing iterations complete correctly.
	 * 
	 * There is a chance that an object can receive a message after it has
	 * unsubscribed itself.
	 * 
	 * @author Allen Holub (in <a
	 *         href="http://www.holub.com/goodies/patterns/index.html"> Holub on
	 *         Patterns</a>)
	 * @author Paul Gestwicki
	 */
	private final class Node {
		public final Node next;
		public final T subscriber;

		private Node(T subscriber, Node next) {
			this.subscriber = subscriber;
			this.next = next;
		}

		/**
		 * Accept a delivery agent visitor.
		 * 
		 * @param deliveryAgent
		 *            the event distributor
		 */
		public void accept(Distributor<T> deliveryAgent) {
			deliveryAgent.deliverTo(subscriber);
		}

		public Node remove(T target) {
			if (target == subscriber)
				return next;
			else if (next == null)
				throw new java.util.NoSuchElementException(target.toString());
			else
				return new Node(subscriber, next.remove(target));
		}
	}
}
