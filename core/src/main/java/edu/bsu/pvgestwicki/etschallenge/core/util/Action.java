package edu.bsu.pvgestwicki.etschallenge.core.util;

import static com.google.common.base.Preconditions.checkNotNull;
import react.Value;
import react.ValueView;

public abstract class Action {

	private final Value<Boolean> enabled = Value.create(true);
	
	private final String name; 

	public Action(String name) {
		this.name=checkNotNull(name);
	}

	public ValueView<Boolean> enabled() {
		return enabled;
	}

	public void enable() {
		enabled.update(true);
	}
	
	public void disable() {
		enabled.update(false);
	}
	
	public String name() {
		return name;
	}
	
	public abstract void run();
}
