package edu.bsu.pvgestwicki.etschallenge.core.math;


public enum Operation {

	ADD, SUBTRACT, MULTIPLY, DIVIDE;

	public boolean isHigherPriorityThan(Operation other) {
		switch (this) {
		case ADD:
		case SUBTRACT:
			return false;
		case MULTIPLY:
		case DIVIDE:
			switch (other) {
			case ADD:
			case SUBTRACT:
				return true;
			case MULTIPLY:
			case DIVIDE:
				return false;
			default:
				throw new IllegalStateException();
			}
		default:
			throw new IllegalStateException();
		}
	}

}
