package edu.bsu.pvgestwicki.etschallenge.core.util;

import static com.google.common.base.Preconditions.checkNotNull;
import playn.core.GroupLayer;
import playn.core.Layer;

public final class LayerEliminator implements Runnable {

	public static Builder layer(Layer child) {
		return new Builder(child);
	}
	
	public static final class Builder {
		private final Layer child;
		private GroupLayer parent;
		private Builder(Layer child) {
			this.child = checkNotNull(child);
		}
		
		public LayerEliminator fromParent(GroupLayer parent) {
			this.parent=parent;
			return new LayerEliminator(this);
		}
	}
	
	private final Layer child;
	private final GroupLayer parent;
	
	private LayerEliminator(Builder builder) {
		this.child=builder.child;
		this.parent=builder.parent;
	}

	@Override
	public void run() {
		parent.remove(child);
		child.destroy();
	}
}
