package edu.bsu.pvgestwicki.etschallenge.core.pos;

public enum Direction {
	INDETERMINATE {
		@Override
		public DirectionFunction decreasingFunction() {
			throw new UnsupportedOperationException("Indeterminate");
		}

		@Override
		public DirectionFunction increasingFunction() {
			throw new UnsupportedOperationException("Indeterminate");
		}
	},
	HORIZONTAL {
		@Override
		public DirectionFunction decreasingFunction() {
			return DirectionFunction.LEFT;
		}

		@Override
		public DirectionFunction increasingFunction() {
			return DirectionFunction.RIGHT;
		}
	},
	VERTICAL {

		@Override
		public DirectionFunction decreasingFunction() {
			return DirectionFunction.ABOVE;
		}

		@Override
		public DirectionFunction increasingFunction() {
			return DirectionFunction.BELOW;
		}
	};

	public abstract DirectionFunction decreasingFunction();

	public abstract DirectionFunction increasingFunction();
}