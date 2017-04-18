package edu.bsu.pvgestwicki.etschallenge.core.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public final class SequenceGenerator {

	public static SequenceGenerator create() {
		return new SequenceGenerator();
	}

	private StringBuffer sequence = new StringBuffer();
	private String previous = null;

	private SequenceGenerator() {
	}

	public SequenceGenerator sequence(String string) {
		previous = string;
		sequence.append(string);
		return this;
	}

	public String generate() {
		return sequence.toString();
	}

	public SequenceGenerator times(int i) {
		checkArgument(i >= 0, "Times must be non-negative");
		checkState(previous != null, "No previous sequence!");
		for (; i > 1; i--) {
			sequence(previous);
		}
		return this;
	}
}
